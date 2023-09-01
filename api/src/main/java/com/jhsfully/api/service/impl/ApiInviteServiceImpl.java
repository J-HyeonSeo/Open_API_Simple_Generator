package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.errortype.ApiErrorType.API_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiInviteErrorType.CANNOT_ASSIGN_INVITE_NOT_TARGET;
import static com.jhsfully.domain.type.errortype.ApiInviteErrorType.CANNOT_INVITE_ALREADY_HAS_PERMISSION;
import static com.jhsfully.domain.type.errortype.ApiInviteErrorType.CANNOT_INVITE_ALREADY_INVITED;
import static com.jhsfully.domain.type.errortype.ApiInviteErrorType.CANNOT_INVITE_NOT_API_OWNER;
import static com.jhsfully.domain.type.errortype.ApiInviteErrorType.CANNOT_REJECT_INVITE_NOT_TARGET;
import static com.jhsfully.domain.type.errortype.ApiInviteErrorType.INVITE_ALREADY_ASSIGN;
import static com.jhsfully.domain.type.errortype.ApiInviteErrorType.INVITE_ALREADY_REJECT;
import static com.jhsfully.domain.type.errortype.ApiInviteErrorType.INVITE_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;

import com.jhsfully.api.exception.ApiException;
import com.jhsfully.api.exception.ApiInviteException;
import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.model.dto.ApiRequestInviteDto;
import com.jhsfully.api.service.ApiInviteService;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiRequestInvite;
import com.jhsfully.domain.entity.ApiUserPermission;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.ApiRequestInviteRepository;
import com.jhsfully.domain.repository.ApiUserPermissionRepository;
import com.jhsfully.domain.repository.MemberRepository;
import com.jhsfully.domain.type.ApiRequestStateType;
import com.jhsfully.domain.type.ApiRequestType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ApiInviteServiceImpl implements ApiInviteService {

  private final ApiRequestInviteRepository apiRequestInviteRepository;
  private final ApiUserPermissionRepository apiUserPermissionRepository;
  private final ApiInfoRepository apiInfoRepository;
  private final MemberRepository memberRepository;

  @Override
  public List<ApiRequestInviteDto> getInviteListForOwner(long memberId, long apiId, int pageSize,
      int pageIdx) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    Pageable pageable = PageRequest.of(pageIdx, pageSize, Sort.by("registeredAt").descending());

    return apiRequestInviteRepository.findByApiInfoAndApiRequestType(apiInfo, ApiRequestType.INVITE, pageable)
        .getContent()
        .stream()
        .map(ApiRequestInviteDto::of)
        .collect(Collectors.toList());
  }

  @Override
  public List<ApiRequestInviteDto> getInviteListForMember(long memberId, int pageSize,
      int pageIdx) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    Pageable pageable = PageRequest.of(pageIdx, pageSize, Sort.by("registeredAt").descending());

    return apiRequestInviteRepository.findByMemberAndApiRequestType(member, ApiRequestType.INVITE, pageable)
        .getContent()
        .stream()
        .map(ApiRequestInviteDto::of)
        .collect(Collectors.toList());
  }

  @Override
  public void apiInvite(long apiId, long ownerMemberId, long targetMemberId) {

    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    Member ownerMember = memberRepository.findById(ownerMemberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    Member targetMember = memberRepository.findById(targetMemberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    validateApiInvite(apiInfo, ownerMember, targetMember);

    ApiRequestInvite invite = ApiRequestInvite.builder()
        .member(targetMember)
        .apiInfo(apiInfo)
        .apiRequestType(ApiRequestType.INVITE)
        .requestStateType(ApiRequestStateType.REQUEST)
        .registeredAt(LocalDateTime.now())
        .build();

    apiRequestInviteRepository.save(invite);

  }

  @Override
  public void apiInviteAssign(long memberId, long inviteId) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    ApiRequestInvite invite = apiRequestInviteRepository.findById(inviteId)
        .orElseThrow(() -> new ApiInviteException(INVITE_NOT_FOUND));

    validateApiInviteAssign(member, invite);

    invite.setRequestStateType(ApiRequestStateType.ASSIGN);
    apiRequestInviteRepository.save(invite);

    ApiUserPermission permission = ApiUserPermission.builder()
        .member(member)
        .apiInfo(invite.getApiInfo())
        .build();

    apiUserPermissionRepository.save(permission);
  }

  @Override
  public void apiInviteReject(long memberId, long inviteId) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    ApiRequestInvite invite = apiRequestInviteRepository.findById(inviteId)
        .orElseThrow(() -> new ApiInviteException(INVITE_NOT_FOUND));

    validateApiInviteReject(member, invite);

    invite.setRequestStateType(ApiRequestStateType.REJECT);
    apiRequestInviteRepository.save(invite);

  }


  /*
      ###############################################################
      ###############                           #####################
      ###############          Validates        #####################
      ###############                           #####################
      ###############################################################
   */

  private void validateApiInvite(ApiInfo apiInfo, Member ownerMember, Member targetMember) {
    //소유주가 아닌 경우 throw
    if(!Objects.equals(apiInfo.getMember().getId(), ownerMember.getId())){
      throw new ApiInviteException(CANNOT_INVITE_NOT_API_OWNER);
    }

    //해당 유저가 이미 권한을 소유하고 있다면 throw
    if(apiUserPermissionRepository.findByApiInfoAndMember(apiInfo, targetMember).isPresent()){
      throw new ApiInviteException(CANNOT_INVITE_ALREADY_HAS_PERMISSION);
    }

    //초대 승인 대기 중인, 상태라면.. throw
    if(apiRequestInviteRepository.findByMemberAndApiInfoAndApiRequestTypeAndRequestStateType(
        targetMember, apiInfo, ApiRequestType.INVITE, ApiRequestStateType.REQUEST
    ).isPresent()){
      throw new ApiInviteException(CANNOT_INVITE_ALREADY_INVITED);
    }
  }

  private static void validateApiInviteAssign(Member member, ApiRequestInvite invite) {
    //초대 받은이와 수락하는 이가 다르면, throw
    if(!Objects.equals(invite.getMember().getId(), member.getId())){
      throw new ApiInviteException(CANNOT_ASSIGN_INVITE_NOT_TARGET);
    }

    //이미 초대를 수락한 경우에도 throw
    if(invite.getRequestStateType() == ApiRequestStateType.ASSIGN){
      throw new ApiInviteException(INVITE_ALREADY_ASSIGN);
    }

    //이미 초대를 거절한 경우에도 throw
    if(invite.getRequestStateType() == ApiRequestStateType.REJECT){
      throw new ApiInviteException(INVITE_ALREADY_REJECT);
    }
  }

  private static void validateApiInviteReject(Member member, ApiRequestInvite invite) {
    //초대 받은이와 거절하는 이가 다르면, throw
    if(!Objects.equals(invite.getMember().getId(), member.getId())){
      throw new ApiInviteException(CANNOT_REJECT_INVITE_NOT_TARGET);
    }

    //이미 초대를 거절한 경우에도 throw
    if(invite.getRequestStateType() == ApiRequestStateType.ASSIGN){
      throw new ApiInviteException(INVITE_ALREADY_ASSIGN);
    }

    //이미 초대를 수락한 경우에도 throw
    if(invite.getRequestStateType() == ApiRequestStateType.REJECT){
      throw new ApiInviteException(INVITE_ALREADY_REJECT);
    }
  }

}