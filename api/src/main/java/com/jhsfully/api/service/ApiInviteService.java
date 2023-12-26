package com.jhsfully.api.service;

import com.jhsfully.api.model.PageResponse;
import com.jhsfully.api.model.dto.ApiRequestInviteDto;
import org.springframework.data.domain.Pageable;

public interface ApiInviteService {

  PageResponse<ApiRequestInviteDto> getInviteListForOwner(
      long memberId,
      long apiId,
      Pageable pageable
  );

  PageResponse<ApiRequestInviteDto> getInviteListForMember(
      long memberId,
      Pageable pageable
  );

  void apiInvite(
      long apiId,
      long ownerMemberId,
      long targetMemberId
  );

  void apiInviteAssign(
      long memberId,
      long inviteId
  );

  void apiInviteReject(
      long memberId,
      long inviteId
  );

}
