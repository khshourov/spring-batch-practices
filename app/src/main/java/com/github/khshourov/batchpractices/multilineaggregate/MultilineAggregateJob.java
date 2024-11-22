package com.github.khshourov.batchpractices.multilineaggregate;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.trade.Trade;
import com.github.khshourov.batchpractices.domain.trade.internal.TradeFieldSetMapper;
import java.util.Map;
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
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer;
import org.springframework.batch.item.file.transform.Range;
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
public class MultilineAggregateJob {
  @Bean
  public Job job(JobRepository jobRepository, Step step) {
    return new JobBuilder("multiLineAggregateJob", jobRepository).start(step).build();
  }

  @Bean
  public Step step(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      AggregateItemReader<Trade> itemReader,
      FlatFileItemWriter itemWriter) {
    return new StepBuilder("multiLineAggregateJobStep", jobRepository)
        .chunk(1, transactionManager)
        .reader(itemReader)
        .writer(itemWriter)
        .build();
  }

  @Bean
  @StepScope
  public AggregateItemReader<Trade> itemReader(
      @Value("#{jobParameters['inputFile']}") Resource resource,
      LineTokenizer lineTokenizer,
      FieldSetMapper fieldSetMapper) {
    FlatFileItemReader<AggregateItem<Trade>> delegate =
        new FlatFileItemReaderBuilder<>()
            .name("itemReader")
            .resource(resource)
            .lineTokenizer(lineTokenizer)
            .fieldSetMapper(fieldSetMapper)
            .build();

    AggregateItemReader<Trade> reader = new AggregateItemReader<>();
    reader.setDelegate(delegate);
    return reader;
  }

  @Bean
  public LineTokenizer lineTokenizer(
      LineTokenizer beginTokenizer, LineTokenizer endTokenizer, LineTokenizer tradeTokenizer) {
    PatternMatchingCompositeLineTokenizer lineTokenizer =
        new PatternMatchingCompositeLineTokenizer();
    lineTokenizer.setTokenizers(
        Map.of(
            "BEGIN", beginTokenizer,
            "END", endTokenizer,
            "*", tradeTokenizer));
    return lineTokenizer;
  }

  @Bean
  public LineTokenizer beginTokenizer() {
    FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
    tokenizer.setColumns(new Range(1, 5));
    return tokenizer;
  }

  @Bean
  public LineTokenizer endTokenizer() {
    FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
    tokenizer.setColumns(new Range(1, 3));
    return tokenizer;
  }

  @Bean
  public LineTokenizer tradeTokenizer() {
    FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
    tokenizer.setNames("isin", "quantity", "price", "customer");
    tokenizer.setColumns(new Range(1, 12), new Range(13, 15), new Range(16, 20), new Range(21, 29));
    return tokenizer;
  }

  @Bean
  AggregateItemFieldSetMapper<AggregateItem<Trade>> fieldSetMapper() {
    AggregateItemFieldSetMapper<AggregateItem<Trade>> fieldSetMapper =
        new AggregateItemFieldSetMapper<>();
    fieldSetMapper.setDelegate(new TradeFieldSetMapper());
    return fieldSetMapper;
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<Trade> itemWriter(
      @Value("#{jobParameters['outputFile']}") WritableResource resource) {
    return new FlatFileItemWriterBuilder<Trade>()
        .name("itemWriter")
        .resource(resource)
        .lineAggregator(new PassThroughLineAggregator<>())
        .build();
  }
}
