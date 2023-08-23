package com.jhsfully.api.security;

import com.jhsfully.api.service.oauthimpl.KakaoOauth2MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration{

  private final KakaoOauth2MemberService kakaoOauth2MemberService;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  public SecurityConfiguration(KakaoOauth2MemberService kakaoOauth2MemberService, TokenProvider tokenProvider){
    this.kakaoOauth2MemberService = kakaoOauth2MemberService;
    this.jwtAuthenticationFilter = new JwtAuthenticationFilter(tokenProvider);
  }

  /*
      기존의 WebSecurityConfigurerAdapter가 Deprecated되었음.
      아래와 같은 방식으로 작성하는 것이 권장방식이 됨.

      카카오 로그인은 -> http:localhost:8080/oauth2/authorization/kakao 로 접속하면,
      oauth2-client가 알아서, provider에 정의된 인증 주소에, 쿼리 파라미터를 붙여서 요청을 보내줌.
      카카오에서 인증이 완료되면, redirect-url로 이동하면서, kakaoOauth2MemberService의 loadUser를 호출함.
      kakaoOauth2MemberService는 email, nickname을 추출하여, 이를 DB에 업데이트 하고,
      토큰을 생성하고 이를 세션에 저장함.
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

    http
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .authorizeRequests().anyRequest().permitAll()
        .and()
        .logout()
        .logoutSuccessUrl("/auth/logout")
        .and()
        .oauth2Login()
        .userInfoEndpoint()
        .userService(kakaoOauth2MemberService);

    return http.build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer(){
    return web -> {
      web.ignoring()
          .antMatchers(
              "/images/**",
              "/js/**",
              "/css/**",
              "/swagger-ui/**",
              "/swagger-resources/**",
              "/v2/**",
              "/v3/**"
              );
    };
  }

}
