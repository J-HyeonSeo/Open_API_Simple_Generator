package com.jhsfully.api.model.api;

import com.jhsfully.domain.type.QueryData;
import com.jhsfully.domain.type.SchemaData;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/*
    ModelAttribute로 입력받기 위해 Setter를 유지.
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class CreateApiInput {
  @NotBlank(message = "API명을 입력해주세요.")
  @Size(min = 2, max = 20, message = "API명은 2~20로 입력해주세요.")
  private String apiName;

  @NotBlank(message = "API소개를 입력해주세요.")
  private String apiIntroduce;

  @Valid
  @Size(min = 1, message = "적어도 하나의 스키마 구조를 입력해주세요.")
  private List<SchemaData> schemaStructure;

  @Valid
  @Size(min = 1, message = "적어도 하나의 질의인수를 입력해주세요.")
  private List<QueryData> queryParameter;
  private boolean isPublic;
  private MultipartFile file;
}
