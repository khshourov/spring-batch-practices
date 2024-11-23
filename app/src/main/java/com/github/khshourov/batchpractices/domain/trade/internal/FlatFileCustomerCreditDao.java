package com.github.khshourov.batchpractices.domain.trade.internal;

import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.beans.factory.DisposableBean;

public class FlatFileCustomerCreditDao implements CustomerCreditDao, ItemStream, DisposableBean {
  private static final String separator = "\t";
  private ItemStreamWriter<String> itemWriter;
  private boolean opened = false;

  @Override
  public void writeCredit(CustomerCredit customerCredit) throws Exception {
    if (!this.opened) {
      this.open(new ExecutionContext());
    }

    itemWriter.write(
        Chunk.of(
            String.join(
                separator,
                new String[] {
                  customerCredit.getName(), String.valueOf(customerCredit.getCredit())
                })));
  }

  @Override
  public void open(ExecutionContext executionContext) throws ItemStreamException {
    this.itemWriter.open(executionContext);
    this.opened = true;
  }

  @Override
  public void update(ExecutionContext executionContext) throws ItemStreamException {
    this.itemWriter.update(executionContext);
  }

  @Override
  public void close() throws ItemStreamException {
    this.itemWriter.close();
  }

  public void setItemWriter(ItemStreamWriter<String> itemWriter) {
    this.itemWriter = itemWriter;
  }

  @Override
  public void destroy() throws Exception {
    this.close();
  }
}
