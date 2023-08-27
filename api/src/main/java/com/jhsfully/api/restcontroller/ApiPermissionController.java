package com.jhsfully.api.restcontroller;

import com.jhsfully.api.service.ApiPermissionService;
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
@RequestMapping("/api/permission")
@RequiredArgsConstructor
public class ApiPermissionController {

  private final ApiPermissionService apiPermissionService;

  @GetMapping("/{apiId}")
  public ResponseEntity<?> getAuthKey(@PathVariable long apiId){
    long memberId = MemberUtil.getMemberId();
    return ResponseEntity.ok(
        apiPermissionService.getAuthKey(memberId, apiId)
    );
  }

  @PostMapping("/{apiId}")
  public ResponseEntity<?> createAuthKey(@PathVariable long apiId){
    long memberId = MemberUtil.getMemberId();
    return ResponseEntity.ok(
        apiPermissionService.createAuthKey(memberId, apiId)
    );
  }

  @PatchMapping("/{apiId}")
  public ResponseEntity<?> refreshAuthKey(@PathVariable long apiId){
    long memberId = MemberUtil.getMemberId();
    return ResponseEntity.ok(
        apiPermissionService.refreshAuthKey(memberId, apiId)
    );
  }

}