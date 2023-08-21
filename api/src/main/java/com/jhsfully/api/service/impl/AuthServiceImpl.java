package com.jhsfully.api.service.impl;

import com.jhsfully.api.service.AuthService;
import com.jhsfully.domain.entity.RefreshToken;
import com.jhsfully.domain.repository.RefreshTokenRepository;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final HttpServletResponse response;


  @Override
  public void logout(String refreshTokenString) {

    Optional<RefreshToken> refreshTokenEntity = refreshTokenRepository.findById(refreshTokenString);

    if(refreshTokenEntity.isPresent()){
      refreshTokenRepository.delete(refreshTokenEntity.get());
    }

    //cookie release
    Cookie accessCookie = new Cookie("AccessToken", null);
    Cookie refreshCookie = new Cookie("RefreshToken", null);
    accessCookie.setMaxAge(0);
    accessCookie.setPath("/");
    refreshCookie.setMaxAge(0);
    refreshCookie.setPath("/");
    refreshCookie.setHttpOnly(true);

    response.addCookie(accessCookie);
    response.addCookie(refreshCookie);
  }
}
