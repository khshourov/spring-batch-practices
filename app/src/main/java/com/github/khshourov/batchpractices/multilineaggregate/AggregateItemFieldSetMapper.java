package com.github.khshourov.batchpractices.multilineaggregate;

import static org.springframework.util.Assert.state;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.validation.BindException;

public class AggregateItemFieldSetMapper<T>
    implements FieldSetMapper<AggregateItem<T>>, InitializingBean {
  private FieldSetMapper<T> delegate;

  @Override
  public AggregateItem<T> mapFieldSet(FieldSet fieldSet) throws BindException {
    if ("BEGIN".equals(fieldSet.readString(0))) {
      return AggregateItem.getHeader();
    } else if ("END".equals(fieldSet.readString(0))) {
      return AggregateItem.getFooter();
    } else {
      return new AggregateItem<>(this.delegate.mapFieldSet(fieldSet));
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    state(this.delegate != null, "A FieldSetMapper delegate must be set");
  }

  public void setDelegate(FieldSetMapper delegate) {
    this.delegate = delegate;
  }
}
