package com.jhsfully.api.restcontroller;

import com.jhsfully.api.model.auth.TokenInput;
import com.jhsfully.api.model.auth.TokenResponse;
import com.jhsfully.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  @PostMapping
  public ResponseEntity<TokenResponse> refresh(@RequestBody TokenInput token){
    return ResponseEntity.ok(
        authService.generateAccessToken(token.getRefreshToken())
    );
  }

  /**
   *  Redis 저장되어 있는 RefreshToken을 삭제하기 때문에
   *  DeleteMapping 으로 지정하였음.
   */
  @DeleteMapping("/signout")
  public ResponseEntity<?> logout(@RequestBody TokenInput tokenInput) {
    authService.logout(tokenInput.getRefreshToken());
    return ResponseEntity.ok().build();
  }

}
