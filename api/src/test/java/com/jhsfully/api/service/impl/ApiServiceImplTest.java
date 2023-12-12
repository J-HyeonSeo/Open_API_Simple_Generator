package com.jhsfully.api.service.impl;

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

import com.jhsfully.api.model.api.CreateApiInput;
import com.jhsfully.api.model.api.CreateApiInput.QueryData;
import com.jhsfully.api.model.api.CreateApiInput.SchemaData;
import com.jhsfully.api.model.api.DeleteApiDataInput;
import com.jhsfully.api.model.api.InsertApiDataInput;
import com.jhsfully.api.model.api.InsertApiDataResponse;
import com.jhsfully.api.model.api.UpdateApiDataInput;
import com.jhsfully.api.service.ApiHistoryService;
import com.jhsfully.api.util.MongoUtil;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiInfoElastic;
import com.jhsfully.domain.entity.ApiPermissionDetail;
import com.jhsfully.domain.entity.ApiUserPermission;
import com.jhsfully.domain.entity.Grade;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.kafkamodel.ExcelParserModel;
import com.jhsfully.domain.repository.ApiInfoElasticRepository;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.ApiUserPermissionRepository;
import com.jhsfully.domain.repository.MemberRepository;
import com.jhsfully.domain.type.ApiPermissionType;
import com.jhsfully.domain.type.ApiQueryType;
import com.jhsfully.domain.type.ApiState;
import com.jhsfully.domain.type.ApiStructureType;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ApiServiceImplTest {

  //constants
  private static final String MONGODB_ID = "_id";
  private static final String HISTORY_SUFFIX = "history";

  //repositories
  @Mock
  private ApiInfoRepository apiInfoRepository;
  @Mock
  private ApiUserPermissionRepository apiUserPermissionRepository;
  @Mock
  private MemberRepository memberRepository;
  @Mock
  private MongoTemplate mongoTemplate;
  @Mock
  private ApiInfoElasticRepository apiInfoElasticRepository;

  //service
  @Mock
  private ApiHistoryService apiHistoryService;

  //for kafka
  @Mock
  private KafkaTemplate<String, ExcelParserModel> kafkaTemplate;

  //util components
  @Mock
  private MongoUtil mongoUtil;

  @InjectMocks
  private ApiServiceImpl apiService;

  private MockMultipartFile getExcelFile() throws IOException {
    return new MockMultipartFile(
        "TestExcel.xlsx", "TestExcel.xlsx", "application/x-tika-ooxml"
        , new FileInputStream(
          new File("src/test/resources/TestExcel.xlsx")
      )
    );
  }

  private Grade getGrade() {
    return Grade.builder()
        .id(1L)
        .gradeName("BRONZE")
        .gradePosition(1)
        .apiMaxCount(1)
        .fieldMaxCount(1)
        .queryMaxCount(1)
        .recordMaxCount(1)
        .dbMaxSize(1000000)
        .accessorMaxCount(1)
        .historyStorageDays(3)
        .build();
  }

  private Member getOwnerMember() {
    return Member.builder()
        .id(1L)
        .email("owner@test.com")
        .grade(getGrade())
        .expiredEnabledAt(LocalDate.of(2023, 11, 28))
        .build();
  }

  private Member getAccessMember(){
    return Member.builder()
        .id(2L)
        .email("accessor@test.com")
        .build();
  }

  private ApiUserPermission getApiUserPermission(boolean hasAllPermission){

    ApiPermissionDetail apiPermissionDetailInsert = ApiPermissionDetail.builder()
        .id(1L)
        .type(ApiPermissionType.INSERT)
        .build();
    ApiPermissionDetail apiPermissionDetailUpdate = ApiPermissionDetail.builder()
        .id(2L)
        .type(ApiPermissionType.UPDATE)
        .build();
    ApiPermissionDetail apiPermissionDetailDelete = ApiPermissionDetail.builder()
        .id(3L)
        .type(ApiPermissionType.DELETE)
        .build();

    ApiUserPermission apiUserPermission = ApiUserPermission.builder()
        .id(1L)
        .apiInfo(getApiInfo())
        .member(getAccessMember())
        .apiPermissionDetails(hasAllPermission ? List.of(
            apiPermissionDetailInsert, apiPermissionDetailUpdate, apiPermissionDetailDelete
        ) : new ArrayList<>())
        .build();

    apiPermissionDetailInsert.setApiUserPermission(apiUserPermission);
    apiPermissionDetailUpdate.setApiUserPermission(apiUserPermission);
    apiPermissionDetailDelete.setApiUserPermission(apiUserPermission);

    return apiUserPermission;
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
        .dataCollectionName("dataCollectionName")
        .historyCollectionName("historyCollectionName")
        .schemaStructure(getSchemaStructure())
        .queryParameter(getQueryParameter())
        .registeredAt(LocalDateTime.of(2023, 10, 31, 9, 30 ,10))
        .updatedAt(LocalDateTime.of(2023, 10, 31, 9, 40, 10))
        .isPublic(true)
        .apiState(ApiState.ENABLED)
        .build();
  }

  private ApiInfoElastic getApiInfoElastic(){
    return ApiInfoElastic.builder()
        .id("1")
        .apiName("test")
        .apiIntroduce("test")
        .ownerEmail("owner@test.com")
        .state(ApiState.ENABLED)
        .isPublic(true)
        .ownerMemberId(1L)
        .build();
  }

  @Nested
  @DisplayName("createOpenApi() 테스트")
  class createOpenApiTest {

    @Test
    @DisplayName("OpenAPI 생성 성공")
    void success_createOpenApi() throws IOException {
      //given
      Member member = getOwnerMember();
      ApiInfo apiInfo = getApiInfo();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(member));

      given(apiInfoRepository.countByMember(any()))
          .willReturn(0);

      given(apiInfoRepository.save(any()))
          .willReturn(apiInfo);

      ReflectionTestUtils.setField(apiService, "EXCEL_STORAGE_PATH", "src/test/resources");
      ReflectionTestUtils.setField(apiService, "KAFKA_TOPIC_NAME", "excelparser");

      //when
      CreateApiInput input = CreateApiInput.builder()
          .apiName(apiInfo.getApiName())
          .apiIntroduce(apiInfo.getApiIntroduce())
          .schemaStructure(List.of(new SchemaData("test", ApiStructureType.STRING)))
          .queryParameter(List.of(new QueryData("test", ApiQueryType.EQUAL)))
          .isPublic(true)
          .file(getExcelFile())
          .build();
      long memberId = 1L;

      apiService.createOpenApi(input, memberId);

      //then
      ArgumentCaptor<ApiInfo> apiInfoCaptor = ArgumentCaptor.forClass(ApiInfo.class);
      ArgumentCaptor<ExcelParserModel> modelCaptor = ArgumentCaptor.forClass(ExcelParserModel.class);

      verify(apiInfoRepository, times(1)).save(apiInfoCaptor.capture());
      verify(kafkaTemplate, times(1)).send(eq("excelparser"), modelCaptor.capture());

      ApiInfo expectedApiInfo = apiInfoCaptor.getValue();
      ExcelParserModel expectedModel = modelCaptor.getValue();

      assertAll(
          () -> assertEquals(apiInfo.getApiName(), expectedApiInfo.getApiName()),
          () -> assertEquals(apiInfo.getMember().getId(), expectedApiInfo.getMember().getId()),
          () -> assertEquals(apiInfo.getApiIntroduce(), expectedApiInfo.getApiIntroduce()),
          () -> assertEquals(apiInfo.getSchemaStructure().get("test"), expectedApiInfo.getSchemaStructure().get("test")),
          () -> assertEquals(apiInfo.getSchemaStructure().size(), expectedApiInfo.getSchemaStructure().size()),
          () -> assertEquals(apiInfo.getQueryParameter().get("test"), expectedApiInfo.getQueryParameter().get("test")),
          () -> assertEquals(apiInfo.getQueryParameter().size(), expectedApiInfo.getQueryParameter().size()),
          () -> assertTrue(expectedApiInfo.getDataCollectionName().matches("[a-z0-9]*")),
          () -> assertEquals(expectedApiInfo.getHistoryCollectionName().split("-")[0],
              expectedApiInfo.getDataCollectionName()),
          () -> assertEquals(HISTORY_SUFFIX, expectedApiInfo.getHistoryCollectionName().split("-")[1]),
          () -> assertEquals(ApiState.READY, expectedApiInfo.getApiState()),
          () -> assertTrue(expectedApiInfo.isPublic()),

          () -> assertEquals(apiInfo.getId(), expectedModel.getApiInfoId()),
          () -> assertEquals("src/test/resources/" + expectedApiInfo.getDataCollectionName() + ".xlsx", expectedModel.getExcelPath()),
          () -> assertEquals(expectedApiInfo.getDataCollectionName(), expectedModel.getDataCollectionName()),
          () -> assertEquals(expectedApiInfo.getSchemaStructure().get("test"), expectedModel.getSchemaStructure().get("test")),
          () -> assertEquals(expectedApiInfo.getSchemaStructure().size(), expectedModel.getSchemaStructure().size()),
          () -> assertEquals(expectedApiInfo.getQueryParameter().get("test"), expectedModel.getQueryParameter().get("test")),
          () -> assertEquals(expectedApiInfo.getQueryParameter().size(), expectedModel.getQueryParameter().size())
      );

      //delete test excelfile.
      // 삭제할 파일 경로 지정
      String filePath = "src/test/resources/" + expectedApiInfo.getDataCollectionName() + ".xlsx";

      // File 객체 생성
      File fileToDelete = new File(filePath);

      // 파일이 존재하면 삭제
      if (fileToDelete.exists()) {
        if (fileToDelete.delete()) {
          System.out.println("파일이 성공적으로 삭제되었습니다.");
        } else {
          System.out.println("파일 삭제 실패");
        }
      } else {
        System.out.println("파일이 존재하지 않습니다.");
      }

    }

  }

  @Nested
  @DisplayName("insertApiData() 테스트")
  class insertApiDataTest {

    @Test
    @DisplayName("Api에 데이터 삽입 성공")
    void success_insertApiData(){
      //given
      Member accessMember = getAccessMember();
      ApiInfo apiInfo = getApiInfo();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(accessMember));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(mongoUtil.getDbSizeByCollection(any(), any()))
          .willReturn(10000L);

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.of(getApiUserPermission(true)));

      MongoCollection<Document> mockCollection = Mockito.mock(MongoCollection.class);

      given(mongoTemplate.getCollection(anyString()))
          .willReturn(mockCollection);

      ObjectId objectId = new ObjectId(new Date());

      given(mockCollection.insertOne(any()))
          .willReturn(new InsertOneResult() {
            @Override
            public boolean wasAcknowledged() {
              return false;
            }

            @Override
            public BsonValue getInsertedId() {
              return new BsonObjectId(objectId);
            }
          });

      //when
      InsertApiDataInput input = InsertApiDataInput.builder()
          .insertData(new HashMap<>(){{put("test", "test1");}})
          .build();

      long apiId = 1L;
      long memberId = 1L;
      LocalDateTime nowTime = LocalDateTime.now();
      InsertApiDataResponse response = apiService.insertApiData(input, apiId, memberId, nowTime);

      //then
      ArgumentCaptor<Document> insertDocumentCaptor = ArgumentCaptor.forClass(Document.class);

      verify(mockCollection, times(1)).insertOne(insertDocumentCaptor.capture());
      verify(apiHistoryService, times(1)).writeInsertHistory(
          any(), eq(apiInfo.getHistoryCollectionName()), eq(memberId), eq(nowTime)
      );

      Document expectedInsertDocument = insertDocumentCaptor.getValue();

      assertAll(
          () -> assertEquals("test1", expectedInsertDocument.get("test")),
          () -> assertEquals(objectId.toString(), response.getDataId())
      );

    }

  }

  @Nested
  @DisplayName("updateApiData() 테스트")
  class updateApiDataTest {

    @Test
    @DisplayName("Api에 데이터 수정 성공")
    void success_updateApiData(){
      //given
      Member member = getAccessMember();
      ApiInfo apiInfo = getApiInfo();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(member));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(mongoUtil.getDbSizeByCollection(any(), any()))
          .willReturn(10000L);

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.of(getApiUserPermission(true)));

      given(mongoTemplate.exists(any(), anyString()))
          .willReturn(true);

      ObjectId objectId = new ObjectId(new Date()); //original object id;
      Document document = new Document();
      document.put("_id", objectId);
      document.put("test", "test1");

      given(mongoTemplate.findOne(any(), any(), any()))
          .willReturn(document);

      //when
      UpdateApiDataInput input = UpdateApiDataInput.builder()
          .dataId(objectId.toString())
          .updateData(new HashMap<>(){{put("test", "updated");}})
          .build();
      long apiId = apiInfo.getId();
      long memberId = member.getId();
      LocalDateTime nowTime = LocalDateTime.now();

      apiService.updateApiData(input, apiId, memberId, nowTime);

      //then
      ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
      ArgumentCaptor<Document> originalDataCaptor = ArgumentCaptor.forClass(Document.class);
      ArgumentCaptor<Map<String, Object>> updateDataCaptor = ArgumentCaptor.forClass(Map.class);

      verify(mongoTemplate, times(1)).updateFirst(
          any(), updateCaptor.capture(), eq(apiInfo.getDataCollectionName())
      );

      verify(apiHistoryService, times(1)).writeUpdateHistory(
          originalDataCaptor.capture(), updateDataCaptor.capture(),
          eq(apiInfo.getHistoryCollectionName()), eq(memberId), eq(nowTime)
      );

      Update update = updateCaptor.getValue();
      Document originalData = originalDataCaptor.getValue();
      Map<String, Object> updateData = updateDataCaptor.getValue();

      assertAll(
          () -> assertEquals("updated", ((Document)update.getUpdateObject().get("$set")).get("test")),

          () -> assertEquals(objectId.toString(), originalData.get(MONGODB_ID).toString()),
          () -> assertEquals("test1", originalData.get("test").toString()),

          () -> assertEquals(objectId.toString(), updateData.get(MONGODB_ID).toString()),
          () -> assertEquals("updated", updateData.get("test").toString())
      );

    }

  }

  @Nested
  @DisplayName("deleteApiData() 테스트")
  class deleteApiDataTest {

    @Test
    @DisplayName("Api에 데이터 삭제 성공")
    void success_deleteApiData(){
      //given
      Member member = getAccessMember();
      ApiInfo apiInfo = getApiInfo();

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(member));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(apiUserPermissionRepository.findByApiInfoAndMember(any(), any()))
          .willReturn(Optional.of(getApiUserPermission(true)));

      given(mongoTemplate.exists(any(), anyString()))
          .willReturn(true);

      ObjectId objectId = new ObjectId(new Date()); //original object id;
      Document document = new Document();
      document.put("_id", objectId);
      document.put("test", "test1");

      given(mongoTemplate.findOne(any(), any(), any()))
          .willReturn(document);

      //when
      DeleteApiDataInput input = DeleteApiDataInput.builder()
          .dataId(objectId.toString())
          .build();
      long apiId = apiInfo.getId();
      long memberId = member.getId();
      LocalDateTime nowTime = LocalDateTime.now();

      apiService.deleteApiData(input, apiId, memberId, LocalDateTime.now());

      //then
      ArgumentCaptor<Document> originalDataCaptor = ArgumentCaptor.forClass(Document.class);

      verify(mongoTemplate, times(1)).remove(any(), eq(apiInfo.getDataCollectionName()));
      verify(apiHistoryService, times(1)).writeDeleteHistory(
          originalDataCaptor.capture(), eq(apiInfo.getHistoryCollectionName())
          ,eq(memberId), eq(nowTime)
      );

      Document originalData = originalDataCaptor.getValue();

      assertAll(
          () -> assertEquals(objectId.toString(), originalData.get(MONGODB_ID).toString()),
          () -> assertEquals("test1", originalData.get("test"))
      );

    }

  }

  @Nested
  @DisplayName("deleteOpenApi() 테스트")
  class deleteOpenApiTest {

    @Test
    @DisplayName("Api 삭제 성공")
    void success_deleteOpenApi(){
      //given
      ApiInfo apiInfo = getApiInfo();
      Member member = getOwnerMember();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(member));

      given(mongoTemplate.collectionExists(eq(apiInfo.getDataCollectionName())))
          .willReturn(true);

      given(mongoTemplate.collectionExists(eq(apiInfo.getHistoryCollectionName())))
          .willReturn(true);

      //when
      apiService.deleteOpenApi(1L, 1L);

      //then
      verify(mongoTemplate, times(1))
          .dropCollection(eq(apiInfo.getDataCollectionName()));
      verify(mongoTemplate, times(1))
          .dropCollection(eq(apiInfo.getHistoryCollectionName()));
      verify(apiInfoElasticRepository, times(1))
          .deleteAccessors(eq(apiInfo.getId()));
      verify(apiInfoElasticRepository, times(1))
          .deleteById(eq(apiInfo.getId().toString()));

      ArgumentCaptor<ApiInfo> captor = ArgumentCaptor.forClass(ApiInfo.class);
      verify(apiInfoRepository, times(1)).delete(captor.capture());
      assertEquals(apiInfo.getId(), captor.getValue().getId());

    }

  }

  @Nested
  @DisplayName("enableOpenApi() 테스트")
  class enableOpenApiTest {

    @Test
    @DisplayName("Api 활성화 성공")
    void success_enableOpenApi(){
      //given
      ApiInfo apiInfo = getApiInfo();
      apiInfo.setApiState(ApiState.DISABLED);
      Member member = getOwnerMember();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(member));

      given(mongoUtil.getDbSizeByCollection(any(), anyString()))
          .willReturn(1000L);

      MongoCollection mockCollection = Mockito.mock(MongoCollection.class);
      given(mongoTemplate.getCollection(anyString()))
          .willReturn(mockCollection);
      given(mockCollection.countDocuments())
          .willReturn(1L);

      given(apiInfoRepository.countByMemberAndApiState(any(), any()))
          .willReturn(0);

      given(apiUserPermissionRepository.countByApiInfo(any()))
          .willReturn(0);

      //when
      LocalDate date = LocalDate.of(2023, 11, 15);
      apiService.enableOpenApi(1L, 1L, date);

      //then
      ArgumentCaptor<ApiInfo> captor = ArgumentCaptor.forClass(ApiInfo.class);
      verify(apiInfoRepository, times(1)).save(captor.capture());
      assertEquals(ApiState.ENABLED, captor.getValue().getApiState());
    }

  }

}