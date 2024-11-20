package com.github.khshourov.batchpractices.multiresource;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
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
public class MultiResourceJob {
  @Bean
  public Job job(JobRepository jobRepository, Step step) {
    return new JobBuilder("multiResourceJob", jobRepository).start(step).build();
  }

  @Bean
  public Step step(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      ItemReader<CustomerCredit> itemReader,
      ItemWriter<CustomerCredit> itemWriter) {
    return new StepBuilder("multiResourceJobStep", jobRepository)
        .<CustomerCredit, CustomerCredit>chunk(1, transactionManager)
        .reader(itemReader)
        .writer(itemWriter)
        .build();
  }

  @Bean
  @StepScope
  public MultiResourceItemReader<CustomerCredit> itemReader(
      @Value("#{jobParameters['inputFiles']}") Resource[] resources,
      FlatFileItemReader<CustomerCredit> delegateReader) {
    return new MultiResourceItemReaderBuilder<CustomerCredit>()
        .name("itemReader")
        .resources(resources)
        .delegate(delegateReader)
        .build();
  }

  @Bean
  public FlatFileItemReader<CustomerCredit> delegateReader() {
    return new FlatFileItemReaderBuilder<CustomerCredit>()
        .name("delegateReader")
        .delimited()
        .names("name", "credit")
        .targetType(CustomerCredit.class)
        .build();
  }

  @Bean
  @StepScope
  public MultiResourceItemWriter<CustomerCredit> itemWriter(
      @Value("#{jobParameters['outputFiles']}") WritableResource resource,
      FlatFileItemWriter<CustomerCredit> delegateWriter) {
    return new MultiResourceItemWriterBuilder<CustomerCredit>()
        .name("itemWriter")
        .resource(resource)
        .delegate(delegateWriter)
        .itemCountLimitPerResource(6)
        .resourceSuffixCreator((index -> String.format(".%d.csv", index)))
        .build();
  }

  @Bean
  public FlatFileItemWriter<CustomerCredit> delegateWriter() {
    return new FlatFileItemWriterBuilder<CustomerCredit>()
        .name("delegateWriter")
        .delimited()
        .names("name", "credit")
        .build();
  }
}
