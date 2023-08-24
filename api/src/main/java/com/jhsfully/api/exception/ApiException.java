package com.jhsfully.api.exception;

import com.jhsfully.domain.type.errortype.ApiErrorType;
import lombok.Getter;

@Getter
public class ApiException extends CustomException{
  private final ApiErrorType apiErrorType;

  public ApiException(ApiErrorType apiErrorType){
    super(apiErrorType.getMessage());
    this.apiErrorType = apiErrorType;
  }
}