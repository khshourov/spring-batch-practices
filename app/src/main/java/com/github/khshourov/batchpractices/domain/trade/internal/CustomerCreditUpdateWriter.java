package com.github.khshourov.batchpractices.domain.trade.internal;

import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class CustomerCreditUpdateWriter implements ItemWriter<CustomerCredit> {
  private CustomerCreditDao customerCreditDao;

  @Override
  public void write(Chunk<? extends CustomerCredit> chunk) throws Exception {
    for (CustomerCredit customerCredit : chunk) {
      this.customerCreditDao.writeCredit(customerCredit);
    }
  }

  public void setCustomerCreditDao(CustomerCreditDao customerCreditDao) {
    this.customerCreditDao = customerCreditDao;
  }
}
