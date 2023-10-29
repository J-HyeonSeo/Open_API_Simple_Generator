package com.jhsfully.api.service;

import com.jhsfully.api.model.history.HistoryResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Map;

public interface ApiHistoryService {
  HistoryResponse getApiHistories(
      long apiId, long memberId,
      LocalDate startDate,
      LocalDate endDate,
      Pageable pageable
  );
  void writeInsertHistory(
      Map<String, Object> insertData,
      String historyCollection,
      long memberId);

  void writeUpdateHistory(
      Map<String, Object> originalData,
      Map<String, Object> newData,
      String historyCollection,
      long memberId);

  void writeDeleteHistory(
      Map<String, Object> originalData,
      String historyCollection,
      long memberId);
}
