package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.errortype.ApiErrorType.API_IS_DISABLED;
import static com.jhsfully.domain.type.errortype.ApiErrorType.API_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiInviteErrorType.CANNOT_ASSIGN_INVITE_NOT_TARGET;
import static com.jhsfully.domain.type.errortype.ApiInviteErrorType.CANNOT_INVITE_ALREADY_HAS_PERMISSION;
import static com.jhsfully.domain.type.errortype.ApiInviteErrorType.CANNOT_INVITE_ALREADY_INVITED;
import static com.jhsfully.domain.type.errortype.ApiInviteErrorType.CANNOT_INVITE_NOT_API_OWNER;
import static com.jhsfully.domain.type.errortype.ApiInviteErrorType.CANNOT_REJECT_INVITE_NOT_TARGET;
import static com.jhsfully.domain.type.errortype.ApiInviteErrorType.INVITE_ALREADY_ASSIGN;
import static com.jhsfully.domain.type.errortype.ApiInviteErrorType.INVITE_ALREADY_REJECT;
import static com.jhsfully.domain.type.errortype.ApiInviteErrorType.INVITE_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.jhsfully.api.exception.ApiException;
import com.jhsfully.api.exception.ApiInviteException;
import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.model.PageResponse;
import com.jhsfully.api.model.dto.ApiRequestInviteDto;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiInfoElastic;
import com.jhsfully.domain.entity.ApiRequestInvite;
import com.jhsfully.domain.entity.ApiUserPermission;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.ApiInfoElasticRepository;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.ApiRequestInviteRepository;
import com.jhsfully.domain.repository.ApiUserPermissionRepository;
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
class ApiInviteServiceImplTest {


  @Mock
  private ApiInfoElasticRepository apiInfoElasticRepository;
  @Mock
  private ApiRequestInviteRepository apiRequestInviteRepository;
  @Mock
  private ApiUserPermissionRepository apiUserPermissionRepository;
  @Mock
  private ApiInfoRepository apiInfoRepository;
  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private ApiInviteServiceImpl apiInviteService;

  private Member getOwnerMember(){
    return Member.builder()
        .id(1L)
        .nickname("owner")
        .profileUrl("ownerProfileUrl")
        .email("owner@test.com")
        .build();
  }

  private Member getTargetMember(){
    return Member.builder()
        .id(2L)
        .nickname("target")
        .profileUrl("targetProfileUrl")
        .email("target@test.com")
        .build();
  }

  private ApiInfo getApiInfo(){
    return ApiInfo.builder()
        .id(1L)
        .apiName("테스트데이터")
        .member(getOwnerMember())
        .apiState(ApiState.ENABLED)
        .build();
  }

  private ApiRequestInvite getApiRequestInvite(){
    return ApiRequestInvite.builder()
        .id(1L)
        .apiInfo(getApiInfo())
        .member(getTargetMember())
        .registeredAt(LocalDateTime.of(2023, 10, 30, 9, 30, 1))
        .requestStateType(ApiRequestStateType.REQUEST)
        .apiRequestType(ApiRequestType.INVITE)
        .build();
  }

  @Nested
  @DisplayName("getInviteListForOwner() 테스트")
  class getInviteListForOwnerTest {

    @Test
    @DisplayName("해당 API에 대한 초대 목록 조회 성공")
    void success_getInviteListForOwner(){
      //given
      Member ownerMember = getOwnerMember();
      Member targetMember = getTargetMember();
      ApiInfo apiInfo = getApiInfo();
      ApiRequestInvite apiRequestInvite = getApiRequestInvite();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(apiRequestInviteRepository.findByApiInfoAndApiRequestType(any(), any(), any()))
          .willReturn(new PageImpl<>(List.of(apiRequestInvite)));

      //when
      PageResponse<ApiRequestInviteDto> responseList = apiInviteService.getInviteListForOwner(1L, 1L,
          PageRequest.of(0, 10));

      //then
      assertAll(
          () -> assertEquals(apiRequestInvite.getId(), responseList.getContent().get(0).getId()),
          () -> assertEquals(apiInfo.getId(), responseList.getContent().get(0).getApiInfoId()),
          () -> assertEquals(targetMember.getNickname(), responseList.getContent().get(0).getMemberNickname()),
          () -> assertEquals(apiInfo.getApiName(), responseList.getContent().get(0).getApiName()),
          () -> assertEquals(targetMember.getProfileUrl(), responseList.getContent().get(0).getProfileUrl()),
          () -> assertEquals(apiRequestInvite.getRegisteredAt(), responseList.getContent().get(0).getRegisteredAt()),
          () -> assertEquals(apiRequestInvite.getRequestStateType(), responseList.getContent().get(0).getRequestStateType())
      );
    }

