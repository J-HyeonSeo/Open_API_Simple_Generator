package com.jhsfully.api.restcontroller;

import com.jhsfully.api.model.PageResponse;
import com.jhsfully.api.model.dto.ApiRequestInviteDto;
import com.jhsfully.api.service.ApiRequestService;
import com.jhsfully.api.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/request")
@RequiredArgsConstructor
public class ApiRequestController {

  private final ApiRequestService apiRequestService;

  /*
      내가 보냈던 API 신청 목록 조회.
   */
  @GetMapping("/member/{pageIdx}/{pageSize}")
  public ResponseEntity<PageResponse<ApiRequestInviteDto>> getRequestListForMember(
      @PathVariable int pageIdx,
      @PathVariable int pageSize
  ){
    long memberId = MemberUtil.getMemberId();
    return ResponseEntity.ok(
        apiRequestService.getRequestListForMember(memberId,
            PageRequest.of(pageIdx, pageSize, Sort.by("registeredAt").descending()))
    );
  }

  /*
      API 소유주가 해당 API 에 대해 들어온 요청 목록 조회.
   */
  @GetMapping("/owner/{apiId}/{pageIdx}/{pageSize}")
  public ResponseEntity<PageResponse<ApiRequestInviteDto>> getRequestListForOwner(
      @PathVariable long apiId,
      @PathVariable int pageIdx,
      @PathVariable int pageSize
  ){
    long memberId = MemberUtil.getMemberId();
    return ResponseEntity.ok(
        apiRequestService.getRequestListForOwner(memberId, apiId,
            PageRequest.of(pageIdx, pageSize, Sort.by("registeredAt").descending()))
    );
  }

  /*
      Member가 오픈된 API에 대해 신청 요청을 보냄.
   */
  @PostMapping("/{apiId}")
  public ResponseEntity<?> apiRequest(@PathVariable long apiId){
    long memberId = MemberUtil.getMemberId();
    apiRequestService.apiRequest(memberId, apiId);
    return ResponseEntity.ok().build();
  }

  /*
      API 소유주가 해당 API에 대한 REQUEST를 수락함.
   */
  @PatchMapping("/assign/{requestId}")
  public ResponseEntity<?> apiRequestAssign(
      @PathVariable long requestId
  ){
    long memberId = MemberUtil.getMemberId();
    apiRequestService.apiRequestAssign(memberId, requestId);
    return ResponseEntity.ok().build();
  }

  /*
      API 소유주가 해당 API에 대한 REQUEST를 거절함.
   */
  @PatchMapping("/reject/{requestId}")
  public ResponseEntity<?> apiRequestReject(
      @PathVariable long requestId
  ){
    long memberId = MemberUtil.getMemberId();
    apiRequestService.apiRequestReject(memberId, requestId);
    return ResponseEntity.ok().build();
  }

}
