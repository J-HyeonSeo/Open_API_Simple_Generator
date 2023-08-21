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
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  public static final String ACCESS_TOKEN_HEADER = "AccessToken";
  public static final String REFRESH_TOKEN_HEADER = "RefreshToken";
  private final TokenProvider tokenProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    if (isSkip(request.getRequestURI())) {
      filterChain.doFilter(request, response);
      return;
    }

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
        request.getSession().setAttribute("AccessToken", "Bearer " + accessToken);
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

  private boolean isSkip(String requestURI) {
    log.info(requestURI);
    if (requestURI.startsWith("/login")) {
      return true;
    }

    //for Develop
    if (requestURI.startsWith("/h2-console")) {
      return true;
    }
    if (requestURI.startsWith("/swagger")) {
      return true;
    }
    if (requestURI.startsWith("/v2")) {
      return true;
    }
    return false;
  }
}
