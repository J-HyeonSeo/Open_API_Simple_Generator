package com.jhsfully.api.model.query;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueryInput {
  private long apiId;
  private String authKey;
  private int pageSize;
  private int pageIdx;
  private Map<String, Object> queryParameter;
}
