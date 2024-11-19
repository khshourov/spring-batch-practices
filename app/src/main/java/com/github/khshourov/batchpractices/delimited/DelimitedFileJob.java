package com.github.khshourov.batchpractices.delimited;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import com.github.khshourov.batchpractices.domain.trade.internal.CustomerCreditIncreaseProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
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
public class DelimitedFileJob {
  @Bean
  public Job job(JobRepository jobRepository, Step step) {
    return new JobBuilder("delimitedFileJob", jobRepository).start(step).build();
  }

  @Bean
  public Step step(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      FlatFileItemReader<CustomerCredit> itemReader,
      FlatFileItemWriter<CustomerCredit> itemWriter) {
    return new StepBuilder("delimitedFileStep", jobRepository)
        .<CustomerCredit, CustomerCredit>chunk(1, transactionManager)
        .reader(itemReader)
        .processor(new CustomerCreditIncreaseProcessor())
        .writer(itemWriter)
        .build();
  }

  @Bean
  @StepScope
  public FlatFileItemReader<CustomerCredit> itemReader(
      @Value("#{jobParameters['inputFile']}") Resource resource) {
    return new FlatFileItemReaderBuilder<CustomerCredit>()
        .name("itemReader")
        .resource(resource)
        .delimited()
        .names("name", "credit")
        .targetType(CustomerCredit.class)
        .build();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<CustomerCredit> itemWriter(
      @Value("#{jobParameters['outputFile']}") WritableResource resource) {
    return new FlatFileItemWriterBuilder<CustomerCredit>()
        .name("itemWriter")
        .resource(resource)
        .delimited()
        .names("name", "credit")
        .build();
  }
}
