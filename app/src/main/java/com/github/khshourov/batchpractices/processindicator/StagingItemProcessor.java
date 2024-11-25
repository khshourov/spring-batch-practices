package com.github.khshourov.batchpractices.processindicator;

import javax.sql.DataSource;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;

public class StagingItemProcessor<T> implements ItemProcessor<ProcessIndicatorItemWrapper<T>, T> {
  private static final String UPDATE_BATCH_STAGING =
      "UPDATE batch_staging SET processed = 'Y' WHERE id = ? AND processed = 'N'";
  private JdbcTemplate jdbcTemplate;

  @Override
  public T process(ProcessIndicatorItemWrapper<T> item) throws Exception {
    boolean updated = this.jdbcTemplate.update(UPDATE_BATCH_STAGING, item.id()) >= 1;
    if (!updated) {
      throw new OptimisticLockingFailureException(
          "The staging record with id = "
              + item.id()
              + " was updated concurrently when trying to mark as complete");
    }

    return item.item();
  }

  public void setDataSource(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }
}
