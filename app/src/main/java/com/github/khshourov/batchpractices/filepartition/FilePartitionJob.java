package com.github.khshourov.batchpractices.filepartition;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import com.github.khshourov.batchpractices.domain.trade.internal.CustomerCreditIncreaseProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
@EnableBatchProcessing
@Import({DataSourceConfiguration.class, EmbeddedDataSourceConfiguration.class})
public class FilePartitionJob {
  @Bean
  public Job job(JobRepository jobRepository, Step partitionStep) {
    return new JobBuilder("filePartitionJob", jobRepository).start(partitionStep).build();
  }

  @Bean
  public Step partitionStep(
      JobRepository jobRepository, MultiResourcePartitioner partitioner, Step partitionedStep) {
    return new StepBuilder("partitionStep", jobRepository)
        .partitioner("multiResourcePartitioner", partitioner)
        .step(partitionedStep)
        .gridSize(2)
        .taskExecutor(new SimpleAsyncTaskExecutor())
        .build();
  }

  @Bean
  @StepScope
  public MultiResourcePartitioner partitioner(
      @Value("#{jobParameters['inputFiles']}") Resource[] resources) {
    MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
    partitioner.setResources(resources);
    return partitioner;
  }

  @Bean
  public Step partitionedStep(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      FlatFileItemReader<CustomerCredit> itemReader,
      CustomerCreditIncreaseProcessor itemProcessor,
      FlatFileItemWriter<CustomerCredit> itemWriter,
      StepExecutionListener itemListener) {
    return new StepBuilder("partitionedStep", jobRepository)
        .<CustomerCredit, CustomerCredit>chunk(2, transactionManager)
        .listener(itemListener)
        .reader(itemReader)
        .processor(itemProcessor)
        .writer(itemWriter)
        .build();
  }

  @Bean
  @StepScope
  public FlatFileItemReader<CustomerCredit> itemReader(
      @Value("#{stepExecutionContext['fileName']}") Resource resource,
      LineMapper<CustomerCredit> itemLineMapper) {
    FlatFileItemReader<CustomerCredit> itemReader = new FlatFileItemReader<>();
    itemReader.setResource(resource);
    itemReader.setLineMapper(itemLineMapper);
    return itemReader;
  }

  @Bean
  public LineMapper<CustomerCredit> itemLineMapper() {
    DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
    lineTokenizer.setNames("name", "credit");

    BeanWrapperFieldSetMapper<CustomerCredit> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(CustomerCredit.class);

    DefaultLineMapper<CustomerCredit> lineMapper = new DefaultLineMapper<>();
    lineMapper.setLineTokenizer(lineTokenizer);
    lineMapper.setFieldSetMapper(fieldSetMapper);
    return lineMapper;
  }

  @Bean
  public CustomerCreditIncreaseProcessor itemProcessor() {
    return new CustomerCreditIncreaseProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<CustomerCredit> itemWriter(
      @Value("#{stepExecutionContext[outputFile]}")WritableResource resource) {
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
