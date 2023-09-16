package com.jhsfully.domain.repository.custom;

import com.jhsfully.domain.entity.ApiInfoElastic;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;

@RequiredArgsConstructor
public class ApiInfoElasticCustomRepositoryImpl implements ApiInfoElasticCustomRepository{

  private final ElasticsearchOperations elasticsearchOperations;

  @Override
  public void deleteAccessors(Long apiInfoId){

    Query query = new StringQuery(
        "{\"has_parent\": {"
            +"\"parent_type\": \"apiInfo\","
            +"\"query\": {"
            +"\"match\": {"
            +"\"id\": \""+ apiInfoId +"\"}}}}"
    );

    elasticsearchOperations.delete(query, ApiInfoElastic.class);
  }
}
