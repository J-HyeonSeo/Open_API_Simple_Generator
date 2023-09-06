package com.jhsfully.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "PaymentReady", timeToLive = 60 * 10)// 10분
public class PaymentReady {

  @Id
  private String paymentUUID;
  private String tid;
  private long gradeId;
  private long memberId;
  private long paidAmount;
}
