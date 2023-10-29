package com.jhsfully.api.service;

import com.jhsfully.api.model.payment.PaymentReadyResponseForClient;
import com.jhsfully.api.model.payment.PaymentResponse;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

  PaymentResponse getPaymentList(long memberId, Pageable pageable);

  PaymentReadyResponseForClient payment(long memberId, long gradeId);

  void refund(long memberId, long paymentId);

  void approvePayment(String paymentUUID, String pgToken);
}
