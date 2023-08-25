package com.jhsfully.domain.type.errortype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiErrorType {

  DUPLICATED_SCHEMA("중복된 스키마 필드는 생성이 불가능합니다."),
  DUPLICATED_QUERY_PARAMETER("중복된 쿼리파라미터는 생성이 불가능합니다."),
  QUERY_PARAMETER_NOT_INCLUDE_SCHEMA("스키마에 대한 쿼리파라미터가 아닙니다."),
  QUERY_PARAMETER_CANNOT_MATCH("쿼리 파라미터가 매치되지 않습니다."),
  DOES_NOT_EXCEL_FILE("업로드 된 파일은 엑셀파일이 아닙니다."),
  FILE_PARSE_ERROR("파일을 읽는 중에 오류가 발생하였습니다.");
  private final String message;
}
