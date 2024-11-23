package com.github.khshourov.batchpractices.football.player;

import java.io.Serializable;

public class Player implements Serializable {
  private String id;
  private String lastName;
  private String firstName;
  private String position;
  private int birthYear;
  private int debutYear;

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLastName() {
    return this.lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getPosition() {
    return this.position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public int getBirthYear() {
    return this.birthYear;
  }

  public void setBirthYear(int birthYear) {
    this.birthYear = birthYear;
  }

  public int getDebutYear() {
    return this.debutYear;
  }

  public void setDebutYear(int debutYear) {
    this.debutYear = debutYear;
  }

  @Override
  public String toString() {
    return "PLAYER:id="
        + id
        + ",Last Name="
        + lastName
        + ",First Name="
        + firstName
        + ",Position="
        + position
        + ",Birth Year="
        + birthYear
        + ",DebutYear="
        + debutYear;
  }
}
