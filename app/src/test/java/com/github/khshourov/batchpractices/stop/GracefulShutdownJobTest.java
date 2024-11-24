package com.github.khshourov.batchpractices.stop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GracefulShutdownJobTest {

  @Test
  void testLaunchJob()
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobParametersInvalidException,
          JobRestartException,
          InterruptedException,
          NoSuchJobExecutionException,
          JobExecutionNotRunningException {
    ApplicationContext applicationContext =
        new AnnotationConfigApplicationContext(GracefulShutdownJob.class);
    JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    Job job = applicationContext.getBean(Job.class);

    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    JobOperator jobOperator = applicationContext.getBean(JobOperator.class);

    JobExecution jobExecution =
        jobLauncher.run(
            job,
            new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters());

    Thread.sleep(1000);

    assertEquals(BatchStatus.STARTED, jobExecution.getStatus());
    assertTrue(jobExecution.isRunning());

    jobOperator.stop(jobExecution.getId());

    Awaitility.await()
        .atMost(1, TimeUnit.SECONDS)
        .pollInterval(100, TimeUnit.MILLISECONDS)
        .until(() -> !jobExecution.isRunning());

    assertFalse(jobExecution.isRunning());
    assertEquals(BatchStatus.STOPPED, jobExecution.getStatus());
  }
}
