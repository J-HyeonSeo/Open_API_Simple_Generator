package com.jhsfully.api.restcontroller;

import static com.jhsfully.domain.type.ApiQueryType.EQUAL;
import static com.jhsfully.domain.type.ApiStructureType.STRING;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhsfully.api.model.PageResponse;
import com.jhsfully.api.model.api.CreateApiInput;
import com.jhsfully.api.model.api.DeleteApiDataInput;
import com.jhsfully.api.model.api.InsertApiDataInput;
import com.jhsfully.api.model.api.UpdateApiDataInput;
import com.jhsfully.api.model.api.UpdateApiInput;
import com.jhsfully.api.model.dto.ApiInfoDto;
import com.jhsfully.api.model.dto.ApiInfoDto.ApiInfoDetailDto;
import com.jhsfully.api.model.dto.ApiInfoDto.ApiInfoSearchDto;
import com.jhsfully.api.security.SecurityConfiguration;
import com.jhsfully.api.service.ApiHistoryService;
import com.jhsfully.api.service.ApiSearchService;
import com.jhsfully.api.service.ApiService;
import com.jhsfully.domain.entity.ApiInfoElastic;
import com.jhsfully.domain.type.ApiState;
import com.jhsfully.domain.type.QueryData;
import com.jhsfully.domain.type.SchemaData;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = ApiController.class, excludeFilters =
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfiguration.class}))
class ApiControllerTest {

    //mocks
    @MockBean
    private ApiService apiService;
    @MockBean
    private ApiHistoryService apiHistoryService;
    @MockBean
    private ApiSearchService apiSearchService;

    //injects
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    //constants
    private static final long TEST_ID = -1L;

    //getData
    private MockMultipartFile getExcelFile() throws IOException {
        return new MockMultipartFile(
            "TestExcel.xlsx", "TestExcel.xlsx", "application/x-tika-ooxml"
            , new FileInputStream(
            "src/test/resources/TestExcel.xlsx"
        )
        );
    }

    @Test
    void createOpenApi() throws Exception {
        //when
        MockMultipartFile excelFile = getExcelFile();

        ResultActions perform = mockMvc.perform(multipart("/api")
            .file("file", excelFile.getBytes())
            .param("apiName", "apiName")
            .param("apiIntroduce", "apiIntroduce")
            .param("schemaStructure[0].field", "test")
            .param("schemaStructure[0].type", "STRING")
            .param("queryParameter[0].field", "test")
            .param("queryParameter[0].type", "EQUAL")
            .param("isPublic", "true")
            .with(request -> {
                request.setMethod("POST");
                return request;
            })
            .with(csrf()).with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().isOk());
        ArgumentCaptor<CreateApiInput> inputCaptor = ArgumentCaptor.forClass(CreateApiInput.class);
        verify(apiService, times(1)).createOpenApi(inputCaptor.capture(), eq(TEST_ID));
        CreateApiInput input = inputCaptor.getValue();
        assertAll(
            () -> assertEquals("apiName", input.getApiName()),
            () -> assertEquals("apiIntroduce", input.getApiIntroduce()),
            () -> assertEquals("test", input.getSchemaStructure().get(0).getField()),
            () -> assertEquals(STRING, input.getSchemaStructure().get(0).getType()),
            () -> assertEquals("test", input.getQueryParameter().get(0).getField()),
            () -> assertEquals(EQUAL, input.getQueryParameter().get(0).getType()),
            () -> assertTrue(input.isPublic()),
            () -> assertEquals(excelFile.getSize(), input.getFile().getSize())
        );
    }

