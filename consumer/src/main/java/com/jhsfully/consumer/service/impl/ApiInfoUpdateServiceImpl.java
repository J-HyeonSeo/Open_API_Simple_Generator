package com.jhsfully.consumer.service.impl;

import com.jhsfully.consumer.service.ApiInfoUpdateService;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiInfoElastic;
import com.jhsfully.domain.repository.ApiInfoElasticRepository;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.type.ApiState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.join.JoinField;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ApiInfoUpdateServiceImpl implements ApiInfoUpdateService {

  private final ApiInfoRepository apiInfoRepository;
  private final ApiInfoElasticRepository apiInfoElasticRepository;

  public void successUpdateInfoData(long apiId){
    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow(() -> new RuntimeException("ApiInfo를 찾을 수 없습니다."));

    apiInfo.setApiState(ApiState.ENABLED);

    apiInfoRepository.save(apiInfo);
    log.info("API 상태를 ENABLED로 수정 : " + apiInfo.getDataCollectionName());

    /*
        퍼블릭 하지 않으면, Elastic에 저장하지 않고, 바로 리턴!
     */
    if(!apiInfo.isPublic()){
      return;
    }

    ApiInfoElastic apiInfoElastic = ApiInfoElastic.builder()
        .id(apiInfo.getId())
        .apiName(apiInfo.getApiName())
        .apiIntroduce(apiInfo.getApiIntroduce())
        .ownerEmail(apiInfo.getMember().getEmail())
        .state(ApiState.ENABLED)
        .isPublic(true)
        .ownerMemberId(apiInfo.getMember().getId())
        .mapping(new JoinField<>("apiInfo"))
        .build();

    apiInfoElasticRepository.save(apiInfoElastic);
    log.info("Elastic Search ApiInfo 데이터 추가! : " + apiInfo.getDataCollectionName());
  }

  public void failedUpdateInfoData(long apiId){
    ApiInfo apiInfo = apiInfoRepository.findById(apiId)
        .orElseThrow();

    apiInfo.setApiState(ApiState.FAILED);

    apiInfoRepository.save(apiInfo);
    log.info("API 상태를 FAILED로 수정 : " + apiInfo.getDataCollectionName());
  }

}
