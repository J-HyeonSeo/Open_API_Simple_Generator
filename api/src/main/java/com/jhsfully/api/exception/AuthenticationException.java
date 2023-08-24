package com.jhsfully.api.exception;

import com.jhsfully.domain.type.errortype.AuthenticationErrorType;
import lombok.Getter;

@Getter
public class AuthenticationException extends CustomException{
  private final AuthenticationErrorType authenticationErrorType;

  public AuthenticationException(AuthenticationErrorType authenticationErrorType){
    super(authenticationErrorType.getMessage());
    this.authenticationErrorType = authenticationErrorType;
  }
}