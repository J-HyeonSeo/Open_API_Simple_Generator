package com.jhsfully.api.service;

import com.jhsfully.api.model.permission.AuthKeyResponse;
import com.jhsfully.api.model.permission.PermissionDto;
import com.jhsfully.api.model.permission.PermissionResponse;
import com.jhsfully.domain.type.ApiPermissionType;

public interface ApiPermissionService {
  AuthKeyResponse getAuthKey(long memberId, long apiId);

  AuthKeyResponse createAuthKey(long memberId, long apiId);

  AuthKeyResponse refreshAuthKey(long memberId, long apiId);

  PermissionDto getPermissionForMember(long apiId, long memberId);

  PermissionResponse getPermissionListForOwner(long apiId, long memberId, int pageSize, int pageIdx);

  void addPermission(long permissionId, long memberId, ApiPermissionType type);

  void subPermission(long permissionDetailId, long memberId);

  void deletePermission(long permissionId, long memberId);
}
