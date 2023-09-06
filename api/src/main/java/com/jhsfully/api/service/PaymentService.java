package com.jhsfully.api.service;

import com.jhsfully.api.model.payment.PaymentReadyResponseForClient;
import com.jhsfully.api.model.payment.PaymentResponse;

public interface PaymentService {

  PaymentResponse getPaymentList(long memberId, int pageSize, int pageIdx);

  PaymentReadyResponseForClient payment(long memberId, long gradeId);

  void refund(long memberId, long paymentId);

  void approvePayment(String paymentUUID, String pgToken);
}
