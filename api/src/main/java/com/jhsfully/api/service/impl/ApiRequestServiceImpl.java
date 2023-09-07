package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.errortype.ApiErrorType.API_IS_DISABLED;
import static com.jhsfully.domain.type.errortype.ApiErrorType.API_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.CANNOT_ASSIGN_REQUEST_NOT_OWNER;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.CANNOT_REJECT_REQUEST_NOT_OWNER;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.CANNOT_REQUEST_ALREADY_REQUESTED;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.CANNOT_REQUEST_API_HAS_PERMISSION;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.CANNOT_REQUEST_API_OWNER;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.CANNOT_REQUEST_BANNED;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.CANNOT_REQUEST_IS_NOT_OPENED;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.REQUEST_ALREADY_ASSIGN;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.REQUEST_ALREADY_REJECT;
import static com.jhsfully.domain.type.errortype.ApiRequestErrorType.REQUEST_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;

import com.jhsfully.api.exception.ApiException;
import com.jhsfully.api.exception.ApiRequestException;
import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.model.dto.ApiRequestInviteDto;
import com.jhsfully.api.service.ApiRequestService;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiInfoElastic;
import com.jhsfully.domain.entity.ApiRequestInvite;
import com.jhsfully.domain.entity.ApiUserPermission;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.ApiInfoElasticRepository;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.ApiRequestInviteRepository;
import com.jhsfully.domain.repository.ApiUserPermissionRepository;
import com.jhsfully.domain.repository.BlackListRepository;
import com.jhsfully.domain.repository.MemberRepository;
import com.jhsfully.domain.type.ApiRequestStateType;
import com.jhsfully.domain.type.ApiRequestType;
import com.jhsfully.domain.type.ApiState;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.join.JoinField;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ApiRequestServiceImpl implements ApiRequestService {

  //ElasticSearch Repositories
  private final ApiInfoElasticRepository apiInfoElasticRepository;

  //MySQL Repositories
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

    return apiRequestInviteRepository.findByApiInfoAndApiRequestType(apiInfo, ApiRequestType.REQUEST, pageable)
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
        .orElseThrow(() -> new ApiRequestException(REQUEST_NOT_FOUND));

    validateRequestAssign(member, request);

    //정보를 수정함.
    request.setRequestStateType(ApiRequestStateType.ASSIGN);
    apiRequestInviteRepository.save(request);

    //권한을 새로 생성함.
    ApiUserPermission permission = ApiUserPermission.builder()
        .apiInfo(request.getApiInfo())
        .member(request.getMember())
        .build();

    //ElasticSearch 접근 가능한 멤버 추가함.
    long permissionId = apiUserPermissionRepository.save(permission).getId();

    ApiInfoElastic elastic = ApiInfoElastic.builder()
        .accessMemberId(member.getId())
        .permissionId(permissionId)
        .mapping(new JoinField<>("accessMember", request.getApiInfo().getId()))
        .build();

    apiInfoElasticRepository.save(elastic);

  }

  @Override
  public void apiRequestReject(long memberId, long requestId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    ApiRequestInvite request = apiRequestInviteRepository.findById(requestId)
        .orElseThrow(() -> new ApiRequestException(REQUEST_NOT_FOUND));

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

      0. 비활성화된 API는 신청불가능
      1. 공개 되지 않은 API는 신청불가능
      2. 자기 자신은 신청이 불가능.
      3. 이미 권한이 존재할 경우에도 신청이 불가능.
      4. 블랙리스트에 존재할 경우에도 신청이 불가능.
      5. 이미 요청한 이력이 존재하는 경우에도 신청이 불가능.
   */
  private void validateApiRequest(Member member, ApiInfo apiInfo){

    //0
    if(apiInfo.getApiState() == ApiState.DISABLED){
      throw new ApiException(API_IS_DISABLED);
    }

    //1
    if(!apiInfo.isPublic()){
      throw new ApiRequestException(CANNOT_REQUEST_IS_NOT_OPENED);
    }

    //2
    if(Objects.equals(member.getId(), apiInfo.getMember().getId())){
      throw new ApiRequestException(CANNOT_REQUEST_API_OWNER);
    }

    //3
    if(apiUserPermissionRepository.findByApiInfoAndMember(apiInfo, member).isPresent()){
      throw new ApiRequestException(CANNOT_REQUEST_API_HAS_PERMISSION);
    }

    //4
    if(blackListRepository.findByApiInfoAndMember(apiInfo, member).isPresent()){
      throw new ApiRequestException(CANNOT_REQUEST_BANNED);
    }

    //5
    if(apiRequestInviteRepository.findByMemberAndApiInfoAndApiRequestTypeAndRequestStateType(
        member, apiInfo, ApiRequestType.REQUEST, ApiRequestStateType.REQUEST
    ).isPresent()){
      throw new ApiRequestException(CANNOT_REQUEST_ALREADY_REQUESTED);
    }

  }

  private void validateRequestAssign(Member member, ApiRequestInvite request){
    if(request.getApiInfo().getApiState() == ApiState.DISABLED){
      throw new ApiException(API_IS_DISABLED);
    }

    if(!Objects.equals(member.getId(), request.getApiInfo().getMember().getId())){
      throw new ApiRequestException(CANNOT_ASSIGN_REQUEST_NOT_OWNER);
    }

    if(request.getRequestStateType() == ApiRequestStateType.REJECT){
      throw new ApiRequestException(REQUEST_ALREADY_REJECT);
    }

    if(request.getRequestStateType() == ApiRequestStateType.ASSIGN){
      throw new ApiRequestException(REQUEST_ALREADY_ASSIGN);
    }
  }

  private void validateRequestReject(Member member, ApiRequestInvite request){
    if(!Objects.equals(member.getId(), request.getApiInfo().getMember().getId())){
      throw new ApiRequestException(CANNOT_REJECT_REQUEST_NOT_OWNER);
    }

    if(request.getRequestStateType() == ApiRequestStateType.REJECT){
      throw new ApiRequestException(REQUEST_ALREADY_REJECT);
    }

    if(request.getRequestStateType() == ApiRequestStateType.ASSIGN){
      throw new ApiRequestException(REQUEST_ALREADY_ASSIGN);
    }
  }

}
