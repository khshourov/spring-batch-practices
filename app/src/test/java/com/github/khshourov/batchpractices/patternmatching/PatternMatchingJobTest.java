package com.github.khshourov.batchpractices.patternmatching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;

public class PatternMatchingJobTest {
  private static final String outputFile = "build/test-output/orders.txt";

  @Test
  void testLaunchJob()
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobParametersInvalidException,
          JobRestartException,
          IOException {
    ApplicationContext applicationContext =
        new AnnotationConfigApplicationContext(PatternMatchingJob.class);
    JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    Job job = applicationContext.getBean(Job.class);

    JobExecution jobExecution =
        jobLauncher.run(
            job,
            new JobParametersBuilder()
                .addString(
                    "inputFile",
                    "com/github/khshourov/batchpractices/patternmatching/data/orders.txt")
                .addString("outputFile", "file:" + outputFile)
                .toJobParameters());

    assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

    Path expectedPath =
        new ClassPathResource(
                "com/github/khshourov/batchpractices/patternmatching/data/expected-output.txt")
            .getFile()
            .toPath();
    Path actualPath = new PathResource(outputFile).getFile().toPath();

    assertLinesMatch(Files.lines(expectedPath), Files.lines(actualPath));
    assertLinesMatch(Files.lines(expectedPath), Files.lines(actualPath));
  }
}
