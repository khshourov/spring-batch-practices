package com.github.khshourov.batchpractices.multiline;

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
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
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
public class MultilineJob {
  @Bean
  public Job job(JobRepository jobRepository, Step step) {
    return new JobBuilder("multiLineJob", jobRepository).start(step).build();
  }

  @Bean
  public Step step(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      MultiLineTradeItemReader itemReader,
      MultiLineTradeItemWriter itemWriter) {
    return new StepBuilder("multiLineJobStep", jobRepository)
        .<Trade, Trade>chunk(1, transactionManager)
        .reader(itemReader)
        .writer(itemWriter)
        .build();
  }

  @Bean
  @StepScope
  public MultiLineTradeItemReader itemReader(
      @Value("#{jobParameters['inputFile']}") Resource resource) {
    MultiLineTradeItemReader itemReader = new MultiLineTradeItemReader();
    itemReader.setDelegate(
        new FlatFileItemReaderBuilder<FieldSet>()
            .name("delegateItemReader")
            .resource(resource)
            .lineTokenizer(new DelimitedLineTokenizer())
            .fieldSetMapper(new PassThroughFieldSetMapper())
            .build());
    return itemReader;
  }

  @Bean
  @StepScope
  public MultiLineTradeItemWriter itemWriter(
      @Value("#{jobParameters['outputFile']}") WritableResource resource) {
    MultiLineTradeItemWriter itemWriter = new MultiLineTradeItemWriter();
    itemWriter.setDelegate(
        new FlatFileItemWriterBuilder<String>()
            .name("delegateItemWriter")
            .resource(resource)
            .lineAggregator(new PassThroughLineAggregator<>())
            .build());
    return itemWriter;
  }
}
