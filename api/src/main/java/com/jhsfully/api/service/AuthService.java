package com.jhsfully.api.service;

import com.jhsfully.api.model.auth.TokenResponse;

public interface AuthService {
  void logout(String refreshTokenString);
  TokenResponse generateAccessToken(String refreshToken);
}
