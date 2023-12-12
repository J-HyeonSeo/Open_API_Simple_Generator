package com.jhsfully.api.model.api;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class InsertApiDataInput {
  private Map<String, Object> insertData;
}
