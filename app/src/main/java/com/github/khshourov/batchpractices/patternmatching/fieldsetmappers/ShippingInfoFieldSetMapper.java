package com.github.khshourov.batchpractices.patternmatching.fieldsetmappers;

import com.github.khshourov.batchpractices.patternmatching.models.ShippingInfo;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class ShippingInfoFieldSetMapper implements FieldSetMapper<ShippingInfo> {
  private static final String ADDITIONAL_SHIPPING_INFO_COLUMN = "ADDITIONAL_SHIPPING_INFO";
  private static final String SHIPPING_TYPE_ID_COLUMN = "SHIPPING_TYPE_ID";
  private static final String SHIPPER_ID_COLUMN = "SHIPPER_ID";

  @Override
  public ShippingInfo mapFieldSet(FieldSet fieldSet) throws BindException {
    ShippingInfo info = new ShippingInfo();

    info.setShipperId(fieldSet.readString(SHIPPER_ID_COLUMN));
    info.setShippingTypeId(fieldSet.readString(SHIPPING_TYPE_ID_COLUMN));
    info.setShippingInfo(fieldSet.readString(ADDITIONAL_SHIPPING_INFO_COLUMN));

    return info;
  }
}
