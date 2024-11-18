package com.github.khshourov.batchpractices.domain.trade.internal;

import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

public class JdbcCustomerDao extends JdbcDaoSupport implements CustomerDao {

  private static final String GET_CUSTOMER_BY_NAME =
      "SELECT ID, NAME, CREDIT from CUSTOMER where NAME = ?";

  private static final String INSERT_CUSTOMER =
      "INSERT into CUSTOMER(ID, NAME, CREDIT) values(?,?,?)";

  private static final String UPDATE_CUSTOMER = "UPDATE CUSTOMER set CREDIT = ? where NAME = ?";

  private DataFieldMaxValueIncrementer incrementer;

  public void setIncrementer(DataFieldMaxValueIncrementer incrementer) {
    this.incrementer = incrementer;
  }

  @Override
  public CustomerCredit getCustomerByName(String name) {
    assert getJdbcTemplate() != null;

    List<CustomerCredit> customers =
        getJdbcTemplate()
            .query(
                GET_CUSTOMER_BY_NAME,
                (rs, rowNum) -> {
                  CustomerCredit customer = new CustomerCredit();
                  customer.setName(rs.getString("NAME"));
                  customer.setId(rs.getInt("ID"));
                  customer.setCredit(rs.getBigDecimal("CREDIT"));
                  return customer;
                },
                name);

    if (customers.isEmpty()) {
      return null;
    } else {
      return customers.getFirst();
    }
  }

  @Override
  public void insertCustomer(String name, BigDecimal credit) {
    assert getJdbcTemplate() != null;

    getJdbcTemplate()
        .update(INSERT_CUSTOMER, new Object[] {incrementer.nextIntValue(), name, credit});
  }

  @Override
  public void updateCustomer(String name, BigDecimal credit) {
    assert getJdbcTemplate() != null;

    getJdbcTemplate().update(UPDATE_CUSTOMER, new Object[] {credit, name});
  }
}
