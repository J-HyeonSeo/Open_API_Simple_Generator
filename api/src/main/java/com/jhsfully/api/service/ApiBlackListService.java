package com.jhsfully.api.service;

import com.jhsfully.api.model.dto.BlackListDto;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApiBlackListService {

  List<BlackListDto> getBlackList(
    long apiId, long memberId, Pageable pageable
  );

  void registerBlackList(
      long apiId, long ownerMemberId, long targetMemberId, LocalDateTime nowTime
  );

  void deleteBlackList(
      long blackListId, long memberId
  );

}
