package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.ApiQueryType.EQUAL;
import static com.jhsfully.domain.type.ApiQueryType.INCLUDE;
import static com.jhsfully.domain.type.ApiQueryType.START;
import static com.jhsfully.domain.type.ApiStructureType.STRING;
import static com.jhsfully.domain.type.errortype.ApiErrorType.DOES_NOT_EXCEL_FILE;
import static com.jhsfully.domain.type.errortype.ApiErrorType.DUPLICATED_QUERY_PARAMETER;
import static com.jhsfully.domain.type.errortype.ApiErrorType.DUPLICATED_SCHEMA;
import static com.jhsfully.domain.type.errortype.ApiErrorType.QUERY_PARAMETER_CANNOT_MATCH;
import static com.jhsfully.domain.type.errortype.ApiErrorType.QUERY_PARAMETER_NOT_INCLUDE_SCHEMA;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhsfully.api.exception.ApiException;
import com.jhsfully.api.model.api.CreateApiInput;
import com.jhsfully.api.model.api.CreateApiInput.QueryData;
import com.jhsfully.api.model.api.CreateApiInput.SchemaData;
import com.jhsfully.api.service.ApiService;
import com.jhsfully.api.util.FileUtil;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.kafkamodel.ExcelParserModel;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.type.ApiQueryType;
import com.jhsfully.domain.type.ApiState;
import com.jhsfully.domain.type.ApiStructureType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiServiceImpl implements ApiService {

  private final ApiInfoRepository apiInfoRepository;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;
  @Value("${spring.excel-storage-path}")
  private String EXCEL_STORAGE_PATH;

  public void createOpenApi(CreateApiInput input) throws JsonProcessingException {
    validateCreateOpenApi(input);

    Map<String, ApiStructureType> schemaStructure = new HashMap<>();
    Map<String, ApiQueryType> queryParameter = new HashMap<>();

    input.getSchemaStructure().forEach(
        x -> schemaStructure.put(x.getField(), x.getType())
    );

    input.getQueryParameter().forEach(
        x -> queryParameter.put(x.getField(), x.getType())
    );

    String dataCollectionName = UUID.randomUUID().toString().replaceAll("-", "");
    String historyCollectionName = dataCollectionName + "-history";
    String filePath = null;

    filePath = fileSave(input.getFile(), dataCollectionName);

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
      throw new ApiException(DOES_NOT_EXCEL_FILE);
    }

    String filepath = EXCEL_STORAGE_PATH + "/" + fileName;
    try {
      File newFile = new File(filepath);
      FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(newFile));
    } catch (IOException e) {
      throw new ApiException(DOES_NOT_EXCEL_FILE);
    }
    return filepath;
  }

  private void sendKafka(ExcelParserModel model) throws JsonProcessingException {
    kafkaTemplate.send("excelparser",
        objectMapper.writeValueAsString(model)
    );
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

    //입력된 쿼리 파라미터에는 중복 필드가 없는가?
    Map<String, ApiQueryType> mapQueryParameter = new HashMap<>();

    for (QueryData data : input.getQueryParameter()) {
      if (mapQueryParameter.containsKey(data.getField())) {
        throw new ApiException(DUPLICATED_QUERY_PARAMETER);
      }
      mapQueryParameter.put(data.getField(), data.getType());
    }

    //쿼리 파라미터 필드는 스키마 필드에 속해 있는가?
    for (QueryData data : input.getQueryParameter()) {
      if (!mapStructure.containsKey(data.getField())) {
        throw new ApiException(QUERY_PARAMETER_NOT_INCLUDE_SCHEMA);
      }
    }

    /*
         쿼리 파라미터가 스트럭쳐에 따라 적정한 값을 가지고 있는지 확인.
         STRING => INCLUDE, START, EQUAL
         EXTRAS => EQUAL, GT, GTE, LT, LTE
     */
    for (SchemaData data : input.getSchemaStructure()) {
      ApiQueryType queryType = mapQueryParameter.get(data.getField());
      if (data.getType() == STRING) {
        if (queryType != INCLUDE && queryType != START && queryType != EQUAL) {
          throw new ApiException(QUERY_PARAMETER_CANNOT_MATCH);
        }
      } else {
        if (queryType == INCLUDE || queryType == START) {
          throw new ApiException(QUERY_PARAMETER_CANNOT_MATCH);
        }
      }
    }

  }

}
