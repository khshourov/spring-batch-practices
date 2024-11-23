package com.github.khshourov.batchpractices.domain.trade.internal;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcCustomerDebitDao implements CustomerDebitDao {
  private static final String UPDATE_CREDIT =
      "UPDATE CUSTOMER SET credit = credit - ? WHERE name = ?";

  private JdbcOperations jdbcTemplate;

  @Override
  public void write(CustomerDebit customerDebit) {
    jdbcTemplate.update(UPDATE_CREDIT, customerDebit.getDebit(), customerDebit.getName());
  }

  public void setDataSource(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }
}
