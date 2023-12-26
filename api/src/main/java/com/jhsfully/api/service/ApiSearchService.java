package com.jhsfully.api.service;

import com.jhsfully.api.model.PageResponse;
import com.jhsfully.api.model.dto.ApiInfoDto.ApiInfoDetailDto;
import com.jhsfully.api.model.dto.ApiInfoDto.ApiInfoSearchDto;
import com.jhsfully.domain.type.SearchType;
import org.springframework.data.domain.Pageable;

public interface ApiSearchService {
  //전체 검색
  PageResponse<ApiInfoSearchDto> getOpenApiList(
      String searchText, SearchType type, Pageable pageable, long memberId
  );

  //상세 조회
  ApiInfoDetailDto getOpenApiDetail(
      long apiId, long memberId
  );

  // 소유하고 있는 API 검색
  PageResponse<ApiInfoSearchDto> getOpenApiListForOwner(
      long memberId, String searchText, SearchType type, Pageable pageable
  );

  // 접근 가능한 API 검색 (본인 소유 미포함)
  PageResponse<ApiInfoSearchDto> getOpenApiListForAccess(
      long memberId, String searchText, SearchType type, Pageable pageable
  );
}
