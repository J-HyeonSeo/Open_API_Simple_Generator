package com.jhsfully.api.restcontroller;

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
@RequestMapping("/api/request")
@RequiredArgsConstructor
public class RequestController {

  /*
      내가 보냈던 API 신청 목록 조회.
   */
  @GetMapping("/member/{pageSize}/{pageIdx}")
  public ResponseEntity<?> getRequestListForMember(
      @PathVariable int pageSize,
      @PathVariable int pageIdx
  ){
    long memberId = MemberUtil.getMemberId();
    return ResponseEntity.ok().build();
  }

  /*
      API 소유주가 해당 API 에 대해 들어온 요청 목록 조회.
   */
  @GetMapping("/owner/{apiId}/{pageSize}/{pageIdx}")
  public ResponseEntity<?> getRequestListForOwner(
      @PathVariable long apiId,
      @PathVariable int pageSize,
      @PathVariable int pageIdx
  ){
    long memberId = MemberUtil.getMemberId();
    return ResponseEntity.ok().build();
  }

  /*
      Member가 오픈된 API에 대해 신청 요청을 보냄.
   */
  @PostMapping("/{apiId}")
  public ResponseEntity<?> apiRequest(){
    long memberId = MemberUtil.getMemberId();
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
    return ResponseEntity.ok().build();
  }

}
