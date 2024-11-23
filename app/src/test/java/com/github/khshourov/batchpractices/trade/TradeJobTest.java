package com.github.khshourov.batchpractices.trade;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.khshourov.batchpractices.lib.SqlFileExecutor;
import java.io.IOException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.JobParameters;
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

public class TradeJobTest {
  @Test
  void testTradeLoadStep()
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobRestartException,
          JobInterruptedException {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(TradeJob.class);
    JobRepository jobRepository = applicationContext.getBean(JobRepository.class);
    JobExecution jobExecution =
        jobRepository.createJobExecution(
            "testJob",
            new JobParametersBuilder()
                .addString(
                    "tradeInputFile", "com/github/khshourov/batchpractices/trade/data/trades.txt")
                .toJobParameters());
    StepExecution stepExecution = jobExecution.createStepExecution("tradeLoad");
    jobRepository.add(stepExecution);
    Step step = applicationContext.getBean("tradeLoad", Step.class);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(applicationContext.getBean(DataSource.class));

    step.execute(stepExecution);

    assertEquals(ExitStatus.COMPLETED, stepExecution.getExitStatus());

    int totalTrades = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TRADE");
    assertEquals(5, totalTrades);
  }

  @Test
  void testTradeLoadFromDbStep()
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobRestartException,
          JobInterruptedException,
          IOException {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(TradeJob.class);
    JobRepository jobRepository = applicationContext.getBean(JobRepository.class);
    JobExecution jobExecution = jobRepository.createJobExecution("testJob", new JobParameters());

    StepExecution stepExecution = jobExecution.createStepExecution("tradeLoadFromDb");
    jobRepository.add(stepExecution);
    Step step = applicationContext.getBean("tradeLoadFromDb", Step.class);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(applicationContext.getBean(DataSource.class));
    SqlFileExecutor fileExecutor = new SqlFileExecutor(jdbcTemplate);
    fileExecutor.executeSqlFile("com/github/khshourov/batchpractices/trade/data/setup.sql");

    step.execute(stepExecution);

    assertEquals(ExitStatus.COMPLETED, stepExecution.getExitStatus());
    assertEquals(
        1,
        JdbcTestUtils.countRowsInTableWhere(
            jdbcTemplate, "CUSTOMER", "name = 'customer1' AND credit = 990"));
    assertEquals(
        1,
        JdbcTestUtils.countRowsInTableWhere(
            jdbcTemplate, "CUSTOMER", "name = 'customer2' AND credit = 980"));
    assertEquals(
        1,
        JdbcTestUtils.countRowsInTableWhere(
            jdbcTemplate, "CUSTOMER", "name = 'customer3' AND credit = 970"));
    assertEquals(
        1,
        JdbcTestUtils.countRowsInTableWhere(
            jdbcTemplate, "CUSTOMER", "name = 'customer4' AND credit = 960"));
  }

  @Test
  void testLaunchJob()
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobParametersInvalidException,
          JobRestartException {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(TradeJob.class);
    JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    Job job = applicationContext.getBean(Job.class);

    JobExecution jobExecution =
        jobLauncher.run(
            job,
            new JobParametersBuilder()
                .addString(
                    "tradeInputFile", "com/github/khshourov/batchpractices/trade/data/trades.txt")
                .addString(
                    "customerCreditOutputFile", "file:build/test-output/customer-credit-output.txt")
                .toJobParameters());

    assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
  }
}
