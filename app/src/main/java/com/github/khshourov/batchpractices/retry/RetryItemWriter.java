package com.github.khshourov.batchpractices.retry;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class RetryItemWriter<T> implements ItemWriter<T> {
  private int counter = 0;

  @Override
  public void write(Chunk<? extends T> chunk) throws Exception {
    int current = this.counter;
    this.counter += chunk.size();
    if (current < 3 && this.counter >= 2) {
      throw new IllegalStateException("Temporary error");
    }
  }

  public int getCounter() {
    return this.counter;
  }
}
