package com.github.khshourov.batchpractices.patternmatching.fieldsetmappers;

import com.github.khshourov.batchpractices.patternmatching.models.LineItem;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class LineItemFieldSetMapper implements FieldSetMapper<LineItem> {
  private static final String TOTAL_PRICE_COLUMN = "TOTAL_PRICE";
  private static final String QUANTITY_COLUMN = "QUANTITY";
  private static final String HANDLING_PRICE_COLUMN = "HANDLING_PRICE";
  private static final String SHIPPING_PRICE_COLUMN = "SHIPPING_PRICE";
  private static final String DISCOUNT_AMOUNT_COLUMN = "DISCOUNT_AMOUNT";
  private static final String DISCOUNT_PERC_COLUMN = "DISCOUNT_PERC";
  private static final String PRICE_COLUMN = "PRICE";
  private static final String ITEM_ID_COLUMN = "ITEM_ID";

  @Override
  public LineItem mapFieldSet(FieldSet fieldSet) throws BindException {
    LineItem item = new LineItem();

    item.setItemId(fieldSet.readLong(ITEM_ID_COLUMN));
    item.setPrice(fieldSet.readBigDecimal(PRICE_COLUMN));
    item.setDiscountPerc(fieldSet.readBigDecimal(DISCOUNT_PERC_COLUMN));
    item.setDiscountAmount(fieldSet.readBigDecimal(DISCOUNT_AMOUNT_COLUMN));
    item.setShippingPrice(fieldSet.readBigDecimal(SHIPPING_PRICE_COLUMN));
    item.setHandlingPrice(fieldSet.readBigDecimal(HANDLING_PRICE_COLUMN));
    item.setQuantity(fieldSet.readInt(QUANTITY_COLUMN));
    item.setTotalPrice(fieldSet.readBigDecimal(TOTAL_PRICE_COLUMN));

    return item;
  }
}
