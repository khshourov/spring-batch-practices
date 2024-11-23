package com.github.khshourov.batchpractices.domain.trade.internal;

import com.github.khshourov.batchpractices.domain.trade.Trade;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class CustomerDebitUpdateWriter implements ItemWriter<Trade> {
  private CustomerDebitDao customerDebitDao;

  @Override
  public void write(Chunk<? extends Trade> chunk) throws Exception {
    for (Trade trade : chunk) {
      CustomerDebit customerDebit = new CustomerDebit();
      customerDebit.setName(trade.getCustomer());
      customerDebit.setDebit(trade.getPrice());

      this.customerDebitDao.write(customerDebit);
    }
  }

  public void setCustomerDebitDao(CustomerDebitDao customerDebitDao) {
    this.customerDebitDao = customerDebitDao;
  }
}
