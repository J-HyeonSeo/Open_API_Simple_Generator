package com.jhsfully.domain.entity;

import com.jhsfully.domain.converter.JsonConverter;
import com.jhsfully.domain.type.ApiQueryType;
import com.jhsfully.domain.type.ApiState;
import com.jhsfully.domain.type.ApiStructureType;
import java.time.LocalDateTime;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Where(clause = "apiState != 'DELETED'")
@SQLDelete(sql = "UPDATE api_info SET apiState = 'DELETED' WHERE id = ?")
@Table(name = "api_info")
public class ApiInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String apiName;
  private String apiIntroduce;
  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;
  @Enumerated(EnumType.STRING)
  private ApiState apiState;
  private String dataCollectionName;
  private String historyCollectionName;
  private int recordCount;
  private int dataMaxLength;
  private boolean isPublic;

  @Convert(converter = JsonConverter.class)
  @Column(columnDefinition = "json")
  private Map<String, ApiStructureType> schemaStructure;
  @Convert(converter = JsonConverter.class)
  @Column(columnDefinition = "json")
  private Map<String, ApiQueryType> queryParameter;
  @CreatedDate
  private LocalDateTime registeredAt;
  @LastModifiedDate
  private LocalDateTime updatedAt;
  private LocalDateTime disabledAt;
}
