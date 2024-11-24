package com.github.khshourov.batchpractices.restart;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.trade.Trade;
import com.github.khshourov.batchpractices.domain.trade.internal.JdbcTradeDao;
import com.github.khshourov.batchpractices.domain.trade.internal.TradeWriter;
import com.github.khshourov.batchpractices.domain.trade.internal.validator.TradeValidator;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

@Configuration
@EnableBatchProcessing
@Import({DataSourceConfiguration.class, EmbeddedDataSourceConfiguration.class})
public class RestartJob {
  @Bean
  public Job job(JobRepository jobRepository, Step step) {
    return new JobBuilder("restartJob", jobRepository).start(step).build();
  }

  @Bean
  public Step step(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      ExceptionThrowingItemReaderProxy<Trade> itemReader,
      ValidatingItemProcessor<Trade> itemProcessor,
      TradeWriter itemWriter) {
    return new StepBuilder("restartJobStep", jobRepository)
        .<Trade, Trade>chunk(1, transactionManager)
        .reader(itemReader)
        .processor(itemProcessor)
        .writer(itemWriter)
        .build();
  }

  @Bean
  @StepScope
  public ExceptionThrowingItemReaderProxy<Trade> itemReader(
      @Value("#{jobParameters['inputFile']}") Resource resource,
      DefaultLineMapper<Trade> tradeItemMapper) {
    FlatFileItemReader<Trade> delegateItemReader =
        new FlatFileItemReaderBuilder<Trade>()
            .name("delegateItemReader")
            .resource(resource)
            .lineMapper(tradeItemMapper)
            .build();

    ExceptionThrowingItemReaderProxy<Trade> itemReaderProxy =
        new ExceptionThrowingItemReaderProxy<>();
    itemReaderProxy.setDelegate(delegateItemReader);
    return itemReaderProxy;
  }

  @Bean
  public DefaultLineMapper<Trade> tradeItemMapper() {
    FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
    tokenizer.setColumns(new Range(1, 12), new Range(13, 15), new Range(16, 20), new Range(21, 29));

    DefaultLineMapper<Trade> lineMapper = new DefaultLineMapper<>();
    lineMapper.setLineTokenizer(tokenizer);
    lineMapper.setFieldSetMapper(
        (FieldSet fieldSet) -> {
          Trade trade = new Trade();
          trade.setIsin(fieldSet.readString(0));
          trade.setQuantity(fieldSet.readInt(1));
          trade.setPrice(fieldSet.readBigDecimal(2));
          trade.setCustomer(fieldSet.readString(3));
          return trade;
        });
    return lineMapper;
  }

  @Bean
  public ValidatingItemProcessor<Trade> itemProcessor(SpringValidator<Trade> itemValidator) {
    ValidatingItemProcessor<Trade> processor = new ValidatingItemProcessor<>();
    processor.setValidator(itemValidator);
    return processor;
  }

  @Bean
  public SpringValidator<Trade> itemValidator() {
    SpringValidator<Trade> validator = new SpringValidator<>();
    validator.setValidator(new TradeValidator());
    return validator;
  }

  @Bean
  public TradeWriter itemWriter(
      DataSource dataSource, DataFieldMaxValueIncrementer tradeIncrementer) {
    JdbcTradeDao tradeDao = new JdbcTradeDao();
    tradeDao.setDataSource(dataSource);
    tradeDao.setIncrementer(tradeIncrementer);

    TradeWriter writer = new TradeWriter();
    writer.setTradeDao(tradeDao);
    return writer;
  }
}
