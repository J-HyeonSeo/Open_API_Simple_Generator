package com.jhsfully.domain.type.errortype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GradeErrorType {
  GRADE_NOT_FOUND("존재하지 않는 등급입니다."),
  ;
  private final String message;
}
