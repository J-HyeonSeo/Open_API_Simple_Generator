package com.jhsfully.api.restcontroller;

import com.jhsfully.api.model.auth.TokenInput;
import com.jhsfully.api.model.auth.TokenResponse;
import com.jhsfully.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  /*
      쿠키에서, accessToken과 refreshToken을 추출해서,
      응답으로 내려줌.
      CSRF공격에 cookie에서 값을 가져오는 대신에,
      Header에 실어서 보내는 방식으로 해야함.
      Front단에서 직접 추출해도 좋지만,
      백엔드 단에서, 쿠키에서 값을 추출하고, 값을 빈 값으로 셋팅하도록 함.
   */
  @GetMapping
  public ResponseEntity<TokenResponse> getTokens(
      @CookieValue(name = "AccessToken", required = true) String accessTokenString,
      @CookieValue(name = "RefreshToken", required = true) String refreshTokenString){

    authService.deleteToken();

    return ResponseEntity.ok(
        new TokenResponse(accessTokenString, refreshTokenString)
    );
  }

  @PostMapping
  ResponseEntity<TokenResponse> refresh(@RequestBody TokenInput token){
    return ResponseEntity.ok(
        authService.generateAccessToken(token.getRefreshToken())
    );
  }

}
