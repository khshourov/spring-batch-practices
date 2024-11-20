package com.github.khshourov.batchpractices.multiline;

import com.github.khshourov.batchpractices.domain.trade.Trade;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;

public class MultiLineTradeItemWriter implements ItemWriter<Trade>, ItemStream {
  private FlatFileItemWriter<String> delegate;

  @Override
  public void write(Chunk<? extends Trade> chunk) throws Exception {
    Chunk<String> lines = new Chunk<>();
    for (Trade trade : chunk) {
      lines.add("BEGIN");
      lines.add(String.format("INFO,%s,%s", trade.getIsin(), trade.getCustomer()));
      lines.add(String.format("AMNT,%d,%.2f", trade.getQuantity(), trade.getPrice()));
      lines.add("END");
    }
    this.delegate.write(lines);
  }

  @Override
  public void open(ExecutionContext executionContext) throws ItemStreamException {
    this.delegate.open(executionContext);
  }

  @Override
  public void update(ExecutionContext executionContext) throws ItemStreamException {
    this.delegate.update(executionContext);
  }

  @Override
  public void close() throws ItemStreamException {
    this.delegate.close();
  }

  public void setDelegate(FlatFileItemWriter<String> delegate) {
    this.delegate = delegate;
  }
}
