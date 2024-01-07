package com.jhsfully.consumer.service;

import com.jhsfully.domain.kafkamodel.ExcelParserModel;

public interface ApiInfoWriteService {
  long saveKafkaMessageToDataBase(ExcelParserModel model);
  void saveApiInfoData(ExcelParserModel model, long apiInfoId, boolean isSuccess);
}
