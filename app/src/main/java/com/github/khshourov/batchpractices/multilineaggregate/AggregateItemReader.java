package com.github.khshourov.batchpractices.multilineaggregate;

import java.util.ArrayList;
import java.util.List;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;

public class AggregateItemReader<T> implements ItemReader<List<T>>, ItemStream {
  private FlatFileItemReader<AggregateItem<T>> delegate;

  @Override
  public List<T> read()
      throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    List<T> records = new ArrayList<>();

    AggregateItem<T> value;
    while (true) {
      value = this.delegate.read();
      if (value == null) {
        return null;
      }

      if (value.isFooter()) {
        return records;
      }

      if (value.isHeader()) {
        records.clear();
      } else {
        records.add(value.getItem());
      }
    }
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

  public void setDelegate(FlatFileItemReader<AggregateItem<T>> delegate) {
    this.delegate = delegate;
  }
}
