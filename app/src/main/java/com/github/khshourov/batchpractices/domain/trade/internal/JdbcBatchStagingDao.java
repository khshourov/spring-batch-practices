package com.github.khshourov.batchpractices.domain.trade.internal;

import com.github.khshourov.batchpractices.domain.trade.Trade;
import com.github.khshourov.batchpractices.domain.trade.TradeDao;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.util.SerializationUtils;

public class JdbcBatchStagingDao implements TradeDao, StepExecutionListener {
  private static final String INSERT_TEMPLATE =
      "INSERT into BATCH_STAGING (ID, JOB_ID, VALUE, PROCESSED) values (?,?,?,?)";
  private JdbcTemplate jdbcTemplate;
  private DataFieldMaxValueIncrementer incrementer;
  private StepExecution stepExecution;

  @Override
  public void writeTrade(Trade trade) {
    this.jdbcTemplate.batchUpdate(
        INSERT_TEMPLATE,
        new BatchPreparedStatementSetter() {
          @Override
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            ps.setLong(1, incrementer.nextLongValue());
            ps.setLong(2, stepExecution.getJobExecution().getJobId());
            ps.setBytes(3, SerializationUtils.serialize(trade));
            ps.setString(4, "N");
          }

          @Override
          public int getBatchSize() {
            return 1;
          }
        });
  }

  @Override
  public void beforeStep(StepExecution stepExecution) {
    this.stepExecution = stepExecution;
  }

  public void setDataSource(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public void setIncrementer(DataFieldMaxValueIncrementer incrementer) {
    this.incrementer = incrementer;
  }
}
