package com.jhsfully.domain.type.errortype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiPermissionErrorType {
  USER_HAS_NOT_API("해당 API에 접근할 수 없습니다."),
  USER_HAS_NOT_PERMISSION("해당 API에 대한 권한이 없습니다."),
  API_KEY_NOT_ISSUED("발급된 API가 없습니다."),
  API_KEY_ALREADY_ISSUED("이미 API키가 발급되었습니다."),
  YOU_ARE_NOT_API_OWNER("API의 소유주가 아닙니다."),
  ALREADY_HAS_PERMISSION("이미 소유하고 있는 권한입니다."),
  PERMISSION_DETAIL_NOT_FOUND("존재하지 않는 권한 상세입니다.");
  private final String message;
}