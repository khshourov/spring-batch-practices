package com.github.khshourov.batchpractices.jdbc;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import com.github.khshourov.batchpractices.domain.trade.internal.CustomerCreditIncreaseProcessor;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
@EnableBatchProcessing
@Import({DataSourceConfiguration.class, EmbeddedDataSourceConfiguration.class})
public class JdbcReaderBatchWriterJob {

  @Bean
  public JdbcBatchItemWriter<CustomerCredit> itemWriter(DataSource dataSource) {
    String sql = "UPDATE CUSTOMER set credit = :credit WHERE id = :id";

    return new JdbcBatchItemWriterBuilder<CustomerCredit>()
        .dataSource(dataSource)
        .sql(sql)
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .assertUpdates(true)
        .build();
  }

  @Bean
  public Step step(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      ItemReader<CustomerCredit> itemReader,
      ItemWriter<CustomerCredit> itemWriter) {
    return new StepBuilder("step1", jobRepository)
        .<CustomerCredit, CustomerCredit>chunk(2, transactionManager)
        .reader(itemReader)
        .processor(new CustomerCreditIncreaseProcessor())
        .writer(itemWriter)
        .build();
  }

  @Bean
  public Job job(JobRepository jobRepository, Step step) {
    return new JobBuilder("ioSampleJob", jobRepository).start(step).build();
  }
}
