package com.jhsfully.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhsfully.api.exception.ApiException;
import com.jhsfully.api.exception.ApiPermissionException;
import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.exception.GradeException;
import com.jhsfully.api.model.api.*;
import com.jhsfully.api.model.api.CreateApiInput.QueryData;
import com.jhsfully.api.model.api.CreateApiInput.SchemaData;
import com.jhsfully.api.service.ApiHistoryService;
import com.jhsfully.api.service.ApiService;
import com.jhsfully.api.util.ConvertUtil;
import com.jhsfully.api.util.FileUtil;
import com.jhsfully.api.util.MongoUtil;
import com.jhsfully.domain.entity.ApiInfo;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.jhsfully.domain.type.ApiQueryType.*;
import static com.jhsfully.domain.type.ApiStructureType.STRING;
import static com.jhsfully.domain.type.errortype.ApiErrorType.*;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.*;
import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.GradeErrorType.MEMBER_HAS_NOT_GRADE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ApiServiceImpl implements ApiService {

  //constants
  private static final String MONGODB_ID = "_id";
  private static final String HISTORY_SUFFIX = "-history";

  //repositories
  private final ApiInfoRepository apiInfoRepository;
  private final ApiUserPermissionRepository apiUserPermissionRepository;
  private final MemberRepository memberRepository;
  private final MongoTemplate mongoTemplate;
  private final ApiInfoElasticRepository apiInfoElasticRepository;

  //service
  private final ApiHistoryService apiHistoryService;


  //for kafka
  private final KafkaTemplate<String, ExcelParserModel> kafkaTemplate;
  private final ObjectMapper objectMapper;
  @Value("${spring.excel-storage-path}")
  private String EXCEL_STORAGE_PATH;
  @Value("${spring.kafka.topic-name}")
  private String KAFKA_TOPIC_NAME;

  /*
      OpenAPI를 새로 생성함.
   */
  public void createOpenApi(CreateApiInput input, long memberId) throws JsonProcessingException {

    Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    validateCreateOpenApi(input, member);

    Map<String, ApiStructureType> schemaStructure = input.getSchemaStructure().stream()
        .collect(Collectors.toMap(SchemaData::getField, SchemaData::getType));
    Map<String, ApiQueryType> queryParameter = input.getQueryParameter().stream()
            .collect(Collectors.toMap(QueryData::getField, QueryData::getType));

    String dataCollectionName = UUID.randomUUID().toString().replaceAll("-", "");
    String historyCollectionName = dataCollectionName + HISTORY_SUFFIX;

    boolean fileEmpty = input.getFile() == null || input.getFile().isEmpty();

    ApiInfo apiInfo = apiInfoRepository.save(ApiInfo.builder()
        .apiName(input.getApiName())
        .member(member)
        .apiIntroduce(input.getApiIntroduce())
        .schemaStructure(schemaStructure)
        .queryParameter(queryParameter)
        .dataCollectionName(dataCollectionName)
        .historyCollectionName(historyCollectionName)
        .apiState(fileEmpty ? ApiState.ENABLED : ApiState.READY)
        .isPublic(input.isPublic())
        .build());

    /*
        입력된 파일이 있는 경우만, 파일 경로를 생성하도록 함.
     */
    String filePath = fileEmpty ? "" : fileSave(input.getFile(), dataCollectionName);

    //kafka가 받기 위한 모델임.
    ExcelParserModel model = ExcelParserModel.builder()
        .apiInfoId(apiInfo.getId())
        .excelPath(filePath)
        .dataCollectionName(dataCollectionName)
        .schemaStructure(schemaStructure)
        .queryParameter(queryParameter)
        .build();

    sendKafka(model);
  }

  private String fileSave(MultipartFile file, String fileName){

    try {
      if (!FileUtil.validFileExtension(file)) {
        throw new ApiException(DOES_NOT_EXCEL_FILE);
      }
    }catch (IOException e){
      throw new ApiException(FILE_PARSE_ERROR);
    }

    /*
        상단에서, 이미 검사를 마쳤기 때문에, split해서 확장자를 가져올 수 있음.
     */
    String fileExtension = file.getOriginalFilename().split("\\.")[1];
    String filepath = EXCEL_STORAGE_PATH + "/" + fileName + "." + fileExtension;
    try {
      File newFile = new File(filepath);
      FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(newFile));
    } catch (IOException e) {
      throw new ApiException(FILE_PARSE_ERROR);
    }
    return filepath;
  }

  private void sendKafka(ExcelParserModel model) throws JsonProcessingException {
    kafkaTemplate.send(KAFKA_TOPIC_NAME,
        model
    );
  }


  /*
      API에 데이터 추가함.
      데이터 추가시에는, 반드시 모든 데이터를 기입해야함.
   */
  public InsertApiDataResponse insertApiData(InsertApiDataInput input, long memberId){
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    ApiInfo apiInfo = apiInfoRepository.findById(input.getApiId())
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    validateInsertApiData(input, apiInfo, member);

    Map<String, ApiStructureType> schemaMap = apiInfo.getSchemaStructure();

    //데이터 형변환
    for(Map.Entry<String, Object> data : input.getInsertData().entrySet()){
      ApiStructureType structureType = schemaMap.get(data.getKey());
      input.getInsertData().put(data.getKey(), ConvertUtil.ObjectToStructureType(data.getValue(), structureType));
    }

    //데이터를 bson.Document 형식으로 변환하기
    Document document = new Document(input.getInsertData());

    MongoCollection<Document> collection = mongoTemplate.getCollection(
        apiInfo.getDataCollectionName());

    InsertOneResult result = collection.insertOne(document);

    input.getInsertData().put(MONGODB_ID, result.getInsertedId().asObjectId().getValue());

    //히스토리 로그 남기기.
    apiHistoryService.writeInsertHistory(
        input.getInsertData(),
        apiInfo.getHistoryCollectionName(),
        memberId
    );

    return new InsertApiDataResponse(
        result.getInsertedId().asObjectId().getValue().toString()
    );
  }

  /*
      API의 데이터를 수정함.
      데이터 수정은 일부만 포함되도 됨.
   */
  public void updateApiData(UpdateApiDataInput input, long memberId){
    Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    ApiInfo apiInfo = apiInfoRepository.findById(input.getApiId())
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    validateUpdateApiData(input, apiInfo, member);

    Map<String, ApiStructureType> schemaMap = apiInfo.getSchemaStructure();

    //변경 할 데이터를 검색할 쿼리 생성
    Query query = new Query(Criteria.where(MONGODB_ID).is(new ObjectId(input.getDataId())));

    //id가 존재하는지 확인하기.
    if(!mongoTemplate.exists(query, apiInfo.getDataCollectionName())){
      throw new ApiException(DATA_IS_NOT_FOUND);
    }

    //기존 데이터 가져오기.
    Map<String, Object> originalData = mongoTemplate.findOne(query, Map.class, apiInfo.getDataCollectionName());

    //업데이트 할 데이터 설정.
    Update update = new Update();

    //데이터 형변환
    input.getUpdateData().forEach((key, value) -> {
        ApiStructureType structureType = schemaMap.get(key);
        update.set(key, ConvertUtil.ObjectToStructureType(value, structureType));
    });

    //데이터 업데이트
    mongoTemplate.updateFirst(query, update, apiInfo.getDataCollectionName());

    //히스토리에 로그 남기기
    input.getUpdateData().put(MONGODB_ID, new ObjectId(input.getDataId()));
    apiHistoryService.writeUpdateHistory(
        originalData,
        input.getUpdateData(),
        apiInfo.getHistoryCollectionName(),
        memberId
    );
  }

  /*
      id만 일치하면 삭제 할 수 있음.
   */
  public void deleteApiData(DeleteApiDataInput input, long memberId){

    Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    ApiInfo apiInfo = apiInfoRepository.findById(input.getApiId())
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    validateDeleteApiData(apiInfo, member);

    Query query = new Query(Criteria.where(MONGODB_ID).is(new ObjectId(input.getDataId())));

    //기존 데이터 가져오기.
    Map<String, Object> originalData = mongoTemplate.findOne(query, Map.class, apiInfo.getDataCollectionName());

    //id가 존재하는지 확인하기.
    if(!mongoTemplate.exists(query, apiInfo.getDataCollectionName())){
      throw new ApiException(DATA_IS_NOT_FOUND);
    }

    //데이터 지우기
    mongoTemplate.remove(query, apiInfo.getDataCollectionName());

    //로그 남기기
    apiHistoryService.writeDeleteHistory(
        originalData,
        apiInfo.getHistoryCollectionName(),
        memberId
    );
  }

  /*
      DELETE는 기록 및 FK참조로 인해, SOFT DELETE방식으로 제거해야함.
   */
  public void deleteOpenApi(long apiId, long memberId){
    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    validateDeleteOpenApi(apiInfo, member);

    /*
        MongoDB, ElasticSearch, MySQL의 데이터를 모두 참조하여 삭제를 진행하여야 함.
     */

    //MongoDB Deletions
    if(mongoTemplate.collectionExists(apiInfo.getDataCollectionName())){//데이터 컬렉션 삭제
      mongoTemplate.dropCollection(apiInfo.getDataCollectionName());
    }

    if(mongoTemplate.collectionExists(apiInfo.getHistoryCollectionName())){//히스토리 컬렉션 삭제
      mongoTemplate.dropCollection(apiInfo.getHistoryCollectionName());
    }

    apiInfoElasticRepository.deleteAccessors(apiInfo.getId());

    //delete apiinfo
    apiInfoElasticRepository.deleteById(apiInfo.getId().toString());


    //RDB Deletions
    //@SQLDelete 어노테이션을 사용하기 때문에, 이렇게 삭제해도, SOFT DELETE가 적용됨.
    apiInfoRepository.delete(apiInfo);
  }

  //유저가 직접 비활성화 된, OpenAPI를 활성화 시키는 메서드
  public void enableOpenApi(long apiId, long memberId){
    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    validateEnableOpenApi(apiInfo, member);

    Grade grade = member.getGrade();

    //비교해서, 가능 할 경우, API를 활성화 하도록 함.
    long dbSize = MongoUtil.getDbSizeByCollection(mongoTemplate, apiInfo.getDataCollectionName());
    long recordCount = mongoTemplate.getCollection(apiInfo.getDataCollectionName())
        .countDocuments();
    int queryCount = apiInfo.getQueryParameter().size();
    int schemaCount = apiInfo.getSchemaStructure().size();
    int apiCount = apiInfoRepository.countByMemberAndApiState(member, ApiState.ENABLED);
    int accessorCount = apiUserPermissionRepository.countByApiInfo(apiInfo);

    if (dbSize <= grade.getDbMaxSize() &&
        recordCount <= grade.getRecordMaxCount() &&
        queryCount <= grade.getQueryMaxCount() &&
        schemaCount <= grade.getFieldMaxCount() &&
        apiCount <= grade.getApiMaxCount() &&
        accessorCount <= grade.getAccessorMaxCount()) {
      apiInfo.setApiState(ApiState.ENABLED);
      apiInfoRepository.save(apiInfo);
    }
  }


  /*
      ###############################################################
      ###############                           #####################
      ###############          Validates        #####################
      ###############                           #####################
      ###############################################################
   */

  /*
      오픈 API를 만들기 전에, 수행하는 밸리데이션
   */
  private void validateCreateOpenApi(CreateApiInput input, Member member) {

    /*
        등급에 위반되는 데이터가 있는지 우선적으로 확인해야함.
     */

    Grade grade = member.getGrade();

    if(member.getGrade() == null){
      throw new GradeException(MEMBER_HAS_NOT_GRADE);
    }

    //엑셀 용량 확인하기. Byte단위로 들어옴.
    if(input.getFile() != null && !input.getFile().isEmpty()){
      if(input.getFile().getSize() > grade.getDbMaxSize()){
        throw new ApiException(OVERFLOW_MAX_FILE_SIZE);
      }
    }

    //field의 갯수 확인하기.
    if(input.getSchemaStructure() == null || input.getSchemaStructure().isEmpty()){
      throw new ApiException(SCHEMA_COUNT_IS_ZERO);
    }
    if(input.getSchemaStructure().size() > grade.getFieldMaxCount()){
      throw new ApiException(OVERFLOW_FIELD_MAX_COUNT);
    }

    //query parameter 갯수 확인하기.
    if(input.getQueryParameter() != null && !input.getQueryParameter().isEmpty()){
      if(input.getQueryParameter().size() > grade.getQueryMaxCount()){
        throw new ApiException(OVERFLOW_QUERY_MAX_COUNT);
      }
    }

    //현재 소유한 API 갯수 찾아오기.
    int apiCount = apiInfoRepository.countByMember(member);

    if(apiCount >= grade.getApiMaxCount()){
      throw new ApiException(OVERFLOW_API_MAX_COUNT);
    }

    //스키마의 구조에는 중복 필드가 없는가?
    Map<String, ApiStructureType> mapStructure = new HashMap<>();

    for (SchemaData data : input.getSchemaStructure()) {
      if (mapStructure.containsKey(data.getField())) {
        throw new ApiException(DUPLICATED_SCHEMA);
      }
      mapStructure.put(data.getField(), data.getType());
    }

    Map<String, ApiQueryType> mapQueryParameter = new HashMap<>();

    for (QueryData data : input.getQueryParameter()) {
      //입력된 쿼리 파라미터에는 중복 필드가 없는가?
      if (mapQueryParameter.containsKey(data.getField())) {
        throw new ApiException(DUPLICATED_QUERY_PARAMETER);
      }
      //쿼리 파라미터 필드는 스키마 필드에 속해 있는가?
      if (!mapStructure.containsKey(data.getField())) {
        throw new ApiException(QUERY_PARAMETER_NOT_INCLUDE_SCHEMA);
      }
      mapQueryParameter.put(data.getField(), data.getType());
    }

    /*
         쿼리 파라미터가 스트럭쳐에 따라 적정한 값을 가지고 있는지 확인.
         STRING => INCLUDE, START, EQUAL
         EXTRAS => EQUAL, GT, GTE, LT, LTE
     */
    for (QueryData data : input.getQueryParameter()) {

      ApiStructureType structureType = mapStructure.get(data.getField());

      if (structureType == STRING) {
        if (data.getType() != INCLUDE && data.getType() != START && data.getType() != EQUAL) {
          throw new ApiException(QUERY_PARAMETER_CANNOT_MATCH);
        }
      } else {
        if (data.getType() == INCLUDE || data.getType() == START) {
          throw new ApiException(QUERY_PARAMETER_CANNOT_MATCH);
        }
      }
    }
  }


  /*
      API의 데이터를 관리하는 밸리데이션 로직
   */

  private void validateInsertApiData(InsertApiDataInput input, ApiInfo apiInfo, Member member){
    validateApiDataManageCommon(apiInfo, member, ApiPermissionType.INSERT);

    Map<String, ApiStructureType> schemaMap = apiInfo.getSchemaStructure();

    // 정의된 필드가 갯수가 같아야 함.
    if(input.getInsertData().size() != schemaMap.size()){
      throw new ApiException(API_FIELD_COUNT_IS_DIFFERENT);
    }

    // 존재하지 하지 않는 필드명 확인.
    input.getInsertData().entrySet().stream()
            .filter(apiData -> !schemaMap.containsKey(apiData.getKey()))
            .findAny()
            .ifPresent(x ->
              {
                throw new ApiException(FIELD_WAS_NOT_DEFINITION_IN_SCHEMA);
              }
            );

  }

  private void validateUpdateApiData(UpdateApiDataInput input, ApiInfo apiInfo, Member member){
    validateApiDataManageCommon(apiInfo, member, ApiPermissionType.UPDATE);

    Map<String, ApiStructureType> schemaMap = apiInfo.getSchemaStructure();

    // 존재하지 하지 않는 필드명 확인.
    input.getUpdateData().entrySet().stream()
            .filter(apiData -> !schemaMap.containsKey(apiData.getKey()))
            .findAny()
            .ifPresent(x -> {
              throw new ApiException(FIELD_WAS_NOT_DEFINITION_IN_SCHEMA);
            });

  }

  private void validateDeleteApiData(ApiInfo apiInfo, Member member){
    validateApiDataManageCommon(apiInfo, member, ApiPermissionType.DELETE);
  }

  private void validateDeleteOpenApi(ApiInfo apiInfo, Member member){

    //소유주는 권한 확인이 필요없음.
    if(!Objects.equals(apiInfo.getMember().getId(), member.getId())){
      throw new ApiPermissionException(YOU_ARE_NOT_API_OWNER);
    }

  }

  private void validateApiDataManageCommon(ApiInfo apiInfo, Member member, ApiPermissionType type){

    //활성화 된 API가 아닌 경우 THROW
    if(apiInfo.getApiState() != ApiState.ENABLED){
      throw new ApiException(API_IS_DISABLED);
    }

    // INSERT, UPDATE 시에는 db의 용량이 제한된 용량을 넘어가는지 판단해야함.
    if(type == ApiPermissionType.INSERT || type == ApiPermissionType.UPDATE){
      Grade grade = apiInfo.getMember().getGrade();

      long dbCollectionSize = MongoUtil.getDbSizeByCollection(mongoTemplate, apiInfo.getDataCollectionName());

      if(dbCollectionSize > grade.getDbMaxSize()){
        throw new ApiException(OVERFLOW_MAX_DB_SIZE);
      }
    }

    //소유주는 권한 확인이 필요없음.
    if(Objects.equals(apiInfo.getMember().getId(), member.getId())){
      return;
    }

    //이외의 유저는 확인해야함.
    ApiUserPermission userPermission = apiUserPermissionRepository
        .findByApiInfoAndMember(apiInfo, member)
        .orElseThrow(() -> new ApiPermissionException(USER_HAS_NOT_API));

    userPermission.getApiPermissionDetails().stream()
        .filter(it -> it.getType() == type).findAny()
        .orElseThrow(() -> new ApiPermissionException(USER_HAS_NOT_PERMISSION));

  }

  private void validateEnableOpenApi(ApiInfo apiInfo, Member member){
    if(!Objects.equals(apiInfo.getMember().getId(), member.getId())){
      throw new ApiPermissionException(USER_HAS_NOT_API);
    }

    if(LocalDate.now().isAfter(member.getExpiredEnabledAt())){
      throw new ApiException(TODAY_IS_AFTER_EXPIRED_AT);
    }

    if(apiInfo.getApiState() == ApiState.ENABLED){
      throw new ApiException(API_IS_ALREADY_ENABLED);
    } else if(apiInfo.getApiState() == ApiState.FAILED){
      throw new ApiException(CANNOT_ENABLE_FAILED_API);
    } else if(apiInfo.getApiState() == ApiState.READY) {
      throw new ApiException(CANNOT_ENABLE_READY_API);
    }
  }
}
