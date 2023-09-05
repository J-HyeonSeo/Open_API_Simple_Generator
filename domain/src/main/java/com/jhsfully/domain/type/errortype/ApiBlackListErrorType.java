package com.jhsfully.domain.type.errortype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiBlackListErrorType {
  BLACKLIST_NOT_FOUND("존재하지 않는 블랙리스트입니다."),
  CANNOT_REGISTER_NOT_OWNER("해당 API의 소유주가 아닙니다."),
  CANNOT_REGISTER_TARGET_IS_OWNER("API 소유주를 블랙리스트로 등록할 수 없습니다."),
  ALREADY_REGISTERED_TARGET("이미 블랙리스트에 등록된 멤버입니다."),
  CANNOT_DELETE_NOT_OWNER("해당 API의 소유주가 아닙니다.");

  private final String message;
}