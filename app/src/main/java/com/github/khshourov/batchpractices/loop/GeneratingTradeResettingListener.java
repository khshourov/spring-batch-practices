package com.github.khshourov.batchpractices.loop;

import com.github.khshourov.batchpractices.domain.trade.internal.GeneratingTradeItemReader;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class GeneratingTradeResettingListener implements StepExecutionListener {
  private GeneratingTradeItemReader itemReader;

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    itemReader.resetCounter();
    return null;
  }

  public void setItemReader(GeneratingTradeItemReader itemReader) {
    this.itemReader = itemReader;
  }
}
