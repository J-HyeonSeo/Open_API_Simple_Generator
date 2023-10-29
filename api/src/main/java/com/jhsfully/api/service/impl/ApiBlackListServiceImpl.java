package com.jhsfully.api.service.impl;

import static com.jhsfully.domain.type.errortype.ApiBlackListErrorType.ALREADY_REGISTERED_TARGET;
import static com.jhsfully.domain.type.errortype.ApiBlackListErrorType.BLACKLIST_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiBlackListErrorType.CANNOT_DELETE_NOT_OWNER;
import static com.jhsfully.domain.type.errortype.ApiBlackListErrorType.CANNOT_REGISTER_NOT_OWNER;
import static com.jhsfully.domain.type.errortype.ApiBlackListErrorType.CANNOT_REGISTER_TARGET_IS_OWNER;
import static com.jhsfully.domain.type.errortype.ApiErrorType.API_NOT_FOUND;
import static com.jhsfully.domain.type.errortype.ApiPermissionErrorType.USER_HAS_NOT_API;
import static com.jhsfully.domain.type.errortype.AuthenticationErrorType.AUTHENTICATION_USER_NOT_FOUND;

import com.jhsfully.api.exception.ApiBlackListException;
import com.jhsfully.api.exception.ApiException;
import com.jhsfully.api.exception.ApiPermissionException;
import com.jhsfully.api.exception.AuthenticationException;
import com.jhsfully.api.model.dto.BlackListDto;
import com.jhsfully.api.service.ApiBlackListService;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.BlackList;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.BlackListRepository;
import com.jhsfully.domain.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ApiBlackListServiceImpl implements ApiBlackListService {

  private final BlackListRepository blackListRepository;
  private final ApiInfoRepository apiInfoRepository;
  private final MemberRepository memberRepository;

  @Override
  @Transactional(readOnly = true)
  public List<BlackListDto> getBlackList(long apiId, long memberId, Pageable pageable) {

    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    validateGetBlackList(apiInfo, member);

    return blackListRepository.findByApiInfo(apiInfo, pageable)
        .getContent()
        .stream()
        .map(BlackListDto::of)
        .collect(Collectors.toList());

  }

  @Override
  public void registerBlackList(long apiId, long ownerMemberId, long targetMemberId, LocalDateTime nowTime) {
    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new ApiException(API_NOT_FOUND));

    Member ownerMember = memberRepository.findById(ownerMemberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    Member targetMember = memberRepository.findById(targetMemberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    validateRegisterBlackList(apiInfo, ownerMember, targetMember);

    BlackList blackList = BlackList.builder()
        .apiInfo(apiInfo)
        .member(targetMember)
        .registeredAt(nowTime)
        .build();

    blackListRepository.save(blackList);

  }

  @Override
  public void deleteBlackList(long blackListId, long memberId) {
    BlackList blackList = blackListRepository.findById(blackListId)
        .orElseThrow(() -> new ApiBlackListException(BLACKLIST_NOT_FOUND));

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new AuthenticationException(AUTHENTICATION_USER_NOT_FOUND));

    validateDeleteBlackList(blackList, member);

    blackListRepository.delete(blackList);
  }


  /*
      ###############################################################
      ###############                           #####################
      ###############          Validates        #####################
      ###############                           #####################
      ###############################################################
   */

  private static void validateGetBlackList(ApiInfo apiInfo, Member member) {
    if(!Objects.equals(apiInfo.getMember().getId(), member.getId())){
      throw new ApiPermissionException(USER_HAS_NOT_API);
    }
  }

  private void validateRegisterBlackList(ApiInfo apiInfo, Member ownerMember, Member targetMember) {
    // 소유주가 아닌 사람이 블랙리스트 등록은 불가능.
    if(!Objects.equals(apiInfo.getMember().getId(), ownerMember.getId())){
      throw new ApiBlackListException(CANNOT_REGISTER_NOT_OWNER);
    }

    // 소유주를 블랙리스트에 등록할 수 없음.
    if(Objects.equals(apiInfo.getMember().getId(), targetMember.getId())){
      throw new ApiBlackListException(CANNOT_REGISTER_TARGET_IS_OWNER);
    }

    // 이미 블랙리스트에 등록된 사람을 등록할 수는 없음.
    if(blackListRepository.findByApiInfoAndMember(apiInfo, targetMember).isPresent()){
      throw new ApiBlackListException(ALREADY_REGISTERED_TARGET);
    }
  }

  private static void validateDeleteBlackList(BlackList blackList, Member member) {
    // 블랙리스트로 지정된 API의 소유주인가요?
    if(!Objects.equals(blackList.getApiInfo().getMember().getId(), member.getId())){
      throw new ApiBlackListException(CANNOT_DELETE_NOT_OWNER);
    }
  }

}
