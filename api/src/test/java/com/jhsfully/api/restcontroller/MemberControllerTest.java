package com.jhsfully.api.restcontroller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhsfully.api.model.dto.MemberSearchDto;
import com.jhsfully.api.model.dto.ProfileDto;
import com.jhsfully.api.model.member.NicknameChangeInput;
import com.jhsfully.api.security.SecurityConfiguration;
import com.jhsfully.api.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = MemberController.class, excludeFilters =
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfiguration.class}))
class MemberControllerTest {

    @MockBean
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private final static long TEST_ID = -1L;

    @Test
    void getProfile() throws Exception {
        //given
        given(memberService.getProfile(anyLong()))
            .willReturn(ProfileDto.builder()
                .memberId(1L)
                .profileUrl("profileUrl")
                .gradeId(1L)
                .email("member@test.com")
                .nickname("member")
                .build());
        //when
        ResultActions perform = mockMvc.perform(get("/member/profile").with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.memberId").value(1),
                jsonPath("$.gradeId").value(1),
                jsonPath("$.profileUrl").value("profileUrl"),
                jsonPath("$.email").value("member@test.com"),
                jsonPath("$.nickname").value("member")
            );
    }

    @Test
    void memberSearch() throws Exception {
        //given
        given(memberService.memberSearch("member@test.com"))
            .willReturn(MemberSearchDto.builder()
                .memberId(1L)
                .memberEmail("member@test.com")
                .memberNickname("member")
                .profileUrl("profileUrl")
                .build());
        //when
        ResultActions perform = mockMvc.perform(get("/member/search?email=member@test.com").with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.memberId").value(1),
                jsonPath("$.memberEmail").value("member@test.com"),
                jsonPath("$.memberNickname").value("member"),
                jsonPath("$.profileUrl").value("profileUrl")
            );
    }

    @Test
    void changeNickname() throws Exception {
        //when
        ResultActions perform = mockMvc.perform(
            patch("/member/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new NicknameChangeInput("변경할닉네임")
                ))
                .with(oauth2Login())
                .with(csrf()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk());

        verify(memberService, times(1)).changeNickname(TEST_ID, "변경할닉네임");
    }
}