package com.github.khshourov.batchpractices.domain.trade.internal;

import com.github.khshourov.batchpractices.domain.trade.Trade;
import java.util.Objects;
import javax.annotation.Nullable;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class TradeFieldSetMapper implements FieldSetMapper<Object> {
  private static final int ISIN_INDEX = 0;
  private static final int QUANTITY_INDEX = 1;
  private static final int PRICE_INDEX = 2;
  private static final int CUSTOMER_INDEX = 3;

  @Override
  @Nullable public Object mapFieldSet(@Nullable FieldSet fieldSet) throws BindException {
    Objects.requireNonNull(fieldSet);

    Trade trade = new Trade();
    trade.setIsin(fieldSet.readString(ISIN_INDEX));
    trade.setQuantity(fieldSet.readInt(QUANTITY_INDEX));
    trade.setPrice(fieldSet.readBigDecimal(PRICE_INDEX));
    trade.setCustomer(fieldSet.readString(CUSTOMER_INDEX));
    return trade;
  }
}
