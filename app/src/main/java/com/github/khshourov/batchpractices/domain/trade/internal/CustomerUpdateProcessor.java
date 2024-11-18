package com.github.khshourov.batchpractices.domain.trade.internal;

import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import org.springframework.batch.item.ItemProcessor;

public class CustomerUpdateProcessor implements ItemProcessor<CustomerUpdate, CustomerUpdate> {
  private CustomerDao customerDao;
  private InvalidCustomerLogger invalidCustomerLogger;

  @Override
  public CustomerUpdate process(CustomerUpdate item) throws Exception {
    if (item.operation() == CustomerOperation.DELETE) {
      invalidCustomerLogger.log(item);
      return null;
    }

    CustomerCredit customerCredit = customerDao.getCustomerByName(item.customerName());
    if (item.operation() == CustomerOperation.ADD) {
      if (customerCredit == null) {
        return item;
      }

      invalidCustomerLogger.log(item);
      return null;
    }

    if (item.operation() == CustomerOperation.UPDATE) {
      if (customerCredit != null) {
        return item;
      }

      invalidCustomerLogger.log(item);
      return null;
    }

    invalidCustomerLogger.log(item);
    return null;
  }

  public void setCustomerDao(CustomerDao customerDao) {
    this.customerDao = customerDao;
  }

  public void setInvalidCustomerLogger(InvalidCustomerLogger invalidCustomerLogger) {
    this.invalidCustomerLogger = invalidCustomerLogger;
  }
}
