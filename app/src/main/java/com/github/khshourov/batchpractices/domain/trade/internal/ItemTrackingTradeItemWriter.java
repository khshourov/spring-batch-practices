package com.github.khshourov.batchpractices.domain.trade.internal;

import com.github.khshourov.batchpractices.domain.trade.Trade;
import java.util.ArrayList;
import java.util.List;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class ItemTrackingTradeItemWriter implements ItemWriter<Trade> {
  private final List<Trade> trades = new ArrayList<>();

  @Override
  public void write(Chunk<? extends Trade> chunk) throws Exception {
    for (Trade trade : chunk) {
      this.trades.add(trade);
    }
  }

  public List<Trade> getTrades() {
    return this.trades;
  }
}
