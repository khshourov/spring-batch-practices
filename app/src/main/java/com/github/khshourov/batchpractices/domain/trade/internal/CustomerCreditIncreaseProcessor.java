package com.github.khshourov.batchpractices.domain.trade.internal;

import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import java.math.BigDecimal;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.Nullable;

public class CustomerCreditIncreaseProcessor
    implements ItemProcessor<CustomerCredit, CustomerCredit> {

  public static final BigDecimal FIXED_AMOUNT = new BigDecimal("5");

  @Nullable @Override
  public CustomerCredit process(CustomerCredit item) throws Exception {
    return item.increaseCreditBy(FIXED_AMOUNT);
  }
}
