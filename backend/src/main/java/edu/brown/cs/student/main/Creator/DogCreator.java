package edu.brown.cs.student.main.Creator;

import edu.brown.cs.student.main.Creator.CustomObjects.Dog;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.util.List;

/** DogCreator class implementing CreatorFromRow interface to create Dog objects. */
public class DogCreator implements CreatorFromRow<Dog> {

  /** Constructor for DogCreator. */
  public DogCreator() {}

  /**
   * Creates a Dog object from the provided row of data.
   *
   * @param row - list of strings representing a row of data
   * @return Dog object created from the row input
   * @throws FactoryFailureException if fails to create objects from the input
   */
  @Override
  public Dog create(List<String> row) throws FactoryFailureException {
    Dog dog = null;
    boolean is_friendly = false;

    if (row.size() == 5) {
      String friendly = row.get(3);
      if (friendly.equalsIgnoreCase("yes")) {
        is_friendly = true;
      }
      dog = new Dog(row.get(0), row.get(1), row.get(2), is_friendly, row.get(4));
    } else {
      System.err.println("Please enter a valid file corresponding to your CreatorFromRow class.");
    }
    return dog;
  }
}
