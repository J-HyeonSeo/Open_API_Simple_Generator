package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.errortype.ApiErrorType.API_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.USER_HAS_NOT_API;
import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;

import com.jhsfully.api.exception.ApiException;
import com.jhsfully.api.exception.ApiPermissionException;
import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.model.PageResponse;
import com.jhsfully.api.model.dto.ApiInfoDto;
import com.jhsfully.api.model.dto.ApiInfoDto.ApiInfoDetailDto;
import com.jhsfully.api.model.dto.ApiInfoDto.ApiInfoSearchDto;
import com.jhsfully.api.service.ApiSearchService;
import com.jhsfully.domain.dto.AccessibleDto;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiInfoElastic;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.ApiInfoElasticRepository;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.ApiUserPermissionRepository;
import com.jhsfully.domain.repository.MemberRepository;
import com.jhsfully.domain.type.SearchType;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
    데이터 검색은, Elastic Search로 진행하고,
    상세 조회는 MySQL로 진행하는 식으로 작성함.
 */

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApiSearchServiceImpl implements ApiSearchService {

  //Elastic Repositories
  private final ApiInfoElasticRepository apiInfoElasticRepository;

  //MySQL Repositories
  private final ApiInfoRepository apiInfoRepository;
  private final MemberRepository memberRepository;
  private final ApiUserPermissionRepository apiUserPermissionRepository;

  private static final Long NOT_MEMBER_ID = -1L;

  @Override
  public PageResponse<ApiInfoSearchDto> getOpenApiList(
      String searchText, SearchType type, Pageable pageable, long memberId
  ){
    Page<ApiInfoElastic> apiInfoElasticPage = apiInfoElasticRepository.search(searchText, type, pageable);
    if (memberId == NOT_MEMBER_ID) { //로그인을 수행하지 않고 조회한 경우.
      return PageResponse.of(apiInfoElasticPage,
          (x) -> ApiInfoDto.of(x, false));
    }
    List<AccessibleDto> accessibleList = apiUserPermissionRepository.findByApiIdListAndMemberId(
        apiInfoElasticPage.getContent().stream().map(ApiInfoElastic::getId).collect(Collectors.toList()),
        memberId
    );
    return PageResponse.of(apiInfoElasticPage,
        (x) -> ApiInfoDto.of(x,
            accessibleList.stream().anyMatch(y -> y.getApiId() == x.getId())
            || memberId == x.getOwnerMemberId()));
  }

  //api 상세 조회
  @Override
  public ApiInfoDetailDto getOpenApiDetail(long apiId, long memberId) {
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
  public PageResponse<ApiInfoSearchDto> getOpenApiListForOwner(long memberId,
      String searchText, SearchType type, Pageable pageable) {

    return PageResponse.of(apiInfoElasticRepository.searchForOwner(memberId, searchText, type, pageable),
        (x) -> ApiInfoDto.of(x, true));
  }

  @Override
  public PageResponse<ApiInfoSearchDto> getOpenApiListForAccess(long memberId,
      String searchText, SearchType type, Pageable pageable) {

    return PageResponse.of(apiInfoElasticRepository.searchForAccessor(memberId, searchText, type, pageable),
        (x) -> ApiInfoDto.of(x, true));
  }

}
