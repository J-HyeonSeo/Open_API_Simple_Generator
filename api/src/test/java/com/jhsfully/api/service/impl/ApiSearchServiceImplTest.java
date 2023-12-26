package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.errortype.ApiErrorType.API_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.jhsfully.api.exception.ApiException;
import com.jhsfully.api.exception.ApiPermissionException;
import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.model.PageResponse;
import com.jhsfully.api.model.dto.ApiInfoDto.ApiInfoDetailDto;
import com.jhsfully.api.model.dto.ApiInfoDto.ApiInfoSearchDto;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiInfoElastic;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.ApiInfoElasticRepository;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.ApiUserPermissionRepository;
import com.jhsfully.domain.repository.MemberRepository;
import com.jhsfully.domain.type.ApiQueryType;
import com.jhsfully.domain.type.ApiState;
import com.jhsfully.domain.type.ApiStructureType;
import com.jhsfully.domain.type.QueryData;
import com.jhsfully.domain.type.SchemaData;
import com.jhsfully.domain.type.SearchType;
import com.jhsfully.domain.type.errortype.ApiPermissionErrorType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ApiSearchServiceImplTest {

  @Mock
  private ApiInfoElasticRepository apiInfoElasticRepository;
  @Mock
  private ApiInfoRepository apiInfoRepository;
  @Mock
  private MemberRepository memberRepository;
  @Mock
  private ApiUserPermissionRepository apiUserPermissionRepository;

  @InjectMocks
  private ApiSearchServiceImpl apiSearchService;

  private ApiInfoElastic getApiInfoElastic(){
    return ApiInfoElastic.builder()
        .id(1L)
        .apiName("test")
        .apiIntroduce("test")
        .ownerNickname("owner")
        .apiState(ApiState.ENABLED)
        .isPublic(true)
        .ownerMemberId(1L)
        .build();
  }

  private Member getOwnerMember(){
    return Member.builder()
        .id(1L)
        .email("owner@test.com")
        .build();
  }

  private Member getOtherMember(){
    return Member.builder()
        .id(2L)
        .email("other@test.com")
        .build();
  }

  private List<SchemaData> getSchemaStructure(){
    return List.of(new SchemaData("test", ApiStructureType.STRING));
  }

  private List<QueryData> getQueryParameter(){
    return List.of(new QueryData("test", ApiQueryType.EQUAL));
  }

  private ApiInfo getApiInfo(){

    return ApiInfo.builder()
        .id(1L)
        .apiName("test")
        .apiIntroduce("test")
        .member(getOwnerMember())
        .apiState(ApiState.ENABLED)
        .schemaStructure(getSchemaStructure())
        .queryParameter(getQueryParameter())
        .registeredAt(LocalDateTime.of(2023, 10, 31, 9, 30 ,10))
        .updatedAt(LocalDateTime.of(2023, 10, 31, 9, 40, 10))
        .isPublic(true)
        .build();
  }

  @Nested
  @DisplayName("getOpenApiList() 테스트")
  class getOpenApiListTest {

    @Test
    @DisplayName("공개된 API 목록 조회 성공")
    void success_getOpenApiList(){
      //given
      ApiInfoElastic apiInfoElastic = getApiInfoElastic();

      given(apiInfoElasticRepository.search(anyString(), any(), any()))
          .willReturn(new PageImpl<>(List.of(apiInfoElastic)));

      //when
      PageResponse<ApiInfoSearchDto> response = apiSearchService
          .getOpenApiList("test", SearchType.API_NAME, PageRequest.of(0, 10), 1L);

      //then
      assertAll(
          () -> assertEquals(apiInfoElastic.getId(), response.getContent().get(0).getId()),
          () -> assertEquals(apiInfoElastic.getApiName(), response.getContent().get(0).getApiName()),
          () -> assertEquals(apiInfoElastic.getOwnerNickname(), response.getContent().get(0).getOwnerNickname()),
          () -> assertEquals(apiInfoElastic.getProfileUrl(), response.getContent().get(0).getProfileUrl()),
          () -> assertEquals(apiInfoElastic.getApiState(), response.getContent().get(0).getApiState()),
          () -> assertFalse(response.getContent().get(0).isAccessible())
      );
    }

  }

  @Nested
  @DisplayName("getOpenApiDetail() 테스트")
  class getOpenApiDetail {

    @Test
    @DisplayName("API 상세 조회 성공")
    void success_getOpenApiDetail(){
      //given
      ApiInfo apiInfo = getApiInfo();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      //when
      ApiInfoDetailDto response = apiSearchService.getOpenApiDetail(1L, 1L);

      //then
      assertAll(
          () -> assertEquals(apiInfo.getId(), response.getId()),
          () -> assertEquals(apiInfo.getApiName(), response.getApiName()),
          () -> assertEquals(apiInfo.getApiIntroduce(), response.getApiIntroduce()),
          () -> assertEquals(apiInfo.getMember().getNickname(), response.getOwnerNickname()),
          () -> assertEquals(apiInfo.getApiState(), response.getApiState()),
          () -> assertEquals(apiInfo.getSchemaStructure().get(0), response.getSchemaStructure().get(0)),
          () -> assertEquals(apiInfo.getQueryParameter().get(0), response.getQueryParameter().get(0)),
          () -> assertEquals(apiInfo.getRegisteredAt(), response.getRegisteredAt()),
          () -> assertEquals(apiInfo.getUpdatedAt(), response.getUpdatedAt()),
          () -> assertEquals(apiInfo.getDisabledAt(), response.getDisabledAt()),
          () -> assertEquals(apiInfo.isPublic(), response.isPublic())
      );
    }

    @Test
    @DisplayName("API 상세 조회 실패 - API X")
    void success_getOpenApiDetail_api_not_found(){
      //given
      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      ApiException exception = assertThrows(ApiException.class,
          () -> apiSearchService.getOpenApiDetail(1L, 1L));

      //then
      assertEquals(API_NOT_FOUND, exception.getApiErrorType());

    }

    @Test
    @DisplayName("API 상세 조회 실패 - 회원 X")
    void success_getOpenApiDetail_authentication_user_not_found(){
      //given
      ApiInfo apiInfo = getApiInfo();
      apiInfo.setPublic(false);

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiSearchService.getOpenApiDetail(1L, 1L));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("API 상세 조회 실패 - 권한 X")
    void success_getOpenApiDetail_user_has_not_api(){
      //given
      ApiInfo apiInfo = getApiInfo();
      apiInfo.setPublic(false);

      Member otherMember = getOtherMember();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(otherMember));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.empty());

      //when
      ApiPermissionException exception = assertThrows(ApiPermissionException.class,
          () -> apiSearchService.getOpenApiDetail(1L, 1L));

      //then
      assertEquals(ApiPermissionErrorType.USER_HAS_NOT_API, exception.getApiPermissionErrorType());
    }

  }

  @Nested
  @DisplayName("getOpenApiListForOwner() 테스트")
  class getOpenApiListForOwnerTest {

    @Test
    @DisplayName("소유한 API 목록 조회 성공")
    void success_getOpenApiListForOwner(){
      //given
      ApiInfoElastic apiInfoElastic = getApiInfoElastic();

      given(apiInfoElasticRepository.searchForOwner(anyLong(), anyString(), any(), any()))
          .willReturn(new PageImpl<>(List.of(apiInfoElastic)));

      //when
      PageResponse<ApiInfoSearchDto> response = apiSearchService.getOpenApiListForOwner(1L,
          "test", SearchType.API_NAME, PageRequest.of(0, 10));

      //then
      assertAll(
          () -> assertEquals(apiInfoElastic.getId(), response.getContent().get(0).getId()),
          () -> assertEquals(apiInfoElastic.getApiName(), response.getContent().get(0).getApiName()),
          () -> assertEquals(apiInfoElastic.getOwnerNickname(), response.getContent().get(0).getOwnerNickname()),
          () -> assertEquals(apiInfoElastic.getProfileUrl(), response.getContent().get(0).getProfileUrl()),
          () -> assertEquals(apiInfoElastic.getApiState(), response.getContent().get(0).getApiState()),
          () -> assertTrue(response.getContent().get(0).isAccessible())
      );
    }

  }

  @Nested
  @DisplayName("getOpenApiListForAccess() 테스트")
  class getOpenApiListForAccess {

    @Test
    @DisplayName("접근 가능한 API 목록 조회 성공")
    void success_getOpenApiListForAccess(){
      //given
      ApiInfoElastic apiInfoElastic = getApiInfoElastic();

      given(apiInfoElasticRepository.searchForAccessor(anyLong(), anyString(), any(), any()))
          .willReturn(new PageImpl<>(List.of(apiInfoElastic)));

      //when
      PageResponse<ApiInfoSearchDto> response = apiSearchService.getOpenApiListForAccess(1L,
          "test", SearchType.API_NAME, PageRequest.of(0, 10));

      //then
      assertAll(
          () -> assertEquals(apiInfoElastic.getId(), response.getContent().get(0).getId()),
          () -> assertEquals(apiInfoElastic.getApiName(), response.getContent().get(0).getApiName()),
          () -> assertEquals(apiInfoElastic.getOwnerNickname(), response.getContent().get(0).getOwnerNickname()),
          () -> assertEquals(apiInfoElastic.getProfileUrl(), response.getContent().get(0).getProfileUrl()),
          () -> assertEquals(apiInfoElastic.getApiState(), response.getContent().get(0).getApiState()),
          () -> assertTrue(response.getContent().get(0).isAccessible())
      );
    }

  }

}