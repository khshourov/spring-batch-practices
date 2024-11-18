package com.github.khshourov.batchpractices.customfilter;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.trade.CompositeCustomerUpdateLineTokenizer;
import com.github.khshourov.batchpractices.domain.trade.internal.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

@Configuration
@EnableBatchProcessing
@Import({DataSourceConfiguration.class, EmbeddedDataSourceConfiguration.class})
public class CustomFilterJob {
  @Bean
  public Job job(JobRepository jobRepository, Step step) {
    return new JobBuilder(
            String.format(
                "customFilter-%s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))),
            jobRepository)
        .start(step)
        .build();
  }

  @Bean
  public Step step(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      FlatFileItemReader<CustomerUpdate> itemReader,
      CustomerUpdateProcessor itemProcessor,
      CustomerUpdateWriter itemWriter,
      CompositeCustomerUpdateLineTokenizer customerLineTokenizer) {
    return new StepBuilder("customFilterStep", jobRepository)
        .<CustomerUpdate, CustomerUpdate>chunk(1, transactionManager)
        .reader(itemReader)
        .processor(itemProcessor)
        .writer(itemWriter)
        .listener(customerLineTokenizer)
        .build();
  }

  @Bean
  public FlatFileItemReader<CustomerUpdate> itemReader(
      CompositeCustomerUpdateLineTokenizer customerLineTokenizer) {
    return new FlatFileItemReaderBuilder<CustomerUpdate>()
        .name("customerReader")
        .resource(
            new ClassPathResource(
                "com/github/khshourov/batchpractices/customfilter/data/customers.txt"))
        .lineTokenizer(customerLineTokenizer)
        .fieldSetMapper(new CustomerUpdateFieldSetMapper())
        .build();
  }

  @Bean
  public CompositeCustomerUpdateLineTokenizer customerLineTokenizer(
      FixedLengthTokenizer customerFixedLengthTokenizer,
      FixedLengthTokenizer footerFixedLengthTokenizer) {
    CompositeCustomerUpdateLineTokenizer tokenizer = new CompositeCustomerUpdateLineTokenizer();
    tokenizer.setCustomerTokenizer(customerFixedLengthTokenizer);
    tokenizer.setFooterTokenizer(footerFixedLengthTokenizer);
    return tokenizer;
  }

  @Bean
  public FixedLengthTokenizer customerFixedLengthTokenizer() {
    FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
    tokenizer.setColumns(new Range(1), new Range(2, 18), new Range(19, 26));
    return tokenizer;
  }

  @Bean
  public FixedLengthTokenizer footerFixedLengthTokenizer() {
    FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
    tokenizer.setColumns(new Range(1), new Range(2, 8));
    return tokenizer;
  }

  @Bean
  public CustomerUpdateProcessor itemProcessor(CustomerDao customerDao) {
    CustomerUpdateProcessor processor = new CustomerUpdateProcessor();
    processor.setCustomerDao(customerDao);
    processor.setInvalidCustomerLogger(new CommonsLoggingInvalidCustomerLogger());
    return processor;
  }

  @Bean
  public CustomerUpdateWriter itemWriter(CustomerDao customerDao) {
    CustomerUpdateWriter writer = new CustomerUpdateWriter();
    writer.setCustomerDao(customerDao);
    return writer;
  }

  @Bean
  public CustomerDao customerDao(
      DataSource dataSource, DataFieldMaxValueIncrementer customerIncrementer) {
    JdbcCustomerDao customerDao = new JdbcCustomerDao();
    customerDao.setDataSource(dataSource);
    customerDao.setIncrementer(customerIncrementer);
    return customerDao;
  }
}
