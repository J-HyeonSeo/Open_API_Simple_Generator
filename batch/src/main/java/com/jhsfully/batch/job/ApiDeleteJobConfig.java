package com.jhsfully.batch.job;

import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.repository.ApiInfoElasticRepository;
import com.jhsfully.domain.type.ApiState;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ApiDeleteJobConfig {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final EntityManagerFactory entityManagerFactory;
  private final MongoTemplate mongoTemplate;
  private final ApiInfoElasticRepository apiInfoElasticRepository;
  private static final int CHUNK_SIZE = 1000;

  /*
      ################# JOB #####################
   */
  @Bean
  public Job apiDeleteJob() {
    return jobBuilderFactory.get("apiDeleteJob")
        .start(apiDeleteStep())
        .build();
  }

  /*
      ################# STEPS ###################
   */
  @Bean
  public Step apiDeleteStep() {
    return stepBuilderFactory.get("apiDeleteStep")
        .<ApiInfo, ApiInfo>chunk(CHUNK_SIZE)
        .reader(apiInfoDisableItemReader())
        .processor(apiInfoDisableItemProcessor())
        .writer(compositeItemWriter())
        .faultTolerant()
        .retry(SQLException.class) //SQLException 에 대해서는 최대 2번 재시도
        .retryLimit(2)
        .build();
  }

  /*
      ################# READERS, PROCESSORS, WRITERS ###################
   */

  /*
      현재날짜에, 2주를 뺀 값이, disabledAt을 넘게되면,
      이는 Disabled상태에 도입하고, 2주가 넘었다는 것을 판단할 수 있을거임.

      now - 2weeks > disabledAt 조건을 만족한다면, 이는 삭제 대상일 거임.
   */
  @Bean
  public JpaPagingItemReader<ApiInfo> apiInfoDisableItemReader() {
    return new JpaPagingItemReaderBuilder<ApiInfo>()
        .name("apiInfoDisableItemReader")
        .entityManagerFactory(entityManagerFactory)
        .pageSize(CHUNK_SIZE)
        .queryString("SELECT a FROM ApiInfo a WHERE a.apiState = 'DISABLED' AND :twoWeeksAgo > a.disabledAt")
        .parameterValues(Collections.singletonMap("twoWeeksAgo", LocalDateTime.now().minusWeeks(2)))
        .build();
  }

  /*
      여기로 온 데이터는 다음과 같은 과정을 거쳐야 함.

      1. apiInfo의 apiState를 Deleted로 변환함.
      2. mongoDB의 컬렉션을 제거해주어야 함.
      3. ElasticSearch의 doc를 제거해주어야 함.

   */
  @Bean
  public ItemProcessor<ApiInfo, ApiInfo> apiInfoDisableItemProcessor() {
    return apiInfo -> {
      apiInfo.setApiState(ApiState.DELETED);
      return apiInfo;
    };
  }

  @Bean
  public CompositeItemWriter<ApiInfo> compositeItemWriter() {
    final CompositeItemWriter<ApiInfo> compositeItemWriter = new CompositeItemWriter<>();
    compositeItemWriter.setDelegates(Arrays.asList(apiInfoDisableItemWriter(), mongoDBItemWriter(), elasticItemWriter()));
    return compositeItemWriter;
  }

  @Bean
  public JpaItemWriter<ApiInfo> apiInfoDisableItemWriter() {
    JpaItemWriter<ApiInfo> jpaItemWriter = new JpaItemWriter<>();
    jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
    return jpaItemWriter;
  }

  @Bean
  public ItemWriter<ApiInfo> mongoDBItemWriter() { //MongoDB의 데이터를 제거해보자.
    return new ItemWriter<ApiInfo>() {
      @Override
      public void write(List<? extends ApiInfo> items) throws Exception {

        for(ApiInfo item : items){
          try{
            mongoTemplate.dropCollection(item.getDataCollectionName());
            mongoTemplate.dropCollection(item.getHistoryCollectionName());
          }catch (Exception e){
            log.error(e.getMessage());
            log.error("{} 컬렉션의 데이터를 삭제하지 못하였습니다.", item.getDataCollectionName());
          }
        }

      }
    };
  }

  @Bean
  public ItemWriter<ApiInfo> elasticItemWriter() { //ElasticSearch의 데이터를 제거해보자.
    return new ItemWriter<ApiInfo>() {
      @Override
      public void write(List<? extends ApiInfo> items) throws Exception {

        for(ApiInfo item : items){
          try{

            //자식 도큐먼트를 찾아서 제거해야함.
            apiInfoElasticRepository.deleteAccessors(item.getId());

            //자기 자신 제거해야함.
            apiInfoElasticRepository.deleteById(item.getId());

          }catch (Exception e){
            log.error(e.getMessage());
            log.error("API_ID {}에 해당되는 엘라스틱서치 데이터를 삭제하지 못하였습니다.", item.getId());
          }
        }

      }
    };
  }

}
