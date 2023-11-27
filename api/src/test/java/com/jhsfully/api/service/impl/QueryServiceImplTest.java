package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.errortype.ApiErrorType.API_IS_DISABLED;
import static com.jhsfully.domain.type.errortype.ApiErrorType.API_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiErrorType.QUERY_PARAMETER_CANNOT_MATCH;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.API_KEY_NOT_ISSUED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.jhsfully.api.exception.ApiException;
import com.jhsfully.api.exception.ApiPermissionException;
import com.jhsfully.api.model.query.QueryInput;
import com.jhsfully.api.model.query.QueryResponse;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiKey;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.ApiKeyRepository;
import com.jhsfully.domain.type.ApiQueryType;
import com.jhsfully.domain.type.ApiState;
import com.jhsfully.domain.type.ApiStructureType;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

@ExtendWith(MockitoExtension.class)
class QueryServiceImplTest {

  private static final String MONGODB_ID = "_id";
  private static final String DELIMITER = " ";

  @Mock
  private ApiInfoRepository apiInfoRepository;
  @Mock
  private ApiKeyRepository apiKeyRepository;
  @Mock
  private MongoTemplate mongoTemplate;

  @InjectMocks
  private QueryServiceImpl queryService;

  private Member getOwnerMember(){
    return Member.builder()
        .id(1L)
        .email("owner@test.com")
        .build();
  }

  private Map<String, ApiStructureType> getSchemaStructure(){
    Map<String, ApiStructureType> schemaStructure = new HashMap<>();
    schemaStructure.put("test", ApiStructureType.STRING);
    return schemaStructure;
  }

  private Map<String, ApiQueryType> getQueryParameter(){
    Map<String, ApiQueryType> queryParameter = new HashMap<>();
    queryParameter.put("test", ApiQueryType.EQUAL);
    return queryParameter;
  }

  private ApiInfo getApiInfo(){

    return ApiInfo.builder()
        .id(1L)
        .apiName("test")
        .apiIntroduce("test")
        .member(getOwnerMember())
        .apiState(ApiState.ENABLED)
        .schemaStructure(getSchemaStructure())
        .queryParameter(getQueryParameter())
        .registeredAt(LocalDateTime.of(2023, 10, 31, 9, 30 ,10))
        .updatedAt(LocalDateTime.of(2023, 10, 31, 9, 40, 10))
        .isPublic(true)
        .build();
  }

  @Test
  @DisplayName("데이터 조회 성공")
  void success_getDataList(){
    //given
    ApiInfo apiInfo = getApiInfo();
    String testAuthKey = "testAuthKey";
    Document responseData = new Document();
    responseData.put(MONGODB_ID, "2a2b4c");
    responseData.put("test", "test");

    given(apiInfoRepository.findById(anyLong()))
        .willReturn(Optional.of(apiInfo));

    given(apiKeyRepository.findByApiInfoAndAuthKey(any(), eq(testAuthKey)))
        .willReturn(Optional.of(ApiKey.builder().build()));

    given(mongoTemplate.find(any(), any(), any()))
        .willReturn(List.of(responseData));

    //when
    Map<String, Object> queryParameter = new HashMap<>();
    queryParameter.put("test", "test");
    QueryResponse queryResponse = queryService.getDataList(
        QueryInput.builder()
            .apiId(1)
            .authKey(testAuthKey)
            .pageIdx(0)
            .pageSize(10)
            .queryParameter(queryParameter)
            .build()
    );

    //then
    assertEquals(queryParameter.get("test"), queryResponse.getDataList().get(0).get("test"));
  }

  @Test
  @DisplayName("데이터 조회 실패 - API X")
  void fail_getDataList_api_not_found(){
    //given
    String testAuthKey = "testAuthKey";
    given(apiInfoRepository.findById(anyLong()))
        .willReturn(Optional.empty());

    //when
    Map<String, Object> queryParameter = new HashMap<>();
    queryParameter.put("test", "test");
    ApiException exception = assertThrows(ApiException.class,
        () -> queryService.getDataList(
            QueryInput.builder()
                .apiId(1)
                .authKey(testAuthKey)
                .pageIdx(0)
                .pageSize(10)
                .queryParameter(queryParameter)
                .build()
        ));

    //then
    assertEquals(API_NOT_FOUND, exception.getApiErrorType());
  }

  @Test
  @DisplayName("데이터 조회 실패 - API 비활성화 상태")
  void fail_getDataList_api_is_disabled(){
    //given
    String testAuthKey = "testAuthKey";
    ApiInfo apiInfo = getApiInfo();
    apiInfo.setApiState(ApiState.DISABLED);

    given(apiInfoRepository.findById(anyLong()))
        .willReturn(Optional.of(apiInfo));

    //when
    Map<String, Object> queryParameter = new HashMap<>();
    queryParameter.put("test", "test");
    ApiException exception = assertThrows(ApiException.class,
        () -> queryService.getDataList(
            QueryInput.builder()
                .apiId(1)
                .authKey(testAuthKey)
                .pageIdx(0)
                .pageSize(10)
                .queryParameter(queryParameter)
                .build()
        ));

    //then
    assertEquals(API_IS_DISABLED, exception.getApiErrorType());
  }

  @Test
  @DisplayName("데이터 조회 실패 - API키 미발급")
  void fail_getDataList_api_key_not_issued(){
    //given
    ApiInfo apiInfo = getApiInfo();
    String testAuthKey = "testAuthKey";

    given(apiInfoRepository.findById(anyLong()))
        .willReturn(Optional.of(apiInfo));

    given(apiKeyRepository.findByApiInfoAndAuthKey(any(), eq(testAuthKey)))
        .willReturn(Optional.empty());

    //when
    Map<String, Object> queryParameter = new HashMap<>();
    queryParameter.put("test", "test");
    ApiPermissionException exception = assertThrows(ApiPermissionException.class,
        () -> queryService.getDataList(
            QueryInput.builder()
                .apiId(1)
                .authKey(testAuthKey)
                .pageIdx(0)
                .pageSize(10)
                .queryParameter(queryParameter)
                .build()
        ));

    //then
    assertEquals(API_KEY_NOT_ISSUED, exception.getApiPermissionErrorType());
  }

  @Test
  @DisplayName("데이터 조회 실패 - 쿼리 파라미터 매칭 X")
  void fail_getDataList_query_parameter_cannot_match(){
    //given
    ApiInfo apiInfo = getApiInfo();
    String testAuthKey = "testAuthKey";
    Map<String, Object> responseData = new HashMap<>();
    responseData.put(MONGODB_ID, "2a2b4c");
    responseData.put("test", "test");

    given(apiInfoRepository.findById(anyLong()))
        .willReturn(Optional.of(apiInfo));

    given(apiKeyRepository.findByApiInfoAndAuthKey(any(), eq(testAuthKey)))
        .willReturn(Optional.of(ApiKey.builder().build()));

    //when
    Map<String, Object> queryParameter = new HashMap<>();
    queryParameter.put("otherParameter", "test");
    ApiException exception = assertThrows(ApiException.class,
        () -> queryService.getDataList(
            QueryInput.builder()
                .apiId(1)
                .authKey(testAuthKey)
                .pageIdx(0)
                .pageSize(10)
                .queryParameter(queryParameter)
                .build()
        ));

    //then
    assertEquals(QUERY_PARAMETER_CANNOT_MATCH, exception.getApiErrorType());
  }

}