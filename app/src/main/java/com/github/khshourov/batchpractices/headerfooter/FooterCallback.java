package com.github.khshourov.batchpractices.headerfooter;

import java.io.IOException;
import java.io.Writer;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.file.FlatFileFooterCallback;

public class FooterCallback implements StepExecutionListener, FlatFileFooterCallback {
  private StepExecution stepExecution;

  @Override
  public void beforeStep(StepExecution stepExecution) {
    this.stepExecution = stepExecution;
  }

  @Override
  public void writeFooter(Writer writer) throws IOException {
    writer.write("footer - number of items written: " + this.stepExecution.getWriteCount());
  }
}
