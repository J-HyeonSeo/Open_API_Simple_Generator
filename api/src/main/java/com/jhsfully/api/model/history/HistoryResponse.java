package com.jhsfully.api.model.history;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.bson.Document;

@Getter
@AllArgsConstructor
@Builder
public class HistoryResponse {

  private long totalCount;
  private int dataCount;
  private List<Document> histories;

}
