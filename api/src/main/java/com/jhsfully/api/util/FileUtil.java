package com.jhsfully.api.util;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {

  private static final Tika tika = new Tika();
  private static final String EXCEL_MIME_TYPE = "application/x-tika-ooxml";

  public static boolean validFileExtension(MultipartFile file) throws IOException {

    try(InputStream inputStream = file.getInputStream()){
      String mimeType = tika.detect(inputStream);
      if (EXCEL_MIME_TYPE.equals(mimeType) &&
        file.getOriginalFilename() != null){
        String[] periodSeparated = file.getOriginalFilename().split("\\.");
        String extension = periodSeparated[periodSeparated.length-1];
        if(extension.equals("xlsx") || extension.equals("xls")){
          return true;
        }
      }
    }
    return false;
  }

}
