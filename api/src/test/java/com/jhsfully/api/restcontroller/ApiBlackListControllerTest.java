package com.jhsfully.api.restcontroller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jhsfully.api.model.PageResponse;
import com.jhsfully.api.model.dto.BlackListDto;
import com.jhsfully.api.security.SecurityConfiguration;
import com.jhsfully.api.service.ApiBlackListService;
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

@WebMvcTest(value = ApiBlackListController.class, excludeFilters =
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfiguration.class}))
class ApiBlackListControllerTest {

    @MockBean
    private ApiBlackListService apiBlackListService;

    @Autowired
    private MockMvc mockMvc;

    //constants
    private static final long TEST_ID = -1L;

    @Test
    void getBlackList() throws Exception {
        //given
        BlackListDto blackListDto = BlackListDto.builder()
            .apiId(1L)
            .memberId(1L)
            .memberEmail("test@test.com")
            .registeredAt(LocalDateTime.of(2023, 12, 1, 9, 3, 3))
            .build();

        given(apiBlackListService.getBlackList(anyLong(), anyLong(), any()))
            .willReturn(PageResponse.of(
                new PageImpl<>(List.of(blackListDto))
            ));

        //when
        ResultActions perform = mockMvc.perform(get("/api/blacklist/1/0/5").with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.content.[0].apiId").value(1L),
                jsonPath("$.content.[0].memberId").value(1L),
                jsonPath("$.content.[0].memberEmail").value("test@test.com"),
                jsonPath("$.content.[0].registeredAt")
                    .value(blackListDto.getRegisteredAt().toString())
            );
    }

    @Test
    void registerBlackList() throws Exception {
        //when
        ResultActions perform = mockMvc.perform(
            post("/api/blacklist/1/2").with(csrf()).with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().isOk());
        verify(apiBlackListService, times(1)).registerBlackList(
            eq(1L), eq(TEST_ID), eq(2L), any(LocalDateTime.class)
        );
    }

    @Test
    void deleteBlackList() throws Exception {
        //when
        ResultActions perform = mockMvc.perform(
            delete("/api/blacklist/1").with(csrf()).with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().isOk());
        verify(apiBlackListService, times(1)).deleteBlackList(
            eq(1L), eq(TEST_ID)
        );
    }
}