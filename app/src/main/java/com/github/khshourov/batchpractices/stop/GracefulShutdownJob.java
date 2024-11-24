package com.github.khshourov.batchpractices.stop;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.trade.Trade;
import com.github.khshourov.batchpractices.domain.trade.internal.GeneratingTradeItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.factory.FaultTolerantStepFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
@EnableBatchProcessing
@Import({DataSourceConfiguration.class, EmbeddedDataSourceConfiguration.class})
public class GracefulShutdownJob {
  @Bean
  public JobLauncher jobLauncher(JobRepository jobRepository) {
    TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
    jobLauncher.setJobRepository(jobRepository);
    jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
    return jobLauncher;
  }

  @Bean
  public Job job(JobRepository jobRepository, Step step) {
    SimpleJob simpleJob = new SimpleJob();
    simpleJob.setJobRepository(jobRepository);
    simpleJob.setRestartable(true);
    simpleJob.setJobParametersIncrementer(new RunIdIncrementer());
    simpleJob.addStep(step);
    return simpleJob;
  }

  @Bean
  public Step step(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      GeneratingTradeItemReader itemReader,
      DummyItemWriter itemWriter)
      throws Exception {
    FaultTolerantStepFactoryBean<Trade, Trade> stepBuilder = new FaultTolerantStepFactoryBean<>();
    stepBuilder.setJobRepository(jobRepository);
    stepBuilder.setTransactionManager(transactionManager);
    stepBuilder.setBeanName("infiniteStep");
    stepBuilder.setItemReader(itemReader);
    stepBuilder.setItemWriter(itemWriter);

    return stepBuilder.getObject();
  }

  @Bean
  public GeneratingTradeItemReader itemReader() {
    GeneratingTradeItemReader itemReader = new GeneratingTradeItemReader();
    itemReader.setLimit(100000);
    return itemReader;
  }

  @Bean
  public DummyItemWriter itemWriter() {
    return new DummyItemWriter();
  }
}
