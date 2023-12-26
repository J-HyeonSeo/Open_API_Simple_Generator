package com.jhsfully.domain.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.springframework.util.StringUtils;

@Converter
public class JsonArrayConverter<T> implements AttributeConverter<List<T>, String> { // 1

  protected final ObjectMapper objectMapper;
  private final TypeReference<List<T>> typeReference;

  public JsonArrayConverter(TypeReference<List<T>> typeReference) {
    this.objectMapper = new ObjectMapper();
    this.typeReference = typeReference;
  }

  @Override
  public String convertToDatabaseColumn(List<T> attribute) {
    if (attribute == null || attribute.isEmpty()) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<T> convertToEntityAttribute(String dbData) {
    if (StringUtils.hasText(dbData)) {
      try {
        return objectMapper.readValue(dbData, typeReference);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }
    return null;
  }
}
