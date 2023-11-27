package com.jhsfully.domain.type.errortype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiErrorType {

  API_NOT_FOUND("존재하지 않는 API입니다."),
  API_IS_ALREADY_ENABLED("이미 활성화된 API입니다."),
  CANNOT_ENABLE_FAILED_API("엑셀 파싱에 실패한 API는 활성화 할 수 없습니다. 삭제 부탁드립니다."),
  CANNOT_ENABLE_READY_API("API가 준비 중입니다. 활성화 될 때 까지, 잠시만 기다려주세요."),
  TODAY_IS_AFTER_EXPIRED_AT("등급 기한이 만료되었습니다."),
  DATA_IS_NOT_FOUND("존재하지 데이터 번호 입니다."),
  API_DATA_CANNOT_PARSE("데이터 구조가 맞지 않아 변환할 수 없습니다."),
  API_FIELD_COUNT_IS_DIFFERENT("API에 존재하는 필드 갯수보다 적습니다."),
  DUPLICATED_SCHEMA("중복된 스키마 필드는 생성이 불가능합니다."),
  DUPLICATED_QUERY_PARAMETER("중복된 쿼리파라미터는 생성이 불가능합니다."),
  QUERY_PARAMETER_NOT_INCLUDE_SCHEMA("스키마에 대한 쿼리파라미터가 아닙니다."),
  QUERY_PARAMETER_CANNOT_MATCH("쿼리 파라미터가 매치되지 않습니다."),
  FIELD_WAS_NOT_DEFINITION_IN_SCHEMA("스키마에 정의되지 않은 필드명입니다."),
  VALUE_STRUCTURE_IS_DIFFERENT("입력된 데이터의 자료구조가 다릅니다."),
  DOES_NOT_EXCEL_FILE("업로드 된 파일은 엑셀파일이 아닙니다."),
  FILE_PARSE_ERROR("파일을 읽는 중에 오류가 발생하였습니다."),
  FILE_NAME_IS_NULL("파일이름이 존재하지 않습니다."),
  SCHEMA_COUNT_IS_ZERO("정의된 스키마 구조가 존재하지 않습니다."),
  OVERFLOW_MAX_FILE_SIZE("지정된 파일 용량을 초과하였습니다."),
  OVERFLOW_MAX_DB_SIZE("지정된 DB 용량을 초과하였습니다."),
  OVERFLOW_API_MAX_COUNT("생성 가능한 API갯수를 초과하였습니다."),
  OVERFLOW_FIELD_MAX_COUNT("생성 가능한 필드 갯수를 초과하였습니다."),
  OVERFLOW_QUERY_MAX_COUNT("생성 가능한 검색 질의 인수 갯수를 초과하였습니다."),
  OVERFLOW_RECORD_MAX_COUNT("생성 가능한 레코드 갯수를 초과하였습니다."),
  API_IS_DISABLED("해당 API는 현재 비활성화 상태입니다."),
  ;
  private final String message;
}
