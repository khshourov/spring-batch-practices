package com.github.khshourov.batchpractices.restart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

public class RestartJobTest {
  private static final String inputFile =
      "com/github/khshourov/batchpractices/restart/data/trades.txt";

  @Test
  void testLaunchJob()
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobParametersInvalidException,
          JobRestartException {
    ApplicationContext applicationContext =
        new AnnotationConfigApplicationContext(RestartJob.class);
    JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    Job job = applicationContext.getBean(Job.class);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(applicationContext.getBean(DataSource.class));

    JobExecution jobExecution =
        jobLauncher.run(
            job, new JobParametersBuilder().addString("inputFile", inputFile).toJobParameters());

    assertEquals(BatchStatus.FAILED, jobExecution.getStatus());
    assertTrue(
        jobExecution
            .getAllFailureExceptions()
            .getFirst()
            .getMessage()
            .toLowerCase()
            .contains("planned"));
    assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, "TRADE"));

    jobExecution =
        jobLauncher.run(
            job, new JobParametersBuilder().addString("inputFile", inputFile).toJobParameters());

    assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate, "TRADE"));
  }
}
