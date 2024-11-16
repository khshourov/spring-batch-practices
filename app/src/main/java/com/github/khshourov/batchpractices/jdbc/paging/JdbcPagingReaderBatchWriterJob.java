package com.github.khshourov.batchpractices.jdbc.paging;

import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import com.github.khshourov.batchpractices.domain.trade.internal.CustomerCreditRowMapper;
import com.github.khshourov.batchpractices.jdbc.JdbcReaderBatchWriterJob;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdbcPagingReaderBatchWriterJob extends JdbcReaderBatchWriterJob {
  @Bean
  @StepScope
  public JdbcPagingItemReader<CustomerCredit> itemReader(
      DataSource dataSource, @Value("#{jobParameters['credit']}") Double credit) {
    return new JdbcPagingItemReaderBuilder<CustomerCredit>()
        .name("customerReader")
        .dataSource(dataSource)
        .selectClause("SELECT id, name, credit")
        .fromClause("FROM customer")
        .whereClause("WHERE credit > :credit")
        .sortKeys(Map.of("id", Order.ASCENDING))
        .rowMapper(new CustomerCreditRowMapper())
        .pageSize(2)
        .parameterValues(
            Map.of(
                "statusCode", "PE",
                "credit", credit,
                "type", "COLLECTION"))
        .build();
  }
}
