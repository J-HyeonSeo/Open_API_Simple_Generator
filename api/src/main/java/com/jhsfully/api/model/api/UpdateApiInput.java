package com.jhsfully.api.model.api;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateApiInput {

  @NotBlank(message = "API명을 입력해주세요.")
  @Size(min = 2, max = 20, message = "API명은 2~20로 입력해주세요.")
  private String apiName;

  @NotBlank(message = "API소개를 입력해주세요.")
  private String apiIntroduce;

  @NotNull(message = "공개여부를 입력해주세요.")
  private Boolean isPublic;

}
