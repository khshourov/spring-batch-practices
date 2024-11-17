package com.github.khshourov.batchpractices.domain.trade.internal;

import com.github.khshourov.batchpractices.domain.trade.Trade;
import com.github.khshourov.batchpractices.domain.trade.TradeDao;
import java.math.BigDecimal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.annotation.AfterWrite;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.batch.item.ItemWriter;

public class TradeWriter extends ItemStreamSupport implements ItemWriter<Trade> {
  private static final Log log = LogFactory.getLog(TradeWriter.class);
  private static final String TOTAL_AMOUNT_KEY = "TOTAL_AMOUNT";
  private BigDecimal totalPrice = BigDecimal.ZERO;

  private TradeDao tradeDao;

  public void setTradeDao(TradeDao tradeDao) {
    this.tradeDao = tradeDao;
  }

  @Override
  public void open(ExecutionContext executionContext) throws ItemStreamException {
    if (executionContext.containsKey(TOTAL_AMOUNT_KEY)) {
      this.totalPrice = (BigDecimal) executionContext.get(TOTAL_AMOUNT_KEY);
    } else {
      this.totalPrice = BigDecimal.ZERO;
    }
  }

  @Override
  public void update(ExecutionContext executionContext) throws ItemStreamException {
    executionContext.put(TOTAL_AMOUNT_KEY, this.totalPrice);
  }

  @Override
  public void write(Chunk<? extends Trade> trades) throws Exception {
    for (Trade trade : trades) {
      log.debug(trade);

      this.tradeDao.writeTrade(trade);
    }
  }

  @AfterWrite
  public void updateTotalPrice(Chunk<Trade> trades) {
    for (Trade trade : trades) {
      this.totalPrice = this.totalPrice.add(trade.getPrice());
    }
  }
}
