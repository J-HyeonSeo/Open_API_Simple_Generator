package com.jhsfully.api.model.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DeleteApiDataInput {
  private long apiId;
  private String dataId;
}
