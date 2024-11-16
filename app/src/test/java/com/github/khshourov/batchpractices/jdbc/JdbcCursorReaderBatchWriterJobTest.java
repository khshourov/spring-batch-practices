package com.github.khshourov.batchpractices.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.khshourov.batchpractices.jdbc.cursor.JdbcCursorReaderBatchWriterJob;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(
    locations = {
      "/simple-job-launcher-context.xml",
      "/com/github/khshourov/batchpractices/jdbc/job/jdbcCursor.xml"
    })
public class JdbcCursorReaderBatchWriterJobTest {
  @Autowired private JobLauncherTestUtils jobLauncherTestUtils;

  @Test
  void testLaunchJobWithXmlConfig() throws Exception {
    JobExecution jobExecution = this.jobLauncherTestUtils.launchJob();

    assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
  }

  @Test
  void testLaunchJobWithJavaConfig() throws Exception {
    ApplicationContext applicationContext =
        new AnnotationConfigApplicationContext(JdbcCursorReaderBatchWriterJob.class);
    JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    Job job = applicationContext.getBean(Job.class);

    JobExecution jobExecution = jobLauncher.run(job, new JobParameters());

    assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
  }
}
