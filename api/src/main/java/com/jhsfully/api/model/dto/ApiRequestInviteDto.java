package com.jhsfully.api.model.dto;

import com.jhsfully.domain.entity.ApiRequestInvite;
import com.jhsfully.domain.type.ApiRequestStateType;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ApiRequestInviteDto {
  private Long id;
  private Long apiInfoId;
  private String memberNickname;
  private String profileUrl;
  private String apiName;
  private LocalDateTime registeredAt;
  private ApiRequestStateType requestStateType;

  public static ApiRequestInviteDto of(ApiRequestInvite entity, boolean isShowOwner){
    return ApiRequestInviteDto.builder()
        .id(entity.getId())
        .apiInfoId(Objects.isNull(entity.getApiInfo()) ? null : entity.getApiInfo().getId())
        .memberNickname(isShowOwner ? entity.getApiInfo().getMember().getNickname() : entity.getMember().getNickname())
        .profileUrl(isShowOwner ? entity.getApiInfo().getMember().getProfileUrl() : entity.getMember().getProfileUrl())
        .apiName(Objects.isNull(entity.getApiInfo()) ? null : entity.getApiInfo().getApiName())
        .registeredAt(entity.getRegisteredAt())
        .requestStateType(entity.getRequestStateType())
        .build();
  }
}
