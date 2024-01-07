package com.jhsfully.consumer.util;

import com.jhsfully.consumer.exception.DataParsingException;
import com.jhsfully.consumer.service.ParsedWriter;
import com.jhsfully.domain.kafkamodel.ExcelParserModel;
import com.mongodb.client.model.InsertOneModel;
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

public class ExcelStreamingParser implements SheetContentsHandler {

  private static final int CHUNK_SIZE = 100;

  private int currCol = -1;
  private int currRow = 0;

  private final Map<String, Object> row = new HashMap<>();
  private final List<InsertOneModel<Document>> rows = new ArrayList<>();
  private final ExcelParserModel excelMetaModel;
  private final ParsedWriter parsedWriter;
  private final int skipRow;

  public ExcelStreamingParser(ExcelParserModel model, ParsedWriter parsedWriter, int skipRow){
    this.excelMetaModel = model;
    this.parsedWriter = parsedWriter;
    this.skipRow = skipRow;
  }

  public static void readExcelAndWriteToDB(InputStream fileInputStream, ExcelParserModel model, int skipRow, ParsedWriter parsedWriter) throws Exception {

    ExcelStreamingParser sheetHandler = new ExcelStreamingParser(model, parsedWriter, skipRow);
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
    this.currCol = -1;
    this.currRow = rowNum;
  }

  @Override
  public void cell(String columnName, String value, XSSFComment var3) {

    //아직 skipRow를 넘지 못했을 경우에는, 데이터를 저장하지 않도록 함.
    if (this.currRow <= this.skipRow) {
      return;
    }

    currCol++;
    Object insertValue = value;

    if(currCol >= this.excelMetaModel.getSchemaStructure().size()){
      throw new DataParsingException();
    }

    if(this.currRow > 0) {
      try {
        switch (this.excelMetaModel.getSchemaStructure().get(this.currCol).getType()) {
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
        throw new DataParsingException();
      }
    } else {

      if (this.excelMetaModel.getSchemaStructure().stream().noneMatch((field) -> field.getField().equals(value))) {
        throw new DataParsingException();
      }

    }

    row.put(this.excelMetaModel.getSchemaStructure().get(this.currCol).getField(), insertValue);
  }

  //모든 데이터를 완전히 읽었고, 데이터가 남아있다면, 나머지 Write 수행.
  @Override
  public void endSheet() {
    this.parsedWriter.bulkWriteAndSaveInfoData(this.rows, this.currRow, true);
  }

  /*
      ROW하나가 끝나면, 수행되는 이벤트.
      여기서 데이터를 추가함.
   */
  @Override
  public void endRow(int rowNum) {

    if (rowNum <= 0) return; //제목은 포함하지 않도록!
    if (rowNum <= skipRow) return; //해당 row는 스킵해야 하므로, 저장하지 않음!

    this.rows.add(new InsertOneModel<>(new Document(this.row)));
    row.clear();

    //rows가 CHUNK_SIZE에 해당되는 경우
    if (this.rows.size() == CHUNK_SIZE) {
      this.parsedWriter.bulkWriteAndSaveInfoData(this.rows, rowNum, false);
      rows.clear();
    }
  }

  @Override
  public void hyperlinkCell(String cellReference, String formattedValue, String url, String toolTip,
      XSSFComment comment) {
  }
}