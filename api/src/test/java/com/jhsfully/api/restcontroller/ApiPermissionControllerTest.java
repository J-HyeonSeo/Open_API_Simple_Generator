package com.jhsfully.api.restcontroller;

import static com.jhsfully.domain.type.ApiPermissionType.INSERT;
import static com.jhsfully.domain.type.ApiPermissionType.UPDATE;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhsfully.api.model.PageResponse;
import com.jhsfully.api.model.dto.PermissionDto;
import com.jhsfully.api.model.dto.PermissionDto.PermissionDetailDto;
import com.jhsfully.api.model.permission.AuthKeyResponse;
import com.jhsfully.api.security.SecurityConfiguration;
import com.jhsfully.api.service.ApiPermissionService;
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

@WebMvcTest(value = ApiPermissionController.class, excludeFilters =
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfiguration.class}))
class ApiPermissionControllerTest {

    @MockBean
    private ApiPermissionService apiPermissionService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static final long TEST_ID = -1L;

    @Test
    void getPermissionForMember() throws Exception {
        //given
        PermissionDto permissionDto = PermissionDto.builder()
            .permissionId(1L)
            .memberNickname("access")
            .profileUrl("profileUrl")
            .permissionList(List.of(
                new PermissionDetailDto(1L, UPDATE),
                new PermissionDetailDto(2L, INSERT)
            ))
            .build();

        given(apiPermissionService.getPermissionForMember(anyLong(), anyLong()))
            .willReturn(permissionDto);

        //when
        ResultActions perform = mockMvc.perform(get("/api/permission/1").with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.permissionId").value(1L),
                jsonPath("$.memberNickname").value("access"),
                jsonPath("$.profileUrl").value("profileUrl"),
                jsonPath("$.permissionList.[0].id").value(1L),
                jsonPath("$.permissionList.[0].type").value("UPDATE"),
                jsonPath("$.permissionList.[1].id").value(2L),
                jsonPath("$.permissionList.[1].type").value("INSERT")
            );
    }

    @Test
    void getPermissionListForOwner() throws Exception {
        //given
        PageResponse<PermissionDto> response = PageResponse.of(
            new PageImpl<>(
                List.of(
                    PermissionDto.builder()
                        .permissionId(1L)
                        .memberNickname("access")
                        .profileUrl("profileUrl")
                        .permissionList(List.of(
                            new PermissionDetailDto(1L, UPDATE),
                            new PermissionDetailDto(2L, INSERT)
                        ))
                        .build()
                )
            )
        );

        given(apiPermissionService.getPermissionListForOwner(anyLong(), anyLong(), any()))
            .willReturn(response);

        //when
        ResultActions perform = mockMvc.perform(get("/api/permission/owner/1/0/1").with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.totalElements").value(1L),
                jsonPath("$.hasNextPage").value(false),
                jsonPath("$.content.[0].permissionId").value("1"),
                jsonPath("$.content.[0].memberNickname").value("access"),
                jsonPath("$.content.[0].profileUrl").value("profileUrl"),
                jsonPath("$.content.[0].permissionList.[0].id").value(1L),
                jsonPath("$.content.[0].permissionList.[0].type").value("UPDATE"),
                jsonPath("$.content.[0].permissionList.[1].id").value(2L),
                jsonPath("$.content.[0].permissionList.[1].type").value("INSERT")
            );
    }

    @Test
    void addPermission() throws Exception {
        //when
        ResultActions perform = mockMvc.perform(put("/api/permission/add/1?type=INSERT")
            .with(csrf()).with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().isOk());
        verify(apiPermissionService, times(1))
            .addPermission(eq(1L), eq(TEST_ID), eq(INSERT));
    }

    @Test
    void subPermission() throws Exception {
        //when
        ResultActions perform = mockMvc.perform(put("/api/permission/sub/1")
            .with(csrf()).with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().isOk());
        verify(apiPermissionService, times(1))
            .subPermission(eq(1L), eq(TEST_ID));
    }

    @Test
    void deletePermission() throws Exception {
        //when
        ResultActions perform = mockMvc.perform(delete("/api/permission/1")
            .with(csrf()).with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().isOk());
        verify(apiPermissionService, times(1))
            .deletePermission(eq(1L), eq(TEST_ID));
    }

    @Test
    void getAuthKey() throws Exception {
        //given
        AuthKeyResponse response = new AuthKeyResponse("authKey");
        given(apiPermissionService.getAuthKey(anyLong(), anyLong()))
            .willReturn(response);

        //when
        ResultActions perform = mockMvc.perform(
            get("/api/permission/authkey/1").with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.authKey").value("authKey")
            );
    }

    @Test
    void createAuthKey() throws Exception {
        //given
        AuthKeyResponse response = new AuthKeyResponse("authKey");
        given(apiPermissionService.createAuthKey(anyLong(), anyLong()))
            .willReturn(response);

        //when
        ResultActions perform = mockMvc.perform(
            post("/api/permission/authkey/1").with(csrf()).with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.authKey").value("authKey")
            );
    }

    @Test
    void refreshAuthKey() throws Exception {
        //given
        AuthKeyResponse response = new AuthKeyResponse("authKey");
        given(apiPermissionService.refreshAuthKey(anyLong(), anyLong()))
            .willReturn(response);

        //when
        ResultActions perform = mockMvc.perform(
            put("/api/permission/authkey/1").with(csrf()).with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.authKey").value("authKey")
            );
    }
}