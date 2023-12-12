package com.jhsfully.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jhsfully.api.model.api.CreateApiInput;
import com.jhsfully.api.model.api.DeleteApiDataInput;
import com.jhsfully.api.model.api.InsertApiDataInput;
import com.jhsfully.api.model.api.InsertApiDataResponse;
import com.jhsfully.api.model.api.UpdateApiDataInput;
import com.jhsfully.api.model.api.UpdateApiInput;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ApiService {
  void createOpenApi(CreateApiInput input, long memberId) throws JsonProcessingException;
  InsertApiDataResponse insertApiData(InsertApiDataInput input, long apiId, long memberId, LocalDateTime nowTime);
  void updateApiData(UpdateApiDataInput input, long apiId, long memberId, LocalDateTime nowTime);
  void deleteApiData(DeleteApiDataInput input, long apiId, long memberId, LocalDateTime nowTime);
  void deleteOpenApi(long apiId, long memberId);
  void enableOpenApi(long apiId, long memberId, LocalDate nowDate);
  void updateOpenApi(UpdateApiInput input, long apiId, long memberId);
}
