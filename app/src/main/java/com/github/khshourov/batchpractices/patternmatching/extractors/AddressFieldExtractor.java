package com.github.khshourov.batchpractices.patternmatching.extractors;

import com.github.khshourov.batchpractices.patternmatching.models.Address;
import com.github.khshourov.batchpractices.patternmatching.models.Order;
import org.springframework.batch.item.file.transform.FieldExtractor;

public class AddressFieldExtractor implements FieldExtractor<Order> {
  @Override
  public Object[] extract(Order item) {
    Address address = item.getBillingAddress();
    return new Object[] {
      "ADDRESS:", address.getAddrLine1(), address.getCity(), address.getZipCode()
    };
  }
}
