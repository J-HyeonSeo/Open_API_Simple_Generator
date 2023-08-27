package com.jhsfully.consumer.util;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

/**
 * Excel형식의 Date를 읽기 위해서, 따로 구현한 Date formatter
 */
public final class ExcelDateFormatter {

  public static final DateTimeFormatter EXCEL_LOCAL_DATE;
  static {
    EXCEL_LOCAL_DATE = new DateTimeFormatterBuilder()
        .appendOptional(DateTimeFormatter.ofPattern("MM/dd/yy"))
        .appendOptional(DateTimeFormatter.ofPattern("M/d/yy"))
        .appendOptional(DateTimeFormatter.ofPattern("MM/d/yy"))
        .appendOptional(DateTimeFormatter.ofPattern("M/dd/yy"))
        .toFormatter();
  }


}
