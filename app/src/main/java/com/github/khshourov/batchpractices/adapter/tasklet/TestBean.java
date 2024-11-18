package com.github.khshourov.batchpractices.adapter.tasklet;

public class TestBean {

  private String value;

  public void setValue(String value) {
    this.value = value;
  }

  public void execute(String strValue, Integer integerValue, double doubleValue) {
    assert this.value.equals("foo");
    assert strValue.equals("foo2");
    assert integerValue == 3;
    assert doubleValue == 3.14;
  }
}