    @Test
    void insertApiData() throws Exception {
        //when
        InsertApiDataInput input = new InsertApiDataInput(
            new HashMap<>(){{
                put("test", "value");
            }}
        );
        ResultActions perform = mockMvc.perform(post("/api/data/manage/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
            .with(csrf())
            .with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().isOk());
        ArgumentCaptor<InsertApiDataInput> inputCaptor = ArgumentCaptor.forClass(InsertApiDataInput.class);
        verify(apiService, times(1)).insertApiData(
            inputCaptor.capture(), eq(1L), eq(TEST_ID), any()
        );
        assertEquals("value", inputCaptor.getValue().getInsertData().get("test"));
    }

    @Test
    void updateApiData() throws Exception {
        //when
        UpdateApiDataInput input = UpdateApiDataInput.builder()
            .dataId("dataId")
            .updateData( new HashMap<>(){{
                put("test", "value");
            }})
            .build();

        ResultActions perform = mockMvc.perform(put("/api/data/manage/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
            .with(csrf())
            .with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().isOk());
        ArgumentCaptor<UpdateApiDataInput> inputCaptor = ArgumentCaptor.forClass(UpdateApiDataInput.class);
        verify(apiService, times(1)).updateApiData(
            inputCaptor.capture(), eq(1L), eq(TEST_ID), any()
        );
        assertEquals("value", inputCaptor.getValue().getUpdateData().get("test"));
        assertEquals("dataId", inputCaptor.getValue().getDataId());
    }

    @Test
    void deleteApiData() throws Exception {
        //when
        DeleteApiDataInput input = new DeleteApiDataInput("dataId");
        ResultActions perform = mockMvc.perform(delete("/api/data/manage/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
            .with(csrf())
            .with(oauth2Login()));
        //then
        perform.andDo(print()).andExpect(status().isOk());
        ArgumentCaptor<DeleteApiDataInput> inputCaptor = ArgumentCaptor.forClass(DeleteApiDataInput.class);
        verify(apiService, times(1)).deleteApiData(
            inputCaptor.capture(), eq(1L), eq(TEST_ID), any()
        );
        assertEquals("dataId", inputCaptor.getValue().getDataId());
    }

    @Test
    void deleteOpenApi() throws Exception {
        //when
        ResultActions perform = mockMvc.perform(delete("/api/1")
            .with(csrf())
            .with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().isOk());
        verify(apiService, times(1)).deleteOpenApi(eq(1L), eq(TEST_ID));
    }

    @Test
    void enableOpenApi() throws Exception {
        //when
        ResultActions perform = mockMvc.perform(patch("/api/enable/1")
            .with(csrf())
            .with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().isOk());
        verify(apiService, times(1)).enableOpenApi(eq(1L), eq(TEST_ID), any());
    }

    @Test
    void updateOpenApi() throws Exception {
        //when
        UpdateApiInput input = UpdateApiInput.builder()
            .apiName("updateName")
            .apiIntroduce("updateIntroduce")
            .build();
        ResultActions perform = mockMvc.perform(patch("/api/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input))
            .with(csrf())
            .with(oauth2Login()));

        //then
        perform.andDo(print()).andExpect(status().isOk());
        ArgumentCaptor<UpdateApiInput> inputCaptor = ArgumentCaptor.forClass(UpdateApiInput.class);
        verify(apiService, times(1)).updateOpenApi(
            inputCaptor.capture(), eq(1L), eq(TEST_ID)
        );
        assertAll(
            () -> assertEquals("updateName", inputCaptor.getValue().getApiName()),
            () -> assertEquals("updateIntroduce", inputCaptor.getValue().getApiIntroduce())
        );
    }

    @Test
    void getOpenApiList() throws Exception {
        //given
        PageResponse<ApiInfoSearchDto> apiSearchResponse = PageResponse.of(
            new PageImpl<>(
                List.of(ApiInfoElastic.builder()
                        .id(1L)
                        .apiName("apiName")
                        .apiIntroduce("apiIntroduce")
                        .ownerNickname("owner")
                        .profileUrl("profileUrl")
                        .apiState(ApiState.ENABLED)
                        .isPublic(true)
                        .ownerMemberId(1L)
                    .build())), (x) -> ApiInfoDto.of(x, false));

        given(apiSearchService.getOpenApiList(anyString(), any(), any(), anyLong()))
            .willReturn(apiSearchResponse);

        //when
        ResultActions perform = mockMvc.perform(
            get("/api/public/0/1?searchText=apiName&type=API_NAME")
                .with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.content.[0].id").value(1L),
                jsonPath("$.content.[0].apiName").value("apiName"),
                jsonPath("$.content.[0].ownerNickname").value("owner"),
                jsonPath("$.content.[0].apiState").value("ENABLED"),
                jsonPath("$.content.[0].profileUrl").value("profileUrl"),
                jsonPath("$.content.[0].accessible").value(false)
            );
    }

    @Test
    void getApiListForOwner() throws Exception {
        //given
        PageResponse<ApiInfoSearchDto> apiSearchResponse = PageResponse.of(
            new PageImpl<>(
                List.of(ApiInfoElastic.builder()
                    .id(1L)
                    .apiName("apiName")
                    .apiIntroduce("apiIntroduce")
                    .ownerNickname("owner")
                    .profileUrl("profileUrl")
                    .apiState(ApiState.ENABLED)
                    .isPublic(true)
                    .ownerMemberId(1L)
                    .build())), (x) -> ApiInfoDto.of(x, false));

        given(apiSearchService.getOpenApiListForOwner(eq(TEST_ID), anyString(), any(), any()))
            .willReturn(apiSearchResponse);

        //when
        ResultActions perform = mockMvc.perform(
            get("/api/owner/0/1?searchText=apiName&type=API_NAME")
                .with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.content.[0].id").value(1L),
                jsonPath("$.content.[0].apiName").value("apiName"),
                jsonPath("$.content.[0].ownerNickname").value("owner"),
                jsonPath("$.content.[0].apiState").value("ENABLED"),
                jsonPath("$.content.[0].profileUrl").value("profileUrl"),
                jsonPath("$.content.[0].accessible").value(false)
            );
    }

    @Test
    void getApiListForAccess() throws Exception {
        //given
        PageResponse<ApiInfoSearchDto> apiSearchResponse = PageResponse.of(
            new PageImpl<>(
                List.of(ApiInfoElastic.builder()
                    .id(1L)
                    .apiName("apiName")
                    .apiIntroduce("apiIntroduce")
                    .ownerNickname("owner")
                    .profileUrl("profileUrl")
                    .apiState(ApiState.ENABLED)
                    .isPublic(true)
                    .ownerMemberId(1L)
                    .build())), (x) -> ApiInfoDto.of(x, false));

        given(apiSearchService.getOpenApiListForAccess(eq(TEST_ID), anyString(), any(), any()))
            .willReturn(apiSearchResponse);

        //when
        ResultActions perform = mockMvc.perform(
            get("/api/access/0/1?searchText=apiName&type=API_NAME")
                .with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.content.[0].id").value(1L),
                jsonPath("$.content.[0].apiName").value("apiName"),
                jsonPath("$.content.[0].ownerNickname").value("owner"),
                jsonPath("$.content.[0].apiState").value("ENABLED"),
                jsonPath("$.content.[0].profileUrl").value("profileUrl"),
                jsonPath("$.content.[0].accessible").value(false)
            );
    }

    @Test
    void getOpenApiDetail() throws Exception {
        //given
        LocalDateTime nowTime = LocalDateTime.of(2023, 12, 1, 9, 3, 3);
        ApiInfoDetailDto response = ApiInfoDetailDto.builder()
            .id(1L)
            .apiName("apiName")
            .apiIntroduce("apiIntroduce")
            .ownerNickname("owner")
            .apiState(ApiState.ENABLED)
            .schemaStructure(List.of(new SchemaData("test", STRING)))
            .queryParameter(List.of(new QueryData("test", EQUAL)))
            .registeredAt(nowTime)
            .updatedAt(nowTime)
            .disabledAt(nowTime)
            .build();
        given(apiSearchService.getOpenApiDetail(anyLong(), anyLong()))
            .willReturn(response);

        //when
        ResultActions perform = mockMvc.perform(get("/api/public/1").with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.id").value(1L),
                jsonPath("$.apiName").value("apiName"),
                jsonPath("$.apiIntroduce").value("apiIntroduce"),
                jsonPath("$.ownerNickname").value("owner"),
                jsonPath("$.apiState").value("ENABLED"),
                jsonPath("$.schemaStructure.[0].field").value("test"),
                jsonPath("$.schemaStructure.[0].type").value("STRING"),
                jsonPath("$.queryParameter.[0].field").value("test"),
                jsonPath("$.queryParameter.[0].type").value("EQUAL"),
                jsonPath("$.registeredAt").value(nowTime.toString()),
                jsonPath("$.updatedAt").value(nowTime.toString()),
                jsonPath("$.disabledAt").value(nowTime.toString())
            );
    }

    @Test
    void getApiHistories() throws Exception {
        //given
        PageResponse<Document> response = PageResponse.of(
            new PageImpl<>(
                List.of(
                    new Document(){{
                        put("original_data", "original");
                        put("new_data", "new");
                    }}
                ))
        );

        given(apiHistoryService.getApiHistories(
            eq(1L), eq(TEST_ID), eq(LocalDate.of(2023, 12, 1)),
            eq(LocalDate.of(2023, 12, 2)), any()
        )).willReturn(response);

        //when
        ResultActions perform = mockMvc.perform(
            get("/api/history/1/0/1?startDate=2023-12-01&endDate=2023-12-02")
                .with(oauth2Login()));

        //then
        perform.andDo(print())
            .andExpectAll(
                status().isOk(),
                jsonPath("$.content.[0].original_data").value("original"),
                jsonPath("$.content.[0].new_data").value("new")
            );
    }
}