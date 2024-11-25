package com.github.khshourov.batchpractices.processindicator;

import static org.springframework.util.Assert.state;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.ReaderNotOpenException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;

public class StagingItemReader<T>
    implements ItemReader<ProcessIndicatorItemWrapper<T>>,
        StepExecutionListener,
        InitializingBean,
        DisposableBean {
  private static final Log logger = LogFactory.getLog(StagingItemReader.class);
  private static final String NEXT_UNPROCESSED_ID =
      "SELECT id FROM batch_staging WHERE job_id = ? AND processed = 'N' ORDER BY id";
  private static final String FETCH_SERIALIZED_TRADE_OBJ =
      "SELECT value FROM batch_staging WHERE id = ?";
  private StepExecution stepExecution;
  private JdbcTemplate jdbcTemplate;
  private final Lock lock = new ReentrantLock();
  private volatile boolean initialized = false;
  private Iterator<Long> keys;

  @Override
  public ProcessIndicatorItemWrapper<T> read()
      throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    if (!this.initialized) {
      throw new ReaderNotOpenException("StagingItemReader must be open before it can be used");
    }

    Long nextId = null;
    synchronized (this.lock) {
      if (this.keys.hasNext()) {
        nextId = this.keys.next();
      }
    }

    if (nextId == null) {
      return null;
    }

    T object =
        this.jdbcTemplate.queryForObject(
            FETCH_SERIALIZED_TRADE_OBJ,
            (rs, rowNum) -> this.deserialize(rs.getBinaryStream(1)),
            nextId);

    return new ProcessIndicatorItemWrapper<>(nextId, object);
  }

  public void setDataSource(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    state(this.jdbcTemplate != null, "DataSource should be set");
  }

  @Override
  public void beforeStep(StepExecution stepExecution) {
    this.stepExecution = stepExecution;
    synchronized (this.lock) {
      if (this.keys == null) {
        this.keys = this.retrieveKeys().iterator();
        logger.info("Keys obtained for staging");
        this.initialized = true;
      }
    }
  }

  @Override
  public void destroy() throws Exception {
    this.initialized = false;
    this.keys = null;
  }

  private List<Long> retrieveKeys() {
    this.lock.lock();
    try {
      return this.jdbcTemplate.query(
          NEXT_UNPROCESSED_ID,
          (rs, rowNum) -> rs.getLong(1),
          this.stepExecution.getJobExecution().getJobId());
    } finally {
      this.lock.unlock();
    }
  }

  private T deserialize(InputStream inputStream) {
    if (inputStream == null) {
      return null;
    }

    try (var objectInputStream = new ObjectInputStream(inputStream)) {
      return (T) objectInputStream.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new IllegalArgumentException("Failed to deserialize object", e);
    }
  }
}
