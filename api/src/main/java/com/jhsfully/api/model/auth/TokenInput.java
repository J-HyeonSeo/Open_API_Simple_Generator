package com.jhsfully.api.model.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenInput {
  private String refreshToken;
}
