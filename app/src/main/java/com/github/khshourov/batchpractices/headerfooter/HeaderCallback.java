package com.github.khshourov.batchpractices.headerfooter;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.LineCallbackHandler;

public class HeaderCallback implements LineCallbackHandler, FlatFileHeaderCallback {
  private String header;

  @Override
  public void handleLine(String line) {
    Objects.requireNonNull(line);

    this.header = line;
  }

  @Override
  public void writeHeader(Writer writer) throws IOException {
    writer.write(this.header);
  }
}
