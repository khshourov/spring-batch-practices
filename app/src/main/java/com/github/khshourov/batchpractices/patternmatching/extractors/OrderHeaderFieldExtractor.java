package com.github.khshourov.batchpractices.patternmatching.extractors;

import com.github.khshourov.batchpractices.patternmatching.models.Order;
import java.text.SimpleDateFormat;
import org.springframework.batch.item.file.transform.FieldExtractor;

public class OrderHeaderFieldExtractor implements FieldExtractor<Order> {
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

  @Override
  public Object[] extract(Order item) {
    return new Object[] {"BEGIN_ORDER:", item.getOrderId(), dateFormat.format(item.getOrderDate())};
  }
}
