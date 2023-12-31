package com.jhsfully.api.service;

import com.jhsfully.api.model.PageResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import org.bson.Document;
import org.springframework.data.domain.Pageable;

public interface ApiHistoryService {
  PageResponse<Document> getApiHistories(
      long apiId, long memberId,
      LocalDate startDate,
      LocalDate endDate,
      Pageable pageable
  );
  void writeInsertHistory(
      Map<String, Object> insertData,
      String historyCollection,
      long memberId, LocalDateTime nowTime);

  void writeUpdateHistory(
      Map<String, Object> originalData,
      Map<String, Object> newData,
      String historyCollection,
      long memberId, LocalDateTime nowTime);

  void writeDeleteHistory(
      Map<String, Object> originalData,
      String historyCollection,
      long memberId, LocalDateTime nowTime);
}
