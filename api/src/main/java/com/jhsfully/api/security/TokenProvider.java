package com.jhsfully.api.security;

import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.domain.entity.redis.RefreshToken;
import com.jhsfully.domain.repository.RefreshTokenRepository;
import com.jhsfully.domain.type.RoleType;
import com.jhsfully.domain.type.errortype.AuthenticationErrorType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class TokenProvider {

  //JWT는 AccessToken과 RefreshToken으로 구별할거임.
  //AccessToekn의 생명주기는 30분, RefreshToken의 생명주기는 2주로 잡음.

  @Value("${spring.jwt.secret}")
  private String secretKey;
  private static final String MEMBER_ID = "memberId";
  private static final String ROLES = "roles";
  private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;//1초 -> 1분 -> 30분
  private static final long REFRESH_TOKEN_EXPIRE_TIME =
      1000 * 60 * 60 * 24 * 14;//1초 -> 1분 -> 1시간 -> 1일 -> 2주

  private final RefreshTokenRepository refreshTokenRepository;


  //Access 토큰 생성
  public String generateAccessToken(Long memberId) {
    Claims claims = Jwts.claims();
    claims.put(MEMBER_ID, memberId);
    claims.put(ROLES, List.of(RoleType.ROLE_USER.name()));

    Date now = new Date();
    Date expiredDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(expiredDate)
        .signWith(SignatureAlgorithm.HS512, secretKey)
        .compact();
  }

  //Refresh토큰으로 AccessToken생성
  public String generateAccessTokenByRefresh(String refreshToken) {

    //redis에 refreshToken이 저장되어있는지 확인하고, 가져옴.
    RefreshToken refreshTokenEntity = refreshTokenRepository.findById(refreshToken)
        .orElseThrow(
            () -> new AuthenticationException(AuthenticationErrorType.AUTHENTICATION_UNAUTHORIZED));

    //memberId 가져오기
    Long memberId = getMemberId(refreshToken);

    return generateAccessToken(memberId);
  }

  //Refresh 토큰 생성
  public String generateRefreshToken(Long memberId, String email) {
    Claims claims = Jwts.claims();
    claims.put(MEMBER_ID, memberId);

    Date now = new Date();
    Date expiredDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

    String refreshToken = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(expiredDate)
        .signWith(SignatureAlgorithm.HS512, secretKey)
        .compact();

    //Redis에 refreshToken을 저장함.
    RefreshToken refreshTokenEntity = new RefreshToken(refreshToken, email);
    refreshTokenRepository.save(refreshTokenEntity);

    return refreshToken;
  }

  //토큰을 통해, 인증 객체 생성.
  public Authentication getAuthentication(String token) {

    Long memberId = getMemberId(token);
    List<String> roles = getRoles(token);

    List<SimpleGrantedAuthority> grantedAuthorities = roles.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    return new UsernamePasswordAuthenticationToken(memberId, "", grantedAuthorities);
  }

  //토큰으로부터 권한 가져오기.
  private List<String> getRoles(String token) {
    Claims claims = parseClaims(token);

    List<?> roles = claims.get(ROLES, List.class);

    return roles.stream().map(String::valueOf)
        .collect(Collectors.toList());
  }

  //회원 번호 가져오기.
  private Long getMemberId(String token) {
    return this.parseClaims(token).get(MEMBER_ID, Long.class);
  }

  //토큰 유효기간 검증.
  public boolean validateToken(String token) {
    if (!StringUtils.hasText(token)) {
      return false;
    }

    try{
      Claims claims = parseClaims(token);
      return !claims.getExpiration().before(new Date());
    } catch (MalformedJwtException e) {
      return false;
    }
  }

  //토큰 파싱
  private Claims parseClaims(String token) {
    try {
      return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }

}
