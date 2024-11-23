package com.github.khshourov.batchpractices.trade;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.trade.Trade;
import com.github.khshourov.batchpractices.domain.trade.internal.JdbcTradeDao;
import com.github.khshourov.batchpractices.domain.trade.internal.TradeWriter;
import com.github.khshourov.batchpractices.domain.trade.internal.validator.TradeValidator;
import javax.sql.DataSource;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
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
public class TradeJob {
  @Bean
  public Step tradeLoad(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      FlatFileItemReader<Trade> tradeItemReader,
      ValidatingItemProcessor<Trade> tradeItemProcessor,
      TradeWriter tradeItemWriter) {
    return new StepBuilder("tradeLoad", jobRepository)
        .<Trade, Trade>chunk(1, transactionManager)
        .reader(tradeItemReader)
        .processor(tradeItemProcessor)
        .writer(tradeItemWriter)
        .build();
  }

  @Bean
  @StepScope
  public FlatFileItemReader<Trade> tradeItemReader(
      @Value("#{jobParameters['tradeInputFile']}") Resource resource,
      DefaultLineMapper<Trade> tradeLineMapper) {
    return new FlatFileItemReaderBuilder<Trade>()
        .name("tradeItemReader")
        .resource(resource)
        .lineMapper(tradeLineMapper)
        .build();
  }

  @Bean
  public DefaultLineMapper<Trade> tradeLineMapper(DelimitedLineTokenizer tradeLineTokenizer) {
    DefaultLineMapper<Trade> lineMapper = new DefaultLineMapper<>();
    lineMapper.setLineTokenizer(tradeLineTokenizer);
    // We already have a line mapper for Trade; TradeFieldSetMapper. But it implements
    // FieldSetMapper<Object> which is causing compiler errors. After fixing the implement, we'll
    // remove this mapper.
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
  public DelimitedLineTokenizer tradeLineTokenizer() {
    DelimitedLineTokenizer lengthTokenizer = new DelimitedLineTokenizer();
    lengthTokenizer.setNames(new String[] {"ISIN", "Quantity", "Price", "Customer"});
    return lengthTokenizer;
  }

  @Bean
  public ValidatingItemProcessor<Trade> tradeItemProcessor(
      SpringValidator<Trade> tradeItemValidator) {
    ValidatingItemProcessor<Trade> itemProcessor = new ValidatingItemProcessor<>();
    itemProcessor.setValidator(tradeItemValidator);
    return itemProcessor;
  }

  @Bean
  public SpringValidator<Trade> tradeItemValidator() {
    SpringValidator<Trade> validator = new SpringValidator<>();
    validator.setValidator(new TradeValidator());
    return validator;
  }

  @Bean
  public TradeWriter tradeItemWriter(
      DataSource dataSource, DataFieldMaxValueIncrementer tradeIncrementer) {
    JdbcTradeDao tradeDao = new JdbcTradeDao();
    tradeDao.setDataSource(dataSource);
    tradeDao.setIncrementer(tradeIncrementer);

    TradeWriter tradeWriter = new TradeWriter();
    tradeWriter.setTradeDao(tradeDao);
    return tradeWriter;
  }
}
