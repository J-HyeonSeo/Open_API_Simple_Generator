package com.jhsfully.domain.repository;

import com.jhsfully.domain.entity.ApiInfoElastic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiInfoElasticRepository extends ElasticsearchRepository<ApiInfoElastic, Long>{
  @Query("{\"bool\": {\"must\": [{\"match\": {\"apiName.ngram\": \"?0\"}}, {\"match\": {\"isPublic\": \"true\"}}]}}")
  Page<ApiInfoElastic> searchByApiName(String searchText, Pageable pageable);

  @Query("{\"bool\": {\"must\": [{\"match\": {\"apiIntroduce.ngram\": \"?0\"}}, {\"match\": {\"isPublic\": \"true\"}}]}}")
  Page<ApiInfoElastic> searchByApiIntroduce(String searchText, Pageable pageable);

  @Query("{\"bool\": {\"must\": [{\"match\": {\"memberEmail.ngram\": \"?0\"}}, {\"match\": {\"isPublic\": \"true\"}}]}}")
  Page<ApiInfoElastic> searchByOwnerEmail(String searchText, Pageable pageable);
}
