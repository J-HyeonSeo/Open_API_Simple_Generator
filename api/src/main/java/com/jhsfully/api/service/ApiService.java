package com.jhsfully.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jhsfully.api.model.api.CreateApiInput;

public interface ApiService {
  void createOpenApi(CreateApiInput input) throws JsonProcessingException;
}
