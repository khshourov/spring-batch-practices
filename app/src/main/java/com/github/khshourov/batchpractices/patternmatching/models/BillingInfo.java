package com.github.khshourov.batchpractices.patternmatching.models;

public class BillingInfo {

  public static final String LINE_ID_BILLING_INFO = "BIN";

  private String paymentId;

  private String paymentDesc;

  public String getPaymentDesc() {
    return paymentDesc;
  }

  public void setPaymentDesc(String paymentDesc) {
    this.paymentDesc = paymentDesc;
  }

  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  @Override
  public String toString() {
    return "BillingInfo [paymentDesc=" + paymentDesc + ", paymentId=" + paymentId + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((paymentDesc == null) ? 0 : paymentDesc.hashCode());
    result = prime * result + ((paymentId == null) ? 0 : paymentId.hashCode());
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
    BillingInfo other = (BillingInfo) obj;
    if (paymentDesc == null) {
      if (other.paymentDesc != null) {
        return false;
      }
    } else if (!paymentDesc.equals(other.paymentDesc)) {
      return false;
    }
    if (paymentId == null) {
      return other.paymentId == null;
    } else {
      return paymentId.equals(other.paymentId);
    }
  }
}
