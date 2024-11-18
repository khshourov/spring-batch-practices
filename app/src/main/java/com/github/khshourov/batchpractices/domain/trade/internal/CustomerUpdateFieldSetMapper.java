package com.github.khshourov.batchpractices.domain.trade.internal;

import java.math.BigDecimal;
import javax.annotation.Nullable;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class CustomerUpdateFieldSetMapper implements FieldSetMapper<CustomerUpdate> {

  @Override
  @Nullable public CustomerUpdate mapFieldSet(@Nullable FieldSet fs) {
    if (fs == null) {
      return null;
    }

    CustomerOperation operation = CustomerOperation.fromCode(fs.readString(0).charAt(0));
    String name = fs.readString(1);
    BigDecimal credit = fs.readBigDecimal(2);

    return new CustomerUpdate(operation, name, credit);
  }
}
