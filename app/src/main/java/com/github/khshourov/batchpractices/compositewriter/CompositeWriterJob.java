package com.github.khshourov.batchpractices.compositewriter;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.trade.Trade;
import com.github.khshourov.batchpractices.domain.trade.TradeDao;
import com.github.khshourov.batchpractices.domain.trade.internal.JdbcTradeDao;
import com.github.khshourov.batchpractices.domain.trade.internal.TradeWriter;
import com.github.khshourov.batchpractices.domain.trade.internal.validator.TradeValidator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.*;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

@Configuration
@EnableBatchProcessing
@Import({DataSourceConfiguration.class, EmbeddedDataSourceConfiguration.class})
public class CompositeWriterJob {

  @Bean
  public Job job(JobRepository jobRepository, Step step) {
    return new JobBuilder(
            String.format(
                "compositeWriter-%s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))),
            jobRepository)
        .start(step)
        .build();
  }

  @Bean
  public Step step(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      FlatFileItemReader<Trade> itemReader,
      ValidatingItemProcessor<Trade> itemProcessor,
      CompositeItemWriter<Trade> itemWriter) {
    return new StepBuilder("compositeWriterStep", jobRepository)
        .<Trade, Trade>chunk(1, transactionManager)
        .reader(itemReader)
        .processor(itemProcessor)
        .writer(itemWriter)
        .build();
  }

  @Bean
  public FlatFileItemReader<Trade> itemReader(DefaultLineMapper<Trade> tradeLineMapper) {
    FlatFileItemReader<Trade> reader = new FlatFileItemReader<>();
    reader.setResource(
        new ClassPathResource(
            "com/github/khshourov/batchpractices/compositewriter/data/trade.txt"));
    reader.setLineMapper(tradeLineMapper);
    return reader;
  }

  @Bean
  public DefaultLineMapper<Trade> tradeLineMapper(
      LineTokenizer tradeTokenizer, FieldSetMapper<Trade> tradeFieldSetMapper) {
    DefaultLineMapper<Trade> lineMapper = new DefaultLineMapper<>();
    lineMapper.setLineTokenizer(tradeTokenizer);
    lineMapper.setFieldSetMapper(tradeFieldSetMapper);
    return lineMapper;
  }

  @Bean
  public LineTokenizer tradeTokenizer() {
    FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
    tokenizer.setNames("ISIN", "Quantity", "price", "CUSTOMER");
    tokenizer.setColumns(new Range(1, 12), new Range(13, 15), new Range(16, 20), new Range(21, 29));
    return tokenizer;
  }

  @Bean
  public FieldSetMapper<Trade> tradeFieldSetMapper() {
    BeanWrapperFieldSetMapper<Trade> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(Trade.class);
    return fieldSetMapper;
  }

  @Bean
  public ValidatingItemProcessor<Trade> itemProcessor(SpringValidator<Trade> tradeValidator) {
    ValidatingItemProcessor<Trade> processor = new ValidatingItemProcessor<>();
    processor.setValidator(tradeValidator);
    return processor;
  }

  @Bean
  public SpringValidator<Trade> tradeValidator() {
    SpringValidator<Trade> validator = new SpringValidator<>();
    validator.setValidator(new TradeValidator());
    return validator;
  }

  @Bean
  public CompositeItemWriter<Trade> itemWriter(
      TradeWriter itemWriter1,
      FlatFileItemWriter<Trade> itemWriter2,
      FlatFileItemWriter<Trade> itemWriter3) {
    return new CompositeItemWriterBuilder<Trade>()
        .delegates(Arrays.asList(itemWriter1, itemWriter2, itemWriter3))
        .build();
  }

  @Bean
  public TradeWriter tradeWriter1(TradeDao tradeDao) {
    TradeWriter tradeWriter = new TradeWriter();
    tradeWriter.setTradeDao(tradeDao);
    return tradeWriter;
  }

  @Bean
  public FlatFileItemWriter<Trade> itemWriter2() {
    return new FlatFileItemWriterBuilder<Trade>()
        .name("itemWriter2")
        .resource(new FileSystemResource("build/test-output/report1.txt"))
        .lineAggregator(new PassThroughLineAggregator<>())
        .build();
  }

  @Bean
  public FlatFileItemWriter<Trade> itemWriter3() {
    return new FlatFileItemWriterBuilder<Trade>()
        .name("itemWriter3")
        .resource(new FileSystemResource("build/test-output/report2.txt"))
        .lineAggregator(new PassThroughLineAggregator<>())
        .build();
  }

  @Bean
  public TradeDao tradeDao(DataSource dataSource, DataFieldMaxValueIncrementer tradeIncrementer) {
    JdbcTradeDao jdbcTradeDao = new JdbcTradeDao();
    jdbcTradeDao.setDataSource(dataSource);
    jdbcTradeDao.setIncrementer(tradeIncrementer);
    return jdbcTradeDao;
  }
}
