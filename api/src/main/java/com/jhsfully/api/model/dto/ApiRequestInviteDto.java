package com.jhsfully.api.model.dto;

import com.jhsfully.domain.entity.ApiRequestInvite;
import com.jhsfully.domain.type.ApiRequestStateType;
import java.time.LocalDateTime;
import java.util.Objects;
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

  public static ApiRequestInviteDto of(ApiRequestInvite entity, boolean isShowOwner){
    return ApiRequestInviteDto.builder()
        .id(entity.getId())
        .apiInfoId(Objects.isNull(entity.getApiInfo()) ? null : entity.getApiInfo().getId())
        .memberId(isShowOwner ? entity.getApiInfo().getMember().getId() : entity.getMember().getId())
        .apiName(Objects.isNull(entity.getApiInfo()) ? null : entity.getApiInfo().getApiName())
        .memberEmail(isShowOwner ? entity.getApiInfo().getMember().getEmail() : entity.getMember().getEmail())
        .registeredAt(entity.getRegisteredAt())
        .requestStateType(entity.getRequestStateType())
        .build();
  }
}
