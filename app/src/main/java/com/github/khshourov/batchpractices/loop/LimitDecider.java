package com.github.khshourov.batchpractices.loop;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class LimitDecider implements JobExecutionDecider {
  private int limit = 1;
  private int counter = 0;

  @Override
  public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
    this.counter = this.counter + 1;
    if (this.counter >= this.limit) {
      return new FlowExecutionStatus("COMPLETED");
    } else {
      return new FlowExecutionStatus("CONTINUE");
    }
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }
}
