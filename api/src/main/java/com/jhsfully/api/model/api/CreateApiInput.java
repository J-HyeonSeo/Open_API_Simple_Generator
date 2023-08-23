package com.jhsfully.api.model.api;

import com.jhsfully.domain.type.ApiQueryType;
import com.jhsfully.domain.type.ApiStructureType;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateApiInput {
  private String apiName;
  private String apiIntroduce;
  private Map<String, ApiStructureType> schemeStructure;
  private Map<String, ApiQueryType> queryParameter;
  private boolean isPublic;
}
