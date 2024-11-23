package com.github.khshourov.batchpractices.domain.trade.internal;

import com.github.khshourov.batchpractices.domain.trade.Trade;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class TradeRowMapper implements RowMapper<Trade> {
  private static final int ISIN_COLUMN = 1;
  private static final int QUANTITY_COLUMN = 2;
  private static final int PRICE_COLUMN = 3;
  private static final int CUSTOMER_COLUMN = 4;
  private static final int ID_COLUMN = 5;
  private static final int VERSION_COLUMN = 6;

  @Override
  public Trade mapRow(ResultSet rs, int rowNum) throws SQLException {
    Trade trade = new Trade(rs.getLong(ID_COLUMN));

    trade.setIsin(rs.getString(ISIN_COLUMN));
    trade.setQuantity(rs.getLong(QUANTITY_COLUMN));
    trade.setPrice(rs.getBigDecimal(PRICE_COLUMN));
    trade.setCustomer(rs.getString(CUSTOMER_COLUMN));
    trade.setVersion(rs.getInt(VERSION_COLUMN));

    return trade;
  }
}
