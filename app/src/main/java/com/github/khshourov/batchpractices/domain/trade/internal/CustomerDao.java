package com.github.khshourov.batchpractices.domain.trade.internal;

import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import java.math.BigDecimal;

public interface CustomerDao {

  CustomerCredit getCustomerByName(String name);

  void insertCustomer(String name, BigDecimal credit);

  void updateCustomer(String name, BigDecimal credit);
}
