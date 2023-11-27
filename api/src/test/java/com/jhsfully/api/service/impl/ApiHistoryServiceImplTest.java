package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.ApiPermissionType.DELETE;
import static com.jhsfully.domain.type.ApiPermissionType.INSERT;
import static com.jhsfully.domain.type.ApiPermissionType.UPDATE;
import static com.jhsfully.domain.type.errortype.ApiErrorType.API_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.YOU_ARE_NOT_API_OWNER;
import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.jhsfully.api.exception.ApiException;
import com.jhsfully.api.exception.ApiPermissionException;
import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.model.history.HistoryResponse;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.MemberRepository;
import com.mongodb.client.MongoCollection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;

@ExtendWith(MockitoExtension.class)
class ApiHistoryServiceImplTest {

  //constants
  private static final String MONGODB_ID = "_id";
  private static final String MONGODB_AT_COL = "at";
  private static final String MONGODB_MEMBER_COL = "member";
  private static final String MONGODB_TYPE_COL = "type";
  private static final String MONGODB_ORIGINAL_COL = "original_data";
  private static final String MONGODB_NEW_COL = "new_data";

  @Mock
  private MongoTemplate mongoTemplate;
  @Mock
  private MemberRepository memberRepository;
  @Mock
  private ApiInfoRepository apiInfoRepository;

  @InjectMocks
  private ApiHistoryServiceImpl apiHistoryService;

  private Member getOwnerMember(){
    return Member.builder()
        .id(1L)
        .email("test@test.com")
        .build();
  }

  private ApiInfo getApiInfo(){
    return ApiInfo.builder()
        .id(1L)
        .member(getOwnerMember())
        .historyCollectionName("test-history")
        .build();
  }

  @Nested
  @DisplayName("getApiHistories() 테스트")
  class getApiHistoriesTest {

    @Test
    @DisplayName("API 기록 가져오기 성공")
    void success_getApiHistories(){
      //given
      ApiInfo apiInfo = getApiInfo();
      Member ownerMember = getOwnerMember();

      Document historyData = new Document();
      historyData.put(MONGODB_ID, "12341234");
      historyData.put("test", "test");
      List<Document> queriedDataList = new ArrayList<>(List.of(historyData));

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(mongoTemplate.count(any(), anyString()))
          .willReturn(1L);

      given(mongoTemplate.find(any(), eq(Document.class), anyString()))
          .willReturn(queriedDataList);

      //when
      HistoryResponse historyResponse = apiHistoryService.getApiHistories(1L, 1L,
          LocalDate.now(), LocalDate.now(),
          PageRequest.of(0, 10));

      //then
      assertAll(
          () -> assertEquals(historyData.get(MONGODB_ID), historyResponse.getHistories().get(0).get(MONGODB_ID)),
          () -> assertEquals(historyData.get("test"), historyResponse.getHistories().get(0).get("test"))
      );

    }

    @Test
    @DisplayName("API 기록 가져오기 실패 - API X")
    void fail_getApiHistories_api_not_found(){
      //given
      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      ApiException exception = assertThrows(ApiException.class,
          () -> apiHistoryService.getApiHistories(1L, 1L,
              LocalDate.now(), LocalDate.now(),
              PageRequest.of(0, 10)));

      //then
      assertEquals(API_NOT_FOUND, exception.getApiErrorType());

    }

