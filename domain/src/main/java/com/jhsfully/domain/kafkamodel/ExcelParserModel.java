package com.jhsfully.domain.kafkamodel;

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
public class ExcelParserModel {
  private long apiInfoId;
  private String excelPath;
  private String dataCollectionName;
  private Map<String, ApiStructureType> schemaStructure;
  private Map<String, ApiQueryType> queryParameter;

}
