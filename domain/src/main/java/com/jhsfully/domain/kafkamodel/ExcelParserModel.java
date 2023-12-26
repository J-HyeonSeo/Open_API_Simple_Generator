package com.jhsfully.domain.kafkamodel;

import com.jhsfully.domain.type.QueryData;
import com.jhsfully.domain.type.SchemaData;
import java.util.List;
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
  private List<SchemaData> schemaStructure;
  private List<QueryData> queryParameter;

}
