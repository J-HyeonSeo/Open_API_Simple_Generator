package com.jhsfully.domain.repository;

import com.jhsfully.domain.entity.ApiInfoElastic;
import com.jhsfully.domain.repository.custom.ApiInfoElasticCustomRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface ApiInfoElasticRepository extends ElasticsearchRepository<ApiInfoElastic, Long>,
    ApiInfoElasticCustomRepository {

  void deleteByPermissionId(Long permissionId); //remove by permission

}
