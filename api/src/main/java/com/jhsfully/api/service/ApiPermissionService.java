package com.jhsfully.api.service;

import com.jhsfully.api.model.permission.AuthKeyResponse;

public interface ApiPermissionService {
  AuthKeyResponse getAuthKey(long memberId, long apiId);

  AuthKeyResponse createAuthKey(long memberId, long apiId);

  AuthKeyResponse refreshAuthKey(long memberId, long apiId);
}
