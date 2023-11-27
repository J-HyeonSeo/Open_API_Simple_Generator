package com.jhsfully.api.model.history;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class HistoryResponse {

  private long totalCount;
  private int dataCount;
  private List<Map> histories;

}
