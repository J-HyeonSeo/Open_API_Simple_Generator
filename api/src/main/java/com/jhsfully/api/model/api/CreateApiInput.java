package com.jhsfully.api.model.api;

import com.jhsfully.domain.type.ApiQueryType;
import com.jhsfully.domain.type.ApiStructureType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateApiInput {
  private String apiName;
  private String apiIntroduce;
  private List<SchemaData> schemaStructure;
  private List<QueryData> queryParameter;
  private boolean isPublic;
  private MultipartFile file;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SchemaData{
    private String field;
    private ApiStructureType type;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class QueryData{
    private String field;
    private ApiQueryType type;
  }
}
