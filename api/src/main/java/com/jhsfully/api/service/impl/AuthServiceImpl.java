package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_UNAUTHORIZED;

import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.model.auth.TokenResponse;
import com.jhsfully.api.security.TokenProvider;
import com.jhsfully.api.service.AuthService;
import com.jhsfully.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final TokenProvider tokenProvider;

  @Override
  public void logout(String refreshTokenString) {
    refreshTokenRepository.findById(refreshTokenString).ifPresent(
        refreshTokenRepository::delete
    );
  }

  @Override
  public TokenResponse generateAccessToken(String refreshToken) {

    if (StringUtils.hasText(refreshToken) && tokenProvider.validateToken(refreshToken)) {
      String accessToken = tokenProvider.generateAccessTokenByRefresh(refreshToken);
      Authentication authentication = tokenProvider.getAuthentication(accessToken);
      SecurityContextHolder.getContext().setAuthentication(authentication);

      return new TokenResponse(accessToken, null);
    }

    throw new AuthenticationException(AUTHENTICATION_UNAUTHORIZED);
  }
}
