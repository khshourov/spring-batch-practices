package com.github.khshourov.batchpractices.patternmatching.extractors;

import com.github.khshourov.batchpractices.patternmatching.models.LineItem;
import org.springframework.batch.item.file.transform.FieldExtractor;

public class LineItemExtractor implements FieldExtractor<LineItem> {
  @Override
  public Object[] extract(LineItem item) {
    return new Object[] {"ITEM:", item.getItemId(), item.getPrice()};
  }
}
