package com.jhsfully.batch.scheduler;

import com.jhsfully.batch.job.ApiDeleteJobConfig;
import com.jhsfully.batch.job.ApiDisableJobConfig;
import com.jhsfully.batch.job.ApiHistoryDeleteJobConfig;
import com.jhsfully.batch.job.MemberChangeStateJobConfig;
import com.jhsfully.domain.repository.GradeRepository;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobScheduler {

  private final JobLauncher jobLauncher;
  private final ApiDisableJobConfig apiDisableJobConfig;
  private final ApiDeleteJobConfig apiDeleteJobConfig;
  private final MemberChangeStateJobConfig memberChangeStateJobConfig;
  private final ApiHistoryDeleteJobConfig apiHistoryDeleteJobConfig;
  private final GradeRepository gradeRepository;

  @Scheduled(cron = "5 0 0 * * *")
  public void launchJobs()
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

    JobParameters jobParameters = new JobParametersBuilder()
        .addDate("requestDate", new Date())
        .toJobParameters();

    /*
        잡은 병렬적 수행이 아닌, 데이터의 일관성을 위해 각각 순차적으로 수행되어야 함.

        1번 잡 -> member의 EnableDays나 Grade의 변경 사항을 감지하여, 변경여부를 확인하고, API의 비활성화를 결정함.
        2번 잡 -> apiInfo의 상태가 Disabled이고, DisabledAt + 2주가 오늘을 넘기는 API에 대해 Delete를 수행함.
        3번 잡 -> member의 EnableDays와 gradeChange를 조정하는 작업을 수행함.
        4번 잡 -> ENABLED 상태의 API 중, history collection의 데이터를 최신 N일까지의 데이터만 남기고, 지우는 작업을 수행함.
     */

    // first job
    jobLauncher.run(apiDisableJobConfig.apiDisableJob(), jobParameters);

    //second job
    jobLauncher.run(apiDeleteJobConfig.apiDeleteJob(), jobParameters);

    //third job
    jobLauncher.run(memberChangeStateJobConfig.memberChangeStateJob(), jobParameters);

    //forth job
    jobLauncher.run(apiHistoryDeleteJobConfig.apiHistoryDeleteJob(), jobParameters);

  }

}
