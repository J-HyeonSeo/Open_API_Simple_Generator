package com.jhsfully.consumer.util;

import com.jhsfully.domain.kafkamodel.ExcelParserModel;
import com.mongodb.client.MongoCollection;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ooxml.util.SAXHelper;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.binary.XSSFBSheetHandler.SheetContentsHandler;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.bson.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class DataSaverFromExcel implements SheetContentsHandler {

  private int currentCol = -1;
  private int currRowNum = 0;

  private final Map<String, Object> row = new HashMap<>();
  private final List<String> header = new ArrayList<>();
  private final MongoCollection<Document> mongoCollection;
  private final ExcelParserModel excelMetaModel;

  public DataSaverFromExcel(MongoCollection<Document> mongoCollection, ExcelParserModel model){
    this.mongoCollection = mongoCollection;
    this.excelMetaModel = model;
  }

  public static void readExcelAndWriteToDB(InputStream fileInputStream, MongoCollection<Document> mongoCollection, ExcelParserModel model) throws Exception {

    DataSaverFromExcel sheetHandler = new DataSaverFromExcel(mongoCollection, model);
    OPCPackage opc = OPCPackage.open(fileInputStream);

    XSSFReader xssfReader = new XSSFReader(opc);
    StylesTable styles = xssfReader.getStylesTable();
    ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(opc);

    InputStream inputStream = xssfReader.getSheetsData().next();
    InputSource inputSource = new InputSource(inputStream);

    ContentHandler handle = new XSSFSheetXMLHandler(styles, strings, sheetHandler, false);

    XMLReader xmlReader = SAXHelper.newXMLReader();
    xmlReader.setContentHandler(handle);

    xmlReader.parse(inputSource);
    inputStream.close();
    opc.close();

  }

  @Override
  public void startRow(int rowNum) {
    this.currentCol = -1;
    this.currRowNum = rowNum;
  }

  @Override
  public void cell(String columnName, String value, XSSFComment var3) {
    currentCol++;

    Object insertValue = value;

    if(currentCol >= this.excelMetaModel.getSchemaStructure().size()){
      throw new RuntimeException("Schema Info not match!");
    }


    if(this.currRowNum > 0) {
      try {
        switch (this.excelMetaModel.getSchemaStructure().get(currentCol).getType()) {
          case INTEGER:
            insertValue = Long.parseLong(value);
            break;
          case FLOAT:
            insertValue = Double.parseDouble(value);
            break;
          case DATE:
            insertValue = LocalDate.parse(value, ExcelDateFormatter.EXCEL_LOCAL_DATE);
            break;
        }
      }catch (Exception e){
        throw new RuntimeException("Data type is mismatch");
      }
    }else{

      if(this.excelMetaModel.getSchemaStructure().stream().noneMatch((field) -> field.getField().equals(value))){
        throw new RuntimeException("Schema Info not match!");
      }
      this.header.add(value);
    }

    row.put(this.header.get(currentCol), insertValue);
  }

  /*
      행 읽기가 끝날 경우 수행되는 이벤트 메서드
   */
  @Override
  public void endRow(int rowNum) {
    if (rowNum > 0) {
        this.mongoCollection.insertOne(new Document(row));
    }
    row.clear();
  }

  /*
      사용하지는 않지만, 인터페이스가 Override하라고 협박함 ㅠㅠㅠ
   */
  @Override
  public void hyperlinkCell(String cellReference, String formattedValue, String url, String toolTip,
      XSSFComment comment) {
  }
}