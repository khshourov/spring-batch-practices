package com.github.khshourov.batchpractices.retry;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.trade.Trade;
import com.github.khshourov.batchpractices.domain.trade.internal.GeneratingTradeItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
@EnableBatchProcessing
@Import({DataSourceConfiguration.class, EmbeddedDataSourceConfiguration.class})
public class RetryJob {
  @Bean
  public Job job(JobRepository jobRepository, Step step) {
    return new JobBuilder("retryJob", jobRepository).start(step).build();
  }

  @Bean
  public Step step(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      GeneratingTradeItemReader itemReader,
      RetryItemWriter<Object> itemWriter) {
    return new StepBuilder("retryJobStep", jobRepository)
        .<Trade, Object>chunk(1, transactionManager)
        .reader(itemReader)
        .writer(itemWriter)
        .faultTolerant()
        .retry(Exception.class)
        .retryLimit(3)
        .build();
  }

  @Bean
  public GeneratingTradeItemReader itemReader() {
    GeneratingTradeItemReader itemReader = new GeneratingTradeItemReader();
    itemReader.setLimit(10);
    return itemReader;
  }

  @Bean
  public RetryItemWriter<Object> itemWriter() {
    return new RetryItemWriter<>();
  }
}
