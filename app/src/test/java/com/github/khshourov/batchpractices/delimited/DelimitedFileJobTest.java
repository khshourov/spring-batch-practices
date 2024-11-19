package com.github.khshourov.batchpractices.delimited;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
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

public class DelimitedFileJobTest {

  @Test
  void testLaunchJob()
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobParametersInvalidException,
          JobRestartException {
    ApplicationContext applicationContext =
        new AnnotationConfigApplicationContext(DelimitedFileJob.class);
    JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    Job job = applicationContext.getBean(Job.class);

    JobExecution jobExecution =
        jobLauncher.run(
            job,
            new JobParametersBuilder()
                .addString(
                    "inputFile", "com/github/khshourov/batchpractices/delimited/data/customers.csv")
                .addString("outputFile", "file:build/test-output/delimited.csv")
                .toJobParameters());

    assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

    File outputFile = new File("build/test-output/delimited.csv");
    assertTrue(outputFile.exists());
    assertTrue(outputFile.delete(), "Please manually delete file:build/test-output/delimited.csv");
  }
}
