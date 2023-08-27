package com.jhsfully.consumer.kafka;

import com.jhsfully.consumer.service.DataSaveService;
import com.jhsfully.domain.kafkamodel.ExcelParserModel;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelParserModelConsumer {

  private final DataSaveService dataSaveService;

  @KafkaListener(topics = "${spring.kafka.topic-name}", groupId = "${spring.kafka.consumer.group-id}")
  public void excelParserModelTopicConsumer(ExcelParserModel message) throws IOException {
    log.info("Kafka Message Pulled : " + message.getDataCollectionName());
    dataSaveService.saveDataFromExcel(message);
  }

}
