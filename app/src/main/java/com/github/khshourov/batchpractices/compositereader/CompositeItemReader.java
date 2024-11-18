package com.github.khshourov.batchpractices.compositereader;

import java.util.Iterator;
import java.util.List;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

public class CompositeItemReader<T> implements ItemStreamReader<T> {

  private final List<ItemStreamReader<? extends T>> delegates;

  private final Iterator<ItemStreamReader<? extends T>> delegatesIterator;

  private ItemStreamReader<? extends T> currentDelegate;

  public CompositeItemReader(List<ItemStreamReader<? extends T>> delegates) {
    this.delegates = delegates;
    this.delegatesIterator = this.delegates.iterator();
    this.currentDelegate = this.delegatesIterator.hasNext() ? this.delegatesIterator.next() : null;
  }

  @Override
  public void open(ExecutionContext executionContext) throws ItemStreamException {
    for (ItemStreamReader<? extends T> delegate : delegates) {
      delegate.open(executionContext);
    }
  }

  @Override
  public T read() throws Exception {
    if (this.currentDelegate == null) {
      return null;
    }
    T item = currentDelegate.read();
    if (item == null) {
      currentDelegate = this.delegatesIterator.hasNext() ? this.delegatesIterator.next() : null;
      return read();
    }
    return item;
  }

  @Override
  public void update(ExecutionContext executionContext) throws ItemStreamException {
    if (this.currentDelegate != null) {
      this.currentDelegate.update(executionContext);
    }
  }

  @Override
  public void close() throws ItemStreamException {
    for (ItemStreamReader<? extends T> delegate : delegates) {
      delegate.close();
    }
  }
}
