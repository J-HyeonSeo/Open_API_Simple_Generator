package com.jhsfully.api.exception;

import com.jhsfully.domain.type.errortype.PaymentErrorType;
import lombok.Getter;

@Getter
public class PaymentException extends CustomException{
  private final PaymentErrorType paymentErrorType;

  public PaymentException(PaymentErrorType paymentErrorType){
    super(paymentErrorType.getMessage());
    this.paymentErrorType = paymentErrorType;
  }
}
