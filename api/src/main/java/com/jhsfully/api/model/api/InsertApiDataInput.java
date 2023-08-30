package com.jhsfully.api.model.api;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InsertApiDataInput {
  private long apiId;
  private Map<String, Object> insertData;
}
