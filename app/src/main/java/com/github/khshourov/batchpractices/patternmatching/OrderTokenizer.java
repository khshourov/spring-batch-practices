package com.github.khshourov.batchpractices.patternmatching;

import java.util.Map;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderTokenizer {
  private static final String DELIMITER = ";";

  @Bean
  public PatternMatchingCompositeLineTokenizer orderTokenizer() {
    PatternMatchingCompositeLineTokenizer orderTokenizer =
        new PatternMatchingCompositeLineTokenizer();

    orderTokenizer.setTokenizers(
        Map.of(
            "HEA*", getTokenizer(new String[] {"LINE_ID", "ORDER_ID", "ORDER_DATE"}),
            "FOT*",
                getTokenizer(
                    new String[] {"LINE_ID", "TOTAL_LINE_ITEMS", "TOTAL_ITEMS", "TOTAL_PRICE"}),
            "BCU*", getTokenizer(new String[] {"LINE_ID", "COMPANY_NAME", "REG_ID", "VIP"}),
            "NCU*",
                getTokenizer(
                    new String[] {
                      "LINE_ID",
                      "LAST_NAME",
                      "FIRST_NAME",
                      "MIDDLE_NAME",
                      "REGISTERED",
                      "REG_ID",
                      "VIP"
                    }),
            "BAD*",
                getTokenizer(
                    new String[] {
                      "LINE_ID",
                      "ADDRESSEE",
                      "ADDR_LINE1",
                      "ADDR_LINE2",
                      "CITY",
                      "ZIP_CODE",
                      "STATE",
                      "COUNTRY"
                    }),
            "SAD*",
                getTokenizer(
                    new String[] {
                      "LINE_ID",
                      "ADDRESSEE",
                      "ADDR_LINE1",
                      "ADDR_LINE2",
                      "CITY",
                      "ZIP_CODE",
                      "STATE",
                      "COUNTRY"
                    }),
            "BIN*", getTokenizer(new String[] {"LINE_ID", "PAYMENT_TYPE_ID", "PAYMENT_DESC"}),
            "SIN*",
                getTokenizer(
                    new String[] {
                      "LINE_ID", "SHIPPER_ID", "SHIPPING_TYPE_ID", "ADDITIONAL_SHIPPING_INFO"
                    }),
            "LIT*",
                getTokenizer(
                    new String[] {
                      "LINE_ID",
                      "ITEM_ID",
                      "PRICE",
                      "DISCOUNT_PERC",
                      "DISCOUNT_AMOUNT",
                      "SHIPPING_PRICE",
                      "HANDLING_PRICE",
                      "QUANTITY",
                      "TOTAL_PRICE"
                    }),
            "*", new DelimitedLineTokenizer()));

    return orderTokenizer;
  }

  private DelimitedLineTokenizer getTokenizer(String[] names) {
    DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(DELIMITER);
    tokenizer.setNames(names);
    return tokenizer;
  }
}
