package com.github.khshourov.batchpractices.domain.trade.internal;

import java.math.BigDecimal;

public record CustomerUpdate(CustomerOperation operation, String customerName, BigDecimal credit) {

  @Override
  public String toString() {
    return "Customer Update, name: ["
        + customerName
        + "], operation: ["
        + operation
        + "], credit: ["
        + credit
        + "]";
  }
}
