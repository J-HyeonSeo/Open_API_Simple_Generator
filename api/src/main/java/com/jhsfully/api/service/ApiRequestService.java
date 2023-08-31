package com.jhsfully.api.service;

import com.jhsfully.api.model.dto.ApiRequestInviteDto;
import java.util.List;

public interface ApiRequestService {

  List<ApiRequestInviteDto> getRequestListForMember(
      long memberId,
      int pageSize,
      int pageIdx
  );

  List<ApiRequestInviteDto> getRequestListForOwner(
      long memberId,
      long apiId,
      int pageSize,
      int pageIdx
  );

  void apiRequest(
      long memberId,
      long apiId
  );

  void apiRequestAssign(
      long memberId,
      long requestId
  );

  void apiRequestReject(
      long memberId,
      long requestId
  );

}
