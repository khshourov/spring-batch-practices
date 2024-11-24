package com.github.khshourov.batchpractices.headerfooter;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
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
public class HeaderFooterJob {
  @Bean
  public Job job(JobRepository jobRepository, Step step) {
    return new JobBuilder("headerFooterJob", jobRepository).start(step).build();
  }

  @Bean
  public Step step(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      FlatFileItemReader itemReader,
      FlatFileItemWriter<String[]> itemWriter,
      StepExecutionListener stepExecutionListener) {
    return new StepBuilder("headerFooterJobStep", jobRepository)
        .<Object, String[]>chunk(1, transactionManager)
        .listener(stepExecutionListener)
        .reader(itemReader)
        .writer(itemWriter)
        .build();
  }

  @Bean
  @StepScope
  public FlatFileItemReader<String[]> itemReader(
      @Value("#{jobParameters['inputFile']}") Resource resource,
      LineCallbackHandler callbackHandler) {
    return new FlatFileItemReaderBuilder<String[]>()
        .name("itemReader")
        .resource(resource)
        .lineTokenizer(new DelimitedLineTokenizer())
        .fieldSetMapper(FieldSet::getValues)
        .linesToSkip(1)
        .skippedLinesCallback(callbackHandler)
        .build();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<String[]> itemWriter(
      @Value("#{jobParameters['outputFile']}") WritableResource resource,
      FlatFileHeaderCallback headerCallback,
      FlatFileFooterCallback footerCallback) {
    return new FlatFileItemWriterBuilder<String[]>()
        .name("itemWriter")
        .resource(resource)
        .lineAggregator(new DelimitedLineAggregator<>())
        .headerCallback(headerCallback)
        .footerCallback(footerCallback)
        .build();
  }

  @Bean(name = {"callbackHandler", "headerCallback"})
  public HeaderCallback headerCallback() {
    return new HeaderCallback();
  }

  @Bean(name = {"stepExecutionListener", "footerCallback"})
  public FooterCallback footerCallback() {
    return new FooterCallback();
  }
}
