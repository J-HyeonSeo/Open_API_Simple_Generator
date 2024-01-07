package com.jhsfully.consumer.kafka;

import com.jhsfully.consumer.exception.ConsumerException;
import com.jhsfully.consumer.service.ApiInfoWriteService;
import com.jhsfully.consumer.service.SaveDataService;
import com.jhsfully.domain.entity.redis.ExcelParseInfo;
import com.jhsfully.domain.kafkamodel.ExcelParserModel;
import com.jhsfully.domain.repository.ExcelParseInfoRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelParserModelConsumer {

  private final SaveDataService saveDataService;
  private final ApiInfoWriteService apiInfoWriteService;
  private final ExcelParseInfoRepository excelParseInfoRepository;

  @KafkaListener(topics = "${spring.kafka.topic-name}", groupId = "${spring.kafka.consumer.group-id}")
  public void excelParserModelTopicConsumer(ExcelParserModel message, Acknowledgment acknowledgment) {
    log.info("collectionName: {} => Kafka Message Pulled : " + message.getDataCollectionName(), message.getDataCollectionName());

    //RDB에 Message데이터를 저장합니다.
    long apiInfoId = apiInfoWriteService.saveKafkaMessageToDataBase(message);

    //과거에 파싱한 이력이 있는지 확인.
    int skipRow = 0;
    boolean isDone = false;
    Optional<ExcelParseInfo> excelParseInfo = excelParseInfoRepository.findById(message.getDataCollectionName());

    if (excelParseInfo.isPresent()) {
      skipRow = excelParseInfo.get().getParsedRow();
      isDone = excelParseInfo.get().isDone();
    }

    try {
      boolean parsedResult = isDone || saveDataService.saveDataFromExcel(message, skipRow);
      apiInfoWriteService.saveApiInfoData(message, apiInfoId, parsedResult);
    } catch (Exception e) {
      throw new ConsumerException(); //정상적으로 처리되지 않은 경우, Exception을 날리고 재시도.
    }

    acknowledgment.acknowledge();
    log.info("collectionName: {} => Kafka Consume Process Done : " + message.getDataCollectionName(), message.getDataCollectionName());
  }

}
