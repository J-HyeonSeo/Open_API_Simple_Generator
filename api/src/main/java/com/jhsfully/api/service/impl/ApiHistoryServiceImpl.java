package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.ApiPermissionType.DELETE;
import static com.jhsfully.domain.type.ApiPermissionType.INSERT;
import static com.jhsfully.domain.type.ApiPermissionType.UPDATE;
import static com.jhsfully.domain.type.errortype.ApiErrorType.API_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.YOU_ARE_NOT_API_OWNER;
import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;

import com.jhsfully.api.exception.ApiException;
import com.jhsfully.api.exception.ApiPermissionException;
import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.model.history.HistoryResponse;
import com.jhsfully.api.service.ApiHistoryService;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.MemberRepository;
import com.mongodb.client.MongoCollection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ApiHistoryServiceImpl implements ApiHistoryService {

  private final MongoTemplate mongoTemplate;
  private final MemberRepository memberRepository;
  private final ApiInfoRepository apiInfoRepository;

  public HistoryResponse getApiHistories(
      long apiId, long memberId, int pageSize, int pageIdx,
      LocalDate startDate,
      LocalDate endDate
  ){

    validateGetHistories(apiId, memberId);

    LocalDateTime startDateTime = startDate.atStartOfDay();
    LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    //질의문 생성
    Query query = new Query(Criteria.where("at").gte(startDateTime).lte(endDateTime));

    //페이징 객체
    Pageable pageable = PageRequest.of(pageIdx, pageSize);

    //총 데이터 갯수
    long totalCount = mongoTemplate.count(query, apiInfo.getHistoryCollectionName());

    query.with(pageable);

    //데이터 질의
    List<Map> queriedDataList = mongoTemplate.find(query, Map.class, apiInfo.getHistoryCollectionName())
        .stream()
        .peek(x -> x.put("_id", x.get("_id").toString()))
        .collect(Collectors.toList());

    //반환객체 생성
    return HistoryResponse
        .builder()
        .totalCount(totalCount)
        .dataCount(queriedDataList.size())
        .histories(queriedDataList)
        .build();
  }

  public void writeInsertHistory(
      Map<String, Object> insertData,
      String historyCollection,
      long memberId){

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));
    MongoCollection<Document> collection = mongoTemplate.getCollection(historyCollection);

    //기록할 데이터 생성하기.
    Document document = new Document();
    document.append("at", LocalDateTime.now());
    document.append("member", member.getEmail());
    document.append("type", INSERT.name());
    document.append("new_data", insertData);

    //DB에 데이터 기록.
    collection.insertOne(document);
  }

  public void writeUpdateHistory(
      Map<String, Object> originalData,
      Map<String, Object> newData,
      String historyCollection,
      long memberId){

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));
    MongoCollection<Document> collection = mongoTemplate.getCollection(historyCollection);

    //기록할 데이터 생성하기.
    Document document = new Document();
    document.append("at", LocalDateTime.now());
    document.append("member", member.getEmail());
    document.append("type", UPDATE.name());
    document.append("original_data", originalData);
    document.append("new_data", newData);

    //DB에 데이터 기록.
    collection.insertOne(document);
  }

  public void writeDeleteHistory(
      Map<String, Object> originalData,
      String historyCollection,
      long memberId){

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));
    MongoCollection<Document> collection = mongoTemplate.getCollection(historyCollection);

    //기록할 데이터 생성하기.
    Document document = new Document();
    document.append("at", LocalDateTime.now());
    document.append("member", member.getEmail());
    document.append("type", DELETE.name());
    document.append("original_data", originalData);

    //DB에 데이터 기록.
    collection.insertOne(document);

  }

    /*
      ###############################################################
      ###############                           #####################
      ###############          Validates        #####################
      ###############                           #####################
      ###############################################################
   */

  private void validateGetHistories(long apiId, long memberId){
    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    if(!Objects.equals(apiInfo.getMember().getId(), member.getId())){
      throw new ApiPermissionException(YOU_ARE_NOT_API_OWNER);
    }
  }

}
