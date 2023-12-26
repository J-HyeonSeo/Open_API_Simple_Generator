package com.jhsfully.api.service;

import com.jhsfully.api.model.PageResponse;
import com.jhsfully.api.model.dto.PermissionDto;
import com.jhsfully.api.model.permission.AuthKeyResponse;
import com.jhsfully.domain.type.ApiPermissionType;
import org.springframework.data.domain.Pageable;

public interface ApiPermissionService {
  AuthKeyResponse getAuthKey(long memberId, long apiId);

  AuthKeyResponse createAuthKey(long memberId, long apiId);

  AuthKeyResponse refreshAuthKey(long memberId, long apiId);

  PermissionDto getPermissionForMember(long apiId, long memberId);

  PageResponse<PermissionDto> getPermissionListForOwner(long apiId, long memberId, Pageable pageable);

  void addPermission(long permissionId, long memberId, ApiPermissionType type);

  void subPermission(long permissionDetailId, long memberId);

  void deletePermission(long permissionId, long memberId);
}
