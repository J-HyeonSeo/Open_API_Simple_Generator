package com.jhsfully.api.exception;

import com.jhsfully.domain.type.errortype.ApiBlackListErrorType;
import lombok.Getter;

@Getter
public class ApiBlackListException extends CustomException{
  private final ApiBlackListErrorType apiBlackListErrorType;

  public ApiBlackListException(ApiBlackListErrorType apiBlackListErrorType){
    super(apiBlackListErrorType.getMessage());
    this.apiBlackListErrorType = apiBlackListErrorType;
  }
}
