package com.github.khshourov.batchpractices.patternmatching.fieldsetmappers;

import com.github.khshourov.batchpractices.patternmatching.models.Order;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class OrderFieldSetItem implements FieldSetMapper<Order> {
  public static final String ORDER_ID_COLUMN = "ORDER_ID";
  public static final String ORDER_DATE_COLUMN = "ORDER_DATE";

  @Override
  public Order mapFieldSet(FieldSet fieldSet) throws BindException {
    Order order = new Order();
    order.setOrderId(fieldSet.readLong(ORDER_ID_COLUMN));
    order.setOrderDate(fieldSet.readDate(ORDER_DATE_COLUMN));

    return order;
  }
}
