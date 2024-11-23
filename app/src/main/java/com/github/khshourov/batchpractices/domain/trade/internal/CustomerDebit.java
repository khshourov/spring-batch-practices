package com.github.khshourov.batchpractices.domain.trade.internal;

import java.math.BigDecimal;

public class CustomerDebit {

  private String name;

  private BigDecimal debit;

  public CustomerDebit() {}

  CustomerDebit(String name, BigDecimal debit) {
    this.name = name;
    this.debit = debit;
  }

  public BigDecimal getDebit() {
    return debit;
  }

  public void setDebit(BigDecimal debit) {
    this.debit = debit;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "CustomerDebit [name=" + name + ", debit=" + debit + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((debit == null) ? 0 : debit.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    CustomerDebit other = (CustomerDebit) obj;
    if (debit == null) {
      if (other.debit != null) {
        return false;
      }
    } else if (!debit.equals(other.debit)) {
      return false;
    }
    if (name == null) {
      return other.name == null;
    } else {
      return name.equals(other.name);
    }
  }
}
