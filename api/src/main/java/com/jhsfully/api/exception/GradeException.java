package com.jhsfully.api.exception;

import com.jhsfully.domain.type.errortype.GradeErrorType;
import lombok.Getter;

@Getter
public class GradeException extends CustomException{
  private final GradeErrorType gradeErrorType;

  public GradeException(GradeErrorType gradeErrorType){
    super(gradeErrorType.getMessage());
    this.gradeErrorType = gradeErrorType;
  }
}
