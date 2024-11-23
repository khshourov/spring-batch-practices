package com.github.khshourov.batchpractices.domain.trade.internal;

import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;

public interface CustomerCreditDao {
  void writeCredit(CustomerCredit customerCredit) throws Exception;
}
