package com.github.khshourov.batchpractices.domain.trade.internal;

import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import com.github.khshourov.batchpractices.domain.trade.Trade;
import javax.annotation.Nullable;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.batch.item.file.transform.LineAggregator;

public class DelegatingTradingCustomerAggregator implements LineAggregator<Object> {
  private FormatterLineAggregator<Trade> tradeFormatterLineAggregator;
  private FormatterLineAggregator<CustomerCredit> customerCreditFormatterLineAggregator;

  @Override
  @Nullable public String aggregate(@Nullable Object item) {
    if (item instanceof Trade) {
      return this.tradeFormatterLineAggregator.aggregate((Trade) item);
    } else if (item instanceof CustomerCredit) {
      return this.customerCreditFormatterLineAggregator.aggregate((CustomerCredit) item);
    } else {
      throw new IllegalArgumentException();
    }
  }

  public void setTradeFormatterLineAggregator(
      FormatterLineAggregator<Trade> tradeFormatterLineAggregator) {
    this.tradeFormatterLineAggregator = tradeFormatterLineAggregator;
  }

  public void setCustomerCreditFormatterLineAggregator(
      FormatterLineAggregator<CustomerCredit> customerCreditFormatterLineAggregator) {
    this.customerCreditFormatterLineAggregator = customerCreditFormatterLineAggregator;
  }
}
