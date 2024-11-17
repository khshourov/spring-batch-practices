package com.github.khshourov.batchpractices.domain.person.internal;

import com.github.khshourov.batchpractices.domain.person.Person;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class PersonWriter implements ItemWriter<Person> {
  private static final Log log = LogFactory.getLog(PersonWriter.class);

  @Override
  public void write(Chunk<? extends Person> chunk) throws Exception {
    if (log.isDebugEnabled()) {
      log.debug("Processing: " + chunk);
    }
  }
}
