package com.github.khshourov.batchpractices.multilineaggregate;

public class AggregateItem<T> {
  private static AggregateItem HEADER =
      new AggregateItem<>(true, false) {
        @Override
        public Object getItem() {
          throw new IllegalStateException("Header has no item");
        }
      };

  public static <T> AggregateItem<T> getHeader() {
    return HEADER;
  }

  private static AggregateItem FOOTER =
      new AggregateItem<>(false, true) {
        @Override
        public Object getItem() {
          throw new IllegalStateException("Footer has no item");
        }
      };

  public static <T> AggregateItem<T> getFooter() {
    return FOOTER;
  }

  private final T item;
  private boolean isHeader = false;
  private boolean isFooter = false;

  public AggregateItem(T item) {
    this.item = item;
  }

  private AggregateItem(boolean isHeader, boolean isFooter) {
    this(null);
    this.isHeader = isHeader;
    this.isFooter = isFooter;
  }

  public T getItem() {
    return this.item;
  }

  public boolean isHeader() {
    return this.isHeader;
  }

  public boolean isFooter() {
    return this.isFooter;
  }
}
