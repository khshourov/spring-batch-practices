package com.github.khshourov.batchpractices.domain.trade.internal;

import com.github.khshourov.batchpractices.domain.trade.Trade;
import com.github.khshourov.batchpractices.domain.trade.TradeDao;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

public class JdbcTradeDao implements TradeDao {

  private final Log log = LogFactory.getLog(JdbcTradeDao.class);

  private static final String INSERT_TRADE_RECORD =
      "INSERT INTO TRADE (id, version, isin, quantity, price, customer) VALUES (?, 0, ?, ? ,?, ?)";

  private JdbcOperations jdbcTemplate;

  private DataFieldMaxValueIncrementer incrementer;

  @Override
  public void writeTrade(Trade trade) {
    Long id = incrementer.nextLongValue();
    if (log.isDebugEnabled()) {
      log.debug("Processing: " + trade);
    }
    jdbcTemplate.update(
        INSERT_TRADE_RECORD,
        id,
        trade.getIsin(),
        trade.getQuantity(),
        trade.getPrice(),
        trade.getCustomer());
  }

  public void setDataSource(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public void setIncrementer(DataFieldMaxValueIncrementer incrementer) {
    this.incrementer = incrementer;
  }
}
