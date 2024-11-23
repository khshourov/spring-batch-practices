package com.github.khshourov.batchpractices.football;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

public class FootballJobTest {

  @Test
  void testPlayerLoadStep()
      throws JobInterruptedException,
          JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobRestartException {
    ApplicationContext applicationContext =
        new AnnotationConfigApplicationContext(FootballJob.class);
    JobRepository jobRepository = applicationContext.getBean(JobRepository.class);
    JobExecution jobExecution =
        jobRepository.createJobExecution(
            "testJob",
            new JobParametersBuilder()
                .addString(
                    "playerInputFile",
                    "com/github/khshourov/batchpractices/football/data/players-small-v1.csv")
                .toJobParameters());
    StepExecution stepExecution = jobExecution.createStepExecution("playerLoad");
    jobRepository.add(stepExecution);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(applicationContext.getBean(DataSource.class));

    Step step = applicationContext.getBean("playerLoad", Step.class);

    step.execute(stepExecution);

    assertEquals(ExitStatus.COMPLETED, stepExecution.getExitStatus());
    int actualPlayersCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "PLAYERS");
    assertEquals(20, actualPlayersCount);
  }

  @Test
  void testGameLoadStep()
      throws JobInterruptedException,
          JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobRestartException {
    ApplicationContext applicationContext =
        new AnnotationConfigApplicationContext(FootballJob.class);
    JobRepository jobRepository = applicationContext.getBean(JobRepository.class);
    JobExecution jobExecution =
        jobRepository.createJobExecution(
            "testJob",
            new JobParametersBuilder()
                .addString(
                    "gameInputFile",
                    "com/github/khshourov/batchpractices/football/data/games-small.csv")
                .toJobParameters());
    StepExecution stepExecution = jobExecution.createStepExecution("gameLoad");
    jobRepository.add(stepExecution);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(applicationContext.getBean(DataSource.class));

    Step step = applicationContext.getBean("gameLoad", Step.class);

    step.execute(stepExecution);

    assertEquals(ExitStatus.COMPLETED, stepExecution.getExitStatus());
    int actualGamesCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "GAMES");
    assertEquals(5, actualGamesCount);
  }

  @Test
  void testLaunchJob()
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobParametersInvalidException,
          JobRestartException {
    ApplicationContext applicationContext =
        new AnnotationConfigApplicationContext(FootballJob.class);
    JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    Job job = applicationContext.getBean(Job.class);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(applicationContext.getBean(DataSource.class));

    JobExecution jobExecution =
        jobLauncher.run(
            job,
            new JobParametersBuilder()
                .addString(
                    "playerInputFile",
                    "com/github/khshourov/batchpractices/football/data/players-small-v1.csv")
                .addString(
                    "gameInputFile",
                    "com/github/khshourov/batchpractices/football/data/games-small.csv")
                .toJobParameters());

    assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

    int playerSummaryCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "PLAYER_SUMMARY");
    assertTrue(playerSummaryCount > 0);
  }
}
