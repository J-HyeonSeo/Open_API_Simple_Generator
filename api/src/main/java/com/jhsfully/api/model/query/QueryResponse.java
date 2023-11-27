package com.jhsfully.api.model.query;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.bson.Document;

@Getter
@AllArgsConstructor
@Builder
public class QueryResponse {

  private long totalCount;
  private long dataCount;
  private List<Document> dataList;

}
