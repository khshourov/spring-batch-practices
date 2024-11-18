package com.github.khshourov.batchpractices.adapter.readerwriter;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.common.EmbeddedDataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.person.Person;
import com.github.khshourov.batchpractices.domain.person.PersonService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.adapter.PropertyExtractingDelegatingItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
@EnableBatchProcessing
@Import({DataSourceConfiguration.class, EmbeddedDataSourceConfiguration.class})
public class ReaderWriterJob {
  @Bean
  public Job job(JobRepository jobRepository, Step step) {
    return new JobBuilder("readerWriter", jobRepository).start(step).build();
  }

  @Bean
  public Step step(
      JobRepository jobRepository,
      JdbcTransactionManager transactionManager,
      ItemReaderAdapter<Person> itemReader,
      PropertyExtractingDelegatingItemWriter<Person> itemWriter) {
    return new StepBuilder("readerWriterStep", jobRepository)
        .<Person, Person>chunk(1, transactionManager)
        .reader(itemReader)
        .writer(itemWriter)
        .build();
  }

  @Bean
  public ItemReaderAdapter<Person> itemReader(PersonService personService) {
    ItemReaderAdapter<Person> adapter = new ItemReaderAdapter<>();
    adapter.setTargetObject(personService);
    adapter.setTargetMethod("getData");
    return adapter;
  }

  @Bean
  public PropertyExtractingDelegatingItemWriter<Person> itemWriter(PersonService personService) {
    PropertyExtractingDelegatingItemWriter<Person> writer =
        new PropertyExtractingDelegatingItemWriter<Person>();
    writer.setTargetObject(personService);
    writer.setTargetMethod("processPerson");
    writer.setFieldsUsedAsTargetMethodArguments(new String[] {"firstName", "address.city"});
    return writer;
  }

  @Bean
  public PersonService personService() {
    return new PersonService();
  }
}
