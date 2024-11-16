package com.github.khshourov.batchpractices.domain.trade;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "CUSTOMER")
public class CustomerCredit {

  @Id private int id;

  private String name;

  private BigDecimal credit;

  public CustomerCredit() {}

  public CustomerCredit(int id, String name, BigDecimal credit) {
    this.id = id;
    this.name = name;
    this.credit = credit;
  }

  @Override
  public String toString() {
    return "CustomerCredit [id=" + id + ",name=" + name + ", credit=" + credit + "]";
  }

  public BigDecimal getCredit() {
    return credit;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setCredit(BigDecimal credit) {
    this.credit = credit;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CustomerCredit increaseCreditBy(BigDecimal sum) {
    CustomerCredit newCredit = new CustomerCredit();
    newCredit.credit = this.credit.add(sum);
    newCredit.name = this.name;
    newCredit.id = this.id;
    return newCredit;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof CustomerCredit) && ((CustomerCredit) o).id == id;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
