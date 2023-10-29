package com.jhsfully.api.service.impl;

import com.jhsfully.api.exception.ApiException;
import com.jhsfully.api.exception.ApiPermissionException;
import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.model.api.ApiSearchResponse;
import com.jhsfully.api.model.dto.ApiInfoDto;
import com.jhsfully.api.model.dto.ApiInfoDto.ApiInfoDetailResponse;
import com.jhsfully.api.service.ApiSearchService;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiInfoElastic;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.ApiInfoElasticRepository;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.ApiUserPermissionRepository;
import com.jhsfully.domain.repository.MemberRepository;
import com.jhsfully.domain.type.SearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.jhsfully.domain.type.errortype.ApiErrorType.API_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.USER_HAS_NOT_API;
import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;

/*
    데이터 검색은, Elastic Search로 진행하고,
    상세 조회는 MySQL로 진행하는 식으로 작성함.
 */

@Service
@RequiredArgsConstructor
public class ApiSearchServiceImpl implements ApiSearchService {

  //Elastic Repositories
  private final ApiInfoElasticRepository apiInfoElasticRepository;

  //MySQL Repositories
  private final ApiInfoRepository apiInfoRepository;
  private final MemberRepository memberRepository;
  private final ApiUserPermissionRepository apiUserPermissionRepository;

  @Override
  public ApiSearchResponse getOpenApiList(
      String searchText, SearchType type, Pageable pageable
  ){

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

  //api 상세 조회
  @Override
  public ApiInfoDetailResponse getOpenApiDetail(long apiId, long memberId) {
    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    //공개된 API가 맞다면, 바로 결과 리턴.
    if(apiInfo.isPublic()){
      return ApiInfoDto.detailOf(apiInfo);
    }

    //소유자 일 경우에도 바로 리턴
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    if(Objects.equals(apiInfo.getMember().getId(), member.getId())){
      return ApiInfoDto.detailOf(apiInfo);
    }

    //소유자도 아닌 경우에는 접근 가능 권한이 있는지 확인함.
    if(apiUserPermissionRepository.findByApiInfoAndMember(apiInfo, member).isPresent()){
      return ApiInfoDto.detailOf(apiInfo);
    }

    throw new ApiPermissionException(USER_HAS_NOT_API);

  }

  @Override
  public ApiSearchResponse getOpenApiListForOwner(long memberId,
      String searchText, SearchType type, Pageable pageable) {

    Page<ApiInfoElastic> apiInfoElasticPage = null;

    switch (type){
      case API_NAME:
        apiInfoElasticPage = apiInfoElasticRepository.searchByApiNameForOwner(memberId, searchText, pageable);
        break;
      case API_INTRODUCE:
        apiInfoElasticPage = apiInfoElasticRepository.searchByApiIntroduceForOwner(memberId, searchText, pageable);
        break;
      case API_OWNER_EMAIL:
        apiInfoElasticPage = apiInfoElasticRepository.searchByOwnerEmailForOwner(memberId, searchText, pageable);
        break;
    }

    return ApiSearchResponse.builder()
        .totalCount(apiInfoElasticPage.getTotalElements())
        .dataCount(apiInfoElasticPage.getNumberOfElements())
        .dataList(apiInfoElasticPage.getContent())
        .build();
  }

  @Override
  public ApiSearchResponse getOpenApiListForAccess(long memberId,
      String searchText, SearchType type, Pageable pageable) {

    Page<ApiInfoElastic> apiInfoElasticPage = null;

    switch (type){
      case API_NAME:
        apiInfoElasticPage = apiInfoElasticRepository.searchByApiNameForAccess(memberId, searchText, pageable);
        break;
      case API_INTRODUCE:
        apiInfoElasticPage = apiInfoElasticRepository.searchByApiIntroduceForAccess(memberId, searchText, pageable);
        break;
      case API_OWNER_EMAIL:
        apiInfoElasticPage = apiInfoElasticRepository.searchByOwnerEmailForAccess(memberId, searchText, pageable);
        break;
    }

    return ApiSearchResponse.builder()
        .totalCount(apiInfoElasticPage.getTotalElements())
        .dataCount(apiInfoElasticPage.getNumberOfElements())
        .dataList(apiInfoElasticPage.getContent())
        .build();
  }

}
