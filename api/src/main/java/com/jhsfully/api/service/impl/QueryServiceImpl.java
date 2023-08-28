package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.errortype.ApiErrorType.API_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiErrorType.QUERY_PARAMETER_CANNOT_MATCH;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.API_KEY_NOT_ISSUED;

import com.jhsfully.api.exception.ApiException;
import com.jhsfully.api.exception.ApiPermissionException;
import com.jhsfully.api.model.query.QueryInput;
import com.jhsfully.api.model.query.QueryResponse;
import com.jhsfully.api.service.QueryService;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.ApiKeyRepository;
import com.jhsfully.domain.type.ApiQueryType;
import com.jhsfully.domain.type.ApiStructureType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {

  private final ApiInfoRepository apiInfoRepository;
  private final ApiKeyRepository apiKeyRepository;
  private final MongoTemplate mongoTemplate;

  public QueryResponse getDataList(QueryInput input){
    validate(input);

    Pageable pageable = PageRequest.of(input.getPageIdx(), input.getPageSize());
    Query query = getQuery(input);
    ApiInfo apiInfo = apiInfoRepository.findById(input.getApiId())
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    long totalCount = mongoTemplate.count(query, apiInfo.getDataCollectionName());

    query.with(pageable);

    List<Map> results = mongoTemplate.find(query, Map.class, apiInfo.getDataCollectionName());

    return QueryResponse.builder()
        .totalCount(totalCount)
        .dataCount(results.size())
        .dataList(results)
        .build();
  }


    /*
      ###############################################################
      ###############                           #####################
      ###############          Validates        #####################
      ###############                           #####################
      ###############################################################
   */

  private void validate(QueryInput input){
    //authKey에 대한 검증
    ApiInfo apiInfo = apiInfoRepository.findById(input.getApiId())
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    apiKeyRepository.findByApiInfoAndAuthKey(apiInfo, input.getAuthKey())
        .orElseThrow(() -> new ApiPermissionException(API_KEY_NOT_ISSUED));

    //쿼리 파라미터에 대한 검증
    Map<String, ApiQueryType> apiQueryTypeMap = apiInfo.getQueryParameter();

    for(String fieldName : input.getQueryParameter().keySet()){
      if(!apiQueryTypeMap.containsKey(fieldName)){
        throw new ApiException(QUERY_PARAMETER_CANNOT_MATCH);
      }
    }
  }

  /*
      ###############################################################
      ###############                           #####################
      ###############          Utils            #####################
      ###############                           #####################
      ###############################################################
   */

  private Query getQuery(QueryInput input){

    Query query = new Query();

    ApiInfo apiInfo = apiInfoRepository.findById(input.getApiId())
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    Map<String, ApiStructureType> structureTypeMap = apiInfo.getSchemaStructure();
    Map<String, ApiQueryType> queryTypeMap = apiInfo.getQueryParameter();

    List<String> fullTextValue = new ArrayList<>();
    List<String> fullTextField = new ArrayList<>();

    for(Map.Entry<String, Object> inputQueryParam : input.getQueryParameter().entrySet()){
      String field = inputQueryParam.getKey();
      ApiStructureType structureType = structureTypeMap.get(field);

      Object value = "";

      try {
        switch (structureType) {
          case INTEGER:
            value = Long.parseLong(inputQueryParam.getValue().toString());
            break;
          case FLOAT:
            value = Double.parseDouble(inputQueryParam.getValue().toString());
            break;
          case DATE:
            value = LocalDate.parse(inputQueryParam.getValue().toString());
            break;
          default:
            value = inputQueryParam.getValue();
        }
      }catch (Exception e){
        throw new ApiException(QUERY_PARAMETER_CANNOT_MATCH);
      }

      ApiQueryType queryType = queryTypeMap.get(field);

      switch (queryType){
        case INCLUDE:
          fullTextField.add(field);
          fullTextValue.add(value.toString());
          break;
        case START:
          query.addCriteria(Criteria.where(field).regex("^" + value));
          break;
        case EQUAL:
          query.addCriteria(Criteria.where(field).is(value));
          break;
        case GT:
          query.addCriteria(Criteria.where(field).gt(value));
          break;
        case GTE:
          query.addCriteria(Criteria.where(field).gte(value));
          break;
        case LT:
          query.addCriteria(Criteria.where(field).lt(value));
          break;
        case LTE:
          query.addCriteria(Criteria.where(field).lte(value));
          break;
      }
    }

    if (fullTextField.size() == 0){
      return query;
    }

    StringBuilder fullTextSearchWord = new StringBuilder();
    for (int i = 0; i < fullTextValue.size(); i++) {
      fullTextSearchWord.append(fullTextValue.get(i));

      if(i == fullTextValue.size()-1){
        break;
      }
      fullTextSearchWord.append(" ");
    }

    TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matchingAny(
        fullTextSearchWord.toString());
    query.addCriteria(textCriteria);

    //full-text-search - field filtering
    List<Criteria> fieldFilterCriteria = new ArrayList<Criteria>();

    for (int i = 0; i < fullTextField.size(); i++) {
      fieldFilterCriteria.add(Criteria.where(fullTextField.get(i)).regex(fullTextValue.get(i)));
    }

    query.addCriteria(new Criteria().orOperator(fieldFilterCriteria));
    return query;
  }
}
