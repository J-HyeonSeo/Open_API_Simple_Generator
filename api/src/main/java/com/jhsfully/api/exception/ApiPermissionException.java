package com.jhsfully.api.exception;

import com.jhsfully.domain.type.errortype.ApiPermissionErrorType;
import lombok.Getter;

@Getter
public class ApiPermissionException extends CustomException{
  private final ApiPermissionErrorType apiPermissionErrorType;

  public ApiPermissionException(ApiPermissionErrorType apiPermissionErrorType){
    super(apiPermissionErrorType.getMessage());
    this.apiPermissionErrorType = apiPermissionErrorType;
  }
}