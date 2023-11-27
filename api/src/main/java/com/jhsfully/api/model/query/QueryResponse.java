package com.jhsfully.api.model.query;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class QueryResponse {

  private long totalCount;
  private long dataCount;
  private List<Map> dataList;

}
