package com.jhsfully.batch.job;

import com.jhsfully.domain.entity.Member;
import java.sql.SQLException;
import javax.persistence.EntityManagerFactory;
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

@Configuration
@Slf4j
@RequiredArgsConstructor
public class MemberMinusDaysJobConfig {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final EntityManagerFactory entityManagerFactory;
  private static final int CHUNK_SIZE = 1000;

  @Bean
  public Job memberMinusDaysJob() {
    return jobBuilderFactory.get("memberMinusDaysJob")
        .start(memberMinusDaysStep())
        .build();
  }

  @Bean
  public Step memberMinusDaysStep() {
    return stepBuilderFactory.get("memberMinusDaysStep")
        .<Member, Member>chunk(CHUNK_SIZE)
        .reader(memberItemReader())
        .processor(memberItemProcessor())
        .writer(memberItemWriter())
        .faultTolerant()
        .retry(SQLException.class) //SQLException 에 대해서는 최대 2번 재시도
        .retryLimit(2)
        .build();
  }

  @Bean
  public JpaPagingItemReader<Member> memberItemReader() {
    return new JpaPagingItemReaderBuilder<Member>()
        .name("memberItemReader")
        .entityManagerFactory(entityManagerFactory)
        .pageSize(CHUNK_SIZE)
        .queryString("SELECT m FROM Member m")
        .build();
  }

  /*
      처리단에서 수행하는 목록,
      하루가 지났으므로, remainEnableDays를 -1 감소 시켜준다. max함수는 음수 값 방지
      member의 등급의 변경을 감지하는 gradeChanged를 다시 false로 변경해준다.
   */
  @Bean
  public ItemProcessor<Member, Member> memberItemProcessor() {
    return member -> {
      member.setRemainEnableDays(Math.max(member.getRemainEnableDays() - 1, 0));
      member.setGradeChanged(false);
      return member;
    };
  }

  @Bean
  public JpaItemWriter<Member> memberItemWriter() {
    JpaItemWriter<Member> jpaItemWriter = new JpaItemWriter<>();
    jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
    return jpaItemWriter;
  }



}
