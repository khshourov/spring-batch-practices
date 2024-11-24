package com.github.khshourov.batchpractices.loop;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.trade.Trade;
import com.github.khshourov.batchpractices.domain.trade.internal.GeneratingTradeItemReader;
import com.github.khshourov.batchpractices.domain.trade.internal.ItemTrackingTradeItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
@EnableBatchProcessing
@Import({DataSourceConfiguration.class, EmbeddedDataSourceConfiguration.class})
public class LoopJob {
  @Bean
  public Job job(JobRepository jobRepository, Step step1, Step step2, JobExecutionDecider decider) {
    return new JobBuilder("loopJob", jobRepository)
        .start(step1)
        .next(step2)
        .next(decider)
        .from(decider)
        .on("CONTINUE")
        .to(step2)
        .on("COMPLETED")
        .end()
        .build()
        .build();
  }

  @Bean
  public Step step1(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      GeneratingTradeItemReader itemReader,
      ItemTrackingTradeItemWriter itemWriter,
      GeneratingTradeResettingListener stepListener) {
    return new StepBuilder("step1", jobRepository)
        .<Trade, Trade>chunk(1, transactionManager)
        .reader(itemReader)
        .writer(itemWriter)
        .listener(stepListener)
        .build();
  }

  @Bean
  public Step step2(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      GeneratingTradeItemReader itemReader,
      ItemTrackingTradeItemWriter itemWriter,
      GeneratingTradeResettingListener stepListener) {
    return new StepBuilder("step2", jobRepository)
        .<Trade, Trade>chunk(1, transactionManager)
        .reader(itemReader)
        .writer(itemWriter)
        .listener(stepListener)
        .allowStartIfComplete(true)
        .build();
  }

  @Bean
  public GeneratingTradeItemReader itemReader() {
    GeneratingTradeItemReader itemReader = new GeneratingTradeItemReader();
    itemReader.setLimit(1);
    return itemReader;
  }

  @Bean
  public ItemTrackingTradeItemWriter itemWriter() {
    return new ItemTrackingTradeItemWriter();
  }

  @Bean
  public JobExecutionDecider decider() {
    LimitDecider decider = new LimitDecider();
    decider.setLimit(9);
    return decider;
  }

  @Bean
  public GeneratingTradeResettingListener stepListener(GeneratingTradeItemReader itemReader) {
    GeneratingTradeResettingListener listener = new GeneratingTradeResettingListener();
    listener.setItemReader(itemReader);
    return listener;
  }
}
