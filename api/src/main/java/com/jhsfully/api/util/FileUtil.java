package com.jhsfully.api.util;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {

  private static final Tika tika = new Tika();
  private static final String EXCEL_MIME_TYPE = "application/x-tika-ooxml";

  public static boolean validFileExtension(MultipartFile file) throws IOException {

    InputStream inputStream = file.getInputStream();

    try{
      String mimeType = tika.detect(inputStream);

      if (EXCEL_MIME_TYPE.equals(mimeType)){

        String extension = file.getOriginalFilename().split("\\.")[1];

        if(extension.equals("xlsx") || extension.equals("xls")){
          return true;
        }

      }

    }catch (IOException e){
      return false;
    }finally {
      inputStream.close();
    }
    return false;
  }

}
