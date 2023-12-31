package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.errortype.ApiErrorType.API_IS_DISABLED;
import static com.jhsfully.domain.type.errortype.ApiErrorType.API_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.CANNOT_ASSIGN_REQUEST_NOT_OWNER;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.CANNOT_REJECT_REQUEST_NOT_OWNER;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.CANNOT_REQUEST_ALREADY_REQUESTED;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.CANNOT_REQUEST_API_HAS_PERMISSION;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.CANNOT_REQUEST_API_OWNER;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.CANNOT_REQUEST_BANNED;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.CANNOT_REQUEST_IS_NOT_OPENED;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.REQUEST_ALREADY_ASSIGN;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.REQUEST_ALREADY_REJECT;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.REQUEST_NOT_FOUND;
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
import com.jhsfully.api.exception.ApiRequestException;
import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.model.PageResponse;
import com.jhsfully.api.model.dto.ApiRequestInviteDto;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiInfoElastic;
import com.jhsfully.domain.entity.ApiRequestInvite;
import com.jhsfully.domain.entity.ApiUserPermission;
import com.jhsfully.domain.entity.BlackList;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.ApiInfoElasticRepository;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.ApiRequestInviteRepository;
import com.jhsfully.domain.repository.ApiUserPermissionRepository;
import com.jhsfully.domain.repository.BlackListRepository;
import com.jhsfully.domain.repository.MemberRepository;
import com.jhsfully.domain.type.ApiRequestStateType;
import com.jhsfully.domain.type.ApiRequestType;
import com.jhsfully.domain.type.ApiState;
import java.time.LocalDateTime;
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
class ApiRequestServiceImplTest {

  @Mock
  private ApiInfoElasticRepository apiInfoElasticRepository;
  @Mock
  private MemberRepository memberRepository;
  @Mock
  private ApiInfoRepository apiInfoRepository;
  @Mock
  private ApiRequestInviteRepository apiRequestInviteRepository;
  @Mock
  private ApiUserPermissionRepository apiUserPermissionRepository;
  @Mock
  private BlackListRepository blackListRepository;

  @InjectMocks
  private ApiRequestServiceImpl apiRequestService;

  private Member getOwnerMember(){
    return Member.builder()
        .id(1L)
        .email("owner@test.com")
        .nickname("owner")
        .profileUrl("ownerProfileUrl")
        .build();
  }

  private Member getRequestMember(){
    return Member.builder()
        .id(2L)
        .email("requester@test.com")
        .nickname("requester")
        .profileUrl("requesterProfileUrl")
        .build();
  }

  private ApiInfo getApiInfo(){
    return ApiInfo.builder()
        .id(1L)
        .apiName("테스트데이터")
        .member(getOwnerMember())
        .apiState(ApiState.ENABLED)
        .isPublic(true)
        .build();
  }

  private ApiRequestInvite getApiRequestInvite(){
    return ApiRequestInvite.builder()
        .id(1L)
        .apiInfo(getApiInfo())
        .member(getRequestMember())
        .registeredAt(LocalDateTime.of(2023, 10, 30, 9, 30, 1))
        .requestStateType(ApiRequestStateType.REQUEST)
        .apiRequestType(ApiRequestType.REQUEST)
        .build();
  }

  @Nested
  @DisplayName("getRequestListForMember() 테스트")
  class getRequestListForMemberTest {

    @Test
    @DisplayName("회원이 요청한 목록 조회 성공")
    void success_getRequestListForMember(){
      //given
      Member requestMember = getRequestMember();
      Member ownerMember = getOwnerMember();
      ApiRequestInvite apiRequestInvite = getApiRequestInvite();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(requestMember));

      given(apiRequestInviteRepository.findByMemberAndApiRequestType(any(), any(), any()))
          .willReturn(new PageImpl<>(List.of(apiRequestInvite)));

      //when
      PageResponse<ApiRequestInviteDto> responseList = apiRequestService.getRequestListForMember(1L,
          PageRequest.of(0, 10));