    @Test
    @DisplayName("해당 API에 대한 초대 목록 조회 실패 - 회원 X")
    void fail_getInviteListForOwner_authentication_user_not_found(){
      //given
      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiInviteService.getInviteListForOwner(1L, 1L,
              PageRequest.of(0, 10)));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("해당 API에 대한 초대 목록 조회 실패 - API X")
    void fail_getInviteListForOwner_api_not_found(){
      //given
      Member ownerMember = getOwnerMember();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      ApiException exception = assertThrows(ApiException.class,
          () -> apiInviteService.getInviteListForOwner(1L, 1L,
              PageRequest.of(0, 10)));

      //then
      assertEquals(API_NOT_FOUND, exception.getApiErrorType());
    }

  }

  @Nested
  @DisplayName("getInviteListForMember() 테스트")
  class getInviteListForMemberTest {

    @Test
    @DisplayName("초대 받은 API목록 조회 성공")
    void success_getInviteListForMember(){
      //given
      Member ownerMember = getOwnerMember();
      ApiInfo apiInfo = getApiInfo();
      ApiRequestInvite apiRequestInvite = getApiRequestInvite();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(apiRequestInviteRepository.findByMemberAndApiRequestTypeAndRequestStateType(any(), any(), any(), any()))
          .willReturn(new PageImpl<>(List.of(apiRequestInvite)));

      //when
      PageResponse<ApiRequestInviteDto> responseList = apiInviteService.getInviteListForMember(2L,
          PageRequest.of(0, 10));

      //then
      assertAll(
          () -> assertEquals(apiRequestInvite.getId(), responseList.getContent().get(0).getId()),
          () -> assertEquals(apiInfo.getId(), responseList.getContent().get(0).getApiInfoId()),
          () -> assertEquals(ownerMember.getNickname(), responseList.getContent().get(0).getMemberNickname()),
          () -> assertEquals(apiInfo.getApiName(), responseList.getContent().get(0).getApiName()),
          () -> assertEquals(ownerMember.getProfileUrl(), responseList.getContent().get(0).getProfileUrl()),
          () -> assertEquals(apiRequestInvite.getRegisteredAt(), responseList.getContent().get(0).getRegisteredAt()),
          () -> assertEquals(apiRequestInvite.getRequestStateType(), responseList.getContent().get(0).getRequestStateType())
      );
    }

    @Test
    @DisplayName("초대 받은 API목록 조회 실패 - 회원 X")
    void fail_getInviteListForMember_authentication_user_not_found(){
      //given
      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiInviteService.getInviteListForMember(2L,
              PageRequest.of(0, 10)));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

  }

  @Nested
  @DisplayName("apiInvite() 테스트")
  class apiInviteTest {

    @Test
    @DisplayName("초대 성공")
    void success_apiInvite(){
      //given
      ApiInfo apiInfo = getApiInfo();
      Member ownerMember = getOwnerMember();
      Member targetMember = getTargetMember();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(eq(1L)))
          .willReturn(Optional.of(ownerMember));

      given(memberRepository.findById(eq(2L)))
          .willReturn(Optional.of(targetMember));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.empty());

      given(apiRequestInviteRepository.findByMemberAndApiInfoAndApiRequestTypeAndRequestStateType(any(), any(), any(), any()))
          .willReturn(Optional.empty());

      //when
      apiInviteService.apiInvite(1L, 1L, 2L);

      //then
      ArgumentCaptor<ApiRequestInvite> captor = ArgumentCaptor.forClass(ApiRequestInvite.class);
      verify(apiRequestInviteRepository, times(1)).save(captor.capture());
      ApiRequestInvite expectedInvite = captor.getValue();

