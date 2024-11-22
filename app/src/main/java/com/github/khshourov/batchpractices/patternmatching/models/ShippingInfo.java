package com.github.khshourov.batchpractices.patternmatching.models;

public class ShippingInfo {

  public static final String LINE_ID_SHIPPING_INFO = "SIN";

  private String shipperId;

  private String shippingTypeId;

  private String shippingInfo;

  public String getShipperId() {
    return shipperId;
  }

  public void setShipperId(String shipperId) {
    this.shipperId = shipperId;
  }

  public String getShippingInfo() {
    return shippingInfo;
  }

  public void setShippingInfo(String shippingInfo) {
    this.shippingInfo = shippingInfo;
  }

  public String getShippingTypeId() {
    return shippingTypeId;
  }

  public void setShippingTypeId(String shippingTypeId) {
    this.shippingTypeId = shippingTypeId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((shipperId == null) ? 0 : shipperId.hashCode());
    result = prime * result + ((shippingInfo == null) ? 0 : shippingInfo.hashCode());
    result = prime * result + ((shippingTypeId == null) ? 0 : shippingTypeId.hashCode());
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
    ShippingInfo other = (ShippingInfo) obj;
    if (shipperId == null) {
      if (other.shipperId != null) {
        return false;
      }
    } else if (!shipperId.equals(other.shipperId)) {
      return false;
    }
    if (shippingInfo == null) {
      if (other.shippingInfo != null) {
        return false;
      }
    } else if (!shippingInfo.equals(other.shippingInfo)) {
      return false;
    }
    if (shippingTypeId == null) {
      return other.shippingTypeId == null;
    } else {
      return shippingTypeId.equals(other.shippingTypeId);
    }
  }
}
