package com.github.khshourov.batchpractices.football.playersummary;

import java.io.Serializable;

public class PlayerSummary implements Serializable {
  private String id;
  private int year;
  private int completes;
  private int attempts;
  private int passingYards;
  private int passingTd;
  private int interceptions;
  private int rushes;
  private int rushYards;
  private int receptions;
  private int receptionYards;
  private int totalTd;

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getYear() {
    return this.year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public int getCompletes() {
    return this.completes;
  }

  public void setCompletes(int completes) {
    this.completes = completes;
  }

  public int getAttempts() {
    return this.attempts;
  }

  public void setAttempts(int attempts) {
    this.attempts = attempts;
  }

  public int getPassingYards() {
    return this.passingYards;
  }

  public void setPassingYards(int passingYards) {
    this.passingYards = passingYards;
  }

  public int getPassingTd() {
    return this.passingTd;
  }

  public void setPassingTd(int passingTd) {
    this.passingTd = passingTd;
  }

  public int getInterceptions() {
    return this.interceptions;
  }

  public void setInterceptions(int interceptions) {
    this.interceptions = interceptions;
  }

  public int getRushes() {
    return this.rushes;
  }

  public void setRushes(int rushes) {
    this.rushes = rushes;
  }

  public int getRushYards() {
    return this.rushYards;
  }

  public void setRushYards(int rushYards) {
    this.rushYards = rushYards;
  }

  public int getReceptions() {
    return this.receptions;
  }

  public void setReceptions(int receptions) {
    this.receptions = receptions;
  }

  public int getReceptionYards() {
    return this.receptionYards;
  }

  public void setReceptionYards(int receptionYards) {
    this.receptionYards = receptionYards;
  }

  public int getTotalTd() {
    return this.totalTd;
  }

  public void setTotalTd(int totalTd) {
    this.totalTd = totalTd;
  }

  @Override
  public String toString() {
    return "Player Summary: ID="
        + id
        + " Year="
        + year
        + "["
        + completes
        + ";"
        + attempts
        + ";"
        + passingYards
        + ";"
        + passingTd
        + ";"
        + interceptions
        + ";"
        + rushes
        + ";"
        + rushYards
        + ";"
        + receptions
        + ";"
        + receptionYards
        + ";"
        + totalTd;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    PlayerSummary other = (PlayerSummary) obj;
    if (id == null) {
      return other.id == null;
    } else {
      return id.equals(other.id);
    }
  }
}
