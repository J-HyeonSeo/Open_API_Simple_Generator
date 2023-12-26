package com.jhsfully.api.service;

import com.jhsfully.api.model.PageResponse;
import com.jhsfully.api.model.dto.ApiRequestInviteDto;
import org.springframework.data.domain.Pageable;

public interface ApiRequestService {

  PageResponse<ApiRequestInviteDto> getRequestListForMember(
      long memberId,
      Pageable pageable
  );

  PageResponse<ApiRequestInviteDto> getRequestListForOwner(
      long memberId,
      long apiId,
      Pageable pageable
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
