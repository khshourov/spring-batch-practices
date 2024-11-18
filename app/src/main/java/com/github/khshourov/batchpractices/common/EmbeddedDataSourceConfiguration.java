package com.github.khshourov.batchpractices.common;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.incrementer.HsqlMaxValueIncrementer;

@Configuration
@Profile("test")
public class EmbeddedDataSourceConfiguration {

  @Bean
  public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.HSQL)
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

  @Bean
  public HsqlMaxValueIncrementer tradeIncrementer(DataSource dataSource) {
    HsqlMaxValueIncrementer incrementer = new HsqlMaxValueIncrementer();
    incrementer.setDataSource(dataSource);
    incrementer.setColumnName("ID");
    incrementer.setIncrementerName("TRADE_SEQ");
    return incrementer;
  }

  @Bean
  public HsqlMaxValueIncrementer customerIncrementer(DataSource dataSource) {
    HsqlMaxValueIncrementer incrementer = new HsqlMaxValueIncrementer();
    incrementer.setDataSource(dataSource);
    incrementer.setColumnName("ID");
    incrementer.setIncrementerName("CUSTOMER_SEQ");
    return incrementer;
  }
}
