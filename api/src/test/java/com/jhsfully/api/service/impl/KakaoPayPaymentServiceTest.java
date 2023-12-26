package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.PaymentStateType.REFUND;
import static com.jhsfully.domain.type.PaymentStateType.SUCCESS;
import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.GradeErrorType.GRADE_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.CANNOT_BUY_THIS_GRADE;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.NOT_CANCEL_STATE;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.PAYMENT_CANNOT_APPROVE;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.PAYMENT_CANNOT_REFUND;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.PAYMENT_IS_ALREADY_REFUNDED;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.PAYMENT_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.PAYMENT_REQUEST_IS_WRONG;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.REFUND_COUNT_MORE_THAN_ONE;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.REMAIN_ENABLE_DAYS_MORE_THAN_ONE;
import static com.jhsfully.domain.type.errortype.PaymentErrorType.YOU_ARE_NOT_PAYMENT_OWNER;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.exception.GradeException;
import com.jhsfully.api.exception.PaymentException;
import com.jhsfully.api.model.PageResponse;
import com.jhsfully.api.model.dto.PaymentDto;
import com.jhsfully.api.model.payment.PaymentApprovedResponse;
import com.jhsfully.api.model.payment.PaymentReadyResponse;
import com.jhsfully.api.model.payment.PaymentReadyResponseForClient;
import com.jhsfully.api.model.payment.PaymentRefundResponse;
import com.jhsfully.api.model.payment.PaymentRefundResponse.Amount;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class KakaoPayPaymentServiceTest {

    //dependencies
    @Mock
    private GradeRepository gradeRepository;
    @Mock
    private PaymentReadyRepository paymentReadyRepository; //redis
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PaymentRepository paymentRepository; //mysql
    @Mock
    private ApiInfoRepository apiInfoRepository;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private KakaoPayPaymentService kakaoPayPaymentService;

    //constants
    private final static int ONE_PAY_ADD_ENABLE_DAYS = 31;
    private final String PART_CANCEL_PAYMENT = "PART_CANCEL_PAYMENT";
    private final String CANCEL_PAYMENT = "CANCEL_PAYMENT";

    //getEntityMethods
    private Member getMember() {
        return Member.builder()
            .id(1L)
            .expiredEnabledAt(LocalDate.of(2023,11,1))
            .refundCount(0)
            .build();
    }

    private Grade getBronzeGrade() {
        return Grade.builder()
            .id(1L)
            .gradeName("BRONZE")
            .price(0L)
            .build();
    }

    private Grade getGoldGrade() {
        return Grade.builder()
            .id(3L)
            .gradeName("GOLD")
            .price(3000L)
            .build();
    }

    private Payment getPayment() {
        return Payment.builder()
            .id(1L)
            .tid("tid")
            .grade(getGoldGrade())
            .member(getMember())
            .paymentAmount(getGoldGrade().getPrice())
            .paidAt(LocalDateTime.of(2023, 11, 1, 9, 0, 0))
            .paymentStateType(SUCCESS)
            .build();
    }

    private PaymentReady getPaymentReady() {
        return PaymentReady.builder()
            .paymentUUID(UUID.randomUUID().toString())
            .tid("tid")
            .gradeId(3L)
            .memberId(1L)
            .paidAmount(3000L)
            .build();
    }


    @Nested
    @DisplayName("getPaymentList() 테스트")
    class GetPaymentListTest {

        @Test
        @DisplayName("결제 내역 조회 성공")
        void success_getPaymentList() {
            //given
            Member member = getMember();
            Payment payment = getPayment();

            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

            given(paymentRepository.findByMember(any(), any()))
                .willReturn(new PageImpl<>(
                    List.of(
                        payment
                    )
                ));

            //when
            PageResponse<PaymentDto> response = kakaoPayPaymentService.getPaymentList(1L, PageRequest.of(0, 1));

            //then
            assertAll(
                () -> assertEquals(payment.getId(), response.getContent().get(0).getId()),
                () -> assertEquals(payment.getGrade().getGradeName(), response.getContent().get(0).getGrade()),
                () -> assertEquals(payment.getPaymentAmount(), response.getContent().get(0).getPaymentAmount()),
                () -> assertEquals(payment.getRefundAmount(), response.getContent().get(0).getRefundAmount()),
                () -> assertEquals(payment.getPaidAt(), response.getContent().get(0).getPaidAt()),
                () -> assertEquals(payment.getRefundAt(), response.getContent().get(0).getRefundAt()),
                () -> assertEquals(payment.getPaymentStateType(), response.getContent().get(0).getPaymentState())
            );

        }

        @Test
        @DisplayName("결제 내역 조회 실패 - 회원 X")
        void failed_getPaymentList_authentication_user_not_found() {
            //given
            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.empty());

            //when
            AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> kakaoPayPaymentService.getPaymentList(1L, PageRequest.of(0, 1)));

            //then
            assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
        }

    }

    @Nested
    @DisplayName("paymentRequest() 테스트")
    class PaymentRequestTest {

        @Test
        @DisplayName("결제 요청 성공")
        void success_paymentRequest() {
            //given
            ReflectionTestUtils.setField(kakaoPayPaymentService, "REQUEST_URL", "TEST_URL");
            Member member = getMember();
            Grade goldGrade = getGoldGrade();
            PaymentReadyResponse paymentReadyResponse = PaymentReadyResponse
                .builder()
                .tid("tid")
                .nextRedirectMobileUrl("http://mobile.url.com")
                .nextRedirectPcUrl("http://pc.url.com")
                .build();

            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

            given(gradeRepository.findById(anyLong()))
                .willReturn(Optional.of(goldGrade));

            given(restTemplate.postForObject(anyString(), any(Object.class), eq(PaymentReadyResponse.class)))
                .willReturn(paymentReadyResponse);

            // when
            PaymentReadyResponseForClient response = kakaoPayPaymentService.paymentRequest(
                1L, 3L, LocalDate.of(2023, 11, 1)
            );

            // then
            ArgumentCaptor<PaymentReady> captor = ArgumentCaptor.forClass(PaymentReady.class);
            verify(paymentReadyRepository, times(1)).save(captor.capture());
            PaymentReady paymentReady = captor.getValue();

            assertAll(
                () -> assertEquals(paymentReadyResponse.getNextRedirectMobileUrl(), response.getNextRedirectMobileUrl()),
                () -> assertEquals(paymentReadyResponse.getNextRedirectPcUrl(), response.getNextRedirectPcUrl()),

                () -> assertEquals(paymentReadyResponse.getTid(), paymentReady.getTid()),
                () -> assertEquals(goldGrade.getId(), paymentReady.getGradeId()),
                () -> assertEquals(member.getId(), paymentReady.getMemberId()),
                () -> assertEquals(goldGrade.getPrice(), paymentReady.getPaidAmount())
            );
        }

        @Test
        @DisplayName("결제 요청 실패 - 회원 X")
        void failed_paymentRequest_authentication_user_not_found() {
            //given
            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.empty());

            // when
            AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> kakaoPayPaymentService.paymentRequest(
                    1L, 3L, LocalDate.of(2023, 11, 1)
                ));

            //then
            assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
        }

        @Test
        @DisplayName("결제 요청 실패 - 결제 등급 ID가 BRONZE 이하")
        void failed_paymentRequest_cannot_buy_this_grade() {
            //given
            ReflectionTestUtils.setField(kakaoPayPaymentService, "REQUEST_URL", "TEST_URL");
            Member member = getMember();

            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

            // when
            PaymentException exception = assertThrows(PaymentException.class,
                () -> kakaoPayPaymentService.paymentRequest(
                    1L, 1L, LocalDate.of(2023, 11, 1)
                ));

            //then
            assertEquals(CANNOT_BUY_THIS_GRADE, exception.getPaymentErrorType());
        }

        @Test
        @DisplayName("결제 요청 실패 - 활성 일수 1일이상 남음.")
        void failed_paymentRequest_remain_enable_days_more_than_one() {
            //given
            ReflectionTestUtils.setField(kakaoPayPaymentService, "REQUEST_URL", "TEST_URL");
            Member member = getMember();

            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

            // when
            PaymentException exception = assertThrows(PaymentException.class,
                () -> kakaoPayPaymentService.paymentRequest(
                    1L, 3L, LocalDate.of(2023, 10, 31)
                ));

            //then
            assertEquals(REMAIN_ENABLE_DAYS_MORE_THAN_ONE, exception.getPaymentErrorType());
        }

        @Test
        @DisplayName("결제 요청 실패 - 환불 이력 1회 초과")
        void failed_paymentRequest_refund_count_more_than_one() {
            //given
            ReflectionTestUtils.setField(kakaoPayPaymentService, "REQUEST_URL", "TEST_URL");
            Member member = getMember();
            member.setRefundCount(2);

            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

            // when
            PaymentException exception = assertThrows(PaymentException.class,
                () -> kakaoPayPaymentService.paymentRequest(
                    1L, 3L, LocalDate.of(2023, 11, 1)
                ));

            //then
            assertEquals(REFUND_COUNT_MORE_THAN_ONE, exception.getPaymentErrorType());
        }

        @Test
        @DisplayName("결제 요청 실패 - 등급 X")
        void failed_paymentRequest_grade_not_found() {
            //given
            ReflectionTestUtils.setField(kakaoPayPaymentService, "REQUEST_URL", "TEST_URL");
            Member member = getMember();

            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

            given(gradeRepository.findById(anyLong()))
                .willReturn(Optional.empty());

            // when
            GradeException exception = assertThrows(GradeException.class,
                () -> kakaoPayPaymentService.paymentRequest(
                    1L, 3L, LocalDate.of(2023, 11, 1)
                ));

            //then
            assertEquals(GRADE_NOT_FOUND, exception.getGradeErrorType());
        }

        @Test
        @DisplayName("결제 요청 실패 - 카카오페이 서버 오류 또는 요청 문제")
        void failed_paymentRequest_payment_request_is_wrong() {
            //given
            ReflectionTestUtils.setField(kakaoPayPaymentService, "REQUEST_URL", "TEST_URL");
            Member member = getMember();
            Grade goldGrade = getGoldGrade();

            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

            given(gradeRepository.findById(anyLong()))
                .willReturn(Optional.of(goldGrade));

            given(restTemplate.postForObject(anyString(), any(Object.class), eq(PaymentReadyResponse.class)))
                .willThrow(new RestClientException("error"));

            // when
            PaymentException exception = assertThrows(PaymentException.class,
                () -> kakaoPayPaymentService.paymentRequest(
                    1L, 3L, LocalDate.of(2023, 11, 1)
                ));

            //then
            assertEquals(PAYMENT_REQUEST_IS_WRONG, exception.getPaymentErrorType());
        }

    }

    @Nested
    @DisplayName("refund() 테스트")
    class RefundTest {

        @Test
        @DisplayName("환불 성공")
        void success_refund() {
            //given
            ReflectionTestUtils.setField(kakaoPayPaymentService, "REFUND_URL", "TEST_URL");
            Member member = getMember();
            Grade bronzeGrade = getBronzeGrade();
            Payment payment = getPayment();
            PaymentRefundResponse paymentRefundResponse = new PaymentRefundResponse(
                new Amount(2806L), //결제 후 2일 뒤 환불.
                CANCEL_PAYMENT
            );

            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

            given(paymentRepository.findById(anyLong()))
                .willReturn(Optional.of(payment));

            given(restTemplate.postForObject(anyString(), any(Object.class), eq(PaymentRefundResponse.class)))
                .willReturn(paymentRefundResponse);

            given(gradeRepository.findById(1L))
                .willReturn(Optional.of(bronzeGrade));

            //when
            LocalDateTime nowTime = LocalDateTime.of(2023, 11, 3, 9, 30, 30);
            kakaoPayPaymentService.refund(1L, 1L, nowTime);

            //then
            ArgumentCaptor<HttpEntity<MultiValueMap<String, String>>> requestCaptor = ArgumentCaptor.forClass(
                HttpEntity.class
            );

            ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
            ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);

            verify(restTemplate, times(1))
                .postForObject(eq("TEST_URL"), requestCaptor.capture(), eq(PaymentRefundResponse.class));
            verify(paymentRepository, times(1)).save(paymentCaptor.capture());
            verify(memberRepository, times(1)).save(memberCaptor.capture());
            verify(apiInfoRepository, times(1)).updateApiInfoToDisabledByMember(
                any(), eq(nowTime)
            );

            assertAll(
                () -> assertEquals(2806L, Long.parseLong(Objects.requireNonNull(requestCaptor.getValue().getBody())
                    .get("cancel_amount").get(0))),

                () -> assertEquals(nowTime, paymentCaptor.getValue().getRefundAt()),
                () -> assertEquals(paymentRefundResponse.getAmount().getTotal(), paymentCaptor.getValue().getRefundAmount()),
                () -> assertEquals(REFUND, paymentCaptor.getValue().getPaymentStateType()),

                () -> assertEquals(bronzeGrade.getGradeName(), memberCaptor.getValue().getGrade().getGradeName()),
                () -> assertEquals(LocalDate.of(2023, 11, 2), memberCaptor.getValue().getExpiredEnabledAt()),
                () -> assertEquals(1, memberCaptor.getValue().getRefundCount())
            );
        }

        @Test
        @DisplayName("환불 실패 - 회원 X")
        void failed_refund_authentication_user_not_found() {
            //given
            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.empty());

            //when
            LocalDateTime nowTime = LocalDateTime.of(2023, 11, 3, 9, 30, 30);

            AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> kakaoPayPaymentService.refund(1L, 1L, nowTime));

            //then
            assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
        }

        @Test
        @DisplayName("환불 실패 - 결제 X")
        void failed_refund_payment_not_found() {
            //given
            Member member = getMember();

            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

            given(paymentRepository.findById(anyLong()))
                .willReturn(Optional.empty());

            //when
            LocalDateTime nowTime = LocalDateTime.of(2023, 11, 3, 9, 30, 30);

            PaymentException exception = assertThrows(PaymentException.class,
                () -> kakaoPayPaymentService.refund(1L, 1L, nowTime));

            //then
            assertEquals(PAYMENT_NOT_FOUND, exception.getPaymentErrorType());
        }

        @Test
        @DisplayName("환불 실패 - 결제의 주인이 아님")
        void failed_refund_you_are_not_payment_owner() {
            //given
            Member member = Member.builder().id(2L).build();
            Payment payment = getPayment();

            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

            given(paymentRepository.findById(anyLong()))
                .willReturn(Optional.of(payment));

            //when
            LocalDateTime nowTime = LocalDateTime.of(2023, 11, 3, 9, 30, 30);

            PaymentException exception = assertThrows(PaymentException.class,
                () -> kakaoPayPaymentService.refund(1L, 1L, nowTime));

            //then
            assertEquals(YOU_ARE_NOT_PAYMENT_OWNER, exception.getPaymentErrorType());
        }

        @Test
        @DisplayName("환불 실패 - 이미 환불된 결제")
        void failed_refund_payment_is_already_refunded() {
            //given
            Member member = getMember();
            Payment payment = getPayment();
            payment.setPaymentStateType(REFUND);

            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

            given(paymentRepository.findById(anyLong()))
                .willReturn(Optional.of(payment));

            //when
            LocalDateTime nowTime = LocalDateTime.of(2023, 11, 3, 9, 30, 30);

            PaymentException exception = assertThrows(PaymentException.class,
                () -> kakaoPayPaymentService.refund(1L, 1L, nowTime));

            //then
            assertEquals(PAYMENT_IS_ALREADY_REFUNDED, exception.getPaymentErrorType());
        }

        @Test
        @DisplayName("환불 실패 - 서버 오류 및 요청 오류")
        void failed_refund_payment_cannot_refund() {
            //given
            ReflectionTestUtils.setField(kakaoPayPaymentService, "REFUND_URL", "TEST_URL");
            Member member = getMember();
            Payment payment = getPayment();

            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

            given(paymentRepository.findById(anyLong()))
                .willReturn(Optional.of(payment));

            given(restTemplate.postForObject(anyString(), any(Object.class), eq(PaymentRefundResponse.class)))
                .willThrow(new RestClientException("error"));

            //when
            LocalDateTime nowTime = LocalDateTime.of(2023, 11, 3, 9, 30, 30);

            PaymentException exception = assertThrows(PaymentException.class,
                () -> kakaoPayPaymentService.refund(1L, 1L, nowTime));

            //then
            assertEquals(PAYMENT_CANNOT_REFUND, exception.getPaymentErrorType());
        }

        @Test
        @DisplayName("환불 실패 - 취소된 상태가 아님.")
        void failed_refund_cancel_not_cancel_state() {
            //given
            ReflectionTestUtils.setField(kakaoPayPaymentService, "REFUND_URL", "TEST_URL");
            Member member = getMember();
            Payment payment = getPayment();
            PaymentRefundResponse paymentRefundResponse = new PaymentRefundResponse(
                new Amount(2806L), //결제 후 2일 뒤 환불.
                "SUCCESS"
            );

            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

            given(paymentRepository.findById(anyLong()))
                .willReturn(Optional.of(payment));

            given(restTemplate.postForObject(anyString(), any(Object.class), eq(PaymentRefundResponse.class)))
                .willReturn(paymentRefundResponse);

            //when
            LocalDateTime nowTime = LocalDateTime.of(2023, 11, 3, 9, 30, 30);

            PaymentException exception = assertThrows(PaymentException.class,
                () -> kakaoPayPaymentService.refund(1L, 1L, nowTime));

            //then
            assertEquals(NOT_CANCEL_STATE, exception.getPaymentErrorType());
        }
    }

    @Nested
    @DisplayName("approvePayment() 테스트")
    class ApprovePaymentTest {

        @Test
        @DisplayName("결제 승인 성공")
        void success_approvePayment() {
            //given
            PaymentReady paymentReady = getPaymentReady();
            PaymentApprovedResponse paymentApprovedResponse = new PaymentApprovedResponse(
                "tid", new PaymentApprovedResponse.Amount(3000L));
            Grade goldGrade = getGoldGrade();
            Member member = getMember();

            given(paymentReadyRepository.findById(anyString()))
                .willReturn(Optional.of(paymentReady));

            ReflectionTestUtils.setField(kakaoPayPaymentService, "APPROVE_URL", "TEST_URL");
            given(restTemplate.postForObject(anyString(), any(Object.class), eq(PaymentApprovedResponse.class)))
                .willReturn(paymentApprovedResponse);

            given(gradeRepository.findById(anyLong()))
                .willReturn(Optional.of(goldGrade));

            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

            //when
            LocalDateTime nowTime = LocalDateTime.of(2023, 12, 1, 9, 30, 30);
            kakaoPayPaymentService.approvePayment(paymentReady.getPaymentUUID(), "pgToken", nowTime);

            //then
            ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
            ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
            ArgumentCaptor<PaymentReady> paymentReadyCaptor = ArgumentCaptor.forClass(PaymentReady.class);

            verify(paymentRepository, times(1)).save(paymentCaptor.capture());
            verify(memberRepository, times(1)).save(memberCaptor.capture());
            verify(paymentReadyRepository, times(1)).delete(paymentReadyCaptor.capture());

            assertAll(
                () -> assertEquals(goldGrade.getId(), paymentCaptor.getValue().getGrade().getId()),
                () -> assertEquals(member.getId(), paymentCaptor.getValue().getMember().getId()),
                () -> assertEquals(paymentReady.getPaidAmount(), paymentCaptor.getValue().getPaymentAmount()),
                () -> assertEquals(paymentReady.getTid(), paymentCaptor.getValue().getTid()),
                () -> assertEquals(SUCCESS, paymentCaptor.getValue().getPaymentStateType()),
                () -> assertEquals(nowTime, paymentCaptor.getValue().getPaidAt()),

                () -> assertEquals(goldGrade.getId(), memberCaptor.getValue().getGrade().getId()),
                () -> assertEquals(LocalDate.of(2024, 1, 1), memberCaptor.getValue().getExpiredEnabledAt()),
                () -> assertEquals(nowTime, memberCaptor.getValue().getLatestPaidAt()),

                () -> assertEquals(paymentReady.getPaymentUUID(), paymentReadyCaptor.getValue().getPaymentUUID())
            );

        }

        @Test
        @DisplayName("결제 승인 실패 - 결제 X")
        void failed_approvePayment_payment_not_found() {
            //given
            PaymentReady paymentReady = getPaymentReady();

            given(paymentReadyRepository.findById(anyString()))
                .willReturn(Optional.empty());

            //when
            LocalDateTime nowTime = LocalDateTime.of(2023, 12, 1, 9, 30, 30);

            PaymentException exception = assertThrows(PaymentException.class,
                () -> kakaoPayPaymentService.approvePayment(paymentReady.getPaymentUUID(), "pgToken", nowTime));

            //then
            assertEquals(PAYMENT_NOT_FOUND, exception.getPaymentErrorType());

        }

        @Test
        @DisplayName("결제 승인 실패 - 결제 실패 -> 카카오페이 서버에서 거절")
        void failed_approvePayment_payment_cannot_approve() {
            //given
            PaymentReady paymentReady = getPaymentReady();

            given(paymentReadyRepository.findById(anyString()))
                .willReturn(Optional.of(paymentReady));

            ReflectionTestUtils.setField(kakaoPayPaymentService, "APPROVE_URL", "TEST_URL");
            given(restTemplate.postForObject(anyString(), any(Object.class), eq(PaymentApprovedResponse.class)))
                .willThrow(new RestClientException("error"));

            //when
            LocalDateTime nowTime = LocalDateTime.of(2023, 12, 1, 9, 30, 30);

            PaymentException exception = assertThrows(PaymentException.class,
                () -> kakaoPayPaymentService.approvePayment(paymentReady.getPaymentUUID(), "pgToken", nowTime));

            //then
            assertEquals(PAYMENT_CANNOT_APPROVE, exception.getPaymentErrorType());
        }

        @Test
        @DisplayName("결제 승인 실패 - 존재하지 않는 등급")
        void failed_approvePayment_grade_not_found() {
            //given
            PaymentReady paymentReady = getPaymentReady();
            PaymentApprovedResponse paymentApprovedResponse = new PaymentApprovedResponse(
                "tid", new PaymentApprovedResponse.Amount(3000L));

            given(paymentReadyRepository.findById(anyString()))
                .willReturn(Optional.of(paymentReady));

            ReflectionTestUtils.setField(kakaoPayPaymentService, "APPROVE_URL", "TEST_URL");
            given(restTemplate.postForObject(anyString(), any(Object.class), eq(PaymentApprovedResponse.class)))
                .willReturn(paymentApprovedResponse);

            given(gradeRepository.findById(anyLong()))
                .willReturn(Optional.empty());

            //when
            LocalDateTime nowTime = LocalDateTime.of(2023, 12, 1, 9, 30, 30);

            GradeException exception = assertThrows(GradeException.class,
                () -> kakaoPayPaymentService.approvePayment(paymentReady.getPaymentUUID(), "pgToken", nowTime));

            //then
            assertEquals(GRADE_NOT_FOUND, exception.getGradeErrorType());
        }

        @Test
        @DisplayName("결제 승인 실패 - 회원 X")
        void failed_approvePayment_authentication_user_not_found() {
            //given
            PaymentReady paymentReady = getPaymentReady();
            PaymentApprovedResponse paymentApprovedResponse = new PaymentApprovedResponse(
                "tid", new PaymentApprovedResponse.Amount(3000L));
            Grade goldGrade = getGoldGrade();

            given(paymentReadyRepository.findById(anyString()))
                .willReturn(Optional.of(paymentReady));

            ReflectionTestUtils.setField(kakaoPayPaymentService, "APPROVE_URL", "TEST_URL");
            given(restTemplate.postForObject(anyString(), any(Object.class), eq(PaymentApprovedResponse.class)))
                .willReturn(paymentApprovedResponse);

            given(gradeRepository.findById(anyLong()))
                .willReturn(Optional.of(goldGrade));

            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.empty());

            //when
            LocalDateTime nowTime = LocalDateTime.of(2023, 12, 1, 9, 30, 30);

            AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> kakaoPayPaymentService.approvePayment(paymentReady.getPaymentUUID(), "pgToken", nowTime));

            //then
            assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
        }
    }
}