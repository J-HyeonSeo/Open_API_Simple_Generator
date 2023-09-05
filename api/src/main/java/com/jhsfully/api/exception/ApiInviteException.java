package com.jhsfully.api.exception;

import com.jhsfully.domain.type.errortype.ApiInviteErrorType;
import lombok.Getter;

@Getter
public class ApiInviteException extends CustomException{
  private final ApiInviteErrorType apiInviteErrorType;

  public ApiInviteException(ApiInviteErrorType apiInviteErrorType){
    super(apiInviteErrorType.getMessage());
    this.apiInviteErrorType = apiInviteErrorType;
  }
}