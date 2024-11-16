package com.github.khshourov.batchpractices.jpa;

import com.github.khshourov.batchpractices.common.DataSourceConfiguration;
import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import com.github.khshourov.batchpractices.domain.trade.internal.CustomerCreditIncreaseProcessor;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
@Import(DataSourceConfiguration.class)
@EnableBatchProcessing(
    isolationLevelForCreate = "ISOLATION_DEFAULT",
    transactionManagerRef = "jpaTransactionManager")
public class JpaJob {
  @Bean
  public JpaPagingItemReader<CustomerCredit> itemReader(EntityManagerFactory entityManagerFactory) {
    return new JpaPagingItemReaderBuilder<CustomerCredit>()
        .name("itemReader")
        .entityManagerFactory(entityManagerFactory)
        .queryString("SELECT c from CustomerCredit c")
        .build();
  }

  @Bean
  public JpaItemWriter<CustomerCredit> itemWriter(EntityManagerFactory entityManagerFactory) {
    return new JpaItemWriterBuilder<CustomerCredit>()
        .entityManagerFactory(entityManagerFactory)
        .build();
  }

  @Bean
  public Step step(
      JobRepository jobRepository,
      JpaTransactionManager jpaTransactionManager,
      JpaPagingItemReader<CustomerCredit> itemReader,
      JpaItemWriter<CustomerCredit> itemWriter) {
    return new StepBuilder("step1", jobRepository)
        .<CustomerCredit, CustomerCredit>chunk(2, jpaTransactionManager)
        .reader(itemReader)
        .processor(new CustomerCreditIncreaseProcessor())
        .writer(itemWriter)
        .build();
  }

  @Bean
  public Job job(JobRepository jobRepository, Step step) {
    return new JobBuilder("ioSampleJob", jobRepository).start(step).build();
  }

  @Bean
  public JpaTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

  @Bean
  public EntityManagerFactory entityManagerFactory(
      PersistenceUnitManager persistenceUnitManager, DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean factoryBean =
        new LocalContainerEntityManagerFactoryBean();

    factoryBean.setDataSource(dataSource);
    factoryBean.setPersistenceUnitManager(persistenceUnitManager);
    factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
    factoryBean.afterPropertiesSet();

    return factoryBean.getObject();
  }

  @Bean
  public PersistenceUnitManager persistenceUnitManager(DataSource dataSource) {
    DefaultPersistenceUnitManager persistenceUnitManager = new DefaultPersistenceUnitManager();

    persistenceUnitManager.setDefaultDataSource(dataSource);
    persistenceUnitManager.afterPropertiesSet();

    return persistenceUnitManager;
  }
}
