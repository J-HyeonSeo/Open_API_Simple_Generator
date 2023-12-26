package com.jhsfully.api.restcontroller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhsfully.api.model.PageResponse;
import com.jhsfully.api.model.dto.ApiRequestInviteDto;
import com.jhsfully.api.security.SecurityConfiguration;
import com.jhsfully.api.service.ApiInviteService;
import com.jhsfully.domain.type.ApiRequestStateType;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = ApiInviteController.class, excludeFilters =
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfiguration.class}))
class ApiInviteControllerTest {

    //mocks
    @MockBean
    private ApiInviteService apiInviteService;

    //injects
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    //constants
    private static final long TEST_ID = -1L;

    @Test
    void getInviteListForOwner() throws Exception {
        //given
        LocalDateTime nowTime = LocalDateTime.of(2023, 12, 1, 9, 3, 3);
        List<ApiRequestInviteDto> response = List.of(
            ApiRequestInviteDto.builder()
                .id(1L)
                .apiInfoId(1L)
                .memberNickname("inviter")
                .profileUrl("profileUrl")
                .apiName("apiName")
                .registeredAt(nowTime)
                .requestStateType(ApiRequestStateType.REQUEST)
                .build()
        );
        given(apiInviteService.getInviteListForOwner(anyLong(), anyLong(), any()))
            .willReturn(PageResponse.of(new PageImpl<>(response)));

        //when
        ResultActions perform = mockMvc.perform(get("/api/invite/owner/1/0/1")
            .with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.content.[0].id").value(1L),
                jsonPath("$.content.[0].apiInfoId").value(1L),
                jsonPath("$.content.[0].memberNickname").value("inviter"),
                jsonPath("$.content.[0].profileUrl").value("profileUrl"),
                jsonPath("$.content.[0].apiName").value("apiName"),
                jsonPath("$.content.[0].registeredAt").value(nowTime.toString()),
                jsonPath("$.content.[0].requestStateType").value("REQUEST")
            );
    }

    @Test
    void getInviteListForMember() throws Exception {
        //given
        LocalDateTime nowTime = LocalDateTime.of(2023, 12, 1, 9, 3, 3);
        List<ApiRequestInviteDto> response = List.of(
            ApiRequestInviteDto.builder()
                .id(1L)
                .apiInfoId(1L)
                .memberNickname("owner")
                .profileUrl("profileUrl")
                .apiName("apiName")
                .registeredAt(nowTime)
                .requestStateType(ApiRequestStateType.REQUEST)
                .build()
        );
        given(apiInviteService.getInviteListForMember(anyLong(), any()))
            .willReturn(PageResponse.of(new PageImpl<>(response)));

        //when
        ResultActions perform = mockMvc.perform(get("/api/invite/member/0/1")
            .with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.content.[0].id").value(1L),
                jsonPath("$.content.[0].apiInfoId").value(1L),
                jsonPath("$.content.[0].memberNickname").value("owner"),
                jsonPath("$.content.[0].profileUrl").value("profileUrl"),
                jsonPath("$.content.[0].apiName").value("apiName"),
                jsonPath("$.content.[0].registeredAt").value(nowTime.toString()),
                jsonPath("$.content.[0].requestStateType").value("REQUEST")
            );
    }

    @Test
    void apiInvite() throws Exception {
        //when
        ResultActions perform = mockMvc.perform(
            post("/api/invite/1/1").with(csrf()).with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().isOk());
        verify(apiInviteService, times(1)).apiInvite(
            eq(1L), eq(TEST_ID), eq(1L)
        );
    }

    @Test
    void apiInviteAssign() throws Exception {
        //when
        ResultActions perform = mockMvc.perform(
            patch("/api/invite/assign/1").with(csrf()).with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().isOk());
        verify(apiInviteService, times(1)).apiInviteAssign(
            eq(TEST_ID), eq(1L)
        );
    }

    @Test
    void apiInviteReject() throws Exception {
        //when
        ResultActions perform = mockMvc.perform(
            patch("/api/invite/reject/1").with(csrf()).with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().isOk());
        verify(apiInviteService, times(1)).apiInviteReject(
            eq(TEST_ID), eq(1L)
        );
    }
}