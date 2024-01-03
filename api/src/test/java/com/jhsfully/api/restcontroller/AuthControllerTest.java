package com.jhsfully.api.restcontroller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhsfully.api.model.auth.TokenInput;
import com.jhsfully.api.model.auth.TokenResponse;
import com.jhsfully.api.security.SecurityConfiguration;
import com.jhsfully.api.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = AuthController.class, excludeFilters =
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfiguration.class}))
class AuthControllerTest {

    @MockBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void refresh() throws Exception {
        //given
        given(authService.generateAccessToken(anyString()))
            .willReturn(new TokenResponse("accessToken", null));
        //when
        TokenInput input = new TokenInput("refreshToken");
        ResultActions perform = mockMvc.perform(post("/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
            .with(csrf()).with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.accessToken").value("accessToken")
            );
    }

    @Test
    void logout() throws Exception {
        //when
        TokenInput input = new TokenInput("refreshToken");
        ResultActions perform = mockMvc.perform(delete("/auth/signout")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
            .with(csrf())
            .with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().isOk());
        verify(authService, times(1)).logout(eq(input.getRefreshToken()));
    }
}