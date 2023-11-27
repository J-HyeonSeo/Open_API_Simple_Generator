package com.jhsfully.api.model.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeleteApiDataInput {
  private long apiId;
  private String dataId;
}
