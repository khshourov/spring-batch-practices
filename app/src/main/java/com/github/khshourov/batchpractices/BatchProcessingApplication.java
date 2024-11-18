package com.github.khshourov.batchpractices;

import com.github.khshourov.batchpractices.helloworld.HelloWorldJobConfiguration;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BatchProcessingApplication {
  public static void main(String[] args)
      throws JobInstanceAlreadyCompleteException,
          JobExecutionAlreadyRunningException,
          JobParametersInvalidException,
          JobRestartException {
    ApplicationContext context = SpringApplication.run(HelloWorldJobConfiguration.class, args);
    Job job = context.getBean(Job.class);
    JobLauncher jobLauncher = context.getBean(JobLauncher.class);
    JobParameters parameters = new JobParametersBuilder().toJobParameters();

    jobLauncher.run(job, parameters);
  }
}
