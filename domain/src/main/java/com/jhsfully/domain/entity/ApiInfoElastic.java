package com.jhsfully.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "apiinfo")
@Mapping(mappingPath = "/elasticsearch/apiInfo-mapping.json")
@Setting(settingPath = "/elasticsearch/analyzer-setting.json")
public class ApiInfoElastic {
  @Id
  private Long id;
  private String apiName;
  private String apiIntroduce;
  private String memberEmail;
  private boolean isPublic;
}
