package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.PaymentStateType.REFUND;
import static com.jhsfully.domain.type.PaymentStateType.SUCCESS;
import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.GradeErrorType.GRADE_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.CANCEL_AMOUNT_IS_WRONG;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.CANNOT_BUY_THIS_GRADE;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.PAYMENT_CANNOT_APPROVE;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.PAYMENT_CANNOT_REFUND;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.PAYMENT_IS_ALREADY_REFUNDED;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.PAYMENT_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.PAYMENT_REQUEST_IS_WRONG;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.REFUND_COUNT_MORE_THAN_ONE;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.REFUND_DEADLINE_IS_ONE_WEEK;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.REMAIN_ENABLE_DAYS_MORE_THAN_ONE;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.YOU_ARE_NOT_PAYMENT_OWNER;

import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.exception.GradeException;
import com.jhsfully.api.exception.PaymentException;
import com.jhsfully.api.model.dto.PaymentDto;
import com.jhsfully.api.model.payment.PaymentApprovedResponse;
import com.jhsfully.api.model.payment.PaymentReadyResponse;
import com.jhsfully.api.model.payment.PaymentReadyResponseForClient;
import com.jhsfully.api.model.payment.PaymentRefundResponse;
import com.jhsfully.api.model.payment.PaymentResponse;
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
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/*
      카카오페이의 결제로직은 다음과 같음.

      1. 클라이언트 -> 백엔드 (결제 요청 날리기)
      2. 백엔드 -> 카카오페이 (결제 요청을 데이터를 조합하여 요청을 보냄)
      - 헤더에 KakaoAK {Admin_KEY} 데이터를 넣어주지 않은 경우 401에러를 반환하므로 넣어드려야함.
      - 0원의 결제요청을 수행하면, 적어도 Amount가 존재해야한다고, 400 BadRequest에러가 발생하므로, 결제요청 값은 반드시 0원이상.
      - 가이드에 따라서, POST 요청으로 처리함.
      3. 카카오페이 -> 백엔드 (tid와 결제창으로 넘어갈 redirectURL을 같이 제공함.)
       - 응답이 오면, tid와 RedirectURL이 제공이 되는데, 여기에서, 절대로 tid를 유저에게 공개해서는 안됨.
       - 물론, 카카오페이 API가 CORS가 등록된 URL로 닫혀있고, ADMIN_KEY를 알아야 악용이 가능함.
       - 하지만, 카카오페이 측에서는, 기본적으로 tid를 유저에게 노출시키 않을 것을 권장하고 있음.
       - 이를 준수하여, tid를 임시적으로 저장하기 위해서, redis에 uuid와 tid를 맵핑하여, 임시적으로 데이터를 저장하기로 함.
      4. 백엔드 -> 클라이언트 (응답받은 redirectURL을 유저에게 넘겨주어, 결제를 수행하도록 해야함.)
      5. 클라이언트 -> 결제 수행 (유저가 직접 돈을 꺼내도록 유도함 ^_^;;)
      6. 카카오페이 -> 백엔드 (REDIRECT)
       - 결제 수행이 완료되면, 카카오페이 측에서, 우리가 임의로 지정한 approval_url 로 REDIRECT시킴.
       - 이 때, 기본적으로 승인 토큰인 pg_token만 parameter로 넘어오는데, 이를 통해, tid를 식별하기가 힘듦.
       - 이를 위해, 2번 단계에서 approval_url에 parameter를 추가적으로 넘겨주면, redirect하면 그대로 넘겨서 받을 수 있다고 함.
       - 임의로 생성한 uuid를 2번단계에서 넘겨주어, 6번단계에서 parameter로 받을 수 있음.
      7. 백엔드 -> 카카오페이 (승인 요청)
       - 결제가 확실히 수행되었다고 판단되면, 백엔드단에서, 카카오페이서버로 approve요청을 날려서, 결제를 마무리지음.
       - 이 때, tid, pg_token 등 여러데이터를 올바르게 실어서 POST요청을 보내야함.
       - 정상 응답이 오면, 결제가 완료되었고, DB를 조작하여, 특정 기능을 제공해주면 끗!
 */


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KakaoPayPaymentService implements PaymentService {

  private final static Long BRONZE_GRADE_ID = 1L;
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


  /*
      결제기록을 가져옵니다요~
   */
  @Override
  public PaymentResponse getPaymentList(long memberId, int pageSize, int pageIdx) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    Pageable pageable = PageRequest.of(pageIdx, pageSize);

    Page<Payment> paymentPage = paymentRepository.findByMember(member, pageable);

    return PaymentResponse.builder()
        .totalCount(paymentPage.getTotalElements())
        .dataCount(paymentPage.getNumberOfElements())
        .dataList(
            paymentPage.getContent()
                .stream()
                .map(PaymentDto::of)
                .collect(Collectors.toList())
        )
        .build();
  }

  /*
      결제 요청을 수행하는 메서드
   */
  @Override
  public PaymentReadyResponseForClient payment(long memberId, long gradeId) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    validatePayment(member, gradeId);

    Grade grade = gradeRepository.findById(gradeId)
        .orElseThrow(() -> new GradeException(GRADE_NOT_FOUND));

    //맵핑시킬 uuid생성함.
    String paymentUUID = UUID.randomUUID().toString().replaceAll("-", "");

    HttpEntity<MultiValueMap<String, String>> requestEntity = getPaymentRequestEntity(paymentUUID, member, grade);

    // 카카오 요청할 URL
    RestTemplate restTemplate = new RestTemplate();

    PaymentReadyResponse response = null;

    try {
      log.info("카카오페이로 결제요청 날림");
      response = restTemplate.postForObject(
          REQUEST_URL,
          requestEntity,
          PaymentReadyResponse.class);
    }catch (Exception e){
      log.info("결제요청 중에, 오류가 발생함");
      //카카오페이 측, 요청 요구 사항이 변경되었거나, 서버오류임.
      throw new PaymentException(PAYMENT_REQUEST_IS_WRONG);
    }
    log.info("성공적으로 결제요청이 완료됨.");

    //Redis에 임시 정보 저장하기.
    PaymentReady paymentReady = PaymentReady.builder()
        .paymentUUID(paymentUUID)
        .tid(response.getTid())
        .gradeId(grade.getId())
        .memberId(memberId)
        .paidAmount(grade.getPrice())
        .build();

    paymentReadyRepository.save(paymentReady);
    log.info("REDIS에 임시적으로 결제대기 데이터를 저장함.");

    return new PaymentReadyResponseForClient(
        response.getNextRedirectMobileUrl(),
        response.getNextRedirectPcUrl());
  }
  private HttpEntity<MultiValueMap<String, String>> getPaymentRequestEntity(String paymentUUID, Member member, Grade grade){
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
    return new HttpEntity<>(parameters, getHeaders());
  }


  /*
      환불 처리를 수행하는 메서드.
   */
  @Override
  public void refund(long memberId, long paymentId) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    Payment payment = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new PaymentException(PAYMENT_NOT_FOUND));

    validateRefund(member, payment);

    int diffDays = Period.between(payment.getPaidAt().toLocalDate(), LocalDate.now()).getDays();
    Long cancelAmount = Math.round((payment.getPaymentAmount() * (ONE_PAY_ADD_ENABLE_DAYS - diffDays)) / (double)ONE_PAY_ADD_ENABLE_DAYS);

    log.info("환불 유저 이메일 : " + member.getEmail() +
        ", 결제 금액 : " + payment.getPaymentAmount() +
        ", 기간 차이 : " + diffDays +
        ", 환불 금액 : " + cancelAmount);

    HttpEntity<MultiValueMap<String, String>> requestEntity = getRefundRequestEntity(payment, member, cancelAmount);

    // 카카오페이 서버로 승인요청을 보냄.
    RestTemplate restTemplate = new RestTemplate();

    PaymentRefundResponse response = null;

    try {
      log.info("카카오페이 서버로 환불 요청 전송! id=" + payment.getId());
      response = restTemplate.postForObject(REFUND_URL, requestEntity, PaymentRefundResponse.class);

      if(response == null){
        throw new PaymentException(PAYMENT_CANNOT_REFUND);
      }

    }catch (RuntimeException e){
      log.info(e.getMessage() + ", 환불이 정상적으로 이루어지지 못했음, id=" + payment.getId());
      throw new PaymentException(PAYMENT_CANNOT_REFUND); //API조작이나, 처리오류등.. 관리자가 추후에 강제로 환불해주어야함.
    }

    //카카오페이 문서에 공개된 취소에 관련된 상태 정보를 참고함.
    //만약, 취소된 이력이 내려오지 않았으면, 무언가가 잘못되었는지 검토가 필요함.
    if(!PART_CANCEL_PAYMENT.equals(response.getStatus()) && !CANCEL_PAYMENT.equals(response.getStatus())){
      log.info("요청 응답이 날아왔지만, 취소 및 부분취소 상태가 아님, id=" + payment.getId());
      throw new PaymentException(CANCEL_AMOUNT_IS_WRONG);
    }

    updateRelatedDataForRefund(payment, member, response);
    log.info("정상적으로 DB에 데이터가 반영됨, id=" + payment.getId());

  }
  private HttpEntity<MultiValueMap<String, String>> getRefundRequestEntity(Payment payment, Member member, Long cancelAmount){
    //환불 요청을 카카오 쪽으로 보내야함.
    MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
    parameters.add("cid", CID);
    parameters.add("tid", payment.getTid());
    parameters.add("cancel_amount", cancelAmount.toString());
    parameters.add("partner_user_id", member.getId().toString());
    parameters.add("cancel_tax_free_amount", "0");

    // 헤더와 파라미터 셋팅.
    return new HttpEntity<>(parameters, getHeaders());
  }
  private void updateRelatedDataForRefund(Payment payment, Member member, PaymentRefundResponse response){
    //성공적으로 환불 처리가 되었으므로, 결제 정보를 변경함.
    payment.setRefundAt(LocalDateTime.now());
    payment.setRefundAmount(response.getAmount().getTotal());
    payment.setPaymentStateType(REFUND);

    paymentRepository.save(payment);

    //등급 또한, 강제로 강등함.
    member.setGrade(
        gradeRepository.findById(BRONZE_GRADE_ID).orElseThrow(() -> new GradeException(GRADE_NOT_FOUND))
    );

    //만료기한을 이미 지난날로 셋팅해버림.
    member.setExpiredEnabledAt(LocalDate.now().minusDays(1));

    //멤버 환불 카운트 올림.
    member.setRefundCount(member.getRefundCount() + 1);

    memberRepository.save(member);

    //추가적으로 소유한 모든 API를 비활성화 조치함.
    apiInfoRepository.updateApiInfoToDisabledByMember(member, LocalDate.now());
  }


  // ############### REDIRECT URL에 의해 트리거 되는 결제 승인 메소드 #####################
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

    //Approve 요청은 한 번만 수행이 가능하므로, 중복 결제가 일어날 수 없음.
    try {
      log.info("카카오페이에 결제 승인 요청을 보냄, tid=" + paymentReady.getTid());
      response = restTemplate.postForObject(APPROVE_URL, requestEntity, PaymentApprovedResponse.class);

      if(response == null){
        throw new PaymentException(PAYMENT_CANNOT_APPROVE);
      }

    }catch (RuntimeException e){
      log.info(e.getMessage() + "카카오페이 요청 중에 오류 발생, tid=" + paymentReady.getTid());
      throw new PaymentException(PAYMENT_CANNOT_APPROVE); //API조작이나, 처리오류등.. 관리자가 추후에 강제로 환불해주어야함.
    }

    //성공적으로 결제가 되었으므로, 결제 객체를 생성 및 멤버 객체 업데이트
    createAndUpdateRelatedEntities(paymentReady, response);
    log.info("결제 승인 완료 후, DB에 데이터 갱신 수행! tid=" + paymentReady.getTid());

    //tid 유출을 막기위해, 임시로 홀딩한 redis 데이터를 제거함.
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
    member.setExpiredEnabledAt(member.getExpiredEnabledAt().plusDays(ONE_PAY_ADD_ENABLE_DAYS));
    member.setLatestPaidAt(payment.getPaidAt());
    memberRepository.save(member);
  }

  /*
      #################### UTILS ##################
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

  private void validatePayment(Member member, long gradeId){
    /*
        결제가 수행되면 안되는 상황

        1. gradeId가 0또는 1인 경우.
        2. 등급 활성 일수가 1보다 큰 경우.
        3. 멤버의 환불 횟수가 1회 초과인 경우.
     */
    if(gradeId <= 1){
      throw new PaymentException(CANNOT_BUY_THIS_GRADE);
    }

    //추후에 로직 검증이 필요함.
    if(member.getExpiredEnabledAt() != null && member.getExpiredEnabledAt().isAfter(LocalDate.now())){
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
