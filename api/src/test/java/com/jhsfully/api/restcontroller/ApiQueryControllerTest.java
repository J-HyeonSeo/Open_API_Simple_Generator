package com.jhsfully.api.restcontroller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jhsfully.api.model.PageResponse;
import com.jhsfully.api.security.SecurityConfiguration;
import com.jhsfully.api.service.QueryService;
import java.util.List;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = ApiQueryController.class, excludeFilters =
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfiguration.class}))
class ApiQueryControllerTest {

    @MockBean
    private QueryService queryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getOpenAPIDataListTest() throws Exception {
        //given
        PageResponse<Document> response = PageResponse.of(
            new PageImpl<>(List.of(
                new Document(){{
                    put("test", "value");
                }}
            )));

        given(queryService.getDataList(any()))
            .willReturn(response);

        //when
        ResultActions perform = mockMvc.perform(
            get("/query/1/authKey/0/1?test=value").with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.totalElements").value(1L),
                jsonPath("$.hasNextPage").value(false),
                jsonPath("$.content.[0].test").value("value")
            );

    }

}