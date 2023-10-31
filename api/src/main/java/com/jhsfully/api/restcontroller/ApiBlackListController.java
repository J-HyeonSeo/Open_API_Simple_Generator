package com.jhsfully.api.restcontroller;

import com.jhsfully.api.service.ApiBlackListService;
import com.jhsfully.api.util.MemberUtil;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/blacklist")
@RequiredArgsConstructor
public class ApiBlackListController {

  private final ApiBlackListService apiBlackListService;

  /*
    API 소유주가 해당 API에 대해 차단한 블랙리스트 유저 조회
 */
  @GetMapping("/{apiId}/{pageIdx}/{pageSize}")
  public ResponseEntity<?> getBlackList(
      @PathVariable long apiId,
      @PathVariable int pageIdx,
      @PathVariable int pageSize
  ) {
    long memberId = MemberUtil.getMemberId();
    return ResponseEntity.ok(
        apiBlackListService.getBlackList(apiId, memberId,
            PageRequest.of(pageIdx, pageSize,
                Sort.by("registeredAt").descending()))
    );
  }

  /*
    API 소유주가 해당 API의 블랙리스트에 해당 Member를 등록함.
 */
  @PostMapping("/{apiId}/{targetMemberId}")
  public ResponseEntity<?> registerBlackList(
      @PathVariable long apiId,
      @PathVariable long targetMemberId
  ) {
    long ownerMemberId = MemberUtil.getMemberId();
    apiBlackListService.registerBlackList(apiId, ownerMemberId, targetMemberId, LocalDateTime.now());
    return ResponseEntity.ok().build();
  }

  /*
      API 소유주가 blackList의 데이터를 제거함.
   */
  @DeleteMapping("/{blackListId}")
  public ResponseEntity<?> deleteBlackList(
      @PathVariable long blackListId
  ) {
    long memberId = MemberUtil.getMemberId();
    apiBlackListService.deleteBlackList(blackListId, memberId);
    return ResponseEntity.ok().build();
  }

}
