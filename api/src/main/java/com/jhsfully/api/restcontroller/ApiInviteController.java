package com.jhsfully.api.restcontroller;

import com.jhsfully.api.service.ApiInviteService;
import com.jhsfully.api.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invite")
@RequiredArgsConstructor
public class ApiInviteController {

  private final ApiInviteService apiInviteService;

  /*
    API 소유주가 해당 API 에 대해 보낸 초대 목록 조회.
 */
  @GetMapping("/owner/{apiId}/{pageSize}/{pageIdx}")
  public ResponseEntity<?> getInviteListForOwner(
      @PathVariable long apiId,
      @PathVariable int pageSize,
      @PathVariable int pageIdx
  ){
    long memberId = MemberUtil.getMemberId();
    return ResponseEntity.ok(
        apiInviteService.getInviteListForOwner(memberId, apiId, pageSize, pageIdx)
    );
  }

  /*
      멤버가 초대받은 API 목록 조회.
   */
  @GetMapping("/member/{pageSize}/{pageIdx}")
  public ResponseEntity<?> getInviteListForMember(
      @PathVariable int pageSize,
      @PathVariable int pageIdx
  ){
    long memberId = MemberUtil.getMemberId();
    return ResponseEntity.ok(
        apiInviteService.getInviteListForMember(memberId, pageSize, pageIdx)
    );
  }

  /*
    API 소유주가 Member에게 해당 API 에 대해 초대 요청을 보냄.
 */
  @PostMapping("/{apiId}/{targetMemberId}")
  public ResponseEntity<?> apiInvite(
      @PathVariable long apiId,
      @PathVariable long targetMemberId
  ){
    long ownerMemberId = MemberUtil.getMemberId();
    apiInviteService.apiInvite(apiId, ownerMemberId, targetMemberId);
    return ResponseEntity.ok().build();
  }

  /*
    Member가 초대된 API요청을 수락함.
 */
  @PatchMapping("/assign/{inviteId}")
  public ResponseEntity<?> apiInviteAssign(
      @PathVariable long inviteId
  ){
    long memberId = MemberUtil.getMemberId();
    apiInviteService.apiInviteAssign(memberId, inviteId);
    return ResponseEntity.ok().build();
  }

  /*
      Member가 초대된 API요청을 거절함.
   */
  @PatchMapping("/reject/{inviteId}")
  public ResponseEntity<?> apiInviteReject(
      @PathVariable long inviteId
  ){
    long memberId = MemberUtil.getMemberId();
    apiInviteService.apiInviteReject(memberId, inviteId);
    return ResponseEntity.ok().build();
  }
}
