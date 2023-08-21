package com.jhsfully.api.controller;

import com.jhsfully.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class LogoutController {

  private final AuthService authService;

  @GetMapping("/auth/logout")
  public String logOut(@CookieValue(name = "RefreshToken", required = true) String refreshTokenString){
    authService.logout(refreshTokenString);
    return "redirect:/";
  }

}
