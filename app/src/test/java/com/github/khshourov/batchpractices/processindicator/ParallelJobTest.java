package com.github.khshourov.batchpractices.processindicator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

public class ParallelJobTest {
  @Test
  void testLoadTradesToBatchStaging()
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobRestartException,
          JobInterruptedException {
    ApplicationContext applicationContext =
        new AnnotationConfigApplicationContext(ParallelJob.class);
    JobRepository jobRepository = applicationContext.getBean(JobRepository.class);
    JobExecution jobExecution =
        jobRepository.createJobExecution(
            "testJob",
            new JobParametersBuilder()
                .addString(
                    "inputFile",
                    "com/github/khshourov/batchpractices/processindicator/data/trades.txt")
                .toJobParameters());

    StepExecution stepExecution = jobExecution.createStepExecution("loadToStaging");
    jobRepository.add(stepExecution);
    Step step = applicationContext.getBean("loadToStaging", Step.class);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(applicationContext.getBean(DataSource.class));

    step.execute(stepExecution);

    assertEquals(ExitStatus.COMPLETED, stepExecution.getExitStatus());
    assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate, "BATCH_STAGING"));
  }

  @Test
  void testLaunchJob()
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobParametersInvalidException,
          JobRestartException {
    ApplicationContext applicationContext =
        new AnnotationConfigApplicationContext(ParallelJob.class);
    JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    Job job = applicationContext.getBean(Job.class);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(applicationContext.getBean(DataSource.class));

    JobExecution jobExecution =
        jobLauncher.run(
            job,
            new JobParametersBuilder()
                .addString(
                    "inputFile",
                    "com/github/khshourov/batchpractices/processindicator/data/trades.txt")
                .toJobParameters());

    assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    assertEquals(
        5, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "BATCH_STAGING", "processed = 'Y'"));
    assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate, "TRADE"));
  }
}
