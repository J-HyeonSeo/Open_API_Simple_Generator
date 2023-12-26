package com.jhsfully.consumer.service.impl;

import static com.jhsfully.domain.type.ApiQueryType.INCLUDE;

import com.jhsfully.consumer.service.DataSaveService;
import com.jhsfully.consumer.util.DataSaverFromExcel;
import com.jhsfully.domain.kafkamodel.ExcelParserModel;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.type.QueryData;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Indexes;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DataSaveServiceImpl implements DataSaveService {

  private final MongoTemplate mongoTemplate;
  private final ApiInfoRepository apiInfoRepository;

  /*
      카프카가 메세지를 중복으로
   */
  public boolean saveDataFromExcel(ExcelParserModel model) throws IOException {
    MongoCollection<Document> collection;
    String historyCollectionName = model.getDataCollectionName() + "-history";
    try {
      collection = getCollectionAndCreate(model.getDataCollectionName());
    }catch (RuntimeException e){
      return false;
    }
    MongoCollection<Document> historyCollection = getCollectionAndCreate(historyCollectionName);
    log.info("DB Collections Created");

    //data collection index
    createIndex(model.getDataCollectionName(), model.getQueryParameter());
    //history collection index
    historyCollection.createIndex(Indexes.descending("at"));

    log.info("DB Indexes Created");

    //file경로가 비어있다고 한다면, 그대로 true를 반환해서 넘김.
    if(model.getExcelPath() == null || model.getExcelPath().isEmpty()){
      log.info("File is Empty, Only Create MongoDB Collections!");
      return true;
    }

    //그 외는 Excel Parsing처리를 수행하고, MongoDB에 데이터 저장을 수행하여야 함.
    File file = new File(model.getExcelPath());
    InputStream inputStream = null;

    try {
      inputStream = new FileInputStream(file);
      DataSaverFromExcel.readExcelAndWriteToDB(inputStream, collection, model);
      log.info("Data Saved.");
      return true;
    } catch (Exception e) {
      mongoTemplate.dropCollection(model.getDataCollectionName());
      mongoTemplate.dropCollection(historyCollectionName);
      log.info("Failed to data save - These Schema are Mismatch!");
      return false;
    }finally {
      if (inputStream != null){
        inputStream.close();
//        file.delete(); //해당 사항은 아직 보류함.
      }
    }

  }

  //컬렉션 생성 및 반환. (기존에 있었으면, Exception)
  private MongoCollection<Document> getCollectionAndCreate(String collectionName){
    if(mongoTemplate.collectionExists(collectionName)){
      log.info("이미 만들어진 컬렉션에 접근하고 있습니다. , collection : " + collectionName);
      throw new RuntimeException("Duplicated collectionName!");
    }
    return mongoTemplate.createCollection(collectionName);
  }


  /*
      쿼리 파라미터로 지정된 필드명은 MongoDB에 인덱싱 될 거임.

      단, INCLUDE는 Full-Text-Search가 필요하므로, 특수인덱스인 text 인덱스로 처리함.
      text-index는 여러개로 구성할 수 없기에, 복합인덱스로 구성함.

      이 외의 인덱스는 정렬인덱스로 생성하도록 함.
   */
  private void createIndex(String collectionName, List<QueryData> queryParameter) {

    Document indexDocument = new Document();
    MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

    for(QueryData param : queryParameter){
      if (param.getType() == INCLUDE){
        indexDocument.append(param.getField(), "text");
      }else{
        collection.createIndex(Indexes.ascending(param.getField()));
      }
    }

    IndexDefinition index =
        new CompoundIndexDefinition(indexDocument);

    mongoTemplate.indexOps(collectionName).ensureIndex(index);

  }

}
