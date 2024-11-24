package com.github.khshourov.batchpractices.domain.trade.internal;

import com.github.khshourov.batchpractices.domain.trade.Trade;
import java.math.BigDecimal;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class GeneratingTradeItemReader implements ItemReader<Trade> {
  private int limit;
  private int counter = 0;

  @Override
  public Trade read()
      throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    if (this.counter < this.limit) {
      this.counter = this.counter + 1;
      return new Trade("isin" + counter, counter, new BigDecimal(counter), "customer" + counter);
    }

    return null;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }
}
