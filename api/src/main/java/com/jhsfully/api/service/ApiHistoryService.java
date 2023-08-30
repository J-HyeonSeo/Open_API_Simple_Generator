package com.jhsfully.api.service;

import com.jhsfully.api.model.history.HistoryResponse;
import java.time.LocalDate;
import java.util.Map;

public interface ApiHistoryService {
  HistoryResponse getApiHistories(
      long apiId, long memberId, int pageSize, int pageIdx,
      LocalDate startDate,
      LocalDate endDate
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