    @Test
    @DisplayName("API 기록 가져오기 실패 - 회원 X")
    void fail_getApiHistories_authentication_user_not_found(){
      //given
      ApiInfo apiInfo = getApiInfo();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiHistoryService.getApiHistories(1L, 1L,
              LocalDate.now(), LocalDate.now(),
              PageRequest.of(0, 10)));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());

    }

    @Test
    @DisplayName("API 기록 가져오기 실패 - API 소유주가 아님")
    void fail_getApiHistories_you_are_not_api_owner(){
      //given
      ApiInfo apiInfo = getApiInfo();

      given(apiInfoRepository.findById(anyLong()))
          .willReturn(Optional.of(apiInfo));

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(Member.builder().id(2L).build()));

      //when
      ApiPermissionException exception = assertThrows(ApiPermissionException.class,
          () -> apiHistoryService.getApiHistories(1L, 1L,
              LocalDate.now(), LocalDate.now(),
              PageRequest.of(0, 10)));

      //then
      assertEquals(YOU_ARE_NOT_API_OWNER, exception.getApiPermissionErrorType());
    }

  }

  @Nested
  @DisplayName("writeInsertHistory() 테스트")
  class writeInsertHistoryTest {

    @Test
    @DisplayName("데이터 삽입 기록 작성 성공")
    void success_writeInsertHistory(){
      //given
      Member ownerMember = getOwnerMember();
      MongoCollection<Document> collection = Mockito.mock(MongoCollection.class);

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(mongoTemplate.getCollection(anyString()))
          .willReturn(collection);

      //when
      Map<String, Object> insertData = new HashMap<>();
      insertData.put("test", "test");
      LocalDateTime nowTime = LocalDateTime.now();

      apiHistoryService.writeInsertHistory(insertData,
          "testCollection", 1L, nowTime);

      //then
      ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
      verify(collection, times(1)).insertOne(captor.capture());
      Document expectedDocument = captor.getValue();

      assertAll(
          () -> assertEquals(nowTime, expectedDocument.get(MONGODB_AT_COL)),
          () -> assertEquals(ownerMember.getEmail(), expectedDocument.get(MONGODB_MEMBER_COL)),
          () -> assertEquals(INSERT.name(), expectedDocument.get(MONGODB_TYPE_COL)),
          () -> assertEquals("test", ((Map<?, ?>)expectedDocument.get(MONGODB_NEW_COL)).get("test"))
      );
    }

    @Test
    @DisplayName("데이터 삽입 기록 작성 실패 - 회원 X")
    void fail_writeInsertHistory_authentication_user_not_found(){
      //given
      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiHistoryService.writeInsertHistory(new HashMap<>(),
              "testCollection", 1L, LocalDateTime.now()));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

  }

  @Nested
  @DisplayName("writeUpdateHistory() 테스트")
  class writeUpdateHistoryTest {
    @Test
    @DisplayName("데이터 수정 기록 작성 성공")
    void success_writeUpdateHistory(){
      //given
      Member ownerMember = getOwnerMember();
      MongoCollection<Document> collection = Mockito.mock(MongoCollection.class);

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(mongoTemplate.getCollection(anyString()))
          .willReturn(collection);

      //when
      Map<String, Object> originalData = new HashMap<>();
      originalData.put("original", "original");

      Map<String, Object> newData = new HashMap<>();
      newData.put("new", "new");

      LocalDateTime nowTime = LocalDateTime.now();

      apiHistoryService.writeUpdateHistory(originalData, newData,
          "testCollection", 1L, nowTime);

      //then
      ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
      verify(collection, times(1)).insertOne(captor.capture());
      Document expectedDocument = captor.getValue();

      assertAll(
          () -> assertEquals(nowTime, expectedDocument.get(MONGODB_AT_COL)),
          () -> assertEquals(ownerMember.getEmail(), expectedDocument.get(MONGODB_MEMBER_COL)),
          () -> assertEquals(UPDATE.name(), expectedDocument.get(MONGODB_TYPE_COL)),
          () -> assertEquals("original", ((Map<?, ?>)expectedDocument.get(MONGODB_ORIGINAL_COL)).get("original")),
          () -> assertEquals("new", ((Map<?, ?>)expectedDocument.get(MONGODB_NEW_COL)).get("new"))
      );
    }

    @Test
    @DisplayName("데이터 수정 기록 작성 실패 - 회원 X")
    void fail_writeUpdateHistory_authentication_user_not_found(){
      //given
      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiHistoryService.writeUpdateHistory(new HashMap<>(), new HashMap<>(),
              "testCollection", 1L, LocalDateTime.now()));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

  }

  @Nested
  @DisplayName("writeDeleteHistory() 테스트")
  class writeDeleteHistoryTest {
    @Test
    @DisplayName("데이터 삭제 기록 작성 성공")
    void success_writeDeleteHistory(){
      //given
      Member ownerMember = getOwnerMember();
      MongoCollection<Document> collection = Mockito.mock(MongoCollection.class);

      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.of(ownerMember));

      given(mongoTemplate.getCollection(anyString()))
          .willReturn(collection);

      //when
      Map<String, Object> deleteData = new HashMap<>();
      deleteData.put("test", "test");
      LocalDateTime nowTime = LocalDateTime.now();

      apiHistoryService.writeDeleteHistory(deleteData,
          "testCollection", 1L, nowTime);

      //then
      ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
      verify(collection, times(1)).insertOne(captor.capture());
      Document expectedDocument = captor.getValue();

      assertAll(
          () -> assertEquals(nowTime, expectedDocument.get(MONGODB_AT_COL)),
          () -> assertEquals(ownerMember.getEmail(), expectedDocument.get(MONGODB_MEMBER_COL)),
          () -> assertEquals(DELETE.name(), expectedDocument.get(MONGODB_TYPE_COL)),
          () -> assertEquals("test", ((Map<?, ?>)expectedDocument.get(MONGODB_ORIGINAL_COL)).get("test"))
      );
    }

    @Test
    @DisplayName("데이터 삭제 기록 작성 실패 - 회원 X")
    void fail_writeDeleteHistory_authentication_user_not_found(){
      //given
      given(memberRepository.findById(anyLong()))
          .willReturn(Optional.empty());

      //when
      AuthenticationException exception = assertThrows(AuthenticationException.class,
          () -> apiHistoryService.writeDeleteHistory(new HashMap<>(),
              "testCollection", 1L, LocalDateTime.now()));

      //then
      assertEquals(AUTHENTICATION_USER_NOT_FOUND, exception.getAuthenticationErrorType());
    }

  }


}