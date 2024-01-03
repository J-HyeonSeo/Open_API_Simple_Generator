package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.model.dto.MemberSearchDto;
import com.jhsfully.api.model.dto.ProfileDto;
import com.jhsfully.domain.entity.Grade;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private MemberServiceImpl memberService;

    private Member getMember() {
        return Member.builder()
            .id(1L)
            .nickname("nickname")
            .email("test@test.com")
            .profileUrl("profileUrl")
            .grade(Grade.builder()
                .id(1L)
                .gradeName("골드")
                .build())
            .build();
    }

    @Nested
    @DisplayName("getProfile() 테스트")
    class getProfileTest {
        @Test
        @DisplayName("프로필 조회 성공")
        void success_getProfile() {
            //given
            Member member = getMember();
            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

            //when
            ProfileDto profileDto = memberService.getProfile(1L);

            //then
            assertAll(
                () -> assertEquals(member.getId(), profileDto.getMemberId()),
                () -> assertEquals(member.getGrade().getId(), profileDto.getGradeId()),
                () -> assertEquals(member.getNickname(), profileDto.getNickname()),
                () -> assertEquals(member.getEmail(), profileDto.getEmail()),
                () -> assertEquals(member.getProfileUrl(), profileDto.getProfileUrl())
            );
        }

        @Test
        @DisplayName("프로필 조회 실패 - 회원 X")
        void fail_getProfile_authentication_user_not_found() {
            //given
            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.empty());

            //when
            AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> memberService.getProfile(1L));

            //then
            assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
        }
    }

    @Nested
    @DisplayName("memberSearch() 테스트")
    class memberSearchTest {
        @Test
        @DisplayName("회원 검색 성공")
        void success_memberSearch() {
            //given
            Member member = getMember();
            given(memberRepository.findByEmail("test@test.com"))
                .willReturn(Optional.of(member));

            //when
            MemberSearchDto memberSearchDto = memberService.memberSearch("test@test.com");

            //then
            assertAll(
                () -> assertEquals(member.getId(), memberSearchDto.getMemberId()),
                () -> assertEquals(member.getNickname(), memberSearchDto.getMemberNickname()),
                () -> assertEquals(member.getEmail(), memberSearchDto.getMemberEmail()),
                () -> assertEquals(member.getProfileUrl(), memberSearchDto.getProfileUrl())
            );
        }

        @Test
        @DisplayName("회원 검색 실패 - 회원 X")
        void fail_memberSearch_authentication_user_not_found() {
            //given
            given(memberRepository.findByEmail("test@test.com"))
                .willReturn(Optional.empty());

            //when
            AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> memberService.memberSearch("test@test.com"));

            //then
            assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
        }
    }

    @Nested
    @DisplayName("changeNickname() 테스트")
    class changeNicknameTest {
        @Test
        @DisplayName("닉네임 변경 성공")
        void success_changeNickname() {
            //given
            Member member = getMember();
            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

            //when
            memberService.changeNickname(1L, "UPDATED");

            //then
            ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
            verify(memberRepository, times(1)).save(captor.capture());
            assertEquals("UPDATED", captor.getValue().getNickname());
        }

        @Test
        @DisplayName("닉네임 변경 실패 - 회원 X")
        void fail_changeNickname_authentication_user_not_found() {
            //given
            given(memberRepository.findById(anyLong()))
                .willReturn(Optional.empty());

            //when
            AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> memberService.changeNickname(1L, "UPDATED"));

            //then
            assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
        }
    }

}