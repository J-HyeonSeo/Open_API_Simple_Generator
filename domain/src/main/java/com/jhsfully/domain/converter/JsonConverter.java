package com.jhsfully.domain.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhsfully.domain.type.ApiQueryType;
import com.jhsfully.domain.type.ApiStructureType;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class JsonConverter implements AttributeConverter<Map<String, Object>, String> { // 1

  protected final ObjectMapper objectMapper;

  public JsonConverter() {
    objectMapper = new ObjectMapper();
  }

  @Override
  public String convertToDatabaseColumn(Map<String, Object> attribute) {
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Map<String, Object> convertToEntityAttribute(String dbData) {
    try {
      TypeReference<HashMap<String,Object>> typeRef = new TypeReference<>() {};
      Map<String, Object> outData = objectMapper.readValue(dbData, typeRef);

      //String으로 받아오는 이슈때문에, 형변환이 필요함.
      for(String key : outData.keySet()){
        Object value = outData.get(key);

        try{
          value = ApiStructureType.valueOf((String) value);
        }catch (IllegalArgumentException e){
          value = ApiQueryType.valueOf((String) value);
        }

        outData.put(key, value);
      }
      return outData;
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
