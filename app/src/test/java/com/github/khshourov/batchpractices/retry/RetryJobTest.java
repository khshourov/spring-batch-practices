package com.github.khshourov.batchpractices.retry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.khshourov.batchpractices.domain.trade.internal.GeneratingTradeItemReader;
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

public class RetryJobTest {
  @Test
  void testLaunchJob()
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobParametersInvalidException,
          JobRestartException {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(RetryJob.class);
    JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    GeneratingTradeItemReader itemReader =
        applicationContext.getBean("itemReader", GeneratingTradeItemReader.class);
    RetryItemWriter<Object> itemWriter =
        applicationContext.getBean("itemWriter", RetryItemWriter.class);
    Job job = applicationContext.getBean(Job.class);

    JobExecution jobExecution = jobLauncher.run(job, new JobParameters());

    assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    assertEquals(itemReader.getLimit() + 2, itemWriter.getCounter());
  }
}
