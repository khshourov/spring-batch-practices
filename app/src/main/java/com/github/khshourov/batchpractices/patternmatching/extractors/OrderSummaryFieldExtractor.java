package com.github.khshourov.batchpractices.patternmatching.extractors;

import com.github.khshourov.batchpractices.patternmatching.models.Order;
import org.springframework.batch.item.file.transform.FieldExtractor;

public class OrderSummaryFieldExtractor implements FieldExtractor<Order> {
  @Override
  public Object[] extract(Order item) {
    return new Object[] {"END_ORDER:", item.getTotalPrice()};
  }
}
