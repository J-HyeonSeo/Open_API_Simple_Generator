package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.errortype.ApiBlackListErrorType.ALREADY_REGISTERED_TARGET;
import static com.jhsfully.domain.type.errortype.ApiBlackListErrorType.BLACKLIST_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiBlackListErrorType.CANNOT_DELETE_NOT_OWNER;
import static com.jhsfully.domain.type.errortype.ApiBlackListErrorType.CANNOT_REGISTER_NOT_OWNER;
import static com.jhsfully.domain.type.errortype.ApiBlackListErrorType.CANNOT_REGISTER_TARGET_IS_OWNER;
import static com.jhsfully.domain.type.errortype.ApiErrorType.API_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.USER_HAS_NOT_API;
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

import com.jhsfully.api.exception.ApiBlackListException;
import com.jhsfully.api.exception.ApiException;
import com.jhsfully.api.exception.ApiPermissionException;
import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.model.PageResponse;
import com.jhsfully.api.model.dto.BlackListDto;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.BlackList;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.BlackListRepository;
import com.jhsfully.domain.repository.MemberRepository;
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
class ApiBlackListServiceImplTest {

  @Mock
  private BlackListRepository blackListRepository;
  @Mock
  private ApiInfoRepository apiInfoRepository;
  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private ApiBlackListServiceImpl apiBlackListService;

  private Member getTargetMember() {
    return Member.builder()
        .id(3L)
        .build();
  }

  private Member getMember() {
    return Member.builder()
        .id(1L)
        .build();
  }

  private ApiInfo getApiInfo() {
    return ApiInfo.builder()
        .id(1L)
        .member(getMember())
        .build();
  }

  private BlackList getBlackList(){
    return BlackList.builder()
        .id(1L)
        .apiInfo(getApiInfo())
        .member(getTargetMember())
        .build();
  }

  @Nested
  @DisplayName("getBlackList() 테스트")
  class getBlackListTest {

