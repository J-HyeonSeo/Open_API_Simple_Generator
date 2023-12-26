package com.jhsfully.domain.type;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jhsfully.domain.converter.JsonArrayConverter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueryData {
    @NotBlank(message = "필드명은 필수값 입니다.")
    private String field;
    @NotNull(message = "타입은 필수값 입니다.")
    private ApiQueryType type;

    public static class QueryDataConverter extends JsonArrayConverter<QueryData> {
        public QueryDataConverter() {
            super(new TypeReference<>() {});
        }
    }
}
