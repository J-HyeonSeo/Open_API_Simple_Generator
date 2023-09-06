package com.jhsfully.domain.type.errortype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiRequestErrorType {
  REQUEST_NOT_FOUND("존재하지 않는 요청입니다."),
  CANNOT_REQUEST_IS_NOT_OPENED("공개되지 않은 API를 신청할 수 없습니다."),
  CANNOT_REQUEST_API_OWNER("API 소유주는 신청할 수 없습니다."),
  CANNOT_REQUEST_API_HAS_PERMISSION("이미 해당 API에 대한 접근이 가능합니다."),
  CANNOT_REQUEST_ALREADY_REQUESTED("이미 신청 요청을 보낸 API입니다."),
  CANNOT_REQUEST_BANNED("신청 정지를 당한 사용자입니다."),
  CANNOT_ASSIGN_REQUEST_NOT_OWNER("소유자 아닌 사람이 신청을 수락할 수 없습니다."),
  CANNOT_REJECT_REQUEST_NOT_OWNER("소유자 아닌 사람이 신청을 거절할 수 없습니다."),
  REQUEST_ALREADY_ASSIGN("이미 수락된 신청입니다."),
  REQUEST_ALREADY_REJECT("이미 거절된 신청입니다."),
  ;

  private final String message;
}