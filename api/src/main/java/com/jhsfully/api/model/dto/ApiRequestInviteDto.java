package com.jhsfully.api.model.dto;

import com.jhsfully.domain.entity.ApiRequestInvite;
import com.jhsfully.domain.type.ApiRequestStateType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiRequestInviteDto {
  private Long id;
  private Long apiInfoId;
  private Long memberId;
  private String apiName;
  private String memberEmail;
  private LocalDateTime registeredAt;
  private ApiRequestStateType requestStateType;

  public static ApiRequestInviteDto of(ApiRequestInvite entity){
    return ApiRequestInviteDto.builder()
        .id(entity.getId())
        .apiInfoId(entity.getId())
        .memberId(entity.getId())
        .apiName(entity.getApiInfo().getApiName())
        .memberEmail(entity.getMember().getEmail())
        .registeredAt(entity.getRegisteredAt())
        .requestStateType(entity.getRequestStateType())
        .build();
  }
}
