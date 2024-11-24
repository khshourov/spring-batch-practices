package com.github.khshourov.batchpractices.headerfooter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

public class HeaderFooterJobTest {
  @Test
  void testLaunchJob()
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobParametersInvalidException,
          JobRestartException,
          IOException {
    ApplicationContext applicationContext =
        new AnnotationConfigApplicationContext(HeaderFooterJob.class);
    JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    Job job = applicationContext.getBean(Job.class);

    JobExecution jobExecution =
        jobLauncher.run(
            job,
            new JobParametersBuilder()
                .addString(
                    "inputFile", "com/github/khshourov/batchpractices/headerfooter/data/names.txt")
                .addString("outputFile", "file:build/test-output/headerfooter.txt")
                .toJobParameters());

    assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    File expectedFile = new File("build/test-output/headerfooter.txt");

    assertTrue(expectedFile.exists());
    assertEquals(5, Files.lines(expectedFile.toPath()).count());
    assertTrue(expectedFile.delete(), "Delete build/test-output/headerfooter.txt manually");
  }
}
