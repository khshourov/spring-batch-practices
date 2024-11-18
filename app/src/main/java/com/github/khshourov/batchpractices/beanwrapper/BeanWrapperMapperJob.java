package com.github.khshourov.batchpractices.beanwrapper;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.person.Person;
import com.github.khshourov.batchpractices.domain.person.internal.PersonWriter;
import com.github.khshourov.batchpractices.domain.trade.Trade;
import com.github.khshourov.batchpractices.domain.trade.TradeDao;
import com.github.khshourov.batchpractices.domain.trade.internal.JdbcTradeDao;
import com.github.khshourov.batchpractices.domain.trade.internal.TradeWriter;
import com.github.khshourov.batchpractices.domain.trade.internal.validator.TradeValidator;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

@Configuration
@EnableBatchProcessing
@Import({DataSourceConfiguration.class, EmbeddedDataSourceConfiguration.class})
public class BeanWrapperMapperJob {
  @Bean
  public Job job(JobRepository jobRepository, Step step1, Step step2) {
    return new JobBuilder("beanWrapperMapper", jobRepository).start(step1).next(step2).build();
  }

  @Bean
  public Step step1(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      FlatFileItemReader<Trade> tradeReader,
      ValidatingItemProcessor<Trade> tradeProcessor,
      TradeWriter tradeWriter) {
    return new StepBuilder("step1", jobRepository)
        .<Trade, Trade>chunk(1, transactionManager)
        .reader(tradeReader)
        .processor(tradeProcessor)
        .writer(tradeWriter)
        .build();
  }

  @Bean
  public Step step2(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      FlatFileItemReader<Person> personReader,
      PersonWriter personWriter) {
    return new StepBuilder("step2", jobRepository)
        .<Person, Person>chunk(1, transactionManager)
        .reader(personReader)
        .writer(personWriter)
        .build();
  }

  @Bean
  public FlatFileItemReader<Trade> tradeReader(DefaultLineMapper<Trade> tradeLineMapper) {
    FlatFileItemReader<Trade> reader = new FlatFileItemReader<>();
    reader.setResource(
        new ClassPathResource("com/github/khshourov/batchpractices/beanwrapper/data/trade.txt"));
    reader.setLineMapper(tradeLineMapper);
    return reader;
  }

  @Bean
  public FlatFileItemReader<Person> personReader(DefaultLineMapper<Person> personalLineWrapper) {
    FlatFileItemReader<Person> reader = new FlatFileItemReader<>();
    reader.setResource(
        new ClassPathResource("com/github/khshourov/batchpractices/beanwrapper/data/person.txt"));
    reader.setLineMapper(personalLineWrapper);
    return reader;
  }

  @Bean
  public DefaultLineMapper<Trade> tradeLineMapper(
      FixedLengthTokenizer tradeTokenizer, FieldSetMapper<Trade> tradeFieldSetMapper) {
    DefaultLineMapper<Trade> mapper = new DefaultLineMapper<>();
    mapper.setLineTokenizer(tradeTokenizer);
    mapper.setFieldSetMapper(tradeFieldSetMapper);
    return mapper;
  }

  @Bean
  public DefaultLineMapper<Person> personLineMapper(
      FixedLengthTokenizer personalTokenizer, FieldSetMapper<Person> personFieldSetMapper) {
    DefaultLineMapper<Person> mapper = new DefaultLineMapper<>();
    mapper.setLineTokenizer(personalTokenizer);
    mapper.setFieldSetMapper(personFieldSetMapper);
    return mapper;
  }

  @Bean
  FixedLengthTokenizer tradeTokenizer() {
    FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
    tokenizer.setNames("ISIN", "Quantity", "price", "CUSTOMER");
    tokenizer.setColumns(new Range(1, 12), new Range(13, 15), new Range(16, 20), new Range(21, 29));
    return tokenizer;
  }

  @Bean
  FixedLengthTokenizer personalTokenizer() {
    FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
    tokenizer.setNames(
        "Title",
        "FirstName",
        "LastName",
        "Age",
        "Address.AddrLine1",
        "children[0].name",
        "children[1].name");
    tokenizer.setColumns(
        new Range(1, 5),
        new Range(6, 20),
        new Range(21, 40),
        new Range(41, 45),
        new Range(46, 55),
        new Range(56, 65),
        new Range(66, 75));
    return tokenizer;
  }

  @Bean
  public FieldSetMapper<Trade> tradeFieldSetMapper() {
    BeanWrapperFieldSetMapper<Trade> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(Trade.class);
    return fieldSetMapper;
  }

  @Bean
  FieldSetMapper<Person> personFieldSetMapper() {
    BeanWrapperFieldSetMapper<Person> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(Person.class);
    return fieldSetMapper;
  }

  @Bean
  public ValidatingItemProcessor<Trade> tradeProcessor(SpringValidator<Trade> tradeValidator) {
    return new ValidatingItemProcessor<>(tradeValidator);
  }

  @Bean
  public SpringValidator<Trade> tradeValidator() {
    SpringValidator<Trade> validator = new SpringValidator<>();
    validator.setValidator(new TradeValidator());
    return validator;
  }

  @Bean
  public TradeWriter tradeWriter(TradeDao tradeDao) {
    TradeWriter tradeWriter = new TradeWriter();
    tradeWriter.setTradeDao(tradeDao);
    return tradeWriter;
  }

  @Bean
  public TradeDao tradeDao(DataSource dataSource, DataFieldMaxValueIncrementer tradeIncrementer) {
    JdbcTradeDao jdbcTradeDao = new JdbcTradeDao();
    jdbcTradeDao.setDataSource(dataSource);
    jdbcTradeDao.setIncrementer(tradeIncrementer);
    return jdbcTradeDao;
  }

  @Bean
  public PersonWriter personWriter() {
    return new PersonWriter();
  }
}
