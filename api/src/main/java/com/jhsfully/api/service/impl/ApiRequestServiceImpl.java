package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.errortype.ApiErrorType.API_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiErrorType.CANNOT_ASSIGN_REQUEST_NOT_OWNER;
import static com.jhsfully.domain.type.errortype.ApiErrorType.CANNOT_REJECT_REQUEST_NOT_OWNER;
import static com.jhsfully.domain.type.errortype.ApiErrorType.CANNOT_REQUEST_ALREADY_REQUESTED;
import static com.jhsfully.domain.type.errortype.ApiErrorType.CANNOT_REQUEST_API_HAS_PERMISSION;
import static com.jhsfully.domain.type.errortype.ApiErrorType.CANNOT_REQUEST_API_OWNER;
import static com.jhsfully.domain.type.errortype.ApiErrorType.CANNOT_REQUEST_BANNED;
import static com.jhsfully.domain.type.errortype.ApiErrorType.REQUEST_ALREADY_ASSIGN;
import static com.jhsfully.domain.type.errortype.ApiErrorType.REQUEST_ALREADY_REJECT;
import static com.jhsfully.domain.type.errortype.ApiErrorType.REQUEST_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;

import com.jhsfully.api.exception.ApiException;
import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.model.dto.ApiRequestInviteDto;
import com.jhsfully.api.service.ApiRequestService;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiRequestInvite;
import com.jhsfully.domain.entity.ApiUserPermission;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.ApiRequestInviteRepository;
import com.jhsfully.domain.repository.ApiUserPermissionRepository;
import com.jhsfully.domain.repository.BlackListRepository;
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
public class ApiRequestServiceImpl implements ApiRequestService {

  private final MemberRepository memberRepository;
  private final ApiInfoRepository apiInfoRepository;
  private final ApiRequestInviteRepository apiRequestInviteRepository;
  private final ApiUserPermissionRepository apiUserPermissionRepository;
  private final BlackListRepository blackListRepository;

  @Override
  public List<ApiRequestInviteDto> getRequestListForMember(
      long memberId,
      int pageSize,
      int pageIdx) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    Pageable pageable = PageRequest.of(pageIdx, pageSize, Sort.by("registeredAt").descending());

    return apiRequestInviteRepository.findByMemberAndApiRequestType(member, ApiRequestType.REQUEST, pageable)
        .getContent()
        .stream()
        .map(ApiRequestInviteDto::of)
        .collect(Collectors.toList());
  }

  @Override
  public List<ApiRequestInviteDto> getRequestListForOwner(
      long memberId,
      long apiId,
      int pageSize,
      int pageIdx) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    Pageable pageable = PageRequest.of(pageIdx, pageSize, Sort.by("registeredAt").descending());

    return apiRequestInviteRepository.findByMemberAndApiInfoAndApiRequestType(member, apiInfo, ApiRequestType.REQUEST, pageable)
        .getContent()
        .stream()
        .map(ApiRequestInviteDto::of)
        .collect(Collectors.toList());
  }

  @Override
  public void apiRequest(long memberId, long apiId) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    validateApiRequest(member, apiInfo);

    ApiRequestInvite request = ApiRequestInvite.builder()
        .member(member)
        .apiInfo(apiInfo)
        .apiRequestType(ApiRequestType.REQUEST)
        .requestStateType(ApiRequestStateType.REQUEST)
        .registeredAt(LocalDateTime.now())
        .build();

    apiRequestInviteRepository.save(request);
  }

  @Override
  public void apiRequestAssign(long memberId, long requestId) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    ApiRequestInvite request = apiRequestInviteRepository.findById(requestId)
        .orElseThrow(() -> new ApiException(REQUEST_NOT_FOUND));

    validateRequestAssign(member, request);

    //정보를 수정함.
    request.setRequestStateType(ApiRequestStateType.ASSIGN);
    apiRequestInviteRepository.save(request);

    //권한을 새로 생성함.
    ApiUserPermission permission = ApiUserPermission.builder()
        .apiInfo(request.getApiInfo())
        .member(request.getMember())
        .build();

    apiUserPermissionRepository.save(permission);
  }

  @Override
  public void apiRequestReject(long memberId, long requestId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    ApiRequestInvite request = apiRequestInviteRepository.findById(requestId)
        .orElseThrow(() -> new ApiException(REQUEST_NOT_FOUND));

    validateRequestReject(member, request);

    request.setRequestStateType(ApiRequestStateType.REJECT);
    apiRequestInviteRepository.save(request);
  }

    /*
      ###############################################################
      ###############                           #####################
      ###############          Validates        #####################
      ###############                           #####################
      ###############################################################
   */


  /*
      API 사용 신청을 보내기전에, 밸리데이션이 수행되어야함.

      1. 자기 자신은 신청이 불가능.
      2. 이미 권한이 존재할 경우에도 신청이 불가능.
      3. 블랙리스트에 존재할 경우에도 신청이 불가능.
      4. 이미 요청한 이력이 존재하는 경우에도 신청이 불가능.
   */
  private void validateApiRequest(Member member, ApiInfo apiInfo){

    // 1
    if(Objects.equals(member.getId(), apiInfo.getMember().getId())){
      throw new ApiException(CANNOT_REQUEST_API_OWNER);
    }

    //2
    if(apiUserPermissionRepository.findByApiInfoAndMember(apiInfo, member).isPresent()){
      throw new ApiException(CANNOT_REQUEST_API_HAS_PERMISSION);
    }

    //3
    if(blackListRepository.findByApiInfoAndMemberId(apiInfo, member).isPresent()){
      throw new ApiException(CANNOT_REQUEST_BANNED);
    }

    //4
    if(apiRequestInviteRepository.findByMemberAndApiInfoAndApiRequestTypeAndRequestStateType(
        member, apiInfo, ApiRequestType.REQUEST, ApiRequestStateType.REQUEST
    ).isPresent()){
      throw new ApiException(CANNOT_REQUEST_ALREADY_REQUESTED);
    }

  }

  private void validateRequestAssign(Member member, ApiRequestInvite request){
    if(!Objects.equals(member.getId(), request.getApiInfo().getMember().getId())){
      throw new ApiException(CANNOT_ASSIGN_REQUEST_NOT_OWNER);
    }

    if(request.getRequestStateType() == ApiRequestStateType.REJECT){
      throw new ApiException(REQUEST_ALREADY_REJECT);
    }

    if(request.getRequestStateType() == ApiRequestStateType.ASSIGN){
      throw new ApiException(REQUEST_ALREADY_ASSIGN);
    }
  }

  private void validateRequestReject(Member member, ApiRequestInvite request){
    if(!Objects.equals(member.getId(), request.getApiInfo().getMember().getId())){
      throw new ApiException(CANNOT_REJECT_REQUEST_NOT_OWNER);
    }

    if(request.getRequestStateType() == ApiRequestStateType.REJECT){
      throw new ApiException(REQUEST_ALREADY_REJECT);
    }

    if(request.getRequestStateType() == ApiRequestStateType.ASSIGN){
      throw new ApiException(REQUEST_ALREADY_ASSIGN);
    }
  }

}
