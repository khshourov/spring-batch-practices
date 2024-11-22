package com.github.khshourov.batchpractices.patternmatching;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.patternmatching.fieldsetmappers.AddressFieldSetMapper;
import com.github.khshourov.batchpractices.patternmatching.fieldsetmappers.BillingInfoFieldSetMapper;
import com.github.khshourov.batchpractices.patternmatching.fieldsetmappers.CustomerFieldSetMapper;
import com.github.khshourov.batchpractices.patternmatching.fieldsetmappers.LineItemFieldSetMapper;
import com.github.khshourov.batchpractices.patternmatching.fieldsetmappers.OrderFieldSetMapper;
import com.github.khshourov.batchpractices.patternmatching.fieldsetmappers.ShippingInfoFieldSetMapper;
import com.github.khshourov.batchpractices.patternmatching.models.LineItem;
import com.github.khshourov.batchpractices.patternmatching.models.Order;
import com.github.khshourov.batchpractices.patternmatching.validators.OrderValidator;
import java.util.Map;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
@EnableBatchProcessing
@Import({
  DataSourceConfiguration.class,
  EmbeddedDataSourceConfiguration.class,
  OrderTokenizer.class,
  OrderLineAggregatorConfiguration.class
})
public class PatternMatchingJob {
  @Bean
  public Job job(JobRepository jobRepository, Step step) {
    return new JobBuilder("patternMatchingJob", jobRepository).start(step).build();
  }

  @Bean
  public Step step(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      OrderItemReader itemReader,
      ItemProcessor<Order, Order> itemProcessor,
      FlatFileItemWriter<Order> itemWriter) {
    return new StepBuilder("patternMatchingJobStep", jobRepository)
        .<Order, Order>chunk(1, transactionManager)
        .reader(itemReader)
        .processor(itemProcessor)
        .writer(itemWriter)
        .build();
  }

  @Bean
  @StepScope
  public OrderItemReader itemReader(
      @Value("#{jobParameters['inputFile']}") Resource resource,
      PatternMatchingCompositeLineTokenizer orderTokenizer) {
    OrderItemReader itemReader = new OrderItemReader();
    itemReader.setDelegate(
        new FlatFileItemReaderBuilder<FieldSet>()
            .name("delegateItemReader")
            .resource(resource)
            .lineTokenizer(orderTokenizer)
            .fieldSetMapper(new PassThroughFieldSetMapper())
            .build());

    itemReader.setAddressFieldSetMapper(new AddressFieldSetMapper());
    itemReader.setBillingInfoFieldSetMapper(new BillingInfoFieldSetMapper());
    itemReader.setCustomerFieldSetMapper(new CustomerFieldSetMapper());
    itemReader.setLineItemFieldSetMapper(new LineItemFieldSetMapper());
    itemReader.setShippingInfoFieldSetMapper(new ShippingInfoFieldSetMapper());
    itemReader.setOrderFieldSetMapper(new OrderFieldSetMapper());

    return itemReader;
  }

  @Bean
  public ValidatingItemProcessor<Order> itemProcessor(SpringValidator<Order> orderValidator) {
    ValidatingItemProcessor<Order> itemProcessor = new ValidatingItemProcessor<>();
    itemProcessor.setValidator(orderValidator);
    return itemProcessor;
  }

  @Bean
  public SpringValidator<Order> orderValidator() {
    SpringValidator<Order> validator = new SpringValidator<>();
    validator.setValidator(new OrderValidator());
    return validator;
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<Order> itemWriter(
      @Value("#{jobParameters['outputFile']}") WritableResource resource,
      Map<String, FormatterLineAggregator<Order>> orderAggregators,
      FormatterLineAggregator<LineItem> lineAggregator) {
    OrderLineAggregator orderLineAggregator = new OrderLineAggregator();
    orderLineAggregator.setAggregators(orderAggregators);
    orderLineAggregator.setLineItemAggregator(lineAggregator);

    return new FlatFileItemWriterBuilder<Order>()
        .name("itemWriter")
        .resource(resource)
        .lineAggregator(orderLineAggregator)
        .build();
  }
}
