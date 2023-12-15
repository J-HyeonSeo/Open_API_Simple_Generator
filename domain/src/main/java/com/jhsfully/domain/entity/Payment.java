package com.jhsfully.domain.entity;

import com.jhsfully.domain.type.PaymentStateType;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment")
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, unique = true)
  private String tid; //카카오페이 TransactionID
  @ManyToOne(fetch = FetchType.LAZY)
  private Grade grade;
  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;
  private long paymentAmount;
  private Long refundAmount;
  private LocalDateTime paidAt;
  private LocalDateTime refundAt;
  @Enumerated(EnumType.STRING)
  private PaymentStateType paymentStateType;
}
