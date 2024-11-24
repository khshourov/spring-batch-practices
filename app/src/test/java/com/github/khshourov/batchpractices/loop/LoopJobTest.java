package com.github.khshourov.batchpractices.loop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.khshourov.batchpractices.domain.trade.internal.ItemTrackingTradeItemWriter;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class LoopJobTest {
  @Test
  void testLaunchJob()
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobParametersInvalidException,
          JobRestartException {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(LoopJob.class);
    JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    Job job = applicationContext.getBean(Job.class);
    ItemTrackingTradeItemWriter itemWriter =
        applicationContext.getBean("itemWriter", ItemTrackingTradeItemWriter.class);

    JobExecution jobExecution = jobLauncher.run(job, new JobParameters());

    // We're executing same step `step2` for 8 more times. Though we're
    // allowing to restart the step, the final batch results in failed.
    assertEquals(BatchStatus.FAILED, jobExecution.getStatus());
    assertEquals(10, itemWriter.getTrades().size());
  }
}
