package com.github.khshourov.batchpractices.patternmatching.fieldsetmappers;

import com.github.khshourov.batchpractices.patternmatching.models.Address;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class AddressFieldSetMapper implements FieldSetMapper<Address> {
  private static final String ADDRESSEE_COLUMN = "ADDRESSEE";
  private static final String ADDRESS_LINE1_COLUMN = "ADDR_LINE1";
  private static final String ADDRESS_LINE2_COLUMN = "ADDR_LINE2";
  private static final String CITY_COLUMN = "CITY";
  private static final String ZIP_CODE_COLUMN = "ZIP_CODE";
  private static final String STATE_COLUMN = "STATE";
  private static final String COUNTRY_COLUMN = "COUNTRY";

  @Override
  public Address mapFieldSet(FieldSet fieldSet) throws BindException {
    Address address = new Address();

    address.setAddressee(fieldSet.readString(ADDRESSEE_COLUMN));
    address.setAddrLine1(fieldSet.readString(ADDRESS_LINE1_COLUMN));
    address.setAddrLine2(fieldSet.readString(ADDRESS_LINE2_COLUMN));
    address.setCity(fieldSet.readString(CITY_COLUMN));
    address.setZipCode(fieldSet.readString(ZIP_CODE_COLUMN));
    address.setState(fieldSet.readString(STATE_COLUMN));
    address.setCountry(fieldSet.readString(COUNTRY_COLUMN));

    return address;
  }
}
