package com.jhsfully.api.model.api;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateApiDataInput {
  private long apiId;
  private String dataId;
  private Map<String, Object> updateData;
}
