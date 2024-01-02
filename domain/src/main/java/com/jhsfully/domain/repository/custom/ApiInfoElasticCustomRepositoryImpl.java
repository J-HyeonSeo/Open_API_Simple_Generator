package com.jhsfully.domain.repository.custom;

import static com.jhsfully.domain.type.SearchType.NONE;

import com.jhsfully.domain.entity.ApiInfoElastic;
import com.jhsfully.domain.type.SearchType;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.HasChildQueryBuilder;
import org.elasticsearch.join.query.HasParentQueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ScriptType;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class ApiInfoElasticCustomRepositoryImpl implements ApiInfoElasticCustomRepository {

  private final ElasticsearchOperations elasticsearchOperations;
  private final ElasticsearchRestTemplate elasticsearchRestTemplate;
  private final RestHighLevelClient restHighLevelClient;

  @Override
  public Page<ApiInfoElastic> search(String searchText, SearchType type, Pageable pageable) {

    String searchByColumn;

    if (type == null) {
      type = NONE;
    }

    switch (type){

      case API_INTRODUCE:
        searchByColumn = "apiIntroduce.ngram";
        break;
      case API_OWNER_NICKNAME:
        searchByColumn = "ownerNickname.ngram";
        break;
      default:
        searchByColumn = "apiName.ngram";
    }


    NativeSearchQuery query = StringUtils.hasText(searchText) ?
        new NativeSearchQueryBuilder()
        .withQuery(QueryBuilders.boolQuery()
            .must(QueryBuilders.matchQuery(searchByColumn, searchText))
            .must(QueryBuilders.matchQuery("isPublic", true))
            .must(QueryBuilders.matchQuery("apiState", "ENABLED"))
        )
        .withPageable(pageable)
        .build() :
        new NativeSearchQueryBuilder()
            .withQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("isPublic", true))
                .must(QueryBuilders.matchQuery("apiState", "ENABLED"))
            )
            .withPageable(pageable)
            .build();


    SearchHits<ApiInfoElastic> searchHits = elasticsearchOperations.search(query, ApiInfoElastic.class);

    return new PageImpl<>(searchHits.getSearchHits()
        .stream()
        .map(SearchHit::getContent)
        .collect(Collectors.toList()), pageable, searchHits.getTotalHits());
  }

  @Override
  public Page<ApiInfoElastic> searchForOwner(Long ownerMemberId, String searchText, SearchType type, Pageable pageable){

    String searchByColumn;

    if (type == null) {
      type = NONE;
    }

    switch (type){

      case API_INTRODUCE:
        searchByColumn = "apiIntroduce.ngram";
        break;
      case API_OWNER_NICKNAME:
        searchByColumn = "ownerNickname.ngram";
        break;
      default:
        searchByColumn = "apiName.ngram";
    }

    NativeSearchQuery query = StringUtils.hasText(searchText) ?
        new NativeSearchQueryBuilder()
        .withQuery(QueryBuilders.boolQuery()
            .must(QueryBuilders.matchQuery(searchByColumn, searchText))
            .must(QueryBuilders.matchQuery("ownerMemberId", ownerMemberId))
        )
        .withPageable(pageable)
        .build() :
        new NativeSearchQueryBuilder()
            .withQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("ownerMemberId", ownerMemberId))
            )
            .withPageable(pageable)
            .build();

    SearchHits<ApiInfoElastic> searchHits = elasticsearchOperations.search(query, ApiInfoElastic.class);

    return new PageImpl<>(searchHits.getSearchHits()
        .stream()
        .map(SearchHit::getContent)
        .collect(Collectors.toList()), pageable, searchHits.getTotalHits());

  }

  @Override
  public Page<ApiInfoElastic> searchForAccessor(Long accessMemberId, String searchText, SearchType type, Pageable pageable){

    String searchByColumn;

    if (type == null) {
      type = NONE;
    }

    switch (type){

      case API_INTRODUCE:
        searchByColumn = "apiIntroduce.ngram";
        break;
      case API_OWNER_NICKNAME:
        searchByColumn = "ownerNickname.ngram";
        break;
      default:
        searchByColumn = "apiName.ngram";
    }

    NativeSearchQuery query = StringUtils.hasText(searchText) ?
        new NativeSearchQueryBuilder()
        .withQuery(
            QueryBuilders.boolQuery()
                .must(new HasChildQueryBuilder("accessMember",
                    QueryBuilders.matchQuery("accessMemberId", accessMemberId), ScoreMode.None))
                .must(QueryBuilders.matchQuery(searchByColumn, searchText))
        )
        .build() :
        new NativeSearchQueryBuilder()
            .withQuery(
                QueryBuilders.boolQuery()
                    .must(new HasChildQueryBuilder("accessMember",
                        QueryBuilders.matchQuery("accessMemberId", accessMemberId), ScoreMode.None))
            )
            .build();

    SearchHits<ApiInfoElastic> searchHits = elasticsearchOperations.search(query, ApiInfoElastic.class);

    return new PageImpl<>(searchHits.getSearchHits()
        .stream()
        .map(SearchHit::getContent)
        .collect(Collectors.toList()), pageable, searchHits.getTotalHits());

  }

  /**
   *  MemberId를 기준으로 Elasticsearch에 저장된 api데이터를 비활성화 시키는 로직.
   */
  @Override
  public void changeToDisabledByMemberId(Long memberId) {
    // 업데이트 대상 지정.
    NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
        .withQuery(QueryBuilders.termQuery("ownerMemberId", memberId))
        .build();

    // 업데이트할 데이터 지정.
    Map<String, Object> params = new HashMap<>(){{put("apiState", "DISABLED");}};
    UpdateQuery updateQuery = UpdateQuery.builder(searchQuery)
        .withScriptType(ScriptType.INLINE)
        .withLang("painless")
        .withScript("ctx._source.apiState = params.apiState") // 변경할 값 직접 스크립트에 포함
        .withParams(params)
        .build();

    // Elasticsearch 업데이트 실행
    elasticsearchOperations.updateByQuery(updateQuery,
        IndexCoordinates.of("api_info"));

  }

  @Override
  public void deleteAccessors(Long apiInfoId) {

//    @Query(
//        "{\"has_parent\": {"
//            +"\"parent_type\": \"apiInfo\","
//            +"\"query\": {"
//            +"\"match\": {"
//            +"\"id\": \"?0\"}}}}"
//    )

    HasParentQueryBuilder findQuery = new HasParentQueryBuilder("apiInfo",
        QueryBuilders.matchQuery("id", apiInfoId), false);

    NativeSearchQuery mainQuery = new NativeSearchQueryBuilder()
        .withQuery(findQuery)
        .build();

    elasticsearchOperations.delete(mainQuery, ApiInfoElastic.class);
  }
}
