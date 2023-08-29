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
public class UpdateApiDataInput {
  private long apiId;
  private String dataId;
  private Map<String, Object> updateData;
}
