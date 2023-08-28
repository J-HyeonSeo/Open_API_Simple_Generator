package com.jhsfully.api.service;

import com.jhsfully.api.model.query.QueryInput;
import com.jhsfully.api.model.query.QueryResponse;

public interface QueryService {
  QueryResponse getDataList(QueryInput input);
}
