package com.github.khshourov.batchpractices.multirecord;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import com.github.khshourov.batchpractices.domain.trade.Trade;
import com.github.khshourov.batchpractices.domain.trade.internal.CustomerCreditFieldSetMapper;
import com.github.khshourov.batchpractices.domain.trade.internal.DelegatingTradingCustomerAggregator;
import com.github.khshourov.batchpractices.domain.trade.internal.TradeFieldSetMapper;
import java.util.Map;
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
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
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
public class MultiRecordJob {
  @Bean
  public Job job(JobRepository jobRepository, Step step) {
    return new JobBuilder("multiRecordJob", jobRepository).start(step).build();
  }

  @Bean
  public Step step(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      ItemReader<Object> itemReader,
      ItemWriter<Object> itemWriter) {
    return new StepBuilder("multiRecordJobStep", jobRepository)
        .chunk(1, transactionManager)
        .reader(itemReader)
        .writer(itemWriter)
        .build();
  }

  @Bean
  @StepScope
  public FlatFileItemReader<Object> itemReader(
      @Value("#{jobParameters['inputFile']}") Resource resource, LineMapper<Object> lineMapper) {
    return new FlatFileItemReaderBuilder<>()
        .name("itemReader")
        .resource(resource)
        .lineMapper(lineMapper)
        .build();
  }

  @Bean
  public PatternMatchingCompositeLineMapper<Object> lineMapper(
      FixedLengthTokenizer tradeTokenizer, FixedLengthTokenizer customerTokenizer) {
    PatternMatchingCompositeLineMapper<Object> lineMapper =
        new PatternMatchingCompositeLineMapper<>();
    lineMapper.setTokenizers(Map.of("TRAD*", tradeTokenizer, "CUST*", customerTokenizer));
    lineMapper.setFieldSetMappers(
        Map.of("TRAD*", new TradeFieldSetMapper(), "CUST*", new CustomerCreditFieldSetMapper()));
    return lineMapper;
  }

  @Bean
  public FixedLengthTokenizer tradeTokenizer() {
    FixedLengthTokenizer tradeTokenizer = new FixedLengthTokenizer();
    tradeTokenizer.setNames("is", "quantity", "price", "customer");
    tradeTokenizer.setColumns(
        new Range(5, 16), new Range(17, 19), new Range(20, 25), new Range(26, 34));
    return tradeTokenizer;
  }

  @Bean
  public FixedLengthTokenizer customerTokenizer() {
    FixedLengthTokenizer customerTokenizer = new FixedLengthTokenizer();
    customerTokenizer.setNames("id", "name", "credit");
    customerTokenizer.setColumns(new Range(5, 9), new Range(10, 18), new Range(19, 26));
    return customerTokenizer;
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<Object> itemWriter(
      @Value("#{jobParameters['outputFile']}") WritableResource resource,
      DelegatingTradingCustomerAggregator lineAggregator) {
    return new FlatFileItemWriterBuilder<>()
        .name("itemWriter")
        .resource(resource)
        .lineAggregator(lineAggregator)
        .build();
  }

  @Bean
  DelegatingTradingCustomerAggregator lineAggregator(
      FormatterLineAggregator<Trade> tradeFormatterLineAggregator,
      FormatterLineAggregator<CustomerCredit> customerCreditFormatterLineAggregator) {
    DelegatingTradingCustomerAggregator delegatingTradingCustomerAggregator =
        new DelegatingTradingCustomerAggregator();
    delegatingTradingCustomerAggregator.setTradeFormatterLineAggregator(
        tradeFormatterLineAggregator);
    delegatingTradingCustomerAggregator.setCustomerCreditFormatterLineAggregator(
        customerCreditFormatterLineAggregator);
    return delegatingTradingCustomerAggregator;
  }

  @Bean
  public FormatterLineAggregator<Trade> tradeFormatterLineAggregator() {
    BeanWrapperFieldExtractor<Trade> extractor = new BeanWrapperFieldExtractor<>();
    extractor.setNames(new String[] {"isin", "quantity", "price", "customer"});

    FormatterLineAggregator<Trade> lineAggregator = new FormatterLineAggregator<>();
    lineAggregator.setFieldExtractor(extractor);
    lineAggregator.setFormat("TRAD%-12s%-3d%6s%-9s");
    return lineAggregator;
  }

  @Bean
  public FormatterLineAggregator<CustomerCredit> customerCreditFormatterLineAggregator() {
    BeanWrapperFieldExtractor<CustomerCredit> extractor = new BeanWrapperFieldExtractor<>();
    extractor.setNames(new String[] {"id", "name", "credit"});

    FormatterLineAggregator<CustomerCredit> lineAggregator = new FormatterLineAggregator<>();
    lineAggregator.setFieldExtractor(extractor);
    lineAggregator.setFormat("CUST%05d%-9s%08.0f");
    return lineAggregator;
  }
}
