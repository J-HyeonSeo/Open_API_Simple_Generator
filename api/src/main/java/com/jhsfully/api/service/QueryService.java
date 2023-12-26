package com.jhsfully.api.service;

import com.jhsfully.api.model.PageResponse;
import com.jhsfully.api.model.query.QueryInput;
import org.bson.Document;

public interface QueryService {
  PageResponse<Document> getDataList(QueryInput input);
}
