package com.jhsfully.api.model.permission;

import java.util.List;

import com.jhsfully.api.model.dto.PermissionDto;
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
public class PermissionResponse {
  private long totalCount;
  private long dataCount;
  private List<PermissionDto> permissionDtoList;
}
