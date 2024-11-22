package com.github.khshourov.batchpractices.patternmatching;

import com.github.khshourov.batchpractices.patternmatching.models.LineItem;
import com.github.khshourov.batchpractices.patternmatching.models.Order;
import java.util.Map;

import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.batch.item.file.transform.LineAggregator;

public class OrderLineAggregator implements LineAggregator<Order> {
  private static final String LINE_SEPARATOR = System.lineSeparator();
  private Map<String, FormatterLineAggregator<Order>> aggregators;
  private FormatterLineAggregator<LineItem> lineItemAggregator;

  @Override
  public String aggregate(Order order) {
    StringBuilder result = new StringBuilder();

    result.append(aggregators.get("header").aggregate(order)).append(LINE_SEPARATOR);
    result.append(aggregators.get("customer").aggregate(order)).append(LINE_SEPARATOR);
    result.append(aggregators.get("address").aggregate(order)).append(LINE_SEPARATOR);
    result.append(aggregators.get("billing").aggregate(order)).append(LINE_SEPARATOR);

    for (LineItem lineItem : order.getLineItems()) {
      result.append(lineItemAggregator.aggregate(lineItem)).append(LINE_SEPARATOR);
    }

    result.append(aggregators.get("footer").aggregate(order));

    return result.toString();
  }

  public void setAggregators(Map<String, FormatterLineAggregator<Order>> aggregators) {
    this.aggregators = aggregators;
  }

  public void setLineItemAggregator(FormatterLineAggregator<LineItem> lineItemAggregator) {
    this.lineItemAggregator = lineItemAggregator;
  }
}
