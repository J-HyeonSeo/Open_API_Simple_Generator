package com.jhsfully.api.service.oauthimpl;

import com.jhsfully.api.security.TokenProvider;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.MemberRepository;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class KakaoOauth2MemberService extends DefaultOAuth2UserService {

  private final DefaultOAuth2UserService defaultOAuth2UserService;
  private final MemberRepository memberRepository;
  private final TokenProvider tokenProvider;
  private final HttpServletResponse httpServletResponse;

  private final int ACCESS_TOKEN_MAX_AGE = 24 * 60 * 60;
  private final int REFRESH_TOKEN_MAX_AGE = 14 * 24 * 60 * 60;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
        .getUserInfoEndpoint().getUserNameAttributeName();
    OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest); // Oauth2 정보를 가져옴

    Map<String, Object> accountInfo = oAuth2User.getAttribute("kakao_account");

    String email = (String) accountInfo.get("email");
    String nickname = (String) ((Map) accountInfo.get("profile")).get("nickname");

    log.info(email);
    log.info(nickname);

    //DB에 데이터 저장 및 업데이트
    Member member = saveOrUpdate(email, nickname);

    //JWT(AccessToken, RefreshToken) 발급해야함.
    String accessToken = tokenProvider.generateAccessToken(member.getId(), member.isAdmin());
    String refreshToken = tokenProvider.generateRefreshToken(member.getId(), email,
        member.isAdmin());

    //쿠키에, 토큰 저장
    Cookie accessCookie = new Cookie("AccessToken", accessToken);
    accessCookie.setMaxAge(ACCESS_TOKEN_MAX_AGE);
    accessCookie.setPath("/");

    Cookie refreshCookie = new Cookie("RefreshToken", refreshToken);
    refreshCookie.setHttpOnly(true);
    refreshCookie.setMaxAge(REFRESH_TOKEN_MAX_AGE);
    refreshCookie.setPath("/");

    httpServletResponse.addCookie(accessCookie);
    httpServletResponse.addCookie(refreshCookie);

    //로그인이 수행되면, JWT로 책임을 위임하므로, 최소한의 내용만 작성해서 return함.
    return new DefaultOAuth2User(null,
        oAuth2User.getAttributes(),
        userNameAttributeName
    );

  }

  private Member saveOrUpdate(String email, String nickname) {

    Optional<Member> member = memberRepository.findByEmail(email);

    if(member.isPresent()){
      Member updateMember = member.get();
      log.info(email + "님이 카카오계정으로 로그인 하였습니다.");
      updateMember.setNickname(nickname);
      return memberRepository.save(updateMember);
    }

    log.info(email + "님이 카카오계정으로 회원가입을 하였습니다.");
    return memberRepository.save(
        Member.builder()
            .email(email)
            .nickname(nickname)
            .isAdmin(false)
            .build()
    );
  }

}
