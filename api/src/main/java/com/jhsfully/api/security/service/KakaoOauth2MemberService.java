package com.jhsfully.api.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhsfully.api.exception.GradeException;
import com.jhsfully.api.model.auth.TokenResponse;
import com.jhsfully.api.security.TokenProvider;
import com.jhsfully.domain.entity.Grade;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.GradeRepository;
import com.jhsfully.domain.repository.MemberRepository;
import com.jhsfully.domain.type.errortype.GradeErrorType;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
  private final GradeRepository gradeRepository;
  private final TokenProvider tokenProvider;
  private final ObjectMapper objectMapper;
  private final HttpServletResponse httpServletResponse;
  private static final long BRONZE_GRADE_ID = 1L;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
        .getUserInfoEndpoint().getUserNameAttributeName();
    OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest); // Oauth2 정보를 가져옴

    Map<String, Object> accountInfo = oAuth2User.getAttribute("kakao_account");

    String email = (String) Objects.requireNonNull(accountInfo).get("email");
    String nickname = (String) ((Map<?, ?>) accountInfo.get("profile")).get("nickname");
    String profileUrl = (String) ((Map<?, ?>) accountInfo.get("profile")).get("profile_image_url");

    log.info(profileUrl);
    log.info(email);
    log.info(nickname);

    //DB에 데이터 저장 및 업데이트
    Member member = saveOrUpdate(email, nickname, profileUrl);

    //JWT(AccessToken, RefreshToken) 발급해야함.
    String accessToken = tokenProvider.generateAccessToken(member.getId());
    String refreshToken = tokenProvider.generateRefreshToken(member.getId(), email);
    TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);

    httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
    try(PrintWriter writer = httpServletResponse.getWriter()) {
      writer.print(objectMapper.writeValueAsString(tokenResponse));
    } catch (Exception e) {
      log.info("Token 응답 쓰기 오류 발생!");
    }

    //로그인이 수행되면, JWT로 책임을 위임하므로, 최소한의 내용만 작성해서 return함.
    return new DefaultOAuth2User(null,
        oAuth2User.getAttributes(),
        userNameAttributeName
    );

  }

  private Member saveOrUpdate(String email, String nickname, String profileUrl) {

    Optional<Member> member = memberRepository.findByEmail(email);

    if(member.isPresent()){
      Member updateMember = member.get();
      log.info(email + "님이 카카오계정으로 로그인 하였습니다.");
      updateMember.setNickname(nickname);
      updateMember.setProfileUrl(profileUrl);
      return memberRepository.save(updateMember);
    }

    //브론즈 등급 가져오기.
    Grade bronzeGrade = gradeRepository.findById(BRONZE_GRADE_ID)
            .orElseThrow(() -> new GradeException(GradeErrorType.GRADE_NOT_FOUND));

    log.info(email + "님이 카카오계정으로 회원가입을 하였습니다.");
    return memberRepository.save(
        Member.builder()
            .email(email)
            .nickname(nickname)
            .profileUrl(profileUrl)
            .grade(bronzeGrade)
            .build()
    );
  }

}
