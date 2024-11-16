package com.github.khshourov.batchpractices.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class JpaJobTest {
  @Test
  void testLaunchJobWithJavaConfig()
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobParametersInvalidException,
          JobRestartException {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(JpaJob.class);
    JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    Job job = applicationContext.getBean(Job.class);

    JobExecution jobExecution = jobLauncher.run(job, new JobParameters());

    assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
  }
}
