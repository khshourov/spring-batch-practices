package com.github.khshourov.batchpractices.patternmatching;

import com.github.khshourov.batchpractices.patternmatching.extractors.AddressFieldExtractor;
import com.github.khshourov.batchpractices.patternmatching.extractors.BillingInfoFieldExtractor;
import com.github.khshourov.batchpractices.patternmatching.extractors.CustomerFieldExtractor;
import com.github.khshourov.batchpractices.patternmatching.extractors.LineItemExtractor;
import com.github.khshourov.batchpractices.patternmatching.extractors.OrderHeaderFieldExtractor;
import com.github.khshourov.batchpractices.patternmatching.extractors.OrderSummaryFieldExtractor;
import com.github.khshourov.batchpractices.patternmatching.models.LineItem;
import com.github.khshourov.batchpractices.patternmatching.models.Order;
import java.util.Map;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderLineAggregatorConfiguration {
  @Bean
  public Map<String, FormatterLineAggregator<Order>> orderAggregators() {
    return Map.of(
        "header", getAggregator("%-12s%-10s%-30s", new OrderHeaderFieldExtractor()),
        "footer", getAggregator("%-10s%20s", new OrderSummaryFieldExtractor()),
        "customer", getAggregator("%-9s%-10s%-10s%-10s%-10s", new CustomerFieldExtractor()),
        "address", getAggregator("%-8s%-20s%-10s%-10s", new AddressFieldExtractor()),
        "billing", getAggregator("%-8s%-10s%-20s", new BillingInfoFieldExtractor()));
  }

  @Bean
  public FormatterLineAggregator<LineItem> lineAggregator() {
    FormatterLineAggregator<LineItem> aggregator = new FormatterLineAggregator<>();
    aggregator.setFormat("%-5s%-10s%-10s");
    aggregator.setFieldExtractor(new LineItemExtractor());
    return aggregator;
  }

  private FormatterLineAggregator<Order> getAggregator(
      String format, FieldExtractor<Order> extractor) {
    FormatterLineAggregator<Order> aggregator = new FormatterLineAggregator<>();
    aggregator.setFormat(format);
    aggregator.setFieldExtractor(extractor);
    return aggregator;
  }
}
