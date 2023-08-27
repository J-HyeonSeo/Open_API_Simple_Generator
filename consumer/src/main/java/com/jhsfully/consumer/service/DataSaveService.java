package com.jhsfully.consumer.service;

import com.jhsfully.domain.kafkamodel.ExcelParserModel;
import java.io.IOException;

public interface DataSaveService {
  void saveDataFromExcel(ExcelParserModel model) throws IOException;
}
