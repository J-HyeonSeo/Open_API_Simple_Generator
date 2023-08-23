package com.jhsfully.api.security;

import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.domain.type.AuthenticationErrorType;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  public static final String ACCESS_TOKEN_HEADER = "AccessToken";
  public static final String REFRESH_TOKEN_HEADER = "RefreshToken";
  private final int ACCESS_TOKEN_MAX_AGE = 24 * 60 * 60;
  private final TokenProvider tokenProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String accessToken = resolveTokenFromRequest(request, ACCESS_TOKEN_HEADER);
    String refreshToken = resolveTokenFromRequest(request, REFRESH_TOKEN_HEADER);

    try {
      if (StringUtils.hasText(accessToken) && tokenProvider.validateToken(accessToken)) {

        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

      } else if (StringUtils.hasText(refreshToken) && tokenProvider.validateToken(refreshToken)) {
        accessToken = tokenProvider.generateAccessTokenByRefresh(refreshToken);
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Cookie accessCookie = new Cookie("AccessToken", accessToken);
        accessCookie.setMaxAge(ACCESS_TOKEN_MAX_AGE);
        accessCookie.setPath("/");
        response.addCookie(accessCookie);
      }else{
        throw new AuthenticationException(AuthenticationErrorType.AUTHENTICATION_UNAUTHORIZED);
      }
    } catch (Exception e) {
      //로그인이 필요한 경우임.
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    filterChain.doFilter(request, response);
  }

  private String resolveTokenFromRequest(HttpServletRequest request, String tokenHeader) {
//    String token = request.getHeader(tokenHeader);
    String token = null;

    Cookie[] cookies = request.getCookies();

    if(cookies == null){
      return null;
    }

    for(Cookie cookie : request.getCookies()){
      if(tokenHeader.equals(cookie.getName())){
        token = cookie.getValue();
        break;
      }
    }

    if (!ObjectUtils.isEmpty(token)) {
      return token;
    }
    return null;
  }

}
