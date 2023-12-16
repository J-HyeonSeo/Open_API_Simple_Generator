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

import com.jhsfully.api.model.dto.ApiRequestInviteDto;
import com.jhsfully.api.security.SecurityConfiguration;
import com.jhsfully.api.service.ApiRequestService;
import com.jhsfully.domain.type.ApiRequestStateType;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = ApiRequestController.class, excludeFilters =
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfiguration.class}))
class ApiRequestControllerTest {

    //mocks
    @MockBean
    private ApiRequestService apiRequestService;

    //injects
    @Autowired
    private MockMvc mockMvc;

    //constants
    private static final long TEST_ID = -1L;


    @Test
    void getRequestListForMember() throws Exception {
        //given
        LocalDateTime nowTime = LocalDateTime.of(2023, 12, 1, 9, 3, 3);
        List<ApiRequestInviteDto> response = List.of(
            ApiRequestInviteDto.builder()
                .id(1L)
                .apiInfoId(1L)
                .memberNickname("owner")
                .memberEmail("owner@test.com")
                .apiName("apiName")
                .registeredAt(nowTime)
                .requestStateType(ApiRequestStateType.REQUEST)
                .build()
        );
        given(apiRequestService.getRequestListForMember(anyLong(), any()))
            .willReturn(response);

        //when
        ResultActions perform = mockMvc.perform(get("/api/request/member/0/1")
            .with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.[0].id").value(1L),
                jsonPath("$.[0].apiInfoId").value(1L),
                jsonPath("$.[0].memberNickname").value("owner"),
                jsonPath("$.[0].memberEmail").value("owner@test.com"),
                jsonPath("$.[0].apiName").value("apiName"),
                jsonPath("$.[0].registeredAt").value(nowTime.toString()),
                jsonPath("$.[0].requestStateType").value("REQUEST")
            );
    }

    @Test
    void getRequestListForOwner() throws Exception {
        //given
        LocalDateTime nowTime = LocalDateTime.of(2023, 12, 1, 9, 3, 3);
        List<ApiRequestInviteDto> response = List.of(
            ApiRequestInviteDto.builder()
                .id(1L)
                .apiInfoId(1L)
                .memberNickname("requester")
                .memberEmail("requester@test.com")
                .apiName("apiName")
                .registeredAt(nowTime)
                .requestStateType(ApiRequestStateType.REQUEST)
                .build()
        );
        given(apiRequestService.getRequestListForOwner(anyLong(), anyLong(), any()))
            .willReturn(response);

        //when
        ResultActions perform = mockMvc.perform(get("/api/request/owner/1/0/1")
            .with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.[0].id").value(1L),
                jsonPath("$.[0].apiInfoId").value(1L),
                jsonPath("$.[0].memberNickname").value("requester"),
                jsonPath("$.[0].memberEmail").value("requester@test.com"),
                jsonPath("$.[0].apiName").value("apiName"),
                jsonPath("$.[0].registeredAt").value(nowTime.toString()),
                jsonPath("$.[0].requestStateType").value("REQUEST")
            );
    }

    @Test
    void apiRequest() throws Exception {
        //when
        ResultActions perform = mockMvc.perform(
            post("/api/request/1").with(csrf()).with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().isOk());
        verify(apiRequestService, times(1)).apiRequest(
            eq(TEST_ID), eq(1L)
        );
    }

    @Test
    void apiRequestAssign() throws Exception {
        //when
        ResultActions perform = mockMvc.perform(
            patch("/api/request/assign/1").with(csrf()).with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().isOk());
        verify(apiRequestService, times(1)).apiRequestAssign(
            eq(TEST_ID), eq(1L)
        );
    }

    @Test
    void apiRequestReject() throws Exception {
        //when
        ResultActions perform = mockMvc.perform(
            patch("/api/request/reject/1").with(csrf()).with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().isOk());
        verify(apiRequestService, times(1)).apiRequestReject(
            eq(TEST_ID), eq(1L)
        );
    }
}