package com.jhsfully.api.security;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
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

  private final TokenProvider tokenProvider;
  public static final String ACCESS_TOKEN_HEADER = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";
  /*
      PUBLIC_URL_LIST는 로그인을 하지 않은 유저 및 로그인을 수행한 유저 모두 접근 가능한 API입니다.
      로그인을 하지 않은 유저와, 로그인을 수행한 유저에 따라 응답하는 결괏값이 다른 경우에 해당됩니다.
      web.ignoring()에 등록된 시큐리티 제외 URL은 로그인 여부와 관계없이 제외되어야 하는 것이므로,
      로그인 여부에 따라 응답이 달라지는 해당 URL 목록과 관련이 없습니다.
   */
  public static final String[] PUBLIC_URL_LIST = {"/api/public"};

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String accessToken = resolveTokenFromRequest(request, ACCESS_TOKEN_HEADER);

    if (StringUtils.hasText(accessToken) && tokenProvider.validateToken(accessToken)) {

      Authentication authentication = tokenProvider.getAuthentication(accessToken);
      SecurityContextHolder.getContext().setAuthentication(authentication);

    } else if (!request.getMethod().equals("GET") //공개 URL 여부인지 확인.
        || Arrays.stream(PUBLIC_URL_LIST).noneMatch((url) -> request.getRequestURI().startsWith(url))) {
      //로그인 하거나, refresh를 통해 accessToken을 재발급 받아야 함.
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    filterChain.doFilter(request, response);
  }

  private String resolveTokenFromRequest(HttpServletRequest request, String tokenHeader) {
    String token = request.getHeader(tokenHeader);

    if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
      return token.substring(TOKEN_PREFIX.length());
    }
    return null;
  }

}
