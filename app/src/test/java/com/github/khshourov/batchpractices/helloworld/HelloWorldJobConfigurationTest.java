package com.github.khshourov.batchpractices.helloworld;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

public class HelloWorldJobConfigurationTest {

  @Test
  void jobShouldBeInCompletedStatusWhenFinished()
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobParametersInvalidException,
          JobRestartException {
    ApplicationContext context =
        new AnnotationConfigApplicationContext(HelloWorldJobConfiguration.class);
    JobLauncher jobLauncher = context.getBean(JobLauncher.class);
    Job job = context.getBean(Job.class);

    JobExecution jobExecution = jobLauncher.run(job, new JobParameters());

    assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
  }
}
