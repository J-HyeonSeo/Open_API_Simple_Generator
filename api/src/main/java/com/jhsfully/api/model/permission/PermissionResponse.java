package com.jhsfully.api.model.permission;

import com.jhsfully.api.model.dto.PermissionDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PermissionResponse {
  private long totalCount;
  private long dataCount;
  private List<PermissionDto> permissionDtoList;
}
