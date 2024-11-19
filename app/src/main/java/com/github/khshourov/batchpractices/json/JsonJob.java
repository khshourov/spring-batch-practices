package com.github.khshourov.batchpractices.json;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.trade.Trade;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.GsonJsonObjectReader;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
@EnableBatchProcessing
@Import({DataSourceConfiguration.class, EmbeddedDataSourceConfiguration.class})
public class JsonJob {
  @Bean
  public Job job(JobRepository jobRepository, Step step) {
    return new JobBuilder("jsonJob", jobRepository).start(step).build();
  }

  @Bean
  public Step step(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      ItemReader<Trade> itemReader,
      ItemWriter<Trade> itemWriter) {
    return new StepBuilder("jsonJobStep", jobRepository)
        .<Trade, Trade>chunk(1, transactionManager)
        .reader(itemReader)
        .writer(itemWriter)
        .build();
  }

  @Bean
  @StepScope
  public JsonItemReader<Trade> itemReader(
      @Value("#{jobParameters['inputFile']}") Resource resource) {
    return new JsonItemReaderBuilder<Trade>()
        .name("itemReader")
        .resource(resource)
        .jsonObjectReader(new GsonJsonObjectReader<>(Trade.class))
        .build();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<Trade> itemWriter(
      @Value("#{jobParameters['outputFile']}") WritableResource resource) {
    return new JsonFileItemWriterBuilder<Trade>()
        .name("itemWriter")
        .resource(resource)
        .lineSeparator("\n")
        .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
        .shouldDeleteIfExists(true)
        .build();
  }
}
