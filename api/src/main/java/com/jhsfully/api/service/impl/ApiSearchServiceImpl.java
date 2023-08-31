package com.jhsfully.api.service.impl;

import com.jhsfully.api.model.api.ApiSearchResponse;
import com.jhsfully.api.service.ApiSearchService;
import com.jhsfully.domain.entity.ApiInfoElastic;
import com.jhsfully.domain.repository.ApiInfoElasticRepository;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.type.SearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/*
    데이터 검색은, Elastic Search로 진행하고,
    상세 조회는 MySQL로 진행하는 식으로 작성함.
 */

@Service
@RequiredArgsConstructor
public class ApiSearchServiceImpl implements ApiSearchService {

  private final ApiInfoElasticRepository apiInfoElasticRepository;
  private final ApiInfoRepository apiInfoRepository;

  public ApiSearchResponse getOpenApiList(
      int pageSize, int pageIdx, String searchText, SearchType type
  ){

    Pageable pageable = PageRequest.of(pageIdx, pageSize);

    Page<ApiInfoElastic> apiInfoElasticPage = null;

    switch (type){
      case API_NAME:
        apiInfoElasticPage = apiInfoElasticRepository.searchByApiName(searchText, pageable);
        break;
      case API_INTRODUCE:
        apiInfoElasticPage = apiInfoElasticRepository.searchByApiIntroduce(searchText, pageable);
        break;
      case API_OWNER_EMAIL:
        apiInfoElasticPage = apiInfoElasticRepository.searchByOwnerEmail(searchText, pageable);
        break;
    }

    return ApiSearchResponse.builder()
        .totalCount(apiInfoElasticPage.getTotalElements())
        .dataCount(apiInfoElasticPage.getNumberOfElements())
        .dataList(apiInfoElasticPage.getContent())
        .build();
  }

}
