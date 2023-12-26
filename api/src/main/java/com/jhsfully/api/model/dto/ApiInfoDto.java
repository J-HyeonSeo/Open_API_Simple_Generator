package com.jhsfully.api.model.dto;

import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiInfoElastic;
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
  public static class ApiInfoSearchDto {
    private long id;
    private String apiName;
    private String ownerNickname;
    private String profileUrl;
    private ApiState apiState;
    private boolean accessible;
  }

  @Getter
  @AllArgsConstructor
  @Builder
  public static class ApiInfoDetailDto {
    private long id;
    private String apiName;
    private String apiIntroduce;
    private String ownerNickname;
    private ApiState apiState;
    private List<SchemaData> schemaStructure;
    private List<QueryData> queryParameter;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private LocalDateTime disabledAt;
    private boolean isPublic;
  }

  public static ApiInfoSearchDto of(ApiInfoElastic entity, boolean accessible) {
    return ApiInfoSearchDto.builder()
        .id(entity.getId())
        .apiName(entity.getApiName())
        .ownerNickname(entity.getOwnerNickname())
        .profileUrl(entity.getProfileUrl())
        .apiState(entity.getApiState())
        .accessible(accessible)
        .build();
  }

  public static ApiInfoDetailDto detailOf(ApiInfo entity){
    return ApiInfoDetailDto.builder()
        .id(entity.getId())
        .apiName(entity.getApiName())
        .apiIntroduce(entity.getApiIntroduce())
        .ownerNickname(entity.getMember().getNickname())
        .apiState(entity.getApiState())
        .schemaStructure(entity.getSchemaStructure())
        .queryParameter(entity.getQueryParameter())
        .registeredAt(entity.getRegisteredAt())
        .updatedAt(entity.getUpdatedAt())
        .isPublic(entity.isPublic())
        .build();
  }

}
