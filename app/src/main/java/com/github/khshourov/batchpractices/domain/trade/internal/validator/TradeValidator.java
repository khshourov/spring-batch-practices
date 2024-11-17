package com.github.khshourov.batchpractices.domain.trade.internal.validator;

import com.github.khshourov.batchpractices.domain.trade.Trade;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class TradeValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(Trade.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    Trade trade = (Trade) target;

    if (trade.getIsin().length() >= 13) {
      errors.rejectValue("isin", "isin_length");
    }
  }
}
