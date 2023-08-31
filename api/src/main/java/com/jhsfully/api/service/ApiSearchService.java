package com.jhsfully.api.service;

import com.jhsfully.api.model.api.ApiSearchResponse;
import com.jhsfully.domain.type.SearchType;

public interface ApiSearchService {
  ApiSearchResponse getOpenApiList(
      int pageSize, int pageIdx, String searchText, SearchType type
  );
}
