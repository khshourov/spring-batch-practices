package com.github.khshourov.batchpractices.compositereader;

import java.util.Arrays;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
@EnableBatchProcessing
public class CompositeReaderJob {
  @Bean
  public Job job(JobRepository jobRepository, Step step) {
    return new JobBuilder("compositeReader", jobRepository).start(step).build();
  }

  @Bean
  public Step step(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      CompositeItemReader<PersonRecord> itemReader,
      JdbcBatchItemWriter<PersonRecord> itemWriter) {
    return new StepBuilder("step", jobRepository)
        .<PersonRecord, PersonRecord>chunk(5, transactionManager)
        .reader(itemReader)
        .writer(itemWriter)
        .build();
  }

  @Bean
  public CompositeItemReader<PersonRecord> itemReader() {
    return new CompositeItemReader<>(Arrays.asList(itemReader1(), itemReader2(), itemReader3()));
  }

  @Bean
  public FlatFileItemReader<PersonRecord> itemReader1() {
    return new FlatFileItemReaderBuilder<PersonRecord>()
        .name("itemReader1")
        .resource(
            new ClassPathResource(
                "com/github/khshourov/batchpractices/compositereader/data/person1.csv"))
        .delimited()
        .names("id", "name")
        .targetType(PersonRecord.class)
        .build();
  }

  @Bean
  public FlatFileItemReader<PersonRecord> itemReader2() {
    return new FlatFileItemReaderBuilder<PersonRecord>()
        .name("itemReader2")
        .resource(
            new ClassPathResource(
                "com/github/khshourov/batchpractices/compositereader/data/person2.csv"))
        .delimited()
        .names("id", "name")
        .targetType(PersonRecord.class)
        .build();
  }

  @Bean
  public JdbcCursorItemReader<PersonRecord> itemReader3() {
    String sql = "SELECT * FROM person_source";
    return new JdbcCursorItemReaderBuilder<PersonRecord>()
        .name("itemReader3")
        .dataSource(dataSource())
        .sql(sql)
        .rowMapper(new DataClassRowMapper<>(PersonRecord.class))
        .build();
  }

  @Bean
  public JdbcBatchItemWriter<PersonRecord> itemWriter(DataSource dataSource) {
    String sql = "INSERT INTO person_target (id, name) VALUES (:id, :name)";

    return new JdbcBatchItemWriterBuilder<PersonRecord>()
        .dataSource(dataSource)
        .sql(sql)
        .beanMapped()
        .build();
  }

  @Bean
  public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.HSQL)
        .addScript("/org/springframework/batch/core/schema-drop-hsqldb.sql")
        .addScript("/org/springframework/batch/core/schema-hsqldb.sql")
        .addScript("/com/github/khshourov/batchpractices/compositereader/sql/schema.sql")
        .addScript("/com/github/khshourov/batchpractices/compositereader/sql/data.sql")
        .build();
  }

  @Bean
  public JdbcTransactionManager transactionManager(DataSource dataSource) {
    return new JdbcTransactionManager(dataSource);
  }
}
