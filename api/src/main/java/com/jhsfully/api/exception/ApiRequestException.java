package com.jhsfully.api.exception;

import com.jhsfully.domain.type.errortype.ApiRequestErrorType;
import lombok.Getter;

@Getter
public class ApiRequestException extends CustomException{
  private final ApiRequestErrorType apiRequestErrorType;

  public ApiRequestException(ApiRequestErrorType apiRequestErrorType){
    super(apiRequestErrorType.getMessage());
    this.apiRequestErrorType = apiRequestErrorType;
  }
}