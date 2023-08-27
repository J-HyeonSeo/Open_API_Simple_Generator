package com.jhsfully.domain.type.errortype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiPermissionErrorType {
  USER_HAS_NOT_API("해당 API에 대한 권한이 없습니다."),
  API_KEY_NOT_ISSUED("발급된 API가 없습니다."),
  API_KEY_ALREADY_ISSUED("이미 API키가 발급되었습니다.");
  private final String message;
}