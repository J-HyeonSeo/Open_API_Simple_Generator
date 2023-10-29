package com.jhsfully.api.service.impl;

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
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.jhsfully.domain.type.ApiPermissionType.*;
import static com.jhsfully.domain.type.errortype.ApiErrorType.API_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.YOU_ARE_NOT_API_OWNER;
import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class ApiHistoryServiceImpl implements ApiHistoryService {

  //constants
  private static final String MONGODB_ID = "_id";
  private static final String MONGODB_AT_COL = "at";
  private static final String MONGODB_MEMBER_COL = "member";
  private static final String MONGODB_TYPE_COL = "type";
  private static final String MONGODB_ORIGINAL_COL = "original_data";
  private static final String MONGODB_NEW_COL = "new_data";

  private final MongoTemplate mongoTemplate;
  private final MemberRepository memberRepository;
  private final ApiInfoRepository apiInfoRepository;

  @Override
  public HistoryResponse getApiHistories(
      long apiId, long memberId,
      LocalDate startDate,
      LocalDate endDate,
      Pageable pageable
  ){

    validateGetHistories(apiId, memberId);

    LocalDateTime startDateTime = startDate.atStartOfDay();
    LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    //질의문 생성
    Query query = new Query(Criteria.where(MONGODB_AT_COL).gte(startDateTime).lte(endDateTime));

    //총 데이터 갯수
    long totalCount = mongoTemplate.count(query, apiInfo.getHistoryCollectionName());

    query.with(pageable);

    //데이터 질의
    List<Map> queriedDataList = mongoTemplate.find(query, Map.class, apiInfo.getHistoryCollectionName())
        .stream()
        .peek(x -> x.put(MONGODB_ID, x.get(MONGODB_ID).toString()))
        .collect(Collectors.toList());

    //반환객체 생성
    return HistoryResponse
        .builder()
        .totalCount(totalCount)
        .dataCount(queriedDataList.size())
        .histories(queriedDataList)
        .build();
  }

  @Override
  public void writeInsertHistory(
      Map<String, Object> insertData,
      String historyCollection,
      long memberId){

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));
    MongoCollection<Document> collection = mongoTemplate.getCollection(historyCollection);

    //기록할 데이터 생성하기.
    Document document = new Document();
    document.append(MONGODB_AT_COL, LocalDateTime.now());
    document.append(MONGODB_MEMBER_COL, member.getEmail());
    document.append(MONGODB_TYPE_COL, INSERT.name());
    document.append(MONGODB_NEW_COL, insertData);

    //DB에 데이터 기록.
    collection.insertOne(document);

  }

  @Override
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
    document.append(MONGODB_AT_COL, LocalDateTime.now());
    document.append(MONGODB_MEMBER_COL, member.getEmail());
    document.append(MONGODB_TYPE_COL, UPDATE.name());
    document.append(MONGODB_ORIGINAL_COL, originalData);
    document.append(MONGODB_NEW_COL, newData);

    //DB에 데이터 기록.
    collection.insertOne(document);
  }

  @Override
  public void writeDeleteHistory(
      Map<String, Object> originalData,
      String historyCollection,
      long memberId){

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));
    MongoCollection<Document> collection = mongoTemplate.getCollection(historyCollection);

    //기록할 데이터 생성하기.
    Document document = new Document();
    document.append(MONGODB_AT_COL, LocalDateTime.now());
    document.append(MONGODB_MEMBER_COL, member.getEmail());
    document.append(MONGODB_TYPE_COL, DELETE.name());
    document.append(MONGODB_ORIGINAL_COL, originalData);

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
