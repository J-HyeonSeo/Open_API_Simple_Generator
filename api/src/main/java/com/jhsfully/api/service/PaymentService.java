package com.jhsfully.api.service;

import com.jhsfully.api.model.payment.PaymentResponse;
import com.jhsfully.api.model.payment.RedirectUrlResponse;

public interface PaymentService {

  PaymentResponse getPaymentList(long memberId, int pageSize, int pageIdx);

  RedirectUrlResponse payment(long memberId, long gradeId);

  void refund(long memberId, long paymentId);

  void approvePayment(String paymentUUID, String pgToken);
}
