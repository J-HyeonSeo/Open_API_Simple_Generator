package com.jhsfully.api.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.jhsfully.api.service.ApiHistoryService;
import com.jhsfully.domain.kafkamodel.ExcelParserModel;
import com.jhsfully.domain.repository.ApiInfoElasticRepository;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.ApiUserPermissionRepository;
import com.jhsfully.domain.repository.MemberRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class ApiServiceImplTest {

  //constants
  private static final String MONGODB_ID = "_id";
  private static final String HISTORY_SUFFIX = "-history";

  //repositories
  @Mock
  private ApiInfoRepository apiInfoRepository;
  @Mock
  private ApiUserPermissionRepository apiUserPermissionRepository;
  @Mock
  private MemberRepository memberRepository;
  @Mock
  private MongoTemplate mongoTemplate;
  @Mock
  private ApiInfoElasticRepository apiInfoElasticRepository;

  //service
  @Mock
  private ApiHistoryService apiHistoryService;

  //for kafka
  @Mock
  private KafkaTemplate<String, ExcelParserModel> kafkaTemplate;

  @InjectMocks
  private ApiServiceImpl apiService;



}