package com.github.khshourov.batchpractices.domain.person;

import com.github.khshourov.batchpractices.patternmatching.models.Address;
import java.util.ArrayList;
import java.util.List;

public class PersonService {

  private static final int GENERATION_LIMIT = 10;

  private int generatedCounter = 0;

  private int processedCounter = 0;

  public Person getData() {
    if (generatedCounter >= GENERATION_LIMIT) {
      return null;
    }

    Person person = new Person();
    Address address = new Address();
    Child child = new Child();
    List<Child> children = new ArrayList<>(1);

    children.add(child);

    person.setFirstName("John" + generatedCounter);
    person.setAge(20 + generatedCounter);
    address.setCity("Johnsville" + generatedCounter);
    child.setName("Little Johny" + generatedCounter);

    person.setAddress(address);
    person.setChildren(children);

    generatedCounter++;

    return person;
  }

  /*
   * Badly designed method signature which accepts multiple implicitly related arguments
   * instead of a single Person argument.
   */
  public void processPerson(String name, String city) {
    processedCounter++;
  }

  public int getReturnedCount() {
    return generatedCounter;
  }

  public int getReceivedCount() {
    return processedCounter;
  }
}
