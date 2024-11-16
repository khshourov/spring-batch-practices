package com.github.khshourov.batchpractices.domain.trade.internal;

import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CustomerCreditRowMapper implements RowMapper<CustomerCredit> {
  public static final String COL_ID = "id";
  public static final String COL_NAME = "name";
  public static final String COL_CREDIT = "credit";

  @Override
  public CustomerCredit mapRow(ResultSet rs, int rowNum) throws SQLException {
    CustomerCredit customerCredit = new CustomerCredit();

    customerCredit.setId(rs.getInt(COL_ID));
    customerCredit.setName(rs.getString(COL_NAME));
    customerCredit.setCredit(rs.getBigDecimal(COL_CREDIT));

    return customerCredit;
  }
}
