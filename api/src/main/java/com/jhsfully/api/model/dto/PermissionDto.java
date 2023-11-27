package com.jhsfully.api.model.dto;

import com.jhsfully.domain.entity.ApiPermissionDetail;
import com.jhsfully.domain.entity.ApiUserPermission;
import com.jhsfully.domain.type.ApiPermissionType;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PermissionDto {
  private long permissionId;
  private String memberEmail;
  private List<ApiPermissionType> permissionList;

  public static PermissionDto of(ApiUserPermission entity){
    return new PermissionDto(
        entity.getId(),
        entity.getMember().getEmail(),
        entity.getApiPermissionDetails()
            .stream()
            .map(ApiPermissionDetail::getType)
            .collect(Collectors.toList())
    );
  }
}
