package com.jhsfully.domain.entity;

import com.jhsfully.domain.type.ApiState;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.core.join.JoinField;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "api_info")
@Mapping(mappingPath = "/elasticsearch/apiInfo-mapping.json")
@Setting(settingPath = "/elasticsearch/analyzer-setting.json")
public class ApiInfoElastic {
  @Id
  private Long id;
  private String apiName; //for search and response
  private String apiIntroduce; //for search and response
  private String ownerEmail; //for search and response
  @Enumerated(EnumType.STRING)
  private ApiState state;
  private boolean isPublic;
  private Long permissionId; //for deletion
  private Long ownerMemberId; //for join
  private Long accessMemberId; //for join
  private JoinField<Long> mapping; //create parent-child relation.
}
