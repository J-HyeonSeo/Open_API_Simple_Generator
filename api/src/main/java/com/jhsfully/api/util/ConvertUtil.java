package com.jhsfully.api.util;

import com.jhsfully.api.exception.ApiException;
import com.jhsfully.domain.type.ApiStructureType;
import com.jhsfully.domain.type.errortype.ApiErrorType;
import java.time.LocalDate;

public class ConvertUtil {

  public static Object ObjectToStructureType(Object value, ApiStructureType type){

    Object returningObj = value.toString();

    try {
      switch (type) {
        case INTEGER:
          returningObj = Integer.parseInt(value.toString());
          break;
        case FLOAT:
          returningObj = Double.parseDouble(value.toString());
          break;
        case DATE:
          returningObj = LocalDate.parse(value.toString());
          break;
      }
    }catch (Exception e){
      throw new ApiException(ApiErrorType.API_DATA_CANNOT_PARSE);
    }
    return returningObj;
  }
}
