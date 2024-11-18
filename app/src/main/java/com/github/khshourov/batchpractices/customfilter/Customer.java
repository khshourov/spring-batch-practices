package com.github.khshourov.batchpractices.customfilter;

public record Customer(String name, double credit) {

  @Override
  public int hashCode() {
    final int Prime = 31;
    int result = 1;
    result = Prime * result + Double.hashCode(credit);
    result = Prime * result + ((name == null) ? 0 : name.hashCode());
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
    final Customer other = (Customer) obj;
    if (Double.doubleToLongBits(credit) != Double.doubleToLongBits(other.credit)) {
      return false;
    }
    if (name == null) {
      return other.name == null;
    } else {
      return name.equals(other.name);
    }
  }
}
