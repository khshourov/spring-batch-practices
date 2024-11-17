package com.github.khshourov.batchpractices.common;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.support.JdbcTransactionManager;

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

  @Bean(name = "dataSource")
  @Profile("dev")
  public DataSource devDataSource() {
    return DataSourceBuilder.create()
        .driverClassName(driverClassName)
        .url(url)
        .username(username)
        .password(password)
        .build();
  }

  @Bean(name = "dataSource")
  public DataSource testDataSource() {
    return new EmbeddedDatabaseBuilder()
        .addScript("/org/springframework/batch/core/schema-drop-hsqldb.sql")
        .addScript("/org/springframework/batch/core/schema-hsqldb.sql")
        .addScript("/com/github/khshourov/batchpractices/common/business-schema-hsqldb.sql")
        .generateUniqueName(true)
        .build();
  }

  @Bean
  public JdbcTransactionManager transactionManager(DataSource dataSource) {
    return new JdbcTransactionManager(dataSource);
  }
}
