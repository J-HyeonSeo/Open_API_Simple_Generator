package com.jhsfully.domain.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member")
public class Member {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  private Grade grade;
  @Column(nullable = false, unique = true)
  private String email;
  private String nickname;
  @CreatedDate
  private LocalDateTime registeredAt;
  @LastModifiedDate
  private LocalDateTime updatedAt;
  private boolean isAdmin;
  private LocalDateTime latestPaidAt;
  private int refundCount;
  private boolean gradeChanged;
  private LocalDate expiredEnabledAt;
//  private int remainEnableDays;
}
