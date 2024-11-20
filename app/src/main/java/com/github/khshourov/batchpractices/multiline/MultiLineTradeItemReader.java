package com.github.khshourov.batchpractices.multiline;

import com.github.khshourov.batchpractices.domain.trade.Trade;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.util.Assert;

public class MultiLineTradeItemReader implements ItemReader<Trade>, ItemStream {
  private FlatFileItemReader<FieldSet> delegate;

  @Override
  public Trade read() throws Exception {
    FieldSet line = null;
    Trade trade = null;
    while ((line = this.delegate.read()) != null) {
      switch (line.readString(0)) {
        case "BEGIN" -> {
          trade = new Trade();
        }
        case "INFO" -> {
          Assert.notNull(trade, "No 'BEGIN' was found");
          trade.setIsin(line.readString(1));
          trade.setCustomer(line.readString(2));
        }
        case "AMNT" -> {
          Assert.notNull(trade, "No 'BEGIN' was found");
          trade.setQuantity(line.readInt(1));
          trade.setPrice(line.readBigDecimal(2));
        }
        case "END" -> {
          return trade;
        }
        default -> throw new IllegalArgumentException();
      }
    }
    Assert.isNull(trade, "No 'END' was found");
    return null;
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

  public void setDelegate(FlatFileItemReader<FieldSet> itemReader) {
    this.delegate = itemReader;
  }
}
