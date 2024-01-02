package com.jhsfully.api.model.dto;

import com.jhsfully.domain.entity.Payment;
import com.jhsfully.domain.type.PaymentStateType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PaymentDto {
  private long id;
  private long gradeId;
  private String grade;
  private long paymentAmount;
  private Long refundAmount;
  private LocalDateTime paidAt;
  private LocalDateTime refundAt;
  private PaymentStateType paymentState;

  public static PaymentDto of(Payment entity){
    return PaymentDto.builder()
        .id(entity.getId())
        .gradeId(entity.getGrade().getId())
        .grade(entity.getGrade().getGradeName())
        .paymentAmount(entity.getPaymentAmount())
        .refundAmount(entity.getRefundAmount())
        .paidAt(entity.getPaidAt())
        .refundAt(entity.getRefundAt())
        .paymentState(entity.getPaymentStateType())
        .build();
  }
}
