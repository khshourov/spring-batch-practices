package com.github.khshourov.batchpractices.restart;

import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class ExceptionThrowingItemReaderProxy<T> implements ItemReader<T>, ItemStream {
  private final int throwExceptionOnRecordNumber = 4;
  private int counter = 0;
  private ItemStreamReader<T> delegate;

  @Override
  public T read()
      throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    this.counter = this.counter + 1;
    if (this.counter == this.throwExceptionOnRecordNumber) {
      throw new UnexpectedJobExecutionException("Planned exception on record number: " + counter);
    }

    return this.delegate.read();
  }

  public void setDelegate(ItemStreamReader<T> delegate) {
    this.delegate = delegate;
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
}
