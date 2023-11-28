package com.jhsfully.api.model.api;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UpdateApiDataInput {
  private long apiId;
  private String dataId;
  private Map<String, Object> updateData;
}
