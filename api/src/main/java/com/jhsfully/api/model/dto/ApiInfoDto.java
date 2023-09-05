package com.jhsfully.api.model.dto;

import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.type.ApiQueryType;
import com.jhsfully.domain.type.ApiState;
import com.jhsfully.domain.type.ApiStructureType;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class ApiInfoDto {

//  @Getter
//  @NoArgsConstructor
//  @AllArgsConstructor
//  @Builder
//  public static class ApiInfoTopResponse{
//    private long id;
//    private String apiName;
//    private String apiIntroduce;
//    private String ownerEmail;
//    private List<ApiPermissionType> permissions;
//    private ApiState apiState;
//    private boolean isPublic;
//  }
//
//  public static ApiInfoTopResponse topOf(ApiInfo entity, ApiUserPermission permission){
//    return ApiInfoTopResponse.builder()
//        .id(entity.getId())
//        .apiName(entity.getApiName())
//        .apiIntroduce(entity.getApiIntroduce())
//        .ownerEmail(entity.getMember().getEmail())
//        .permissions(permission.getApiPermissionDetails().stream().map( x -> x.getType()).collect(
//            Collectors.toList()))
//        .apiState(entity.getApiState())
//        .isPublic(entity.isPublic())
//        .build();
//  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ApiInfoDetailResponse{
    private long id;
    private String apiName;
    private String apiIntroduce;
    private String ownerEmail;
    private ApiState apiState;
    private Map<String, ApiStructureType> schemaStructure;
    private Map<String, ApiQueryType> queryParameter;
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
