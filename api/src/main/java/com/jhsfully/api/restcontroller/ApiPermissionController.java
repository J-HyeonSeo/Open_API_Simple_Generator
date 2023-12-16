package com.jhsfully.api.restcontroller;

import com.jhsfully.api.model.dto.PermissionDto;
import com.jhsfully.api.model.permission.PermissionResponse;
import com.jhsfully.api.service.ApiPermissionService;
import com.jhsfully.api.util.MemberUtil;
import com.jhsfully.domain.type.ApiPermissionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
public class ApiPermissionController {

  private final ApiPermissionService apiPermissionService;

  /*
      ######## PERMISSION MANAGE #########
   */

  //Member가 해당 API에 가지고 있는 퍼미션 종류를 조회함.
  @GetMapping("/{apiId}")
  public ResponseEntity<?> getPermissionForMember(
      @PathVariable long apiId
  ){
    long memberId = MemberUtil.getMemberId();
    PermissionDto permissionDto = apiPermissionService.getPermissionForMember(apiId, memberId);
    return ResponseEntity.ok(permissionDto);
  }

  //Owner가 해당 API에 대한 Member의 Permission들을 페이징하여 조회함.
  @GetMapping("/owner/{apiId}/{pageIdx}/{pageSize}")
  public ResponseEntity<?> getPermissionListForOwner(
      @PathVariable long apiId,
      @PathVariable int pageIdx,
      @PathVariable int pageSize
  ){
    long memberId = MemberUtil.getMemberId();
    PermissionResponse permissionResponse = apiPermissionService
        .getPermissionListForOwner(apiId, memberId, PageRequest.of(pageIdx, pageSize));
    return ResponseEntity.ok(permissionResponse);
  }

  @PutMapping("/add/{permissionId}")
  public ResponseEntity<?> addPermission(
      @PathVariable long permissionId,
      @RequestParam ApiPermissionType type
  ){
    long memberId = MemberUtil.getMemberId();
    apiPermissionService.addPermission(permissionId, memberId, type);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/sub/{permissionDetailId}")
  public ResponseEntity<?> subPermission(
      @PathVariable long permissionDetailId
  ){
    long memberId = MemberUtil.getMemberId();
    apiPermissionService.subPermission(permissionDetailId, memberId);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{permissionId}")
  public ResponseEntity<?> deletePermission(
      @PathVariable long permissionId
  ){
    long memberId = MemberUtil.getMemberId();
    apiPermissionService.deletePermission(permissionId, memberId);
    return ResponseEntity.ok().build();
  }

  /*
      ######## AUTH KEY ########
   */

  @GetMapping("/authkey/{apiId}")
  public ResponseEntity<?> getAuthKey(@PathVariable long apiId){
    long memberId = MemberUtil.getMemberId();
    return ResponseEntity.ok(
        apiPermissionService.getAuthKey(memberId, apiId)
    );
  }

  @PostMapping("/authkey/{apiId}")
  public ResponseEntity<?> createAuthKey(@PathVariable long apiId){
    long memberId = MemberUtil.getMemberId();
    return ResponseEntity.ok(
        apiPermissionService.createAuthKey(memberId, apiId)
    );
  }

  @PutMapping("/authkey/{apiId}")
  public ResponseEntity<?> refreshAuthKey(@PathVariable long apiId){
    long memberId = MemberUtil.getMemberId();
    return ResponseEntity.ok(
        apiPermissionService.refreshAuthKey(memberId, apiId)
    );
  }

}