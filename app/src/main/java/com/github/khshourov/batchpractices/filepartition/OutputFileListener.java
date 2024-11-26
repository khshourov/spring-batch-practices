package com.github.khshourov.batchpractices.filepartition;

import org.apache.commons.io.FilenameUtils;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class OutputFileListener implements StepExecutionListener {
  private static final String outputBasePath = "file:build/test-output/";

  @Override
  public void beforeStep(StepExecution stepExecution) {
    String outputFile = stepExecution.getStepName().replace(":", "-");
    if (stepExecution.getExecutionContext().containsKey("inputFile")) {
      outputFile = stepExecution.getExecutionContext().getString("inputFile");
    }

    stepExecution
        .getExecutionContext()
        .put("outputFile", outputBasePath + FilenameUtils.getBaseName(outputFile) + ".csv");
  }
}
