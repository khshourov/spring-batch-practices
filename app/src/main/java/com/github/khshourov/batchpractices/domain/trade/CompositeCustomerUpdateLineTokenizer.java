package com.github.khshourov.batchpractices.domain.trade;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.lang.Nullable;

public class CompositeCustomerUpdateLineTokenizer implements StepExecutionListener, LineTokenizer {

  private LineTokenizer customerTokenizer;

  private LineTokenizer footerTokenizer;

  private StepExecution stepExecution;

  @Override
  public FieldSet tokenize(@Nullable String line) {
    assert line != null;

    if (line.charAt(0) == 'F') {
      // line starts with F, so the footer tokenizer should tokenize it.
      FieldSet fs = footerTokenizer.tokenize(line);
      long customerUpdateTotal = stepExecution.getReadCount();
      long fileUpdateTotal = fs.readLong(1);
      if (customerUpdateTotal != fileUpdateTotal) {
        throw new IllegalStateException(
            "The total number of customer updates in the file footer does not match the "
                + "number entered  File footer total: ["
                + fileUpdateTotal
                + "] Total encountered during processing: ["
                + customerUpdateTotal
                + "]");
      } else {
        // return null, because the footer indicates an end of processing.
        return null;
      }
    } else if (line.charAt(0) == 'A' || line.charAt(0) == 'U' || line.charAt(0) == 'D') {
      // line starts with A,U, or D, so it must be a customer operation.
      return customerTokenizer.tokenize(line);
    } else {
      // If the line doesn't start with any of the characters above, it must
      // obviously be invalid.
      throw new IllegalArgumentException("Invalid line encountered for tokenizing: " + line);
    }
  }

  @Override
  public void beforeStep(StepExecution stepExecution) {
    this.stepExecution = stepExecution;
  }

  public void setCustomerTokenizer(LineTokenizer customerTokenizer) {
    this.customerTokenizer = customerTokenizer;
  }

  public void setFooterTokenizer(LineTokenizer footerTokenizer) {
    this.footerTokenizer = footerTokenizer;
  }
}
