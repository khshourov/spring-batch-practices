package com.github.khshourov.batchpractices.processindicator;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.trade.Trade;
import com.github.khshourov.batchpractices.domain.trade.internal.JdbcBatchStagingDao;
import com.github.khshourov.batchpractices.domain.trade.internal.JdbcTradeDao;
import com.github.khshourov.batchpractices.domain.trade.internal.TradeWriter;
import com.github.khshourov.batchpractices.domain.trade.internal.validator.TradeValidator;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

@Configuration
@EnableBatchProcessing
@Import({DataSourceConfiguration.class, EmbeddedDataSourceConfiguration.class})
public class ParallelJob {
  @Bean
  public Job job(JobRepository jobRepository, Step loadToStaging, Step parallelStagingToTrade) {
    return new JobBuilder("parallelJob", jobRepository)
        .start(loadToStaging)
        .next(parallelStagingToTrade)
        .build();
  }

  @Bean
  public Step loadToStaging(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      FlatFileItemReader<Trade> tradeItemReader,
      ValidatingItemProcessor<Trade> tradeItemProcessor,
      TradeWriter tradeItemStagingWriter,
      @Qualifier("batchStagingDao") StepExecutionListener listener) {
    return new StepBuilder("loadToStaging", jobRepository)
        .<Trade, Trade>chunk(1, transactionManager)
        .reader(tradeItemReader)
        .processor(tradeItemProcessor)
        .writer(tradeItemStagingWriter)
        .listener(listener)
        .build();
  }

  @Bean
  public Step parallelStagingToTrade(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      StagingItemReader<Trade> stagingItemReader,
      StagingItemProcessor<Trade> tradeStagingItemProcessor,
      @Qualifier("stagingItemReader") StepExecutionListener listener,
      TradeWriter tradeWriter) {
    return new StepBuilder("parallelStagingToTrade", jobRepository)
        .<ProcessIndicatorItemWrapper<Trade>, Trade>chunk(1, transactionManager)
        .reader(stagingItemReader)
        .processor(tradeStagingItemProcessor)
        .writer(tradeWriter)
        .listener(listener)
        .taskExecutor(new SimpleAsyncTaskExecutor())
        .build();
  }

  @Bean
  @StepScope
  public FlatFileItemReader<Trade> tradeItemReader(
      @Value("#{jobParameters['inputFile']}") Resource resource,
      DefaultLineMapper<Trade> tradeItemLineMapper) {
    return new FlatFileItemReaderBuilder<Trade>()
        .name("tradeItemReader")
        .resource(resource)
        .lineMapper(tradeItemLineMapper)
        .build();
  }

  @Bean
  public DefaultLineMapper<Trade> tradeItemLineMapper() {
    FixedLengthTokenizer lineTokenizer = new FixedLengthTokenizer();
    lineTokenizer.setColumns(
        new Range(1, 12), new Range(13, 15), new Range(16, 20), new Range(21, 29));

    DefaultLineMapper<Trade> lineMapper = new DefaultLineMapper<>();
    lineMapper.setLineTokenizer(lineTokenizer);
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
  public StagingItemProcessor<Trade> tradeStagingItemProcessor(DataSource dataSource) {
    StagingItemProcessor<Trade> itemProcessor = new StagingItemProcessor<>();
    itemProcessor.setDataSource(dataSource);
    return itemProcessor;
  }

  @Bean
  public TradeWriter tradeItemStagingWriter(JdbcBatchStagingDao batchStagingDao) {
    TradeWriter tradeWriter = new TradeWriter();
    tradeWriter.setTradeDao(batchStagingDao);
    return tradeWriter;
  }

  @Bean
  public JdbcBatchStagingDao batchStagingDao(
      DataSource dataSource, DataFieldMaxValueIncrementer batchStagingIncrementer) {
    JdbcBatchStagingDao stagingDao = new JdbcBatchStagingDao();
    stagingDao.setDataSource(dataSource);
    stagingDao.setIncrementer(batchStagingIncrementer);
    return stagingDao;
  }

  @Bean
  public ValidatingItemProcessor<Trade> tradeItemProcessor() {
    SpringValidator<Trade> validator = new SpringValidator<>();
    validator.setValidator(new TradeValidator());

    return new ValidatingItemProcessor<>(validator);
  }

  @Bean
  public StagingItemReader<Trade> stagingItemReader(DataSource dataSource) {
    StagingItemReader<Trade> itemReader = new StagingItemReader<>();
    itemReader.setDataSource(dataSource);
    return itemReader;
  }

  @Bean
  public TradeWriter tradeWriter(
      DataSource dataSource, DataFieldMaxValueIncrementer tradeIncrementer) {
    JdbcTradeDao tradeDao = new JdbcTradeDao();
    tradeDao.setDataSource(dataSource);
    tradeDao.setIncrementer(tradeIncrementer);

    TradeWriter tradeWriter = new TradeWriter();
    tradeWriter.setTradeDao(tradeDao);
    return tradeWriter;
  }
}
