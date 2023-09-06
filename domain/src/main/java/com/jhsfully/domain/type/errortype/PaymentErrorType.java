package com.jhsfully.domain.type.errortype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentErrorType {
  PAYMENT_NOT_FOUND("존재하지 않은 결제입니다."),
  PAYMENT_REQUEST_IS_WRONG("결제 요청 중 에러가 발생하였습니다. 고객센터로 연락바랍니다."),
  PAYMENT_CANNOT_APPROVE("승인 할 수 없는 결제입니다."),
  PAYMENT_CANNOT_REFUND("환불에 실패하였습니다, 고객센터로 연락바랍니다."),
  CANCEL_AMOUNT_IS_WRONG("취소금액오류로 환불에 실패하였습니다. 고객센터로 연락바랍니다."),
  REMAIN_ENABLE_DAYS_MORE_THAN_ONE("API 활성 일수가 1일을 넘습니다."),
  REFUND_COUNT_MORE_THAN_ONE("당월 환불 횟수가 2회 이상일 경우 결제가 불가능합니다."),
  YOU_ARE_NOT_PAYMENT_OWNER("해당 결제를 수행한 유저가 아닙니다."),
  PAYMENT_IS_ALREADY_REFUNDED("이미 환불된 결제입니다."),
  REFUND_DEADLINE_IS_ONE_WEEK("환불 가능일은 구매일로부터 일주일 이내입니다."),
  CANNOT_BUY_THIS_GRADE("구매가 불가능한 등급입니다."),
  ;
  private final String message;
}
