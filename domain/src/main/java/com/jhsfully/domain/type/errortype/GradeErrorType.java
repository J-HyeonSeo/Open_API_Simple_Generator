package com.jhsfully.domain.type.errortype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GradeErrorType {
  GRADE_NOT_FOUND("존재하지 않는 등급입니다."),
  MEMBER_HAS_NOT_GRADE("등급을 소유하고 있지 않습니다. 고객센터로 연락바랍니다."),
  ;
  private final String message;
}
