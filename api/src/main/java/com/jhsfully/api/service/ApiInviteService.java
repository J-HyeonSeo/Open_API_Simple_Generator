package com.jhsfully.api.service;

import com.jhsfully.api.model.dto.ApiRequestInviteDto;
import java.util.List;

public interface ApiInviteService {

  List<ApiRequestInviteDto> getInviteListForOwner(
      long memberId,
      long apiId,
      int pageSize,
      int pageIdx
  );

  List<ApiRequestInviteDto> getInviteListForMember(
      long memberId,
      int pageSize,
      int pageIdx
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
