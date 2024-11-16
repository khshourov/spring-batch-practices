package com.github.khshourov.batchpractices.jdbc.cursor;

import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import com.github.khshourov.batchpractices.domain.trade.internal.CustomerCreditRowMapper;
import com.github.khshourov.batchpractices.jdbc.JdbcReaderBatchWriterJob;
import javax.sql.DataSource;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdbcCursorReaderBatchWriterJob extends JdbcReaderBatchWriterJob {
  @Bean
  public JdbcCursorItemReader<CustomerCredit> itemReader(DataSource dataSource) {
    String sql = "SELECT id, name, credit FROM customer";

    return new JdbcCursorItemReaderBuilder<CustomerCredit>()
        .name("customerReader")
        .dataSource(dataSource)
        .sql(sql)
        .rowMapper(new CustomerCreditRowMapper())
        .build();
  }
}
