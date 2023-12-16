package com.jhsfully.api.service;

import com.jhsfully.api.model.payment.PaymentReadyResponseForClient;
import com.jhsfully.api.model.payment.PaymentResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

  PaymentResponse getPaymentList(long memberId, Pageable pageable);

  PaymentReadyResponseForClient paymentRequest(long memberId, long gradeId, LocalDate nowDate);

  void refund(long memberId, long paymentId, LocalDateTime nowTime);

  void approvePayment(String paymentUUID, String pgToken, LocalDateTime nowTime);
}
