package com.jhsfully.consumer.service.impl;

import static com.jhsfully.domain.type.ApiQueryType.INCLUDE;

import com.jhsfully.consumer.exception.ConsumerException;
import com.jhsfully.consumer.exception.DataParsingException;
import com.jhsfully.consumer.service.SaveDataService;
import com.jhsfully.consumer.util.ExcelStreamingParser;
import com.jhsfully.domain.entity.redis.ExcelParseInfo;
import com.jhsfully.domain.kafkamodel.ExcelParserModel;
import com.jhsfully.domain.repository.ExcelParseInfoRepository;
import com.jhsfully.domain.type.QueryData;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Indexes;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
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
public class SaveDataServiceImpl implements SaveDataService {

  private final MongoTemplate mongoTemplate;
  private final ExcelParseInfoRepository excelParseInfoRepository;

  /*
      재처리 로직을 수행하기 위해서, 최대한 유연하게 수정하였음.
      이미 존재하는 부분이 있다면, 기존에 있던 것을 그대로 반환하는 로직으로 작성되었음.
   */
  public boolean saveDataFromExcel(ExcelParserModel model, int skipRow) {
    MongoCollection<Document> dataCollection = getCollectionAndCreate(model.getDataCollectionName());
    getCollectionAndCreate(model.getHistoryCollectionName());
    log.info("collectionName: {} => DB Collections Created", model.getDataCollectionName());

    //create collection indexes..
    createDataCollectionIndex(model.getDataCollectionName(), model.getQueryParameter());
    createHistoryCollectionIndex(model.getHistoryCollectionName());
    log.info("collectionName: {} => DB Indexes Created", model.getDataCollectionName());

    //file경로가 비어있다고 한다면, 그대로 true를 반환해서 넘김.
    if (model.isFileEmpty()) {
      log.info("collectionName: {} => File is Empty, Only Create MongoDB Collections!", model.getDataCollectionName());
      return true;
    }

    //그 외는 Excel Parsing처리를 수행하고, MongoDB에 데이터 저장을 수행하여야 함.
    File file = new File(model.getExcelPath());

    try (InputStream inputStream = new FileInputStream(file)) {
      /*
          실제 Excel파일 통해서 Parsing 작업을 수행하는 메서드!
          중간에, 데이터 처리를 위한 Callback함수를 넘겨주고 있음.
          Callback함수에서는 MongoDB에 데이터를 BulkWrite하고, Redis에 현재까지 Parsed된 Row와 파싱 종료 여부를 저장함.
          서버가 다운 되었을 경우, 파싱을 재시도해야 하므로, 중단된 지점을 찾기 위해서, 이와 같이 진행함.
       */
        ExcelStreamingParser.readExcelAndWriteToDB(inputStream, model, skipRow, (rows, currRow, isDone) -> {
          if (!rows.isEmpty()){
            dataCollection.bulkWrite(rows);
          }
          excelParseInfoRepository.save(
              ExcelParseInfo.builder()
                  .dataCollectionName(model.getDataCollectionName())
                  .parsedRow(currRow)
                  .isDone(isDone)
                  .build()
          );
        });
        log.info("collectionName: {} => SUCCESS!", model.getDataCollectionName());
        return true;
    } catch (DataParsingException e) { //엑셀 데이터가 잘못되어, 파싱을 수행하지 못하였음. 데이터 확인 필요!!
        dropMongoCollection(model.getDataCollectionName());
        dropMongoCollection(model.getHistoryCollectionName());
        log.info("collectionName: {} => FAILED! Cause: Data are not allowed Schemas!",
            model.getDataCollectionName());
        return false;
    } catch (Exception e) { //파일 입출력 오류, DB 오류 등으로, 재시도를 위해 Exception Throw 처리.
        throw new ConsumerException();
    }

  }

  //컬렉션 생성 및 반환. (기존에 있었으면, 기존 컬렉션 반환.)
  private MongoCollection<Document> getCollectionAndCreate(String collectionName){
    if(mongoTemplate.collectionExists(collectionName)){
      log.info("collectionName: {} => Already Collection Created!", collectionName);
      return mongoTemplate.getCollection(collectionName);
    }
    return mongoTemplate.createCollection(collectionName);
  }


  /*
      쿼리 파라미터로 지정된 필드명은 MongoDB에 인덱싱 될 거임.

      단, INCLUDE는 Full-Text-Search가 필요하므로, 특수인덱스인 text 인덱스로 처리함.
      text-index는 여러개로 구성할 수 없기에, 복합인덱스로 구성함.

      이 외의 인덱스는 정렬인덱스로 생성하도록 함.
   */
  private void createDataCollectionIndex(String collectionName, List<QueryData> queryParameter) {

    //기본적으로 "_id" 인덱스가 생성되므로, 이 이상 존재하면, 인덱스를 이미 만들었다고 볼 수 있음.
    if (mongoTemplate.indexOps(collectionName).getIndexInfo().size() > 1) {
      log.info("collectionName: {} => Already Created Index!!", collectionName);
      return;
    }

    Document indexDocument = new Document();
    MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

    for(QueryData param : queryParameter){
      if (param.getType() == INCLUDE){
        indexDocument.append(param.getField(), "text");
      }else{
        collection.createIndex(Indexes.ascending(param.getField()));
      }
    }

    if(!indexDocument.isEmpty()) {
      IndexDefinition index =
          new CompoundIndexDefinition(indexDocument);

      mongoTemplate.indexOps(collectionName).ensureIndex(index);
    }

  }

  //HistoryCollection에 대한 인덱스
  private void createHistoryCollectionIndex(String collectionName) {

    //기본적으로 "_id" 인덱스가 생성되므로, 이 이상 존재하면, 인덱스를 이미 만들었다고 볼 수 있음.
    if (mongoTemplate.indexOps(collectionName).getIndexInfo().size() > 1) {
      log.info("collectionName: {} => Already Created Index!!", collectionName);
      return;
    }

    mongoTemplate.getCollection(collectionName).createIndex(Indexes.descending("at"));
  }

  private void dropMongoCollection(String collectionName) {
    if (mongoTemplate.collectionExists(collectionName)) {
      mongoTemplate.dropCollection(collectionName);
    }
  }

}