      assertAll(
          () -> assertEquals(targetMember.getId(), expectedInvite.getMember().getId()),
          () -> assertEquals(apiInfo.getId(), expectedInvite.getApiInfo().getId()),
          () -> assertEquals(ApiRequestType.INVITE, expectedInvite.getApiRequestType()),
          () -> assertEquals(ApiRequestStateType.REQUEST, expectedInvite.getRequestStateType())
      );

    }

    @Test
    @DisplayName("초대 실패 - API X")
    void fail_apiInvite_api_not_found(){
      //given
      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      ApiException exception = assertThrows(ApiException.class,
          () -> apiInviteService.apiInvite(1L, 1L, 2L));

      //then
      assertEquals(API_NOT_FOUND, exception.getApiErrorType());
    }

    @Test
    @DisplayName("초대 실패 - ownerMember X")
    void fail_apiInvite_owner_member_not_found(){
      //given
      ApiInfo apiInfo = getApiInfo();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(eq(1L)))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiInviteService.apiInvite(1L, 1L, 2L));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("초대 실패 - targetMember X")
    void fail_apiInvite_target_member_not_found(){
      //given
      ApiInfo apiInfo = getApiInfo();
      Member ownerMember = getOwnerMember();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(eq(1L)))
          .willReturn(Optional.of(ownerMember));

      given(memberRepository.findById(eq(2L)))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiInviteService.apiInvite(1L, 1L, 2L));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("초대 실패 - API 비활성화")
    void fail_apiInvite_api_is_disabled(){
      //given
      ApiInfo apiInfo = getApiInfo();
      apiInfo.setApiState(ApiState.DISABLED);

      Member ownerMember = getOwnerMember();
      Member targetMember = getTargetMember();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(eq(1L)))
          .willReturn(Optional.of(ownerMember));

      given(memberRepository.findById(eq(2L)))
          .willReturn(Optional.of(targetMember));

      //when
      ApiException exception = assertThrows(ApiException.class,
          () -> apiInviteService.apiInvite(1L, 1L, 2L));

      //then
      assertEquals(API_IS_DISABLED, exception.getApiErrorType());
    }

    @Test
    @DisplayName("초대 실패 - 소유주 X")
    void fail_apiInvite_cannot_invite_not_api_owner(){
      //given
      ApiInfo apiInfo = getApiInfo();
      Member targetMember = getTargetMember();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(eq(1L)))
          .willReturn(Optional.of(targetMember));

      given(memberRepository.findById(eq(2L)))
          .willReturn(Optional.of(targetMember));

      //when
      ApiInviteException exception = assertThrows(ApiInviteException.class,
          () -> apiInviteService.apiInvite(1L, 1L, 2L));

      //then
      assertEquals(CANNOT_INVITE_NOT_API_OWNER, exception.getApiInviteErrorType());
    }

    @Test
    @DisplayName("초대 실패 - 해당 유저가 이미 권한 소유")
    void fail_apiInvite_cannot_invite_already_has_permission(){
      //given
      ApiInfo apiInfo = getApiInfo();
      Member ownerMember = getOwnerMember();
      Member targetMember = getTargetMember();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(eq(1L)))
          .willReturn(Optional.of(ownerMember));

      given(memberRepository.findById(eq(2L)))
          .willReturn(Optional.of(targetMember));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.of(ApiUserPermission.builder().build()));

      //when
      ApiInviteException exception = assertThrows(ApiInviteException.class,
          () -> apiInviteService.apiInvite(1L, 1L, 2L));

      //then
      assertEquals(CANNOT_INVITE_ALREADY_HAS_PERMISSION, exception.getApiInviteErrorType());
    }

    @Test
    @DisplayName("초대 실패 - 이미 초대된 targetMember")
    void fail_apiInvite_cannot_invite_already_invited(){
      //given
      ApiInfo apiInfo = getApiInfo();
      Member ownerMember = getOwnerMember();
      Member targetMember = getTargetMember();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(eq(1L)))
          .willReturn(Optional.of(ownerMember));

      given(memberRepository.findById(eq(2L)))
          .willReturn(Optional.of(targetMember));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.empty());

      given(apiRequestInviteRepository.findByMemberAndApiInfoAndApiRequestTypeAndRequestStateType(any(), any(), any(), any()))
          .willReturn(Optional.of(ApiRequestInvite.builder().build()));

      //when
      ApiInviteException exception = assertThrows(ApiInviteException.class,
          () -> apiInviteService.apiInvite(1L, 1L, 2L));

      //then
      assertEquals(CANNOT_INVITE_ALREADY_INVITED, exception.getApiInviteErrorType());
    }

  }

  @Nested
  @DisplayName("apiInviteAssign() 테스트")
  class apiInviteAssignTest {

    @Test
    @DisplayName("초대 수락 성공")
    void success_apiInviteAssign(){
      //given
      Member targetMember = getTargetMember();
      ApiRequestInvite invite = getApiRequestInvite();
      ApiInfo apiInfo = getApiInfo();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(targetMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.of(invite));

      given(apiUserPermissionRepository.save(any()))
          .willReturn(ApiUserPermission.builder().id(1L).build());

      //when
      apiInviteService.apiInviteAssign(1L, 1L);

      //then
      ArgumentCaptor<ApiRequestInvite> inviteCaptor = ArgumentCaptor.forClass(ApiRequestInvite.class);
      ArgumentCaptor<ApiUserPermission> permissionCaptor = ArgumentCaptor.forClass(ApiUserPermission.class);
      ArgumentCaptor<ApiInfoElastic> apiElasticCaptor = ArgumentCaptor.forClass(ApiInfoElastic.class);

      verify(apiRequestInviteRepository, times(1)).save(inviteCaptor.capture());
      verify(apiUserPermissionRepository, times(1)).save(permissionCaptor.capture());
      verify(apiInfoElasticRepository, times(1)).save(apiElasticCaptor.capture());

      assertAll(
          () -> assertEquals(ApiRequestStateType.ASSIGN, inviteCaptor.getValue().getRequestStateType()),

          () -> assertEquals(targetMember.getId(), permissionCaptor.getValue().getMember().getId()),
          () -> assertEquals(apiInfo.getId(), permissionCaptor.getValue().getApiInfo().getId()),

          () -> assertEquals(1L, apiElasticCaptor.getValue().getPermissionId()),
          () -> assertEquals(targetMember.getId(), apiElasticCaptor.getValue().getAccessMemberId()),
          () -> assertEquals(apiInfo.getId(), apiElasticCaptor.getValue().getMapping().getParent())
      );

    }

    @Test
    @DisplayName("초대 수락 실패 - 회원 X")
    void fail_apiInviteAssign_authentication_user_not_found(){
      //given
      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiInviteService.apiInviteAssign(1L, 1L));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("초대 수락 실패 - 초대 X")
    void fail_apiInviteAssign_invite_not_found(){
      //given
      Member targetMember = getTargetMember();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(targetMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      ApiInviteException exception = assertThrows(ApiInviteException.class,
          () -> apiInviteService.apiInviteAssign(1L, 1L));

      //then
      assertEquals(INVITE_NOT_FOUND, exception.getApiInviteErrorType());
    }

    @Test
    @DisplayName("초대 수락 실패 - API 비활성화")
    void fail_apiInviteAssign_api_is_disabled(){
      //given
      Member targetMember = getTargetMember();
      ApiRequestInvite invite = getApiRequestInvite();
      invite.getApiInfo().setApiState(ApiState.DISABLED);

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(targetMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.of(invite));

      //when
      ApiException exception = assertThrows(ApiException.class,
          () -> apiInviteService.apiInviteAssign(1L, 1L));

      //then
      assertEquals(API_IS_DISABLED, exception.getApiErrorType());
    }

    @Test
    @DisplayName("초대 수락 실패 - 초대 받은 이와 상이")
    void fail_apiInviteAssign_cannot_assign_invite_not_target(){
      //given
      ApiRequestInvite invite = getApiRequestInvite();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(getOwnerMember()));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.of(invite));

      //when
      ApiInviteException exception = assertThrows(ApiInviteException.class,
          () -> apiInviteService.apiInviteAssign(1L, 1L));

      //then
      assertEquals(CANNOT_ASSIGN_INVITE_NOT_TARGET, exception.getApiInviteErrorType());
    }

    @Test
    @DisplayName("초대 수락 실패 - 이미 수락한 초대")
    void fail_apiInviteAssign_invite_already_assign(){
      //given
      Member targetMember = getTargetMember();
      ApiRequestInvite invite = getApiRequestInvite();
      invite.setRequestStateType(ApiRequestStateType.ASSIGN);

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(targetMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.of(invite));

      //when
      ApiInviteException exception = assertThrows(ApiInviteException.class,
          () -> apiInviteService.apiInviteAssign(1L, 1L));

      //then
      assertEquals(INVITE_ALREADY_ASSIGN, exception.getApiInviteErrorType());
    }

    @Test
    @DisplayName("초대 수락 실패 - 이미 거절한 초대")
    void fail_apiInviteAssign_invite_already_reject(){
      //given
      Member targetMember = getTargetMember();
      ApiRequestInvite invite = getApiRequestInvite();
      invite.setRequestStateType(ApiRequestStateType.REJECT);

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(targetMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.of(invite));

      //when
      ApiInviteException exception = assertThrows(ApiInviteException.class,
          () -> apiInviteService.apiInviteAssign(1L, 1L));

      //then
      assertEquals(INVITE_ALREADY_REJECT, exception.getApiInviteErrorType());
    }

  }

  @Nested
  @DisplayName("apiInviteReject() 테스트")
  class apiInviteRejectTest {
    @Test
    @DisplayName("초대 거절 성공")
    void success_apiInviteReject(){
      //given
      Member targetMember = getTargetMember();
      ApiRequestInvite invite = getApiRequestInvite();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(targetMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.of(invite));

      //when
      apiInviteService.apiInviteReject(1L, 1L);

      //then
      ArgumentCaptor<ApiRequestInvite> inviteCaptor = ArgumentCaptor.forClass(ApiRequestInvite.class);

      verify(apiRequestInviteRepository, times(1)).save(inviteCaptor.capture());

      assertAll(
          () -> assertEquals(ApiRequestStateType.REJECT, inviteCaptor.getValue().getRequestStateType())
      );
    }

    @Test
    @DisplayName("초대 거절 실패 - 회원 X")
    void fail_apiInviteReject_authentication_user_not_found(){
      //given
      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiInviteService.apiInviteReject(1L, 1L));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("초대 거절 실패 - 초대 X")
    void fail_apiInviteReject_invite_not_found(){
      //given
      Member targetMember = getTargetMember();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(targetMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.empty());
      //when
      ApiInviteException exception = assertThrows(ApiInviteException.class,
          () -> apiInviteService.apiInviteReject(1L, 1L));

      //then
      assertEquals(INVITE_NOT_FOUND, exception.getApiInviteErrorType());
    }

    @Test
    @DisplayName("초대 거절 실패 - 초대 받은 이와 상이")
    void fail_apiInviteReject_cannot_reject_invite_not_target(){
      //given
      ApiRequestInvite invite = getApiRequestInvite();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(getOwnerMember()));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.of(invite));
      //when
      ApiInviteException exception = assertThrows(ApiInviteException.class,
          () -> apiInviteService.apiInviteReject(1L, 1L));

      //then
      assertEquals(CANNOT_REJECT_INVITE_NOT_TARGET, exception.getApiInviteErrorType());
    }

    @Test
    @DisplayName("초대 거절 실패 - 이미 수락된 초대")
    void fail_apiInviteReject_invite_already_assign(){
      //given
      Member targetMember = getTargetMember();
      ApiRequestInvite invite = getApiRequestInvite();
      invite.setRequestStateType(ApiRequestStateType.ASSIGN);

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(targetMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.of(invite));
      //when
      ApiInviteException exception = assertThrows(ApiInviteException.class,
          () -> apiInviteService.apiInviteReject(1L, 1L));

      //then
      assertEquals(INVITE_ALREADY_ASSIGN, exception.getApiInviteErrorType());
    }

    @Test
    @DisplayName("초대 거절 실패 - 이미 거절된 초대")
    void fail_apiInviteReject_invite_already_reject(){
      //given
      Member targetMember = getTargetMember();
      ApiRequestInvite invite = getApiRequestInvite();
      invite.setRequestStateType(ApiRequestStateType.REJECT);

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(targetMember));

      given(apiRequestInviteRepository.findById(anyLong()))
          .willReturn(Optional.of(invite));
      //when
      ApiInviteException exception = assertThrows(ApiInviteException.class,
          () -> apiInviteService.apiInviteReject(1L, 1L));

      //then
      assertEquals(INVITE_ALREADY_REJECT, exception.getApiInviteErrorType());
    }
  }

}