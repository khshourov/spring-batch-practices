package com.github.khshourov.batchpractices.domain.trade.internal;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class CustomerUpdateWriter implements ItemWriter<CustomerUpdate> {
  private CustomerDao customerDao;

  @Override
  public void write(Chunk<? extends CustomerUpdate> items) throws Exception {
    for (CustomerUpdate customerUpdate : items) {
      if (customerUpdate.operation() == CustomerOperation.ADD) {
        customerDao.insertCustomer(customerUpdate.customerName(), customerUpdate.credit());
      } else if (customerUpdate.operation() == CustomerOperation.UPDATE) {
        customerDao.updateCustomer(customerUpdate.customerName(), customerUpdate.credit());
      }
    }
  }

  public void setCustomerDao(CustomerDao customerDao) {
    this.customerDao = customerDao;
  }
}
