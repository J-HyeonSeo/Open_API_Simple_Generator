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
import com.jhsfully.api.model.PageResponse;
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
import org.springframework.data.domain.PageImpl;
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

  //constants
  private static final String MONGODB_ID = "_id";
  private static final String MONGODB_AT_COL = "at";
  private static final String MONGODB_MEMBER_NAME_COL = "member_name";
  private static final String MONGODB_MEMBER_EMAIL_COL = "member_email";
  private static final String MONGODB_PROFILE_IMAGE_COL = "profile_image";
  private static final String MONGODB_TYPE_COL = "type";
  private static final String MONGODB_ORIGINAL_COL = "original_data";
  private static final String MONGODB_NEW_COL = "new_data";

  private final MongoTemplate mongoTemplate;
  private final MemberRepository memberRepository;
  private final ApiInfoRepository apiInfoRepository;

  @Override
  @Transactional(readOnly = true)
  public PageResponse<Document> getApiHistories(
      long apiId, long memberId,
      LocalDate startDate,
      LocalDate endDate,
      Pageable pageable
  ){

    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    validateGetHistories(apiInfo, member);

    LocalDateTime startDateTime = startDate.atStartOfDay().plusHours(9);
    LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX).plusHours(9);

    //질의문 생성
    Query query = new Query(Criteria.where(MONGODB_AT_COL).gte(startDateTime).lte(endDateTime));

    //총 데이터 갯수
    long totalCount = mongoTemplate.count(query, apiInfo.getHistoryCollectionName());

    query.with(pageable);

    //데이터 질의
    List<Document> queriedDataList = mongoTemplate.find(query, Document.class, apiInfo.getHistoryCollectionName())
        .stream()
        .peek(x -> x.put(MONGODB_ID, x.get(MONGODB_ID).toString()))
        .collect(Collectors.toList());

    return PageResponse.of(new PageImpl<>(queriedDataList, pageable, totalCount));
  }

  @Override
  public void writeInsertHistory(
      Map<String, Object> insertData,
      String historyCollection,
      long memberId, LocalDateTime nowTime){

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));
    MongoCollection<Document> collection = mongoTemplate.getCollection(historyCollection);

    //기록할 데이터 생성하기.
    Document document = new Document();
    document.append(MONGODB_AT_COL, nowTime);
    document.append(MONGODB_MEMBER_NAME_COL, member.getNickname());
    document.append(MONGODB_MEMBER_EMAIL_COL, member.getEmail());
    document.append(MONGODB_PROFILE_IMAGE_COL, member.getProfileUrl());
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
      long memberId, LocalDateTime nowTime){

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));
    MongoCollection<Document> collection = mongoTemplate.getCollection(historyCollection);

    //기록할 데이터 생성하기.
    Document document = new Document();
    document.append(MONGODB_AT_COL, nowTime);
    document.append(MONGODB_MEMBER_NAME_COL, member.getNickname());
    document.append(MONGODB_MEMBER_EMAIL_COL, member.getEmail());
    document.append(MONGODB_PROFILE_IMAGE_COL, member.getProfileUrl());
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
      long memberId, LocalDateTime nowTime){

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));
    MongoCollection<Document> collection = mongoTemplate.getCollection(historyCollection);

    //기록할 데이터 생성하기.
    Document document = new Document();
    document.append(MONGODB_AT_COL, nowTime);
    document.append(MONGODB_MEMBER_NAME_COL, member.getNickname());
    document.append(MONGODB_MEMBER_EMAIL_COL, member.getEmail());
    document.append(MONGODB_PROFILE_IMAGE_COL, member.getProfileUrl());
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

  private void validateGetHistories(ApiInfo apiInfo, Member member){
    if(!Objects.equals(apiInfo.getMember().getId(), member.getId())){
      throw new ApiPermissionException(YOU_ARE_NOT_API_OWNER);
    }
  }

}
