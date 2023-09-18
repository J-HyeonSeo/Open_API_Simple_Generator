package com.jhsfully.batch.job;

import com.jhsfully.domain.entity.ApiInfo;
import java.sql.SQLException;
import java.time.LocalDate;
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
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/*
    ENABLE 상태인 API를 대상으로,
    특정 기간의 최신의 N일까지의 HISTORY데이터를 남기고,
    나머지 데이터를 제거해주는 JOB
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class ApiHistoryDeleteJobConfig {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final EntityManagerFactory entityManagerFactory;
  private final MongoTemplate mongoTemplate;

  private static final int CHUNK_SIZE = 1000;

  @Bean
  public Job apiHistoryDeleteJob() {
    return jobBuilderFactory.get("apiHistoryDeleteJob")
        .start(apiHistoryDeleteStep())
        .build();
  }

  @Bean
  public Step apiHistoryDeleteStep() {
    return stepBuilderFactory.get("apiHistoryDeleteStep")
        .<ApiInfo, ApiInfo>chunk(CHUNK_SIZE)
        .reader(apiInfoEnableItemReader())
        .processor(apiInfoPassProcessor())
        .writer(mongoHistoryWriter())
        .faultTolerant()
        .retry(SQLException.class) //SQLException 에 대해서는 최대 2번 재시도
        .retryLimit(2)
        .build();
  }

  @Bean
  public JpaPagingItemReader<ApiInfo> apiInfoEnableItemReader() {
    return new JpaPagingItemReaderBuilder<ApiInfo>()
        .name("apiInfoEnableItemReader")
        .entityManagerFactory(entityManagerFactory)
        .pageSize(CHUNK_SIZE)
        .queryString("SELECT a FROM ApiInfo a WHERE a.apiState = 'ENABLED'")
        .build();
  }

  @Bean
  public ItemProcessor<ApiInfo, ApiInfo> apiInfoPassProcessor(){
    return apiinfo -> apiinfo;
  }

  @Bean
  public ItemWriter<ApiInfo> mongoHistoryWriter() { //몽고 DB history 데이터 제거 해야함.
    return new ItemWriter<ApiInfo>() {
      @Override
      public void write(List<? extends ApiInfo> items) throws Exception {

        for(ApiInfo item : items){
          try{

            int remainDays = item.getMember().getGrade().getHistoryStorageDays();
            Query query = new Query(Criteria.where("at").lte(LocalDate.now().minusDays(remainDays)));
            mongoTemplate.remove(query, item.getHistoryCollectionName());
            log.info("API_ID {}에 해당되는 History데이터를 일부 삭제하였습니다.", item.getId());

          }catch (Exception e){
            log.error(e.getMessage());
            log.error("API_ID {}에 해당되는 History데이터를 삭제 하지 못하였습니다.", item.getId());
          }
        }

      }
    };
  }



}
