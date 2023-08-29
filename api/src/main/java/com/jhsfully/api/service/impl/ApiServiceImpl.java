package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.ApiQueryType.EQUAL;
import static com.jhsfully.domain.type.ApiQueryType.INCLUDE;
import static com.jhsfully.domain.type.ApiQueryType.START;
import static com.jhsfully.domain.type.ApiStructureType.STRING;
import static com.jhsfully.domain.type.errortype.ApiErrorType.API_FIELD_COUNT_IS_DIFFERENT;
import static com.jhsfully.domain.type.errortype.ApiErrorType.API_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiErrorType.DOES_NOT_EXCEL_FILE;
import static com.jhsfully.domain.type.errortype.ApiErrorType.DUPLICATED_QUERY_PARAMETER;
import static com.jhsfully.domain.type.errortype.ApiErrorType.DUPLICATED_SCHEMA;
import static com.jhsfully.domain.type.errortype.ApiErrorType.FIELD_WAS_NOT_DEFINITION_IN_SCHEMA;
import static com.jhsfully.domain.type.errortype.ApiErrorType.FILE_PARSE_ERROR;
import static com.jhsfully.domain.type.errortype.ApiErrorType.QUERY_PARAMETER_CANNOT_MATCH;
import static com.jhsfully.domain.type.errortype.ApiErrorType.QUERY_PARAMETER_NOT_INCLUDE_SCHEMA;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.USER_HAS_NOT_API;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.USER_HAS_NOT_PERMISSION;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.YOU_ARE_NOT_API_OWNER;
import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhsfully.api.exception.ApiException;
import com.jhsfully.api.exception.ApiPermissionException;
import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.model.api.CreateApiInput;
import com.jhsfully.api.model.api.CreateApiInput.QueryData;
import com.jhsfully.api.model.api.CreateApiInput.SchemaData;
import com.jhsfully.api.model.api.DeleteApiDataInput;
import com.jhsfully.api.model.api.InsertApiDataInput;
import com.jhsfully.api.model.api.InsertApiDataResponse;
import com.jhsfully.api.model.api.UpdateApiDataInput;
import com.jhsfully.api.service.ApiService;
import com.jhsfully.api.util.ConvertUtil;
import com.jhsfully.api.util.FileUtil;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiPermissionDetail;
import com.jhsfully.domain.entity.ApiUserPermission;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.kafkamodel.ExcelParserModel;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
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

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ApiServiceImpl implements ApiService {

  //repositories
  private final ApiInfoRepository apiInfoRepository;
  private final ApiUserPermissionRepository apiUserPermissionRepository;
  private final MemberRepository memberRepository;
  private final MongoTemplate mongoTemplate;


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
  public void createOpenApi(CreateApiInput input) throws JsonProcessingException {
    validateCreateOpenApi(input);

    Map<String, ApiStructureType> schemaStructure = input.getSchemaStructure().stream()
        .collect(Collectors.toMap(SchemaData::getField, SchemaData::getType));
    Map<String, ApiQueryType> queryParameter = input.getQueryParameter().stream()
            .collect(Collectors.toMap(QueryData::getField, QueryData::getType));

    String dataCollectionName = UUID.randomUUID().toString().replaceAll("-", "");
    String historyCollectionName = dataCollectionName + "-history";

    String filePath = fileSave(input.getFile(), dataCollectionName);

    ApiInfo apiInfo = apiInfoRepository.save(ApiInfo.builder()
        .apiName(input.getApiName())
        .apiIntroduce(input.getApiIntroduce())
        .schemaStructure(schemaStructure)
        .queryParameter(queryParameter)
        .dataCollectionName(dataCollectionName)
        .historyCollectionName(historyCollectionName)
        .apiState(ApiState.READY)
        .isPublic(input.isPublic())
        .build());

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
    validateInsertApiData(input, memberId);

    ApiInfo apiInfo = apiInfoRepository.findById(input.getApiId())
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

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

    return new InsertApiDataResponse(
        result.getInsertedId().asObjectId().getValue().toString()
    );
  }

  /*
      API의 데이터를 수정함.
      데이터 수정은 일부만 포함되도 됨.
   */
  public void updateApiData(UpdateApiDataInput input, long memberId){
    validateUpdateApiData(input, memberId);

    ApiInfo apiInfo = apiInfoRepository.findById(input.getApiId())
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    Map<String, ApiStructureType> schemaMap = apiInfo.getSchemaStructure();

    //업데이트 할 데이터 설정.
    Update update = new Update();

    //데이터 형변환
    for(Map.Entry<String, Object> data : input.getUpdateData().entrySet()){
      ApiStructureType structureType = schemaMap.get(data.getKey());
      update.set(data.getKey(), ConvertUtil.ObjectToStructureType(data.getValue(), structureType));
    }

    //변경 할 데이터를 검색할 쿼리 생성
    Query query = new Query(Criteria.where("_id").is(new ObjectId(input.getDataId())));

    //데이터 업데이트
    mongoTemplate.updateFirst(query, update, apiInfo.getDataCollectionName());
  }

  /*
      id만 일치하면 삭제 할 수 있음.
   */
  public void deleteApiData(DeleteApiDataInput input, long memberId){
    validateDeleteApiData(input, memberId);

    ApiInfo apiInfo = apiInfoRepository.findById(input.getApiId())
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    Query query = new Query(Criteria.where("_id").is(new ObjectId(input.getDataId())));

    mongoTemplate.remove(query, apiInfo.getDataCollectionName());
  }

  /*
      연관 데이터 존재시에는, 소프트 삭제,
      연관 데이터가 없다면, 하드 삭제
   */
  public void deleteOpenApi(long apiId, long memberId){

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
  private void validateCreateOpenApi(CreateApiInput input) {

    //TODO 결제 기능과 등급기능이 추가되면, 해당 사항에 맞는 제한사항이 추가적으로 확인해야함.

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
      API의 데이터를 관리하는 밸리데이션 로직은,
      추후에, 반드시 사용자의 등급에 관련된 제약사항을 확인해야 함.
   */

  private void validateInsertApiData(InsertApiDataInput input, long memberId){
    validateApiDataManageCommon(input.getApiId(), memberId, ApiPermissionType.INSERT);

    ApiInfo apiInfo = apiInfoRepository.findById(input.getApiId())
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    Map<String, ApiStructureType> schemaMap = apiInfo.getSchemaStructure();

    // 정의된 필드가 갯수가 같아야 함.
    if(input.getInsertData().size() != schemaMap.size()){
      throw new ApiException(API_FIELD_COUNT_IS_DIFFERENT);
    }

    // 존재하지 하지 않는 필드명 확인.
    for(Map.Entry<String, Object> apiData : input.getInsertData().entrySet()){

      if (!schemaMap.containsKey(apiData.getKey())){
        throw new ApiException(FIELD_WAS_NOT_DEFINITION_IN_SCHEMA);
      }
    }

  }

  private void validateUpdateApiData(UpdateApiDataInput input, long memberId){
    validateApiDataManageCommon(input.getApiId(), memberId, ApiPermissionType.UPDATE);

    ApiInfo apiInfo = apiInfoRepository.findById(input.getApiId())
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    Map<String, ApiStructureType> schemaMap = apiInfo.getSchemaStructure();

    // 존재하지 하지 않는 필드명 확인.
    for(Map.Entry<String, Object> apiData : input.getUpdateData().entrySet()){

      if (!schemaMap.containsKey(apiData.getKey())){
        throw new ApiException(FIELD_WAS_NOT_DEFINITION_IN_SCHEMA);
      }
    }
  }

  private void validateDeleteApiData(DeleteApiDataInput input, long memberId){
    validateApiDataManageCommon(input.getApiId(), memberId, ApiPermissionType.DELETE);
  }

  private void validateDeleteOpenApi(long apiId, long memberId){
    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    //소유주는 권한 확인이 필요없음.
    if(!Objects.equals(apiInfo.getMember().getId(), member.getId())){
      throw new ApiPermissionException(YOU_ARE_NOT_API_OWNER);
    }

    //TODO continue~~~


  }

  private void validateApiDataManageCommon(long apiId, long memberId, ApiPermissionType type){

    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    //소유주는 권한 확인이 필요없음.
    if(Objects.equals(apiInfo.getMember().getId(), member.getId())){
      return;
    }

    //이외의 유저는 확인해야함.
    ApiUserPermission userPermission = apiUserPermissionRepository
        .findByApiInfoAndMember(apiInfo, member)
        .orElseThrow(() -> new ApiPermissionException(USER_HAS_NOT_API));

    for(ApiPermissionDetail permission : userPermission.getApiPermissionDetails()){
      //해당 권한이 있을 경우 바로 리턴
      if(type == permission.getType()){
        return;
      }
    }

    //권한을 확인해도 존재하지 않을 경우에는 에러 발생.
    throw new ApiPermissionException(USER_HAS_NOT_PERMISSION);

  }

}
