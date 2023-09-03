package com.jhsfully.domain.repository;

import com.jhsfully.domain.entity.ApiInfoElastic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiInfoElasticRepository extends ElasticsearchRepository<ApiInfoElastic, Long>{

  /*
      ########## FOR ALL SEARCH METHOD ###########
   */
  @Query("{\"bool\": {\"must\": "
      + "["
      + "{\"match\": {\"apiName.ngram\": \"?0\"}}, "
      + "{\"match\": {\"isPublic\": \"true\"}}, "
      + "{\"match\": {\"state\": \"ENABLED\"}}"
      + "]}}")
  Page<ApiInfoElastic> searchByApiName(String searchText, Pageable pageable);

  @Query("{\"bool\": {\"must\": "
      + "["
      + "{\"match\": {\"apiIntroduce.ngram\": \"?0\"}}, "
      + "{\"match\": {\"isPublic\": \"true\"}}, "
      + "{\"match\": {\"state\": \"ENABLED\"}}"
      + "]}}")
  Page<ApiInfoElastic> searchByApiIntroduce(String searchText, Pageable pageable);

  @Query("{\"bool\": {\"must\": "
      + "["
      + "{\"match\": {\"ownerEmail.ngram\": \"?0\"}}, "
      + "{\"match\": {\"isPublic\": \"true\"}}, "
      + "{\"match\": {\"state\": \"ENABLED\"}}"
      + "]}}")
  Page<ApiInfoElastic> searchByOwnerEmail(String searchText, Pageable pageable);



  /*
      ########## FOR OWNER SEARCH METHOD ###########
   */
  @Query("{\"bool\":{\"must\": "
      + "["
      + "{\"match\":{\"ownerMemberId\":\"?0\"}},"
      + "{\"match\":{\"apiName.ngram\":\"?1\"}}"
      + "]}}")
  Page<ApiInfoElastic> searchByApiNameForOwner(Long ownerMemberId, String searchText, Pageable pageable);

  @Query("{\"bool\":{\"must\": "
      + "["
      + "{\"match\":{\"ownerMemberId\":\"?0\"}},"
      + "{\"match\":{\"apiIntroduce.ngram\":\"?1\"}}"
      + "]}}")
  Page<ApiInfoElastic> searchByApiIntroduceForOwner(Long ownerMemberId, String searchText, Pageable pageable);

  @Query("{\"bool\":{\"must\": "
      + "["
      + "{\"match\":{\"ownerMemberId\":\"?0\"}},"
      + "{\"match\":{\"ownerEmail.ngram\":\"?1\"}}"
      + "]}}")
  Page<ApiInfoElastic> searchByOwnerEmailForOwner(Long ownerMemberId, String searchText, Pageable pageable);



    /*
      ########## FOR ACCESS MEMBER SEARCH METHOD ###########
   */

  @Query("{\"bool\":{\"must\": "
      + "["
      + "{\"has_child\":{\"type\":\"accessMember\",\"query\":{\"match\":{\"accessMemberId\":\"?0\"}}}},"
      + "{\"match\":{\"apiName.ngram\":\"?1\"}}"
      + "]}}")
  Page<ApiInfoElastic> searchByApiNameForAccess(Long accessMemberId, String searchText, Pageable pageable);

  @Query("{\"bool\":{\"must\": "
      + "["
      + "{\"has_child\":{\"type\":\"accessMember\",\"query\":{\"match\":{\"accessMemberId\":\"?0\"}}}},"
      + "{\"match\":{\"apiIntroduce.ngram\":\"?1\"}}"
      + "]}}")
  Page<ApiInfoElastic> searchByApiIntroduceForAccess(Long accessMemberId, String searchText, Pageable pageable);

  @Query("{\"bool\":{\"must\": "
      + "["
      + "{\"has_child\":{\"type\":\"accessMember\",\"query\":{\"match\":{\"accessMemberId\":\"?0\"}}}},"
      + "{\"match\":{\"ownerEmail.ngram\":\"?1\"}}"
      + "]}}")
  Page<ApiInfoElastic> searchByOwnerEmailForAccess(Long accessMemberId, String searchText, Pageable pageable);


  //deletion
  void deleteByPermissionId(Long permissionId);
}
