package com.jhsfully.consumer.service;

import com.mongodb.client.model.InsertOneModel;
import java.util.List;
import org.bson.Document;

public interface ParsedWriter {
    void bulkWriteAndSaveInfoData(List<InsertOneModel<Document>> rows, int currRow, boolean isDone);
}
