package com.jhsfully.batch.job;

import com.jhsfully.batch.util.MongoUtil;
import com.jhsfully.domain.entity.ApiInfo;
import com.jhsfully.domain.entity.Grade;
import com.jhsfully.domain.entity.Member;
import com.jhsfully.domain.repository.ApiInfoRepository;
import com.jhsfully.domain.repository.ApiUserPermissionRepository;
import com.jhsfully.domain.type.ApiState;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.persistence.EntityManagerFactory;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ApiDisableJobConfig {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final EntityManagerFactory entityManagerFactory;
  private final MongoTemplate mongoTemplate;
  private final ApiInfoRepository apiInfoRepository;
  private final ApiUserPermissionRepository apiUserPermissionRepository;

  private static final int CHUNK_SIZE = 1000;

  @Bean
  public Job apiDisableJob() {
    return jobBuilderFactory.get("apiDisableJob")
        .start(apiDisableStep())
        .build();
  }

  @Bean
  public Step apiDisableStep() {
    return stepBuilderFactory.get("apiDisableStep")
        .<ApiInfo, ApiInfo>chunk(CHUNK_SIZE)
        .reader(apiInfoItemReader())
        .processor(apiInfoItemProcessor())
        .writer(apiInfoItemWriter())
        .faultTolerant()
        .retry(SQLException.class) //SQLException 에 대해서는 최대 2번 재시도
        .retryLimit(2)
        .build();
  }

  @Bean
  public JpaPagingItemReader<ApiInfo> apiInfoItemReader() {
    return new JpaPagingItemReaderBuilder<ApiInfo>()
        .name("jpaPagingItemReader")
        .entityManagerFactory(entityManagerFactory)
        .pageSize(CHUNK_SIZE)
        .queryString("SELECT a FROM ApiInfo a WHERE "
            + "(a.apiState = 'ENABLED' "
            + "AND a.member.grade.gradeName <> 'BRONZE' "
            + "AND a.member.expiredEnabledAt < :dateNow) "
            + "OR (a.member.gradeChanged = true)")
        .parameterValues(Collections.singletonMap("dateNow", LocalDate.now()))
        .build();
  }

  @Bean
  public ItemProcessor<ApiInfo, ApiInfo> apiInfoItemProcessor() {
    return apiInfo -> {

      Member member = apiInfo.getMember();

      //우선적으로 member의 API 활성 만료 기한이 오늘을 넘길 경우, 비활성화 시킴.
      if (LocalDate.now().isAfter(member.getExpiredEnabledAt())) {
        apiInfo.setApiState(ApiState.DISABLED);
        apiInfo.setDisabledAt(LocalDateTime.now());
        return apiInfo;
      }

      //그 다음은, Grade가 변동되었으므로, Member의 Grade와 MongoTemplate을 조회하여 적합성 검사가 필요함.
      Grade grade = member.getGrade();

      //compare datas..
      long dbSize = MongoUtil.getDbSizeByCollection(mongoTemplate, apiInfo.getDataCollectionName());
      long recordCount = mongoTemplate.getCollection(apiInfo.getDataCollectionName())
          .countDocuments();
      int queryCount = apiInfo.getQueryParameter().size();
      int schemaCount = apiInfo.getSchemaStructure().size();
      int apiCount = apiInfoRepository.countByMemberAndApiState(member, ApiState.ENABLED);
      int accessorCount = apiUserPermissionRepository.countByApiInfo(apiInfo);

      if (dbSize > grade.getDbMaxSize() ||
          recordCount > grade.getRecordMaxCount() ||
          queryCount > grade.getQueryMaxCount() ||
          schemaCount > grade.getFieldMaxCount() ||
          apiCount > grade.getApiMaxCount() ||
          accessorCount > grade.getAccessorMaxCount()) {
        apiInfo.setApiState(ApiState.DISABLED);
        apiInfo.setDisabledAt(LocalDateTime.now());
      }

      return apiInfo;
    };
  }

  @Bean
  public JpaItemWriter<ApiInfo> apiInfoItemWriter() {
    JpaItemWriter<ApiInfo> jpaItemWriter = new JpaItemWriter<>();
    jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
    return jpaItemWriter;
  }


}
