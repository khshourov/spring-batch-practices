package com.github.khshourov.batchpractices.multiline;

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

public class MultilineJobTest {
  private static final String inputFile =
      "com/github/khshourov/batchpractices/multiline/data/trades.txt";
  private static final String outputFile = "build/test-output/multiline.txt";

  @Test
  void testLaunchJob()
      throws IOException,
          JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobParametersInvalidException,
          JobRestartException {
    ApplicationContext applicationContext =
        new AnnotationConfigApplicationContext(MultilineJob.class);
    JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    Job job = applicationContext.getBean(Job.class);

    JobExecution jobExecution =
        jobLauncher.run(
            job,
            new JobParametersBuilder()
                .addString("inputFile", inputFile)
                .addString("outputFile", "file:" + outputFile)
                .toJobParameters());

    assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

    Path inputPath = new ClassPathResource(inputFile).getFile().toPath();
    Path outputPath = new PathResource(outputFile).getFile().toPath();
    assertLinesMatch(Files.lines(inputPath), Files.lines(outputPath));
  }
}
