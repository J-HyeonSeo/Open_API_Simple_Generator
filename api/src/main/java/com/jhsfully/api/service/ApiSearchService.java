package com.jhsfully.api.service;

import com.jhsfully.api.model.api.ApiSearchResponse;
import com.jhsfully.api.model.dto.ApiInfoDto.ApiInfoDetailResponse;
import com.jhsfully.domain.type.SearchType;
import org.springframework.data.domain.Pageable;

public interface ApiSearchService {
  //전체 검색
  ApiSearchResponse getOpenApiList(
      String searchText, SearchType type, Pageable pageable
  );

  //상세 조회
  ApiInfoDetailResponse getOpenApiDetail(
      long apiId, long memberId
  );

  // 소유하고 있는 API 검색
  ApiSearchResponse getOpenApiListForOwner(
      long memberId, String searchText, SearchType type, Pageable pageable
  );

  // 접근 가능한 API 검색 (본인 소유 미포함)
  ApiSearchResponse getOpenApiListForAccess(
      long memberId, String searchText, SearchType type, Pageable pageable
  );
}