    @Test
    @DisplayName("API에 대한 블랙리스트 유저 가져오기 성공")
    void success_getBlackList() {
      //given
      Member customer = Member.builder()
          .id(2L)
          .email("customer@test.com")
          .profileUrl("profileUrl")
          .nickname("customer")
          .build();

      Member member = getMember();
      ApiInfo apiInfo = getApiInfo();
      BlackList blackList = BlackList.builder()
          .id(1L)
          .apiInfo(apiInfo)
          .member(customer)
          .registeredAt(LocalDateTime.of(2023, 10,
              28, 9, 31, 20))
          .build();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));
      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(member));
      given(blackListRepository.findByApiInfo(any(), any()))
          .willReturn(new PageImpl<>(
              List.of(
                  blackList
              )
          ));
      //when
      PageResponse<BlackListDto> responseList = apiBlackListService.getBlackList(
          1L, 1L, PageRequest.of(0, 10)
      );

      //then
      assertAll(
          () -> assertEquals(blackList.getId(), responseList.getContent().get(0).getId()),
          () -> assertEquals(customer.getId(), responseList.getContent().get(0).getMemberId()),
          () -> assertEquals(customer.getNickname(), responseList.getContent().get(0).getMemberNickname()),
          () -> assertEquals(customer.getProfileUrl(), responseList.getContent().get(0).getProfileUrl()),
          () -> assertEquals(blackList.getRegisteredAt(), responseList.getContent().get(0).getRegisteredAt())
      );
    }

    @Test
    @DisplayName("API에 대한 블랙리스트 유저 가져오기 실패 - API X")
    void fail_getBlackList_api_not_found() {
      //given
      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.empty());
      //when
      ApiException exception = assertThrows(ApiException.class,
          () -> apiBlackListService.getBlackList(1L, 1L, PageRequest.of(0, 10)));

      //then
      assertEquals(API_NOT_FOUND, exception.getApiErrorType());
    }

    @Test
    @DisplayName("API에 대한 블랙리스트 유저 가져오기 실패 - member X")
    void fail_getBlackList_authentication_user_not_found() {
      //given
      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(getApiInfo()));
      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());
      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiBlackListService.getBlackList(1L, 1L, PageRequest.of(0, 10)));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("API에 대한 블랙리스트 유저 가져오기 실패 - API와 Member가 매칭 X")
    void fail_getBlackList_user_has_not_api() {
      //given
      Member customer = Member.builder()
          .id(2L)
          .email("customer@test.com")
          .build();

      ApiInfo apiInfo = getApiInfo();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));
      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(customer));

      //when
      ApiPermissionException exception = assertThrows(ApiPermissionException.class,
          () -> apiBlackListService.getBlackList(1L, 1L, PageRequest.of(0, 10)));

      //then
      assertEquals(USER_HAS_NOT_API, exception.getApiPermissionErrorType());
    }

  }

  @Nested
  @DisplayName("registerBlackList() 테스트")
  class RegisterBlackListTest {

    @Test
    @DisplayName("블랙리스트 등록 성공")
    void success_registerBlackList(){
      //given
      ApiInfo apiInfo = getApiInfo();
      Member ownerMember = getMember();
      Member targetMember = getTargetMember();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(eq(1L)))
          .willReturn(Optional.of(ownerMember));

      given(memberRepository.findById(eq(targetMember.getId())))
          .willReturn(Optional.of(targetMember));

      given(blackListRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.empty());

      //when

      LocalDateTime nowTime = LocalDateTime.now();

      apiBlackListService.registerBlackList(1L, 1L, targetMember.getId(),
          nowTime);

      //then
      ArgumentCaptor<BlackList> captor = ArgumentCaptor.forClass(BlackList.class);
      verify(blackListRepository, times(1)).save(captor.capture());

      BlackList expectedBlackList = captor.getValue();

      assertAll(
          () -> assertEquals(targetMember.getId(), expectedBlackList.getMember().getId()),
          () -> assertEquals(apiInfo.getId(), expectedBlackList.getApiInfo().getId()),
          () -> assertEquals(targetMember.getId(), expectedBlackList.getMember().getId()),
          () -> assertEquals(nowTime, expectedBlackList.getRegisteredAt())
      );

    }

    @Test
    @DisplayName("블랙리스트 등록 실패 - API X")
    void fail_registerBlackList_api_not_found(){
      //given
      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      ApiException exception = assertThrows(ApiException.class,
          () -> apiBlackListService.registerBlackList(1L, 1L, 3L,
              LocalDateTime.now()));

      //then
      assertEquals(API_NOT_FOUND, exception.getApiErrorType());

    }

    @Test
    @DisplayName("블랙리스트 등록 실패 - owner member X")
    void fail_registerBlackList_owner_member_not_found(){
      //given
      ApiInfo apiInfo = getApiInfo();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(eq(1L)))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiBlackListService.registerBlackList(1L, 1L, 3L,
              LocalDateTime.now()));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("블랙리스트 등록 실패 - target member X")
    void fail_registerBlackList_target_member_not_found(){
      //given
      ApiInfo apiInfo = getApiInfo();
      Member ownerMember = getMember();
      Member targetMember = getTargetMember();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(eq(1L)))
          .willReturn(Optional.of(ownerMember));

      given(memberRepository.findById(eq(targetMember.getId())))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiBlackListService.registerBlackList(1L, 1L, 3L,
              LocalDateTime.now()));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("블랙리스트 등록 실패 - API 소유주 X")
    void fail_registerBlackList_cannot_register_not_owner(){
      //given
      ApiInfo apiInfo = getApiInfo();
      Member ownerMember = getMember();
      Member targetMember = getTargetMember();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(eq(1L)))
          .willReturn(Optional.of(targetMember));

      given(memberRepository.findById(eq(targetMember.getId())))
          .willReturn(Optional.of(ownerMember));

      //when
      ApiBlackListException exception = assertThrows(ApiBlackListException.class,
          () -> apiBlackListService.registerBlackList(1L, 1L, 3L,
              LocalDateTime.now()));

      //then
      assertEquals(CANNOT_REGISTER_NOT_OWNER, exception.getApiBlackListErrorType());
    }

    @Test
    @DisplayName("블랙리스트 등록 실패 - 소유주를 블랙리스트로 등록")
    void fail_registerBlackList_cannot_register_target_is_owner(){
      //given
      ApiInfo apiInfo = getApiInfo();
      Member ownerMember = getMember();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(eq(1L)))
          .willReturn(Optional.of(ownerMember));

      //when
      ApiBlackListException exception = assertThrows(ApiBlackListException.class,
          () -> apiBlackListService.registerBlackList(1L, 1L, 1L,
              LocalDateTime.now()));

      //then
      assertEquals(CANNOT_REGISTER_TARGET_IS_OWNER, exception.getApiBlackListErrorType());
    }

    @Test
    @DisplayName("블랙리스트 등록 실패 - 이미 등록된 target")
    void fail_registerBlackList_already_registered_target(){
      //given
      ApiInfo apiInfo = getApiInfo();
      Member ownerMember = getMember();
      Member targetMember = getTargetMember();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(eq(1L)))
          .willReturn(Optional.of(ownerMember));

      given(memberRepository.findById(eq(targetMember.getId())))
          .willReturn(Optional.of(targetMember));

      given(blackListRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.of(BlackList.builder().build()));

      //when
      ApiBlackListException exception = assertThrows(ApiBlackListException.class,
          () -> apiBlackListService.registerBlackList(1L, 1L, 3L,
              LocalDateTime.now()));

      //then
      assertEquals(ALREADY_REGISTERED_TARGET, exception.getApiBlackListErrorType());
    }

  }

  @Nested
  @DisplayName("deleteBlackList() 테스트")
  class DeleteBlackListTest {

    @Test
    @DisplayName("블랙리스트 삭제 성공")
    void success_deleteBlackList(){
      //given
      BlackList blackList = getBlackList();
      Member ownerMember = getMember();

      given(blackListRepository.findById(anyLong()))
          .willReturn(Optional.of(blackList));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));
      //when
      apiBlackListService.deleteBlackList(1L, 1L);

      //then
      ArgumentCaptor<BlackList> captor = ArgumentCaptor.forClass(BlackList.class);
      verify(blackListRepository, times(1)).delete(captor.capture());

      assertAll(
          () -> assertEquals(blackList.getId(), captor.getValue().getId()),
          () -> assertEquals(blackList.getApiInfo().getId(), captor.getValue().getApiInfo().getId()),
          () -> assertEquals(blackList.getMember().getId(), captor.getValue().getMember().getId())
      );
    }

    @Test
    @DisplayName("블랙리스트 삭제 실패 - 블랙리스트 X")
    void fail_deleteBlackList_blackList_not_found(){
      //given
      given(blackListRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      ApiBlackListException exception = assertThrows(ApiBlackListException.class,
          () -> apiBlackListService.deleteBlackList(1L, 1L));

      //then
      assertEquals(BLACKLIST_NOT_FOUND, exception.getApiBlackListErrorType());
    }

    @Test
    @DisplayName("블랙리스트 삭제 실패 - 회원 X")
    void fail_deleteBlackList_authentication_user_not_found(){
      //given
      BlackList blackList = getBlackList();

      given(blackListRepository.findById(anyLong()))
          .willReturn(Optional.of(blackList));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());
      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiBlackListService.deleteBlackList(1L, 1L));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

    @Test
    @DisplayName("블랙리스트 삭제 실패 - 소유주 X")
    void fail_deleteBlackList_cannot_delete_not_owner(){
      //given
      BlackList blackList = getBlackList();

      given(blackListRepository.findById(anyLong()))
          .willReturn(Optional.of(blackList));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(getTargetMember()));
      //when
      ApiBlackListException exception = assertThrows(ApiBlackListException.class,
          () -> apiBlackListService.deleteBlackList(1L, 1L));

      //then
      assertEquals(CANNOT_DELETE_NOT_OWNER, exception.getApiBlackListErrorType());
    }

  }


}