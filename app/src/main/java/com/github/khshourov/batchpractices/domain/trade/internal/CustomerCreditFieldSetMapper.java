package com.github.khshourov.batchpractices.domain.trade.internal;

import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import java.util.Objects;
import javax.annotation.Nullable;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class CustomerCreditFieldSetMapper implements FieldSetMapper<Object> {
  private static final int ID_INDEX = 0;
  private static final int NAME_INDEX = 1;
  private static final int CREDIT_INDEX = 2;

  @Override
  @Nullable public Object mapFieldSet(@Nullable FieldSet fieldSet) throws BindException {
    Objects.requireNonNull(fieldSet);

    CustomerCredit customerCredit = new CustomerCredit();
    customerCredit.setId(fieldSet.readInt(ID_INDEX));
    customerCredit.setName(fieldSet.readString(NAME_INDEX));
    customerCredit.setCredit(fieldSet.readBigDecimal(CREDIT_INDEX));
    return customerCredit;
  }
}
