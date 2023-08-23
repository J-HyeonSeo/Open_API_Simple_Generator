package com.jhsfully.domain.entity;

import com.jhsfully.domain.type.ApiState;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "api_info")
public class ApiInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String apiName;
  private String apiIntroduce;
  @ManyToOne
  private Member member;
  @Enumerated(EnumType.STRING)
  private ApiState apiState;
  private String dataCollectionName;
  private String historyCollectionName;
  private int recordCount;
  private int dataMaxLength;
  private boolean isPublic;
  private String schemaStructure;
  private String queryParameter;
  @CreatedDate
  private LocalDateTime registeredAt;
  @LastModifiedDate
  private LocalDateTime updatedAt;
  private LocalDateTime disabledAt;
}