      //then
      assertAll(
          () -> assertEquals(apiRequestInvite.getId(), responseList.getContent().get(0).getId()),
          () -> assertEquals(apiRequestInvite.getApiInfo().getId(), responseList.getContent().get(0).getApiInfoId()),
          () -> assertEquals(ownerMember.getNickname(), responseList.getContent().get(0).getMemberNickname()),
          () -> assertEquals(apiRequestInvite.getApiInfo().getApiName(), responseList.getContent().get(0).getApiName()),
          () -> assertEquals(ownerMember.getProfileUrl(), responseList.getContent().get(0).getProfileUrl()),
          () -> assertEquals(apiRequestInvite.getRegisteredAt(), responseList.getContent().get(0).getRegisteredAt()),
          () -> assertEquals(apiRequestInvite.getRequestStateType(), responseList.getContent().get(0).getRequestStateType())
      );

    }

    @Test
    @DisplayName("회원이 요청한 목록 조회 실패 - 회원 X")
    void fail_getRequestListForMember_authentication_user_not_found(){
      //given
      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiRequestService.getRequestListForMember(1L,
              PageRequest.of(0, 10)));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

  }

  @Nested
  @DisplayName("getRequestListForOwner() 테스트")
  class getRequestListForOwnerTest {

    @Test
    @DisplayName("API에 대한 요청 목록 조회 성공")
    void success_getRequestListForOwner(){
      //given
      Member requestMember = getRequestMember();
      Member ownerMember = getOwnerMember();
      ApiInfo apiInfo = getApiInfo();
      ApiRequestInvite apiRequestInvite = getApiRequestInvite();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(apiRequestInviteRepository.findByApiInfoAndApiRequestTypeAndRequestStateType(any(), any(), any(), any()))
          .willReturn(new PageImpl<>(List.of(apiRequestInvite)));

      //when
      PageResponse<ApiRequestInviteDto> responseList = apiRequestService.getRequestListForOwner(1L,
          1L, PageRequest.of(0, 10));

      //then
      assertAll(
          () -> assertEquals(apiRequestInvite.getId(), responseList.getContent().get(0).getId()),
          () -> assertEquals(apiRequestInvite.getApiInfo().getId(), responseList.getContent().get(0).getApiInfoId()),
          () -> assertEquals(requestMember.getNickname(), responseList.getContent().get(0).getMemberNickname()),
          () -> assertEquals(apiRequestInvite.getApiInfo().getApiName(), responseList.getContent().get(0).getApiName()),
          () -> assertEquals(requestMember.getProfileUrl(), responseList.getContent().get(0).getProfileUrl()),
          () -> assertEquals(apiRequestInvite.getRegisteredAt(), responseList.getContent().get(0).getRegisteredAt()),
          () -> assertEquals(apiRequestInvite.getRequestStateType(), responseList.getContent().get(0).getRequestStateType())
      );
    }

    @Test
    @DisplayName("API에 대한 요청 목록 조회 실패 - 회원 X")
    void fail_getRequestListForOwner_authentication_user_not_found(){
      //given
      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiRequestService.getRequestListForOwner(1L,
              1L, PageRequest.of(0, 10)));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("API에 대한 요청 목록 조회 실패 - API X")
    void fail_getRequestListForOwner_api_not_found(){
      //given
      Member ownerMember = getOwnerMember();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      ApiException exception = assertThrows(ApiException.class,
          () -> apiRequestService.getRequestListForOwner(1L,
              1L, PageRequest.of(0, 10)));

      //then
      assertEquals(API_NOT_FOUND, exception.getApiErrorType());
    }

  }

  @Nested
  @DisplayName("apiRequest() 테스트")
  class apiRequestTest {

    @Test
    @DisplayName("API 사용 요청 성공")
    void success_apiRequest(){
      //given
      Member requestMember = getRequestMember();
      ApiInfo apiInfo = getApiInfo();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(requestMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.empty());

      given(blackListRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.empty());

      given(apiRequestInviteRepository.findByMemberAndApiInfoAndApiRequestTypeAndRequestStateType(
          any(), any(), any(), any()
      )).willReturn(Optional.empty());

      //when
      apiRequestService.apiRequest(1L, 1L);

      //then
      ArgumentCaptor<ApiRequestInvite> captor = ArgumentCaptor.forClass(ApiRequestInvite.class);
      verify(apiRequestInviteRepository, times(1)).save(captor.capture());

      assertAll(
          () -> assertEquals(apiInfo.getId(), captor.getValue().getApiInfo().getId()),
          () -> assertEquals(requestMember.getId(), captor.getValue().getMember().getId()),
          () -> assertEquals(ApiRequestType.REQUEST, captor.getValue().getApiRequestType()),
          () -> assertEquals(ApiRequestStateType.REQUEST, captor.getValue().getRequestStateType())
      );

    }

    @Test
    @DisplayName("API 사용 요청 실패 - 회원 X")
    void fail_apiRequest_authentication_user_not_found(){
      //given
      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiRequestService.apiRequest(1L, 1L));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("API 사용 요청 실패 - API X")
    void fail_apiRequest_api_not_found(){
      //given
      Member requestMember = getRequestMember();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(requestMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      ApiException exception = assertThrows(ApiException.class,
          () -> apiRequestService.apiRequest(1L, 1L));

      //then
      assertEquals(API_NOT_FOUND, exception.getApiErrorType());
    }

    @Test
    @DisplayName("API 사용 요청 실패 - API 비활성화 상태")
    void fail_apiRequest_api_is_disabled(){
      //given
      Member requestMember = getRequestMember();
      ApiInfo apiInfo = getApiInfo();
      apiInfo.setApiState(ApiState.DISABLED);

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(requestMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));
      //when
      ApiException exception = assertThrows(ApiException.class,
          () -> apiRequestService.apiRequest(1L, 1L));

      //then
      assertEquals(API_IS_DISABLED, exception.getApiErrorType());
    }

    @Test
    @DisplayName("API 사용 요청 실패 - API가 비공개 상태")
    void fail_apiRequest_cannot_request_is_not_opened(){
      //given
      Member requestMember = getRequestMember();
      ApiInfo apiInfo = getApiInfo();
      apiInfo.setPublic(false);

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(requestMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      //when
      ApiRequestException exception = assertThrows(ApiRequestException.class,
          () -> apiRequestService.apiRequest(1L, 1L));

      //then
      assertEquals(CANNOT_REQUEST_IS_NOT_OPENED, exception.getApiRequestErrorType());
    }

    @Test
    @DisplayName("API 사용 요청 실패 - API 소유주는 신청 불가능")
    void fail_apiRequest_cannot_request_api_owner(){
      //given
      Member ownerMember = getOwnerMember();
      ApiInfo apiInfo = getApiInfo();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      //when
      ApiRequestException exception = assertThrows(ApiRequestException.class,
          () -> apiRequestService.apiRequest(1L, 1L));

      //then
      assertEquals(CANNOT_REQUEST_API_OWNER, exception.getApiRequestErrorType());
    }

    @Test
    @DisplayName("API 사용 요청 실패 - 이미 권한을 가지고 있음")
    void fail_apiRequest_cannot_request_api_has_permission(){
      //given
      Member requestMember = getRequestMember();
      ApiInfo apiInfo = getApiInfo();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(requestMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.of(ApiUserPermission.builder().build()));

      //when
      ApiRequestException exception = assertThrows(ApiRequestException.class,
          () -> apiRequestService.apiRequest(1L, 1L));

      //then
      assertEquals(CANNOT_REQUEST_API_HAS_PERMISSION, exception.getApiRequestErrorType());
    }

    @Test
    @DisplayName("API 사용 요청 실패 - 블랙리스트에 추가된 회원")
    void fail_apiRequest_cannot_request_banned(){
      //given
      Member requestMember = getRequestMember();
      ApiInfo apiInfo = getApiInfo();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(requestMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.empty());

      given(blackListRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.of(BlackList.builder().build()));

      //when
      ApiRequestException exception = assertThrows(ApiRequestException.class,
          () -> apiRequestService.apiRequest(1L, 1L));

      //then
      assertEquals(CANNOT_REQUEST_BANNED, exception.getApiRequestErrorType());
    }

    @Test
    @DisplayName("API 사용 요청 실패 - 이미 보낸 요청이 있음")
    void fail_apiRequest_cannot_request_already_requested(){
      //given
      Member requestMember = getRequestMember();
      ApiInfo apiInfo = getApiInfo();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(requestMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.empty());

      given(blackListRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.empty());

      given(apiRequestInviteRepository.findByMemberAndApiInfoAndApiRequestTypeAndRequestStateType(
          any(), any(), any(), any()
      )).willReturn(Optional.of(getApiRequestInvite()));

      //when
      ApiRequestException exception = assertThrows(ApiRequestException.class,
          () -> apiRequestService.apiRequest(1L, 1L));

      //then
      assertEquals(CANNOT_REQUEST_ALREADY_REQUESTED, exception.getApiRequestErrorType());
    }

  }

  @Nested
  @DisplayName("apiRequestAssign() 테스트")
  class apiRequestAssignTest {

    @Test
    @DisplayName("API 요청 수락 성공")
    void success_apiRequestAssign(){
      //given
      Member ownerMember = getOwnerMember();
      ApiRequestInvite apiRequestInvite = getApiRequestInvite();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.of(apiRequestInvite));

      given(apiUserPermissionRepository.save(any()))
          .willReturn(ApiUserPermission.builder().id(1L).build());

      //when
      apiRequestService.apiRequestAssign(1L, 1L);

      //then
      ArgumentCaptor<ApiRequestInvite> requestCaptor = ArgumentCaptor.forClass(ApiRequestInvite.class);
      ArgumentCaptor<ApiUserPermission> permissionCaptor = ArgumentCaptor.forClass(ApiUserPermission.class);
      ArgumentCaptor<ApiInfoElastic> apiElasticCaptor = ArgumentCaptor.forClass(ApiInfoElastic.class);

      verify(apiRequestInviteRepository, times(1)).save(requestCaptor.capture());
      verify(apiUserPermissionRepository, times(1)).save(permissionCaptor.capture());
      verify(apiInfoElasticRepository, times(1)).save(apiElasticCaptor.capture());

      assertAll(
          () -> assertEquals(ApiRequestStateType.ASSIGN, requestCaptor.getValue().getRequestStateType()),

          () -> assertEquals(apiRequestInvite.getApiInfo().getId(), permissionCaptor.getValue().getApiInfo().getId()),
          () -> assertEquals(apiRequestInvite.getMember().getId(), permissionCaptor.getValue().getMember().getId()),

          () -> assertEquals(1L, apiElasticCaptor.getValue().getPermissionId()),
          () -> assertEquals(apiRequestInvite.getMember().getId(), apiElasticCaptor.getValue().getAccessMemberId()),
          () -> assertEquals(apiRequestInvite.getApiInfo().getId(), apiElasticCaptor.getValue().getMapping().getParent())
      );

    }

    @Test
    @DisplayName("API 요청 수락 실패 - 회원 X")
    void fail_apiRequestAssign_authentication_user_not_found(){
      //given
      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiRequestService.apiRequestAssign(1L, 1L));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("API 요청 수락 실패 - 요청 X")
    void fail_apiRequestAssign_request_not_found(){
      //given
      Member ownerMember = getOwnerMember();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      ApiRequestException exception = assertThrows(ApiRequestException.class,
          () -> apiRequestService.apiRequestAssign(1L, 1L));

      //then
      assertEquals(REQUEST_NOT_FOUND, exception.getApiRequestErrorType());
    }

    @Test
    @DisplayName("API 요청 수락 실패 - API 비활성화 상태")
    void fail_apiRequestAssign_api_is_disabled(){
      //given
      Member ownerMember = getOwnerMember();
      ApiRequestInvite apiRequestInvite = getApiRequestInvite();
      apiRequestInvite.getApiInfo().setApiState(ApiState.DISABLED);

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.of(apiRequestInvite));

      //when
      ApiException exception = assertThrows(ApiException.class,
          () -> apiRequestService.apiRequestAssign(1L, 1L));

      //then
      assertEquals(API_IS_DISABLED, exception.getApiErrorType());
    }

    @Test
    @DisplayName("API 요청 수락 실패 - API 소유주가 아님")
    void fail_apiRequestAssign_cannot_assign_request_not_owner(){
      //given
      Member requestMember = getRequestMember();
      ApiRequestInvite apiRequestInvite = getApiRequestInvite();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(requestMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.of(apiRequestInvite));

      //when
      ApiRequestException exception = assertThrows(ApiRequestException.class,
          () -> apiRequestService.apiRequestAssign(1L, 1L));

      //then
      assertEquals(CANNOT_ASSIGN_REQUEST_NOT_OWNER, exception.getApiRequestErrorType());
    }

    @Test
    @DisplayName("API 요청 수락 실패 - 이미 거절된 요청")
    void fail_apiRequestAssign_request_already_reject(){
      //given
      Member ownerMember = getOwnerMember();
      ApiRequestInvite apiRequestInvite = getApiRequestInvite();
      apiRequestInvite.setRequestStateType(ApiRequestStateType.REJECT);

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.of(apiRequestInvite));

      //when
      ApiRequestException exception = assertThrows(ApiRequestException.class,
          () -> apiRequestService.apiRequestAssign(1L, 1L));

      //then
      assertEquals(REQUEST_ALREADY_REJECT, exception.getApiRequestErrorType());
    }

    @Test
    @DisplayName("API 요청 수락 실패 - 이미 수락된 요청")
    void fail_apiRequestAssign_request_already_assign(){
      //given
      Member ownerMember = getOwnerMember();
      ApiRequestInvite apiRequestInvite = getApiRequestInvite();
      apiRequestInvite.setRequestStateType(ApiRequestStateType.ASSIGN);

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.of(apiRequestInvite));

      //when
      ApiRequestException exception = assertThrows(ApiRequestException.class,
          () -> apiRequestService.apiRequestAssign(1L, 1L));

      //then
      assertEquals(REQUEST_ALREADY_ASSIGN, exception.getApiRequestErrorType());
    }

  }

  @Nested
  @DisplayName("apiRequestReject() 테스트")
  class apiRequestRejectTest {

    @Test
    @DisplayName("API 요청 거절 성공")
    void success_apiRequestReject(){
      //given
      Member ownerMember = getOwnerMember();
      ApiRequestInvite apiRequestInvite = getApiRequestInvite();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.of(apiRequestInvite));

      //when
      apiRequestService.apiRequestReject(1L, 1L);

      //then
      ArgumentCaptor<ApiRequestInvite> captor = ArgumentCaptor.forClass(ApiRequestInvite.class);
      verify(apiRequestInviteRepository, times(1)).save(captor.capture());

      assertEquals(ApiRequestStateType.REJECT, captor.getValue().getRequestStateType());
    }

    @Test
    @DisplayName("API 요청 거절 실패 - 회원 X")
    void fail_apiRequestReject_authentication_user_not_found(){
      //given
      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiRequestService.apiRequestReject(1L, 1L));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("API 요청 거절 실패 - 요청 X")
    void fail_apiRequestReject_request_not_found(){
      //given
      Member ownerMember = getOwnerMember();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      ApiRequestException exception = assertThrows(ApiRequestException.class,
          () -> apiRequestService.apiRequestReject(1L, 1L));

      //then
      assertEquals(REQUEST_NOT_FOUND, exception.getApiRequestErrorType());
    }

    @Test
    @DisplayName("API 요청 거절 실패 - API 소유주가 아님")
    void fail_apiRequestReject_cannot_reject_request_not_owner(){
      //given
      Member requestMember = getRequestMember();
      ApiRequestInvite apiRequestInvite = getApiRequestInvite();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(requestMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.of(apiRequestInvite));

      //when
      ApiRequestException exception = assertThrows(ApiRequestException.class,
          () -> apiRequestService.apiRequestReject(1L, 1L));

      //then
      assertEquals(CANNOT_REJECT_REQUEST_NOT_OWNER, exception.getApiRequestErrorType());
    }

    @Test
    @DisplayName("API 요청 거절 실패 - 이미 거절된 요청")
    void fail_apiRequestReject_request_already_reject(){
      //given
      Member ownerMember = getOwnerMember();
      ApiRequestInvite apiRequestInvite = getApiRequestInvite();
      apiRequestInvite.setRequestStateType(ApiRequestStateType.REJECT);

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.of(apiRequestInvite));

      //when
      ApiRequestException exception = assertThrows(ApiRequestException.class,
          () -> apiRequestService.apiRequestReject(1L, 1L));

      //then
      assertEquals(REQUEST_ALREADY_REJECT, exception.getApiRequestErrorType());
    }

    @Test
    @DisplayName("API 요청 거절 실패 - 이미 수락된 요청")
    void fail_apiRequestReject_request_already_assign(){
      Member ownerMember = getOwnerMember();
      ApiRequestInvite apiRequestInvite = getApiRequestInvite();
      apiRequestInvite.setRequestStateType(ApiRequestStateType.ASSIGN);

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.of(apiRequestInvite));

      //when
      ApiRequestException exception = assertThrows(ApiRequestException.class,
          () -> apiRequestService.apiRequestReject(1L, 1L));

      //then
      assertEquals(REQUEST_ALREADY_ASSIGN, exception.getApiRequestErrorType());
    }

  }

}