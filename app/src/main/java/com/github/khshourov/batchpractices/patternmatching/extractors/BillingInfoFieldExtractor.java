package com.github.khshourov.batchpractices.patternmatching.extractors;

import com.github.khshourov.batchpractices.patternmatching.models.BillingInfo;
import com.github.khshourov.batchpractices.patternmatching.models.Order;
import org.springframework.batch.item.file.transform.FieldExtractor;

public class BillingInfoFieldExtractor implements FieldExtractor<Order> {
  @Override
  public Object[] extract(Order item) {
    BillingInfo billingInfo = item.getBilling();
    return new Object[] {"BILLING:", billingInfo.getPaymentId(), billingInfo.getPaymentDesc()};
  }
}
