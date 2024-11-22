package com.github.khshourov.batchpractices.patternmatching.extractors;

import com.github.khshourov.batchpractices.patternmatching.models.Customer;
import com.github.khshourov.batchpractices.patternmatching.models.Order;
import org.springframework.batch.item.file.transform.FieldExtractor;

public class CustomerFieldExtractor implements FieldExtractor<Order> {
  @Override
  public Object[] extract(Order item) {
    Customer customer = item.getCustomer();
    return new Object[] {
      "CUSTOMER:",
      customer.getRegistrationId(),
      emptyIfNull(customer.getFirstName()),
      emptyIfNull(customer.getMiddleName()),
      emptyIfNull(customer.getLastName())
    };
  }

  private String emptyIfNull(String s) {
    return s != null ? s : "";
  }
}
