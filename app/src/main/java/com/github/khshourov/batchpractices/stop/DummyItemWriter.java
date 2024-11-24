package com.github.khshourov.batchpractices.stop;

import com.github.khshourov.batchpractices.domain.trade.Trade;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class DummyItemWriter implements ItemWriter<Trade> {
  @Override
  public void write(Chunk<? extends Trade> chunk) throws Exception {
    Thread.sleep(500);
  }
}
