package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.errortype.ApiErrorType.API_IS_DISABLED;
import static com.jhsfully.domain.type.errortype.ApiErrorType.API_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.ALREADY_HAS_PERMISSION;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.API_KEY_ALREADY_ISSUED;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.API_KEY_NOT_ISSUED;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.PERMISSION_DETAIL_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.USER_HAS_NOT_API;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.USER_HAS_NOT_PERMISSION;
import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;

import com.jhsfully.api.exception.ApiException;
import com.jhsfully.api.exception.ApiPermissionException;
import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.model.permission.AuthKeyResponse;
import com.jhsfully.api.model.permission.PermissionDto;
import com.jhsfully.api.model.permission.PermissionResponse;
import com.jhsfully.api.service.ApiPermissionService;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiKey;
import com.jhsfully.domain.entity.ApiPermissionDetail;
import com.jhsfully.domain.entity.ApiUserPermission;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.ApiInfoElasticRepository;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.ApiKeyRepository;
import com.jhsfully.domain.repository.ApiPermissionDetailRepository;
import com.jhsfully.domain.repository.ApiUserPermissionRepository;
import com.jhsfully.domain.repository.MemberRepository;
import com.jhsfully.domain.type.ApiPermissionType;
import com.jhsfully.domain.type.ApiState;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ApiPermissionServiceImpl implements ApiPermissionService {

  //ElasticSearch Repositories
  private final ApiInfoElasticRepository apiInfoElasticRepository; //for deletion

  //MySQL Repositories
  private final ApiInfoRepository apiInfoRepository;
  private final ApiUserPermissionRepository apiUserPermissionRepository;
  private final ApiPermissionDetailRepository apiPermissionDetailRepository;
  private final ApiKeyRepository apiKeyRepository;
  private final MemberRepository memberRepository;

  /*
        ######### PERMISSION AREA ############
   */

  public PermissionDto getPermissionForMember(long apiId, long memberId){

    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    ApiUserPermission permission = apiUserPermissionRepository.findByApiInfoAndMember(apiInfo, member)
        .orElseThrow(() -> new ApiPermissionException(USER_HAS_NOT_PERMISSION));

    return PermissionDto.of(permission);
  }

  public PermissionResponse getPermissionListForOwner(long apiId, long memberId, Pageable pageable){
    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    if(!Objects.equals(apiInfo.getMember().getId(), member.getId())){
      throw new ApiPermissionException(USER_HAS_NOT_API);
    }

    Page<ApiUserPermission> permissionPage = apiUserPermissionRepository.findByApiInfo(apiInfo, pageable);

    return PermissionResponse.builder()
        .totalCount(permissionPage.getTotalElements())
        .dataCount(permissionPage.getNumberOfElements())
        .permissionDtoList(
            permissionPage.getContent()
                .stream()
                .map(PermissionDto::of)
                .collect(Collectors.toList())
        )
        .build();
  }

  public void addPermission(long permissionId, long memberId, ApiPermissionType type){
    ApiUserPermission userPermission = apiUserPermissionRepository.findById(permissionId)
        .orElseThrow(() -> new ApiPermissionException(USER_HAS_NOT_PERMISSION));

    if(userPermission.getApiInfo().getMember().getId() != memberId){
      throw new ApiPermissionException(USER_HAS_NOT_API);
    }

    if(userPermission.getApiPermissionDetails().stream()
        .anyMatch(it -> it.getType() == type)){
      throw new ApiPermissionException(ALREADY_HAS_PERMISSION);
    }

    ApiPermissionDetail permissionDetail = ApiPermissionDetail.builder()
        .apiUserPermission(userPermission)
        .type(type)
        .build();

    apiPermissionDetailRepository.save(permissionDetail);
  }

  public void subPermission(long permissionDetailId, long memberId){
    ApiPermissionDetail permissionDetail = apiPermissionDetailRepository.findById(permissionDetailId)
        .orElseThrow(() -> new ApiPermissionException(PERMISSION_DETAIL_NOT_FOUND));

    if(permissionDetail.getApiUserPermission().getApiInfo().getMember().getId() != memberId){
      throw new ApiPermissionException(USER_HAS_NOT_API);
    }

    apiPermissionDetailRepository.delete(permissionDetail);
  }

  public void deletePermission(long permissionId, long memberId){
    ApiUserPermission userPermission = apiUserPermissionRepository.findById(permissionId)
        .orElseThrow(() -> new ApiPermissionException(USER_HAS_NOT_PERMISSION));

    if(userPermission.getApiInfo().getMember().getId() != memberId){
      throw new ApiPermissionException(USER_HAS_NOT_API);
    }

    //delete all permission details.
    userPermission.getApiPermissionDetails()
        .forEach(
            apiPermissionDetailRepository::delete
        );

    //delete permission.
    apiUserPermissionRepository.delete(userPermission);

    //delete from elastic search
    apiInfoElasticRepository.deleteByPermissionId(userPermission.getId());

  }


  /*
        ########## AUTH KEY AREA #############
   */

  @Override
  public AuthKeyResponse getAuthKey(long memberId, long apiId) {
    validate(memberId, apiId);

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));
    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    ApiKey apiKey = apiKeyRepository.findByApiInfoAndMember(apiInfo, member)
        .orElseThrow(() -> new ApiPermissionException(API_KEY_NOT_ISSUED));
    return new AuthKeyResponse(apiKey.getAuthKey());
  }

  @Override
  public AuthKeyResponse createAuthKey(long memberId, long apiId) {
    validate(memberId, apiId);

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));
    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    if(apiKeyRepository.findByApiInfoAndMember(apiInfo, member).isPresent()){
      throw new ApiPermissionException(API_KEY_ALREADY_ISSUED);
    }

    String apiKeyString = UUID.randomUUID().toString().replaceAll("-", "");

    ApiKey apiKey = ApiKey.builder()
        .apiInfo(apiInfo)
        .member(member)
        .authKey(apiKeyString)
        .build();

    apiKeyRepository.save(apiKey);

    return new AuthKeyResponse(apiKeyString);
  }

  @Override
  public AuthKeyResponse refreshAuthKey(long memberId, long apiId) {
    validate(memberId, apiId);

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));
    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    ApiKey apiKey = apiKeyRepository.findByApiInfoAndMember(apiInfo, member)
        .orElseThrow(() -> new ApiPermissionException(API_KEY_NOT_ISSUED));

    String apiKeyString = UUID.randomUUID().toString().replaceAll("-", "");

    apiKey.setAuthKey(apiKeyString);

    apiKeyRepository.save(apiKey);

    return new AuthKeyResponse(apiKeyString);
  }

    /*
      ###############################################################
      ###############                           #####################
      ###############          Validates        #####################
      ###############                           #####################
      ###############################################################
   */

  private void validate(long memberId, long apiId){
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    //API가 비활성 상태라면, 발급 불가
    if(apiInfo.getApiState() == ApiState.DISABLED){
      throw new ApiException(API_IS_DISABLED);
    }

    //자신이 소유하고 있는 API라면, 바로 리턴.
    if(Objects.equals(apiInfo.getMember().getId(), member.getId())){
      return;
    }

    //허락된 API인지 확인해야함.
    apiUserPermissionRepository.findByApiInfoAndMember(apiInfo, member)
        .orElseThrow(() -> new ApiPermissionException(USER_HAS_NOT_API));
  }


}
