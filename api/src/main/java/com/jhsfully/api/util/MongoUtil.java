package com.jhsfully.api.util;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

public class MongoUtil {

  private final static String MONGO_STAT_COMMAND = "collStats";
  private final static String MONGO_STORAGE_PROPERTY = "storageSize";

  public static long getDbSizeByCollection(MongoTemplate mongoTemplate, String collectionName){
    return Long.parseLong(mongoTemplate.getDb()
        .runCommand(new Document(MONGO_STAT_COMMAND, collectionName))
        .get(MONGO_STORAGE_PROPERTY).toString());
  }

}
