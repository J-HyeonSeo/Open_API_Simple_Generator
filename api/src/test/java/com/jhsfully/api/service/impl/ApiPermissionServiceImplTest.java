package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.errortype.ApiErrorType.API_IS_DISABLED;
import static com.jhsfully.domain.type.errortype.ApiErrorType.API_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.ALREADY_HAS_PERMISSION;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.API_KEY_ALREADY_ISSUED;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.API_KEY_NOT_ISSUED;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.PERMISSION_DETAIL_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.USER_HAS_NOT_API;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.USER_HAS_NOT_PERMISSION;
import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.jhsfully.api.exception.ApiException;
import com.jhsfully.api.exception.ApiPermissionException;
import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.model.PageResponse;
import com.jhsfully.api.model.permission.AuthKeyResponse;
import com.jhsfully.api.model.dto.PermissionDto;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiKey;
import com.jhsfully.domain.entity.ApiPermissionDetail;
import com.jhsfully.domain.entity.ApiUserPermission;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.ApiInfoElasticRepository;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.ApiKeyRepository;
import com.jhsfully.domain.repository.ApiPermissionDetailRepository;
import com.jhsfully.domain.repository.ApiUserPermissionRepository;
import com.jhsfully.domain.repository.MemberRepository;
import com.jhsfully.domain.type.ApiPermissionType;
import com.jhsfully.domain.type.ApiState;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ApiPermissionServiceImplTest {

  @Mock
  private ApiInfoElasticRepository apiInfoElasticRepository;
  @Mock
  private ApiInfoRepository apiInfoRepository;
  @Mock
  private ApiUserPermissionRepository apiUserPermissionRepository;
  @Mock
  private ApiPermissionDetailRepository apiPermissionDetailRepository;
  @Mock
  private ApiKeyRepository apiKeyRepository;
  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private ApiPermissionServiceImpl apiPermissionService;

  private Member getOwnerMember(){
    return Member.builder()
        .id(1L)
        .email("owner@test.com")
        .nickname("owner")
        .profileUrl("ownerProfileUrl")
        .build();
  }

  private Member getAccessor(){
    return Member.builder()
        .id(2L)
        .email("accessor@test.com")
        .nickname("accessor")
        .profileUrl("profileUrl")
        .build();
  }

  private ApiInfo getApiInfo(){
    return ApiInfo.builder()
        .id(1L)
        .member(getOwnerMember())
        .apiState(ApiState.ENABLED)
        .build();
  }

  private ApiUserPermission getApiUserPermission(){

    ApiPermissionDetail apiPermissionDetail = ApiPermissionDetail.builder()
        .id(1L)
        .type(ApiPermissionType.INSERT)
        .build();

    ApiUserPermission apiUserPermission = ApiUserPermission.builder()
        .id(1L)
        .apiInfo(getApiInfo())
        .member(getAccessor())
        .apiPermissionDetails(List.of(
            apiPermissionDetail
        ))
        .build();

    apiPermissionDetail.setApiUserPermission(apiUserPermission);

    return apiUserPermission;
  }

  private ApiKey getApiKey(){
    return ApiKey.builder()
        .id(1L)
        .member(getAccessor())
        .apiInfo(getApiInfo())
        .authKey("eafc123Xd8939fdk")
        .build();
  }

  @Nested
  @DisplayName("getPermissionForMember() 테스트")
  class getPermissionForMemberTest {

    @Test
    @DisplayName("해당 API에 대해 회원이 소유한 권한 조회 성공")
    void success_getPermissionForMember(){
      //given
      ApiInfo apiInfo = getApiInfo();
      Member accessMember = getAccessor();
      ApiUserPermission permission = getApiUserPermission();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.of(permission));

      //when
      PermissionDto permissionDto = apiPermissionService.getPermissionForMember(1L, 1L);

      //then
      assertAll(
          () -> assertEquals(permission.getId(), permissionDto.getPermissionId()),
          () -> assertEquals(accessMember.getNickname(), permissionDto.getMemberNickname()),
          () -> assertEquals(accessMember.getProfileUrl(), permissionDto.getProfileUrl()),
          () -> assertEquals(permission.getApiPermissionDetails().get(0).getType(),
              permissionDto.getPermissionList().get(0).getType())
      );
    }

    @Test
    @DisplayName("해당 API에 대해 회원이 소유한 권한 조회 실패 - API X")
    void fail_getPermissionForMember_api_not_found(){
      //given
      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      ApiException exception = assertThrows(ApiException.class,
          () -> apiPermissionService.getPermissionForMember(1L, 1L));

      //then
      assertEquals(API_NOT_FOUND, exception.getApiErrorType());
    }

    @Test
    @DisplayName("해당 API에 대해 회원이 소유한 권한 조회 실패 - 회원 X")
    void fail_getPermissionForMember_authentication_user_not_found(){
      //given
      ApiInfo apiInfo = getApiInfo();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiPermissionService.getPermissionForMember(1L, 1L));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("해당 API에 대해 회원이 소유한 권한 조회 실패 - 권한 X")
    void fail_getPermissionForMember_user_has_not_permission(){
      //given
      ApiInfo apiInfo = getApiInfo();
      Member accessMember = getAccessor();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.empty());
      //when
      ApiPermissionException exception = assertThrows(ApiPermissionException.class,
          () -> apiPermissionService.getPermissionForMember(1L, 1L));

      //then
      assertEquals(USER_HAS_NOT_PERMISSION, exception.getApiPermissionErrorType());
    }

  }

  @Nested
  @DisplayName("getPermissionListForOwner() 테스트")
  class getPermissionListForOwnerTest {

    @Test
    @DisplayName("API 소유주가 Accessor대한 권한 목록 조회 성공")
    void success_getPermissionListForOwner(){
      //given
      ApiInfo apiInfo = getApiInfo();
      Member ownerMember = getOwnerMember();
      Member accessMember = getAccessor();
      ApiUserPermission permission = getApiUserPermission();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(apiUserPermissionRepository.findByApiInfo(any(), any()))
          .willReturn(new PageImpl<>(List.of(permission)));

      //when
      PageResponse<PermissionDto> permissionResponse = apiPermissionService.getPermissionListForOwner(1L,
          1L, PageRequest.of(0, 10));

      //then
      assertAll(
          () -> assertEquals(permission.getId(), permissionResponse.getContent().get(0).getPermissionId()),
          () -> assertEquals(accessMember.getNickname(), permissionResponse.getContent().get(0).getMemberNickname()),
          () -> assertEquals(accessMember.getProfileUrl(), permissionResponse.getContent().get(0).getProfileUrl()),
          () -> assertEquals(permission.getApiPermissionDetails().get(0).getType(),
              permissionResponse.getContent().get(0).getPermissionList().get(0).getType())
      );
    }

    @Test
    @DisplayName("API 소유주가 Accessor대한 권한 목록 조회 실패 - API X")
    void fail_getPermissionListForOwner_api_not_found(){
      //given
      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      ApiException exception = assertThrows(ApiException.class,
          () -> apiPermissionService.getPermissionListForOwner(1L,
              1L, PageRequest.of(0, 10)));

      //then
      assertEquals(API_NOT_FOUND, exception.getApiErrorType());
    }

    @Test
    @DisplayName("API 소유주가 Accessor대한 권한 목록 조회 실패 - 회원 X")
    void fail_getPermissionListForOwner_authentication_user_not_found(){
      //given
      ApiInfo apiInfo = getApiInfo();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiPermissionService.getPermissionListForOwner(1L,
              1L, PageRequest.of(0, 10)));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("API 소유주가 Accessor대한 권한 목록 조회 실패 - API의 소유주가 아님")
    void fail_getPermissionListForOwner_user_has_not_api(){
      //given
      ApiInfo apiInfo = getApiInfo();
      Member accessMember = getAccessor();
      ApiUserPermission permission = getApiUserPermission();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));

      //when
      ApiPermissionException exception = assertThrows(ApiPermissionException.class,
          () -> apiPermissionService.getPermissionListForOwner(1L,
              1L, PageRequest.of(0, 10)));

      //then
      assertEquals(USER_HAS_NOT_API, exception.getApiPermissionErrorType());
    }

  }

  @Nested
  @DisplayName("addPermission() 테스트")
  class addPermissionTest {

    @Test
    @DisplayName("권한 추가 성공")
    void success_addPermission(){
      //given
      ApiUserPermission permission = getApiUserPermission();
      Member ownerMember = getOwnerMember();

      given(apiUserPermissionRepository.findById(anyLong()))
          .willReturn(Optional.of(permission));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      //when
      apiPermissionService.addPermission(1L, 1L, ApiPermissionType.UPDATE);

      //then
      ArgumentCaptor<ApiPermissionDetail> captor = ArgumentCaptor.forClass(ApiPermissionDetail.class);
      verify(apiPermissionDetailRepository, times(1)).save(captor.capture());

      assertEquals(ApiPermissionType.UPDATE, captor.getValue().getType());

    }

    @Test
    @DisplayName("권한 추가 실패 - 권한 X")
    void fail_addPermission_user_has_not_permission(){
      //given
      given(apiUserPermissionRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      ApiPermissionException exception = assertThrows(ApiPermissionException.class,
          () -> apiPermissionService.addPermission(1L, 1L, ApiPermissionType.UPDATE));

      //then
      assertEquals(USER_HAS_NOT_PERMISSION, exception.getApiPermissionErrorType());
    }

    @Test
    @DisplayName("권한 추가 실패 - 회원 X")
    void fail_addPermission_authentication_user_not_found(){
      //given
      ApiUserPermission permission = getApiUserPermission();

      given(apiUserPermissionRepository.findById(anyLong()))
          .willReturn(Optional.of(permission));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());
      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiPermissionService.addPermission(1L, 1L, ApiPermissionType.UPDATE));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("권한 추가 실패 - API의 소유주가 아님")
    void fail_addPermission_user_has_not_api(){
      //given
      ApiUserPermission permission = getApiUserPermission();
      Member accessMember = getAccessor();

      given(apiUserPermissionRepository.findById(anyLong()))
          .willReturn(Optional.of(permission));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));
      //when
      ApiPermissionException exception = assertThrows(ApiPermissionException.class,
          () -> apiPermissionService.addPermission(1L, 1L, ApiPermissionType.UPDATE));

      //then
      assertEquals(USER_HAS_NOT_API, exception.getApiPermissionErrorType());
    }

    @Test
    @DisplayName("권한 추가 실패 - 이미 가지고 있는 권한임.")
    void fail_addPermission_already_has_permission(){
      //given
      ApiUserPermission permission = getApiUserPermission();
      Member ownerMember = getOwnerMember();

      given(apiUserPermissionRepository.findById(anyLong()))
          .willReturn(Optional.of(permission));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));
      //when
      ApiPermissionException exception = assertThrows(ApiPermissionException.class,
          () -> apiPermissionService.addPermission(1L, 1L, ApiPermissionType.INSERT));

      //then
      assertEquals(ALREADY_HAS_PERMISSION, exception.getApiPermissionErrorType());
    }

  }

  @Nested
  @DisplayName("subPermission() 테스트")
  class subPermissionTest {

    @Test
    @DisplayName("권한 차감 성공")
    void success_subPermission(){
      //given
      ApiPermissionDetail permissionDetail = getApiUserPermission().getApiPermissionDetails().get(0);
      Member ownerMember = getOwnerMember();

      given(apiPermissionDetailRepository.findById(anyLong()))
          .willReturn(Optional.of(permissionDetail));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      //when
      apiPermissionService.subPermission(1L, 1L);

      //then
      ArgumentCaptor<ApiPermissionDetail> captor = ArgumentCaptor.forClass(ApiPermissionDetail.class);
      verify(apiPermissionDetailRepository, times(1)).delete(captor.capture());

      assertAll(
          () -> assertEquals(permissionDetail.getId(), captor.getValue().getId()),
          () -> assertEquals(permissionDetail.getType(), captor.getValue().getType())
      );
    }

    @Test
    @DisplayName("권한 차감 실패 - 존재 하지 않는 권한")
    void fail_subPermission_permission_detail_not_found(){
      //given
      given(apiPermissionDetailRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      ApiPermissionException exception = assertThrows(ApiPermissionException.class,
          () -> apiPermissionService.subPermission(1L, 1L));

      //then
      assertEquals(PERMISSION_DETAIL_NOT_FOUND, exception.getApiPermissionErrorType());
    }

    @Test
    @DisplayName("권한 차감 실패 - 회원 X")
    void fail_subPermission_authentication_user_not_found(){
      //given
      ApiPermissionDetail permissionDetail = getApiUserPermission().getApiPermissionDetails().get(0);

      given(apiPermissionDetailRepository.findById(anyLong()))
          .willReturn(Optional.of(permissionDetail));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());
      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiPermissionService.subPermission(1L, 1L));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("권한 차감 실패 - API의 소유주가 아님")
    void fail_subPermission_user_has_not_api(){
      //given
      ApiPermissionDetail permissionDetail = getApiUserPermission().getApiPermissionDetails().get(0);
      Member accessMember = getAccessor();

      given(apiPermissionDetailRepository.findById(anyLong()))
          .willReturn(Optional.of(permissionDetail));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));
      //when
      ApiPermissionException exception = assertThrows(ApiPermissionException.class,
          () -> apiPermissionService.subPermission(1L, 1L));

      //then
      assertEquals(USER_HAS_NOT_API, exception.getApiPermissionErrorType());
    }

  }

  @Nested
  @DisplayName("deletePermission() 테스트")
  class deletePermissionTest {

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("권한 삭제 성공")
    void success_deletePermission(){
      //given
      ApiUserPermission permission = getApiUserPermission();
      Member ownerMember = getOwnerMember();

      given(apiUserPermissionRepository.findById(anyLong()))
          .willReturn(Optional.of(permission));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      //when
      apiPermissionService.deletePermission(1L, 1L);

      //then
      ArgumentCaptor<List<ApiPermissionDetail>> permissionDetailCaptor = ArgumentCaptor.forClass(List.class);
      ArgumentCaptor<ApiUserPermission> permissionCaptor = ArgumentCaptor.forClass(ApiUserPermission.class);
      ArgumentCaptor<Long> deletePermissionIdCaptor = ArgumentCaptor.forClass(Long.class);

      verify(apiPermissionDetailRepository, times(1))
          .deleteAll(permissionDetailCaptor.capture());
      verify(apiUserPermissionRepository, times(1))
          .delete(permissionCaptor.capture());
      verify(apiInfoElasticRepository, times(1))
          .deleteByPermissionId(deletePermissionIdCaptor.capture());

      assertAll(
          () -> assertEquals(permission.getApiPermissionDetails().get(0).getId(),
              permissionDetailCaptor.getValue().get(0).getId()),
          () -> assertEquals(permission.getApiPermissionDetails().get(0).getType(),
              permissionDetailCaptor.getValue().get(0).getType()),

          () -> assertEquals(permission.getId(), permissionCaptor.getValue().getId()),

          () -> assertEquals(permission.getId(), deletePermissionIdCaptor.getValue())

      );

    }

    @Test
    @DisplayName("권한 삭제 실패 - 권한 X")
    void fail_deletePermission_user_has_not_permission(){
      //given
      given(apiUserPermissionRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      ApiPermissionException exception = assertThrows(ApiPermissionException.class,
          () -> apiPermissionService.deletePermission(1L, 1L));

      //then
      assertEquals(USER_HAS_NOT_PERMISSION, exception.getApiPermissionErrorType());
    }

    @Test
    @DisplayName("권한 삭제 실패 - 회원 X")
    void fail_deletePermission_authentication_user_not_found(){
      //given
      ApiUserPermission permission = getApiUserPermission();

      given(apiUserPermissionRepository.findById(anyLong()))
          .willReturn(Optional.of(permission));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());
      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiPermissionService.deletePermission(1L, 1L));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("권한 삭제 실패 - API의 소유주가 아님")
    void fail_deletePermission_user_has_not_api(){
      //given
      ApiUserPermission permission = getApiUserPermission();
      Member accessmember = getAccessor();

      given(apiUserPermissionRepository.findById(anyLong()))
          .willReturn(Optional.of(permission));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessmember));

      //when
      ApiPermissionException exception = assertThrows(ApiPermissionException.class,
          () -> apiPermissionService.deletePermission(1L, 1L));

      //then
      assertEquals(USER_HAS_NOT_API, exception.getApiPermissionErrorType());
    }

  }

  @Nested
  @DisplayName("getAuthKey() 테스트")
  class getAuthKeyTest {

    @Test
    @DisplayName("인증키 조회 성공")
    void success_getAuthKey(){
      //given
      Member accessMember = getAccessor();
      ApiInfo apiInfo = getApiInfo();
      ApiKey apiKey = getApiKey();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.of(getApiUserPermission()));

      given(apiKeyRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.of(apiKey));

      //when
      AuthKeyResponse authKeyResponse = apiPermissionService.getAuthKey(1L, 1L);

      //then
      assertEquals(apiKey.getAuthKey(), authKeyResponse.getAuthKey());
    }

    @Test
    @DisplayName("인증키 조회 실패 - 회원 X")
    void fail_getAuthKey_authentication_user_not_found(){
      //given
      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiPermissionService.getAuthKey(1L, 1L));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("인증키 조회 실패 - API X")
    void fail_getAuthKey_api_not_found(){
      //given
      Member accessMember = getAccessor();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      ApiException exception = assertThrows(ApiException.class,
          () -> apiPermissionService.getAuthKey(1L, 1L));

      //then
      assertEquals(API_NOT_FOUND, exception.getApiErrorType());
    }

    @Test
    @DisplayName("인증키 조회 실패 - API 비활성화 상태")
    void fail_getAuthKey_api_is_disabled(){
      //given
      Member accessMember = getAccessor();
      ApiInfo apiInfo = getApiInfo();
      apiInfo.setApiState(ApiState.DISABLED);

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      ApiException exception = assertThrows(ApiException.class,
          () -> apiPermissionService.getAuthKey(1L, 1L));

      //then
      assertEquals(API_IS_DISABLED, exception.getApiErrorType());
    }

    @Test
    @DisplayName("인증키 조회 실패 - 권한 X")
    void fail_getAuthKey_user_has_not_api(){
      //given
      Member accessMember = getAccessor();
      ApiInfo apiInfo = getApiInfo();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.empty());

      ApiPermissionException exception = assertThrows(ApiPermissionException.class,
          () -> apiPermissionService.getAuthKey(1L, 1L));

      //then
      assertEquals(USER_HAS_NOT_API, exception.getApiPermissionErrorType());
    }

    @Test
    @DisplayName("인증키 조회 실패 - API키 미발급")
    void fail_getAuthKey_api_key_not_issued(){
      //given
      Member accessMember = getAccessor();
      ApiInfo apiInfo = getApiInfo();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.of(getApiUserPermission()));

      given(apiKeyRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.empty());

      ApiPermissionException exception = assertThrows(ApiPermissionException.class,
          () -> apiPermissionService.getAuthKey(1L, 1L));

      //then
      assertEquals(API_KEY_NOT_ISSUED, exception.getApiPermissionErrorType());
    }

  }

  @Nested
  @DisplayName("createAuthKey() 테스트")
  class createAuthKeyTest {

    @Test
    @DisplayName("인증키 생성 성공")
    void success_createAuthKey(){
      //given
      Member accessMember = getAccessor();
      ApiInfo apiInfo = getApiInfo();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.of(getApiUserPermission()));

      given(apiKeyRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.empty());

      //when
      AuthKeyResponse authKeyResponse = apiPermissionService.createAuthKey(1L, 1L);

      //then
      ArgumentCaptor<ApiKey> captor = ArgumentCaptor.forClass(ApiKey.class);
      verify(apiKeyRepository, times(1)).save(captor.capture());

      assertEquals(authKeyResponse.getAuthKey(), captor.getValue().getAuthKey());

    }

    @Test
    @DisplayName("인증키 생성 실패 - 회원 X")
    void fail_createAuthKey_authentication_user_not_found(){
      //given
      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiPermissionService.createAuthKey(1L, 1L));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("인증키 생성 실패 - API X")
    void fail_createAuthKey_api_not_found(){
      //given
      Member accessMember = getAccessor();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      ApiException exception = assertThrows(ApiException.class,
          () -> apiPermissionService.createAuthKey(1L, 1L));

      //then
      assertEquals(API_NOT_FOUND, exception.getApiErrorType());
    }

    @Test
    @DisplayName("인증키 생성 실패 - API 비활성화 상태")
    void fail_createAuthKey_api_is_disabled(){
      //given
      Member accessMember = getAccessor();
      ApiInfo apiInfo = getApiInfo();
      apiInfo.setApiState(ApiState.DISABLED);

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      ApiException exception = assertThrows(ApiException.class,
          () -> apiPermissionService.createAuthKey(1L, 1L));

      //then
      assertEquals(API_IS_DISABLED, exception.getApiErrorType());
    }

    @Test
    @DisplayName("인증키 생성 실패 - 권한 X")
    void fail_createAuthKey_user_has_not_api(){
      //given
      Member accessMember = getAccessor();
      ApiInfo apiInfo = getApiInfo();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.empty());

      ApiPermissionException exception = assertThrows(ApiPermissionException.class,
          () -> apiPermissionService.createAuthKey(1L, 1L));

      //then
      assertEquals(USER_HAS_NOT_API, exception.getApiPermissionErrorType());
    }

    @Test
    @DisplayName("인증키 생성 실패 - 이미 발급 받음")
    void fail_createAuthKey_api_key_already_issued(){
      //given
      Member accessMember = getAccessor();
      ApiInfo apiInfo = getApiInfo();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.of(getApiUserPermission()));

      given(apiKeyRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.of(getApiKey()));

      ApiPermissionException exception = assertThrows(ApiPermissionException.class,
          () -> apiPermissionService.createAuthKey(1L, 1L));

      //then
      assertEquals(API_KEY_ALREADY_ISSUED, exception.getApiPermissionErrorType());
    }

  }

  @Nested
  @DisplayName("refreshAuthKey() 테스트")
  class refreshAuthKeyTest {

    @Test
    @DisplayName("인증키 재발급 성공")
    void success_refreshAuthKey(){
      //given
      Member accessMember = getAccessor();
      ApiInfo apiInfo = getApiInfo();
      ApiKey apiKey = getApiKey();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.of(getApiUserPermission()));

      given(apiKeyRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.of(apiKey));

      //when
      AuthKeyResponse authKeyResponse = apiPermissionService.refreshAuthKey(1L, 1L);

      //then
      ArgumentCaptor<ApiKey> captor = ArgumentCaptor.forClass(ApiKey.class);
      verify(apiKeyRepository, times(1)).save(captor.capture());

      assertEquals(authKeyResponse.getAuthKey(), captor.getValue().getAuthKey());
    }

    @Test
    @DisplayName("인증키 재발급 실패 - 회원 X")
    void fail_refreshAuthKey_authentication_user_not_found(){
      //given
      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiPermissionService.refreshAuthKey(1L, 1L));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("인증키 재발급 실패 - API X")
    void fail_refreshAuthKey_api_not_found(){
      //given
      Member accessMember = getAccessor();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      ApiException exception = assertThrows(ApiException.class,
          () -> apiPermissionService.refreshAuthKey(1L, 1L));

      //then
      assertEquals(API_NOT_FOUND, exception.getApiErrorType());
    }

    @Test
    @DisplayName("인증키 재발급 실패 - API 비활성화 상태")
    void fail_refreshAuthKey_api_is_disabled(){
      //given
      Member accessMember = getAccessor();
      ApiInfo apiInfo = getApiInfo();
      apiInfo.setApiState(ApiState.DISABLED);
      ApiKey apiKey = getApiKey();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      //when
      ApiException exception = assertThrows(ApiException.class,
          () -> apiPermissionService.refreshAuthKey(1L, 1L));

      //then
      assertEquals(API_IS_DISABLED, exception.getApiErrorType());
    }

    @Test
    @DisplayName("인증키 재발급 실패 - 권한 X")
    void fail_refreshAuthKey_user_has_not_api(){
      //given
      Member accessMember = getAccessor();
      ApiInfo apiInfo = getApiInfo();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.empty());

      //when
      ApiPermissionException exception = assertThrows(ApiPermissionException.class,
          () -> apiPermissionService.refreshAuthKey(1L, 1L));

      //then
      assertEquals(USER_HAS_NOT_API, exception.getApiPermissionErrorType());
    }

    @Test
    @DisplayName("인증키 재발급 실패 - 발급 되지 않은 인증키")
    void fail_refreshAuthKey_api_key_not_issued(){
      //given
      Member accessMember = getAccessor();
      ApiInfo apiInfo = getApiInfo();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.of(getApiUserPermission()));

      given(apiKeyRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.empty());

      //when
      ApiPermissionException exception = assertThrows(ApiPermissionException.class,
          () -> apiPermissionService.refreshAuthKey(1L, 1L));

      //then
      assertEquals(API_KEY_NOT_ISSUED, exception.getApiPermissionErrorType());
    }

  }

}