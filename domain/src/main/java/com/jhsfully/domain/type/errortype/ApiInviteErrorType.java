package com.jhsfully.domain.type.errortype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiInviteErrorType {
  INVITE_NOT_FOUND("해당 초대가 존재하지 않습니다."),
  CANNOT_INVITE_NOT_API_OWNER("API 소유주 이외의 사람은 초대할 수 없습니다."),
  CANNOT_INVITE_ALREADY_HAS_PERMISSION("초대할 멤버가 이미 권한을 가지고 있습니다."),
  CANNOT_INVITE_ALREADY_INVITED("이미 초대 요청을 보낸 멤버입니다."),
  CANNOT_ASSIGN_INVITE_NOT_TARGET("초대받은자 이외의 사람이 초대을 수락할 수 없습니다."),
  CANNOT_REJECT_INVITE_NOT_TARGET("초대받은자 이외의 사람이 초대을 거절할 수 없습니다."),
  INVITE_ALREADY_ASSIGN("이미 수락한 초대입니다."),
  INVITE_ALREADY_REJECT("이미 거절한 초대입니다.");

  private final String message;
}