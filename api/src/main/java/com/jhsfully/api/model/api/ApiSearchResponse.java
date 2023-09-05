package com.jhsfully.api.model.api;

import com.jhsfully.domain.entity.ApiInfoElastic;
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
public class ApiSearchResponse {
  private long totalCount;
  private long dataCount;
  private List<ApiInfoElastic> dataList;
}
