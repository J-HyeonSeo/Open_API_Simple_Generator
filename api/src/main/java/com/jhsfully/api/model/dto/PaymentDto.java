package com.jhsfully.api.model.dto;

import com.jhsfully.domain.entity.Payment;
import com.jhsfully.domain.type.PaymentStateType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
  private long id;
  private String grade;
  private long paymentAmount;
  private Long refundAmount;
  private LocalDateTime paidAt;
  private LocalDateTime refundAt;
  private PaymentStateType paymentState;

  public static PaymentDto of(Payment entity){
    return PaymentDto.builder()
        .id(entity.getId())
        .grade(entity.getGrade().getGradeName())
        .paymentAmount(entity.getPaymentAmount())
        .refundAmount(entity.getRefundAmount())
        .paidAt(entity.getPaidAt())
        .refundAt(entity.getRefundAt())
        .paymentState(entity.getPaymentStateType())
        .build();
  }
}