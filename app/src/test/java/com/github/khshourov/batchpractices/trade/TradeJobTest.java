package com.github.khshourov.batchpractices.trade;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
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
}
