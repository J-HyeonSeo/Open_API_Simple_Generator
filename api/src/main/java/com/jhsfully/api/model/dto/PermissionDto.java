package com.jhsfully.api.model.dto;

import com.jhsfully.domain.entity.ApiUserPermission;
import com.jhsfully.domain.type.ApiPermissionType;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PermissionDto {
  private long permissionId;
  private String memberNickname;
  private String profileUrl;
  private List<PermissionDetailDto> permissionList;

  @Getter
  @AllArgsConstructor
  public static class PermissionDetailDto {
    private long id;
    private ApiPermissionType type;
  }

  public static PermissionDto of(ApiUserPermission entity){

    return PermissionDto.builder()
        .permissionId(entity.getId())
        .memberNickname(entity.getMember().getNickname())
        .profileUrl(entity.getMember().getProfileUrl())
        .permissionList(entity.getApiPermissionDetails()
            .stream()
            .map(x -> new PermissionDetailDto(x.getId(), x.getType()))
            .collect(Collectors.toList()))
        .build();
  }
}
