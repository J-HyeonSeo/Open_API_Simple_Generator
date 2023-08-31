package com.jhsfully.domain.type.errortype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiErrorType {

  API_NOT_FOUND("존재하지 않는 API입니다."),
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



  //REQUEST
  REQUEST_NOT_FOUND("존재하지 않는 요청입니다."),
  CANNOT_REQUEST_API_OWNER("API 소유주는 신청할 수 없습니다."),
  CANNOT_REQUEST_API_HAS_PERMISSION("이미 해당 API에 대한 접근이 가능합니다."),
  CANNOT_REQUEST_ALREADY_REQUESTED("이미 신청 요청을 보낸 API입니다."),
  CANNOT_REQUEST_BANNED("신청 정지를 당한 사용자입니다."),
  CANNOT_ASSIGN_REQUEST_NOT_OWNER("소유자 아닌 사람이 신청을 수락할 수 없습니다."),
  CANNOT_REJECT_REQUEST_NOT_OWNER("소유자 아닌 사람이 신청을 거절할 수 없습니다."),
  REQUEST_ALREADY_ASSIGN("이미 수락된 신청입니다."),
  REQUEST_ALREADY_REJECT("이미 거절된 신청입니다.");
  private final String message;
}
