package com.github.khshourov.batchpractices.adapter.tasklet;

import org.springframework.batch.core.scope.context.ChunkContext;

public class Task {
  public boolean doWork(ChunkContext chunkContext) {
    chunkContext
        .getStepContext()
        .getStepExecution()
        .getJobExecution()
        .getExecutionContext()
        .put("done", "yes");
    return true;
  }
}
