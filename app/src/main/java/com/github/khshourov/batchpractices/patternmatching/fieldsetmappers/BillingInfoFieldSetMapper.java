package com.github.khshourov.batchpractices.patternmatching.fieldsetmappers;

import com.github.khshourov.batchpractices.patternmatching.models.BillingInfo;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class BillingInfoFieldSetMapper implements FieldSetMapper<BillingInfo> {
  private static final String PAYMENT_TYPE_ID_COLUMN = "PAYMENT_TYPE_ID";
  private static final String PAYMENT_DESC_COLUMN = "PAYMENT_DESC";

  @Override
  public BillingInfo mapFieldSet(FieldSet fieldSet) throws BindException {
    BillingInfo info = new BillingInfo();

    info.setPaymentId(fieldSet.readString(PAYMENT_TYPE_ID_COLUMN));
    info.setPaymentDesc(fieldSet.readString(PAYMENT_DESC_COLUMN));

    return info;
  }
}
