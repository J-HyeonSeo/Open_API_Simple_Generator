package com.jhsfully.api.service;

import com.jhsfully.api.model.PageResponse;
import com.jhsfully.api.model.dto.PaymentDto;
import com.jhsfully.api.model.payment.PaymentReadyResponseForClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

  PageResponse<PaymentDto> getPaymentList(long memberId, Pageable pageable);

  PaymentReadyResponseForClient paymentRequest(long memberId, long gradeId, LocalDate nowDate);

  void refund(long memberId, long paymentId, LocalDateTime nowTime);

  void approvePayment(String paymentUUID, String pgToken, LocalDateTime nowTime);
}
