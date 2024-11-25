package com.github.khshourov.batchpractices.common;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.incrementer.PostgresSequenceMaxValueIncrementer;

@Configuration
public class DataSourceConfiguration {

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

  @Value("${spring.datasource.driver-class-name}")
  private String driverClassName;

  @Bean
  @Profile("dev")
  public DataSource dataSource() {
    return DataSourceBuilder.create()
        .driverClassName(driverClassName)
        .url(url)
        .username(username)
        .password(password)
        .build();
  }

  @Bean
  public JdbcTransactionManager transactionManager(DataSource dataSource) {
    return new JdbcTransactionManager(dataSource);
  }

  @Bean
  public PostgresSequenceMaxValueIncrementer tradeIncrementer(DataSource dataSource) {
    PostgresSequenceMaxValueIncrementer incrementer = new PostgresSequenceMaxValueIncrementer();
    incrementer.setDataSource(dataSource);
    incrementer.setIncrementerName("TRADE_SEQ");
    return incrementer;
  }

  @Bean
  public PostgresSequenceMaxValueIncrementer customerIncrementer(DataSource dataSource) {
    PostgresSequenceMaxValueIncrementer incrementer = new PostgresSequenceMaxValueIncrementer();
    incrementer.setDataSource(dataSource);
    incrementer.setIncrementerName("CUSTOMER_SEQ");
    return incrementer;
  }

  @Bean
  public PostgresSequenceMaxValueIncrementer batchStagingIncrementer(DataSource dataSource) {
    PostgresSequenceMaxValueIncrementer incrementer = new PostgresSequenceMaxValueIncrementer();
    incrementer.setDataSource(dataSource);
    incrementer.setIncrementerName("BATCH_STAGING_SEQ");
    return incrementer;
  }
}
