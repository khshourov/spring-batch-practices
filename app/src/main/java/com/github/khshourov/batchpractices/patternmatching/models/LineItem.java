package com.github.khshourov.batchpractices.patternmatching.models;

import java.math.BigDecimal;

public class LineItem {

  public static final String LINE_ID_ITEM = "LIT";

  private long itemId;

  private BigDecimal price;

  private BigDecimal discountPerc;

  private BigDecimal discountAmount;

  private BigDecimal shippingPrice;

  private BigDecimal handlingPrice;

  private int quantity;

  private BigDecimal totalPrice;

  public BigDecimal getDiscountAmount() {
    return discountAmount;
  }

  public void setDiscountAmount(BigDecimal discountAmount) {
    this.discountAmount = discountAmount;
  }

  public BigDecimal getDiscountPerc() {
    return discountPerc;
  }

  public void setDiscountPerc(BigDecimal discountPerc) {
    this.discountPerc = discountPerc;
  }

  public BigDecimal getHandlingPrice() {
    return handlingPrice;
  }

  public void setHandlingPrice(BigDecimal handlingPrice) {
    this.handlingPrice = handlingPrice;
  }

  public long getItemId() {
    return itemId;
  }

  public void setItemId(long itemId) {
    this.itemId = itemId;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public BigDecimal getShippingPrice() {
    return shippingPrice;
  }

  public void setShippingPrice(BigDecimal shippingPrice) {
    this.shippingPrice = shippingPrice;
  }

  public BigDecimal getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(BigDecimal totalPrice) {
    this.totalPrice = totalPrice;
  }

  @Override
  public String toString() {
    return "LineItem [price="
        + price
        + ", quantity="
        + quantity
        + ", totalPrice="
        + totalPrice
        + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Long.hashCode(itemId);
    result = prime * result + quantity;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }

    if (getClass() != obj.getClass()) {
      return false;
    }

    LineItem other = (LineItem) obj;

    if (itemId != other.itemId) {
      return false;
    }

    return quantity == other.quantity;
  }
}
