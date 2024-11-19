package com.github.khshourov.batchpractices.xml;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import com.github.khshourov.batchpractices.domain.trade.internal.CustomerCreditIncreaseProcessor;
import com.thoughtworks.xstream.security.ExplicitTypePermission;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.oxm.xstream.XStreamMarshaller;

@Configuration
@EnableBatchProcessing
@Import({DataSourceConfiguration.class, EmbeddedDataSourceConfiguration.class})
public class XmlJob {
  @Bean
  public Job job(JobRepository jobRepository, Step step) {
    return new JobBuilder("xmlJob", jobRepository).start(step).build();
  }

  @Bean
  public Step step(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      ItemReader<CustomerCredit> itemReader,
      ItemWriter<CustomerCredit> itemWriter) {
    return new StepBuilder("xmlJobStep", jobRepository)
        .<CustomerCredit, CustomerCredit>chunk(1, transactionManager)
        .reader(itemReader)
        .processor(new CustomerCreditIncreaseProcessor())
        .writer(itemWriter)
        .build();
  }

  @Bean
  @StepScope
  StaxEventItemReader<CustomerCredit> itemReader(
      @Value("#{jobParameters['inputFile']}") Resource resource) {
    return new StaxEventItemReaderBuilder<CustomerCredit>()
        .name("itemReader")
        .resource(resource)
        .addFragmentRootElements("customer")
        .unmarshaller(customerCreditMarshaller())
        .build();
  }

  @Bean
  @StepScope
  StaxEventItemWriter<CustomerCredit> itemWriter(
      @Value("#{jobParameters['outputFile']}") WritableResource resource) {
    return new StaxEventItemWriterBuilder<CustomerCredit>()
        .name("itemWriter")
        .resource(resource)
        .marshaller(customerCreditMarshaller())
        .rootTagName("customers")
        .overwriteOutput(true)
        .build();
  }

  @Bean
  public XStreamMarshaller customerCreditMarshaller() {
    XStreamMarshaller marshaller = new XStreamMarshaller();
    marshaller.setAliases(
        Map.of("customer", CustomerCredit.class, "credit", BigDecimal.class, "name", String.class));
    marshaller.setTypePermissions(new ExplicitTypePermission(new Class[] {CustomerCredit.class}));
    return marshaller;
  }
}
