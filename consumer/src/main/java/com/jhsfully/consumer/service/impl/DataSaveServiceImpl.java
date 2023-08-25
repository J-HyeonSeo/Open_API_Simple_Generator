package com.jhsfully.consumer.service.impl;

import static com.jhsfully.domain.type.ApiQueryType.INCLUDE;

import com.jhsfully.consumer.service.DataSaveService;
import com.jhsfully.consumer.util.DataSaverFromExcel;
import com.jhsfully.domain.kafkamodel.ExcelParserModel;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.type.ApiQueryType;
import com.mongodb.client.MongoCollection;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSaveServiceImpl implements DataSaveService {

  private final MongoTemplate mongoTemplate;
  private final ApiInfoRepository apiInfoRepository;

  /*
      카프카가 메세지를 중복으로
   */
  public void saveDataFromExcel(ExcelParserModel model) throws IOException {
    MongoCollection<Document> collection;
    try {
      collection = getCollectionAndCreate(model.getDataCollectionName());
    }catch (RuntimeException e){
      return;
    }
    getCollectionAndCreate(model.getDataCollectionName() + "-history");
    log.info("DB Collections Created");

    createIndex(model.getDataCollectionName(), model);
    log.info("DB Indexes Created");

    File file = new File(model.getExcelPath());
    InputStream inputStream = null;

    try {
      inputStream = new FileInputStream(file);
      DataSaverFromExcel.readExcelAndWriteToDB(inputStream, collection, model);
      log.info("Data Saved.");
    } catch (Exception e) {
      mongoTemplate.dropCollection(model.getDataCollectionName());
      mongoTemplate.dropCollection(model.getDataCollectionName() + "-history");
      log.info("Failed to data save - These Schema are Mismatch!");
    }finally {
      if (inputStream != null){
        inputStream.close();
//        file.delete();
      }
    }

  }

  //컬렉션 생성 및 반환. (기존에 있었으면, Exception)
  private MongoCollection<Document> getCollectionAndCreate(String collectionName){
    if(mongoTemplate.collectionExists(collectionName)){
      throw new RuntimeException("Duplicated collectionName!");
    }
    return mongoTemplate.getCollection(collectionName);
  }


  /*
      쿼리 파라미터로 지정된 필드명은 MongoDB에 인덱싱 될 거임.

      단, INCLUDE는 Full-Text-Search가 필요하므로, 특수인덱스인 text 인덱스로 처리함.
      이 외의 인덱스는 정렬인덱스로 생성하도록 함.
   */
  private void createIndex(String collectionName, ExcelParserModel model) {

    Document indexDocument = new Document();

    for(Map.Entry<String, ApiQueryType> param : model.getQueryParameter().entrySet()){
      if (param.getValue() == INCLUDE){
        indexDocument.append(param.getKey(), "text");
      }else{
        indexDocument.append(param.getKey(), 1);
      }
    }

    IndexDefinition index =
        new CompoundIndexDefinition(indexDocument);

    mongoTemplate.indexOps(collectionName).ensureIndex(index);

  }

}
