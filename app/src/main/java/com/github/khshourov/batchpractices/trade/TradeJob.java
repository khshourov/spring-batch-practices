package com.github.khshourov.batchpractices.trade;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import com.github.khshourov.batchpractices.domain.trade.Trade;
import com.github.khshourov.batchpractices.domain.trade.internal.CustomerCreditRowMapper;
import com.github.khshourov.batchpractices.domain.trade.internal.CustomerCreditUpdateWriter;
import com.github.khshourov.batchpractices.domain.trade.internal.CustomerDebitUpdateWriter;
import com.github.khshourov.batchpractices.domain.trade.internal.FlatFileCustomerCreditDao;
import com.github.khshourov.batchpractices.domain.trade.internal.JdbcCustomerDebitDao;
import com.github.khshourov.batchpractices.domain.trade.internal.JdbcTradeDao;
import com.github.khshourov.batchpractices.domain.trade.internal.TradeRowMapper;
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
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
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

  @Bean
  public Step tradeLoadFromDb(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      JdbcCursorItemReader<Trade> tradeJdbcCursorItemReader,
      CustomerDebitUpdateWriter customerUpdateWriter) {
    return new StepBuilder("tradeLoadFromDb", jobRepository)
        .<Trade, Trade>chunk(1, transactionManager)
        .reader(tradeJdbcCursorItemReader)
        .writer(customerUpdateWriter)
        .build();
  }

  @Bean
  public JdbcCursorItemReader<Trade> tradeJdbcCursorItemReader(DataSource dataSource) {
    String sql = "SELECT isin, quantity, price, customer, id, version from TRADE";

    return new JdbcCursorItemReaderBuilder<Trade>()
        .name("tradeJdbcCursorItemReader")
        .dataSource(dataSource)
        .sql(sql)
        .rowMapper(new TradeRowMapper())
        .build();
  }

  @Bean
  public CustomerDebitUpdateWriter customerUpdateWriter(DataSource dataSource) {
    JdbcCustomerDebitDao customerDebitDao = new JdbcCustomerDebitDao();
    customerDebitDao.setDataSource(dataSource);

    CustomerDebitUpdateWriter writer = new CustomerDebitUpdateWriter();
    writer.setCustomerDebitDao(customerDebitDao);
    return writer;
  }

  @Bean
  public Step customerCreditWrite(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      JdbcCursorItemReader<CustomerCredit> customerJdbcCursorItemReader,
      CustomerCreditUpdateWriter customerCreditUpdateWriter) {
    return new StepBuilder("customerCreditWrite", jobRepository)
        .<CustomerCredit, CustomerCredit>chunk(1, transactionManager)
        .reader(customerJdbcCursorItemReader)
        .writer(customerCreditUpdateWriter)
        .build();
  }

  @Bean
  public JdbcCursorItemReader<CustomerCredit> customerJdbcCursorItemReader(DataSource dataSource) {
    String sql = "SELECT id, name, credit FROM CUSTOMER";

    return new JdbcCursorItemReaderBuilder<CustomerCredit>()
        .name("customerJdbcCursorItemReader")
        .dataSource(dataSource)
        .sql(sql)
        .rowMapper(new CustomerCreditRowMapper())
        .build();
  }

  @Bean
  @StepScope
  public CustomerCreditUpdateWriter customerCreditUpdateWriter(
      @Value("#{jobParameters['customerCreditOutputFile']}") WritableResource resource) {
    FlatFileItemWriter<String> itemWriter =
        new FlatFileItemWriterBuilder<String>()
            .name("customerCreditUpdateWriter")
            .resource(resource)
            .lineAggregator(new PassThroughLineAggregator<>())
            .build();

    FlatFileCustomerCreditDao customerCreditDao = new FlatFileCustomerCreditDao();
    customerCreditDao.setItemWriter(itemWriter);

    CustomerCreditUpdateWriter updateWriter = new CustomerCreditUpdateWriter();
    updateWriter.setCustomerCreditDao(customerCreditDao);
    return updateWriter;
  }

  @Bean
  public Job job(
      JobRepository jobRepository, Step tradeLoad, Step tradeLoadFromDb, Step customerCreditWrite) {
    return new JobBuilder("tradeJob", jobRepository)
        .start(tradeLoad)
        .next(tradeLoadFromDb)
        .next(customerCreditWrite)
        .build();
  }
}
