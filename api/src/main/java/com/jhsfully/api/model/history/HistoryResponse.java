package com.jhsfully.api.model.history;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryResponse {

  private long totalCount;
  private int dataCount;
  private List<Map> histories;

}
