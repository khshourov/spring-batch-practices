package com.github.khshourov.batchpractices.tablepartition;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import com.github.khshourov.batchpractices.domain.trade.internal.CustomerCreditIncreaseProcessor;
import com.github.khshourov.batchpractices.domain.trade.internal.CustomerCreditRowMapper;
import com.github.khshourov.batchpractices.filepartition.OutputFileListener;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.WritableResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
@EnableBatchProcessing
@Import({DataSourceConfiguration.class, EmbeddedDataSourceConfiguration.class})
public class TablePartitionJob {
  @Bean
  public Job job(JobRepository jobRepository, Step partition) {
    return new JobBuilder("tablePartitionJob", jobRepository).start(partition).build();
  }

  @Bean
  public Step partition(
      JobRepository jobRepository, Partitioner partitioner, Step partitionedStep) {
    return new StepBuilder("partitionStep", jobRepository)
        .partitioner("tablePartitioner", partitioner)
        .step(partitionedStep)
        .taskExecutor(new SimpleAsyncTaskExecutor())
        .gridSize(5)
        .build();
  }

  @Bean
  public Partitioner partitioner(DataSource dataSource) {
    ColumnRangePartitioner partitioner = new ColumnRangePartitioner();
    partitioner.setDataSource(dataSource);
    return partitioner;
  }

  @Bean
  public Step partitionedStep(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      JdbcPagingItemReader<CustomerCredit> itemReader,
      CustomerCreditIncreaseProcessor itemProcessor,
      FlatFileItemWriter<CustomerCredit> itemWriter,
      StepExecutionListener itemListener) {
    return new StepBuilder("tablePartitionedStep", jobRepository)
        .<CustomerCredit, CustomerCredit>chunk(1, transactionManager)
        .reader(itemReader)
        .processor(itemProcessor)
        .writer(itemWriter)
        .listener(itemListener)
        .build();
  }

  @Bean
  @StepScope
  public JdbcPagingItemReader<CustomerCredit> itemReader(
      DataSource dataSource,
      @Value("#{stepExecutionContext['minId']}") Integer minId,
      @Value("#{stepExecutionContext['maxId']}") Integer maxId)
      throws Exception {
    JdbcPagingItemReader<CustomerCredit> itemReader = new JdbcPagingItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setRowMapper(new CustomerCreditRowMapper());

    SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
    queryProvider.setDataSource(dataSource);
    queryProvider.setSelectClause("id, name, credit");
    queryProvider.setFromClause("customer");
    queryProvider.setWhereClause("id >= :minId AND id <= :maxId");
    queryProvider.setSortKeys(Map.of("id", Order.ASCENDING));

    itemReader.setQueryProvider(queryProvider.getObject());
    itemReader.setParameterValues(Map.of("minId", minId, "maxId", maxId));
    return itemReader;
  }

  @Bean
  public CustomerCreditIncreaseProcessor itemProcessor() {
    return new CustomerCreditIncreaseProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<CustomerCredit> itemWriter(
      @Value("#{stepExecutionContext[outputFile]}") WritableResource resource) {
    BeanWrapperFieldExtractor<CustomerCredit> extractor = new BeanWrapperFieldExtractor<>();
    extractor.setNames(new String[] {"name", "credit"});

    DelimitedLineAggregator<CustomerCredit> lineAggregator = new DelimitedLineAggregator<>();
    lineAggregator.setFieldExtractor(extractor);

    FlatFileItemWriter<CustomerCredit> itemWriter = new FlatFileItemWriter<>();
    itemWriter.setResource(resource);
    itemWriter.setLineAggregator(lineAggregator);
    return itemWriter;
  }

  @Bean
  @StepScope
  public StepExecutionListener itemListener() {
    return new OutputFileListener();
  }
}
