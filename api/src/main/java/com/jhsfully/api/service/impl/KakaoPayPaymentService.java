package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.PaymentStateType.REFUND;
import static com.jhsfully.domain.type.PaymentStateType.SUCCESS;
import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.GradeErrorType.GRADE_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.PAYMENT_CANNOT_APPROVE;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.PAYMENT_CANNOT_REFUND;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.PAYMENT_IS_ALREADY_REFUNDED;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.PAYMENT_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.REFUND_COUNT_MORE_THAN_ONE;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.REFUND_DEADLINE_IS_ONE_WEEK;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.REMAIN_ENABLE_DAYS_MORE_THAN_ONE;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.YOU_ARE_NOT_PAYMENT_OWNER;

import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.exception.GradeException;
import com.jhsfully.api.exception.PaymentException;
import com.jhsfully.api.model.payment.PaymentApprovedResponse;
import com.jhsfully.api.model.payment.PaymentRefundResponse;
import com.jhsfully.api.model.payment.PaymentResponse;
import com.jhsfully.api.model.payment.RedirectUrlResponse;
import com.jhsfully.api.service.PaymentService;
import com.jhsfully.domain.entity.Grade;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.entity.Payment;
import com.jhsfully.domain.entity.PaymentReady;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.GradeRepository;
import com.jhsfully.domain.repository.MemberRepository;
import com.jhsfully.domain.repository.PaymentReadyRepository;
import com.jhsfully.domain.repository.PaymentRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KakaoPayPaymentService implements PaymentService {

  private final static int ONE_PAY_ADD_ENABLE_DAYS = 31;
  private final String PART_CANCEL_PAYMENT = "PART_CANCEL_PAYMENT";
  private final String CANCEL_PAYMENT = "CANCEL_PAYMENT";
  @Value("${spring.payment.kakao.request-url}")
  private String REQUEST_URL;
  @Value("${spring.payment.kakao.approve-url}")
  private String APPROVE_URL;
  @Value("${spring.payment.kakao.success-url}")
  private String SUCCESS_URL;
  @Value("${spring.payment.kakao.refund-url}")
  private String REFUND_URL;
  @Value("${spring.payment.kakao.fail-url}")
  private String FAIL_URL;
  @Value("${spring.payment.kakao.cancel-url}")
  private String CANCEL_URL;


  @Value("${spring.payment.kakao.cid}")
  private String CID;
  @Value("${spring.payment.kakao.admin-key}")
  private String ADMIN_KEY;
  private final GradeRepository gradeRepository;
  private final PaymentReadyRepository paymentReadyRepository; //redis
  private final MemberRepository memberRepository;
  private final PaymentRepository paymentRepository; //mysql
  private final ApiInfoRepository apiInfoRepository;


  @Override
  public PaymentResponse getPaymentList(long memberId, int pageSize, int pageIdx) {
    return null;
  }

  @Override
  public RedirectUrlResponse payment(long memberId, long gradeId) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    validatePayment(member);

    Grade grade = gradeRepository.findById(gradeId)
        .orElseThrow(() -> new GradeException(GRADE_NOT_FOUND));

    //맵핑시킬 uuid생성함.
    String paymentUUID = UUID.randomUUID().toString().replaceAll("-", "");

    // 카카오페이 요청 양식
    MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
    parameters.add("cid", CID);
    parameters.add("partner_order_id", paymentUUID);
    parameters.add("partner_user_id", member.getId().toString());
    parameters.add("item_name", grade.getGradeName());
    parameters.add("item_code", grade.getId().toString());
    parameters.add("quantity", "1");
    parameters.add("total_amount", String.valueOf(grade.getPrice()));
    parameters.add("tax_free_amount", "0");
    parameters.add("approval_url", SUCCESS_URL + "?payment_uuid=" + paymentUUID); // 성공 시 redirect url
    parameters.add("cancel_url", CANCEL_URL); // 취소 시 redirect url
    parameters.add("fail_url", FAIL_URL); // 실패 시 redirect url

    // 헤더 및 파라미터 셋팅
    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, getHeaders());

    // 카카오 요청할 URL
    RestTemplate restTemplate = new RestTemplate();

    Map<String, String> response = restTemplate.postForObject(
        REQUEST_URL,
        requestEntity,
        Map.class);

    //Redis에 임시 정보 저장하기.
    PaymentReady paymentReady = PaymentReady.builder()
        .paymentUUID(paymentUUID)
        .tid(response.get("tid"))
        .gradeId(grade.getId())
        .memberId(memberId)
        .paidAmount(grade.getPrice())
        .build();

    paymentReadyRepository.save(paymentReady);

    return new RedirectUrlResponse(
        response.get("next_redirect_mobile_url"),
        response.get("next_redirect_pc_url"));
  }

  @Override
  public void refund(long memberId, long paymentId) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    Payment payment = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new PaymentException(PAYMENT_NOT_FOUND));

    validateRefund(member, payment);

    int diffDays = Period.between(payment.getPaidAt().toLocalDate(), LocalDate.now()).getDays();

    Long cancelAmount = Math.round((payment.getPaymentAmount() * (30 - diffDays)) / (double)30);

    //환불 요청을 카카오 쪽으로 보내야함.
    MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
    parameters.add("cid", CID);
    parameters.add("tid", payment.getTid());
    parameters.add("cancel_amount", cancelAmount.toString());
    parameters.add("partner_user_id", member.getId().toString());
    parameters.add("cancel_tax_free_amount", "0");

    // 헤더와 파라미터 셋팅.
    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, getHeaders());

    // 카카오페이 서버로 승인요청을 보냄.
    RestTemplate restTemplate = new RestTemplate();

    PaymentRefundResponse response = null;

    try {
      response = restTemplate.postForObject(REFUND_URL, requestEntity, PaymentRefundResponse.class);

      if(response == null){
        throw new PaymentException(PAYMENT_CANNOT_REFUND);
      }

    }catch (RuntimeException e){
      log.info(e.getMessage());
      throw new PaymentException(PAYMENT_CANNOT_REFUND); //API조작이나, 처리오류등.. 관리자가 추후에 강제로 환불해주어야함.
    }

    if(!PART_CANCEL_PAYMENT.equals(response.getStatus()) && !CANCEL_PAYMENT.equals(response.getStatus())){
      throw new PaymentException(PAYMENT_CANNOT_REFUND);
    }

    //성공적으로 환불 처리가 되었으므로, 결제 정보를 변경함.
    payment.setRefundAt(LocalDateTime.now());
    payment.setRefundAmount(cancelAmount);

    paymentRepository.save(payment);

    //등급 또한, 강제로 강등함.
    member.setGrade(
        gradeRepository.findById(1L).orElseThrow(() -> new GradeException(GRADE_NOT_FOUND))
    );

    memberRepository.save(member);

    //추가적으로 소유한 모든 API를 비활성화 조치함.
    apiInfoRepository.updateApiInfoToDisabledByMember(member);
  }

  // ############### REDIRECT URL에 의해 트리거 되는 메소드 #####################
  @Override
  public void approvePayment(String paymentUUID, String pgToken) {

    PaymentReady paymentReady = paymentReadyRepository.findById(paymentUUID)
        .orElseThrow(() -> new PaymentException(PAYMENT_NOT_FOUND));

    //카카오페이 단으로 approve요청을 보내서 승인 받아야함.
    MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
    parameters.add("cid", CID);
    parameters.add("tid", paymentReady.getTid());
    parameters.add("partner_order_id", paymentUUID);
    parameters.add("partner_user_id", String.valueOf(paymentReady.getMemberId()));
    parameters.add("pg_token", pgToken);

    // 헤더와 파라미터 셋팅.
    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, getHeaders());

    // 카카오페이 서버로 승인요청을 보냄.
    RestTemplate restTemplate = new RestTemplate();

    PaymentApprovedResponse response = null;

    try {
      response = restTemplate.postForObject(APPROVE_URL, requestEntity, PaymentApprovedResponse.class);

      if(response == null){
        throw new PaymentException(PAYMENT_CANNOT_APPROVE);
      }

    }catch (RuntimeException e){
      System.out.println(e.getMessage());
      throw new PaymentException(PAYMENT_CANNOT_APPROVE); //API조작이나, 처리오류등.. 관리자가 추후에 강제로 환불해주어야함.
    }

    //성공적으로 결제가 되었으므로, 결제 객체를 생성 및 멤버 객체 업데이트
    createAndUpdateRelatedEntities(paymentReady, response);

    //중복 결제를 방지하기 위해, Redis에서 지워줌.
    paymentReadyRepository.delete(paymentReady);
  }

  private void createAndUpdateRelatedEntities(PaymentReady paymentReady, PaymentApprovedResponse response){
    Grade grade = gradeRepository.findById(paymentReady.getGradeId())
        .orElseThrow(() -> new GradeException(GRADE_NOT_FOUND));

    Member member = memberRepository.findById(paymentReady.getMemberId())
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    Payment payment = Payment.builder()
        .grade(grade)
        .member(member)
        .paymentAmount(response.getAmount().getTotal())
        .tid(response.getTid())
        .paymentStateType(SUCCESS)
        .paidAt(LocalDateTime.now())
        .build();

    paymentRepository.save(payment);

    //멤버 객체 또한, 수정해야함.
    member.setGrade(grade);
    member.setRemainEnableDays(member.getRemainEnableDays() + ONE_PAY_ADD_ENABLE_DAYS);
    member.setLatestPaidAt(payment.getPaidAt());
    memberRepository.save(member);
  }

  /*
      #################### UTILS ##############################
   */

  private HttpHeaders getHeaders() {
    HttpHeaders httpHeaders = new HttpHeaders();

    String auth = "KakaoAK " + ADMIN_KEY;

    httpHeaders.set("Authorization", auth);
    httpHeaders.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    return httpHeaders;
  }

  /*
      ################ Validates ##################
   */

  private void validatePayment(Member member){
    /*
        결제가 수행되면 안되는 상황

        1. 등급 활성 일수가 1보다 큰 경우.
        2. 멤버의 환불 횟수가 1회 초과인 경우.
     */

    if(member.getRemainEnableDays() > 1){
      throw new PaymentException(REMAIN_ENABLE_DAYS_MORE_THAN_ONE);
    }

    if(member.getRefundCount() > 1){
      throw new PaymentException(REFUND_COUNT_MORE_THAN_ONE);
    }

  }

  private void validateRefund(Member member, Payment payment){

    /*
        환불이 불가능한 경우
        1. member와 payment가 일치하지 않는 경우.
        2. 이미 환불된, payment인 경우.
        3. 해당 Payment의 결제일로부터, 7일을 초과한 경우.
     */

    if(!Objects.equals(member.getId(), payment.getMember().getId())){
      throw new PaymentException(YOU_ARE_NOT_PAYMENT_OWNER);
    }

    if(payment.getPaymentStateType() == REFUND){
      throw new PaymentException(PAYMENT_IS_ALREADY_REFUNDED);
    }

    if(LocalDateTime.now().isAfter(payment.getPaidAt().plusWeeks(1))){
      throw new PaymentException(REFUND_DEADLINE_IS_ONE_WEEK);
    }

  }
}
