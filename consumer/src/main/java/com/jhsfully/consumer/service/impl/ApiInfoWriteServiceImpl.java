package com.jhsfully.consumer.service.impl;

import com.jhsfully.consumer.exception.ConsumerException;
import com.jhsfully.consumer.service.ApiInfoWriteService;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.ApiInfoElastic;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.kafkamodel.ExcelParserModel;
import com.jhsfully.domain.repository.ApiInfoElasticRepository;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.MemberRepository;
import com.jhsfully.domain.type.ApiState;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.join.JoinField;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ApiInfoWriteServiceImpl implements ApiInfoWriteService {

  private final ApiInfoRepository apiInfoRepository;
  private final MemberRepository memberRepository;
  private final ApiInfoElasticRepository apiInfoElasticRepository;

  public long saveKafkaMessageToDataBase(ExcelParserModel model) {
    Optional<ApiInfo> apiInfo = apiInfoRepository.findByDataCollectionName(model.getDataCollectionName());

    if (apiInfo.isPresent()) {
      return apiInfo.get().getId();
    }

    Member member = memberRepository.findById(model.getMemberId())
        .orElseThrow(ConsumerException::new);

    return apiInfoRepository.save(ApiInfo.builder()
        .apiName(model.getApiName())
        .member(member)
        .apiIntroduce(model.getApiIntroduce())
        .schemaStructure(model.getSchemaStructure())
        .queryParameter(model.getQueryParameter())
        .dataCollectionName(model.getDataCollectionName())
        .historyCollectionName(model.getHistoryCollectionName())
        .apiState(model.isFileEmpty() ? ApiState.ENABLED : ApiState.READY)
        .isPublic(model.isPublic())
        .build()).getId();
  }

  public void saveApiInfoData(ExcelParserModel model, long apiInfoId, boolean isSuccess){

    ApiInfo apiInfo = apiInfoRepository.findByDataCollectionName(model.getDataCollectionName())
        .orElseThrow(ConsumerException::new);

    ApiInfoElastic apiInfoElastic = apiInfoElasticRepository.findById(apiInfoId)
        .orElse(
            ApiInfoElastic.builder()
                .id(apiInfo.getId())
                .apiName(apiInfo.getApiName())
                .apiIntroduce(apiInfo.getApiIntroduce())
                .ownerNickname(apiInfo.getMember().getNickname())
                .profileUrl(apiInfo.getMember().getProfileUrl())
                .isPublic(apiInfo.isPublic())
                .ownerMemberId(apiInfo.getMember().getId())
                .mapping(new JoinField<>("apiInfo"))
                .build()
        );

    apiInfo.setApiState(isSuccess ? ApiState.ENABLED : ApiState.FAILED);
    apiInfoElastic.setApiState(isSuccess ? ApiState.ENABLED : ApiState.FAILED);

    apiInfoRepository.save(apiInfo);
    apiInfoElasticRepository.save(apiInfoElastic);

    log.info("collectionName: {} => API 상태를 {}로 수정", model.getDataCollectionName(), apiInfo.getApiState().name());
  }
}
