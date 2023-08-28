package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.errortype.ApiErrorType.API_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.API_KEY_ALREADY_ISSUED;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.API_KEY_NOT_ISSUED;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.USER_HAS_NOT_API;
import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;

import com.jhsfully.api.exception.ApiException;
import com.jhsfully.api.exception.ApiPermissionException;
import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.model.permission.AuthKeyResponse;
import com.jhsfully.api.service.ApiPermissionService;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiKey;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.ApiKeyRepository;
import com.jhsfully.domain.repository.ApiUserPermissionRepository;
import com.jhsfully.domain.repository.MemberRepository;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ApiPermissionServiceImpl implements ApiPermissionService {

  private final ApiInfoRepository apiInfoRepository;
  private final ApiUserPermissionRepository apiUserPermissionRepository;
  private final ApiKeyRepository apiKeyRepository;
  private final MemberRepository memberRepository;


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

    //자신이 소유하고 있는 API라면, 바로 리턴.
    if(Objects.equals(apiInfo.getMember().getId(), member.getId())){
      return;
    }

    //허락된 API인지 확인해야함.
    apiUserPermissionRepository.findByApiInfoAndMember(apiInfo, member)
        .orElseThrow(() -> new ApiPermissionException(USER_HAS_NOT_API));
  }


}
