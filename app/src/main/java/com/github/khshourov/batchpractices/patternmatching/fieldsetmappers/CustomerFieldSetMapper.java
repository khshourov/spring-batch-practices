package com.github.khshourov.batchpractices.patternmatching.fieldsetmappers;

import com.github.khshourov.batchpractices.patternmatching.models.Customer;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class CustomerFieldSetMapper implements FieldSetMapper<Customer> {
  private static final String LINE_ID_COLUMN = "LINE_ID";
  private static final String COMPANY_NAME_COLUMN = "COMPANY_NAME";
  private static final String LAST_NAME_COLUMN = "LAST_NAME";
  private static final String FIRST_NAME_COLUMN = "FIRST_NAME";
  private static final String MIDDLE_NAME_COLUMN = "MIDDLE_NAME";
  private static final String TRUE_SYMBOL = "T";
  private static final String REGISTERED_COLUMN = "REGISTERED";
  private static final String REG_ID_COLUMN = "REG_ID";
  private static final String VIP_COLUMN = "VIP";

  @Override
  public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
    Customer customer = new Customer();

    if (Customer.LINE_ID_BUSINESS_CUST.equals(fieldSet.readString(LINE_ID_COLUMN))) {
      customer.setCompanyName(fieldSet.readString(COMPANY_NAME_COLUMN));
      // business customer must be always registered
      customer.setRegistered(true);
    }

    if (Customer.LINE_ID_NON_BUSINESS_CUST.equals(fieldSet.readString(LINE_ID_COLUMN))) {
      customer.setLastName(fieldSet.readString(LAST_NAME_COLUMN));
      customer.setFirstName(fieldSet.readString(FIRST_NAME_COLUMN));
      customer.setMiddleName(fieldSet.readString(MIDDLE_NAME_COLUMN));
      customer.setRegistered(TRUE_SYMBOL.equals(fieldSet.readString(REGISTERED_COLUMN)));
    }

    customer.setRegistrationId(fieldSet.readLong(REG_ID_COLUMN));
    customer.setVip(TRUE_SYMBOL.equals(fieldSet.readString(VIP_COLUMN)));

    return customer;
  }
}
