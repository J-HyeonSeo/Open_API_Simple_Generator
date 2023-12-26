package com.jhsfully.api.model.dto;

import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.type.ApiState;
import com.jhsfully.domain.type.QueryData;
import com.jhsfully.domain.type.SchemaData;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


public class ApiInfoDto {

  @Getter
  @AllArgsConstructor
  @Builder
  public static class ApiInfoDetailResponse{
    private long id;
    private String apiName;
    private String apiIntroduce;
    private String ownerEmail;
    private ApiState apiState;
    private List<SchemaData> schemaStructure;
    private List<QueryData> queryParameter;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private LocalDateTime disabledAt;
    private boolean isPublic;
  }

  public static ApiInfoDetailResponse detailOf(ApiInfo entity){
    return ApiInfoDetailResponse.builder()
        .id(entity.getId())
        .apiName(entity.getApiName())
        .apiIntroduce(entity.getApiIntroduce())
        .ownerEmail(entity.getMember().getEmail())
        .apiState(entity.getApiState())
        .schemaStructure(entity.getSchemaStructure())
        .queryParameter(entity.getQueryParameter())
        .registeredAt(entity.getRegisteredAt())
        .updatedAt(entity.getUpdatedAt())
        .isPublic(entity.isPublic())
        .build();
  }

}
