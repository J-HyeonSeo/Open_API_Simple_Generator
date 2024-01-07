package com.jhsfully.consumer.service;

import com.jhsfully.domain.kafkamodel.ExcelParserModel;
import java.io.IOException;

public interface SaveDataService {
  boolean saveDataFromExcel(ExcelParserModel model, int skipRow) throws IOException;
}
