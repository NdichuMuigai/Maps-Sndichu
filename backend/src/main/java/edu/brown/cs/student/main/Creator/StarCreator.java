package edu.brown.cs.student.main.Creator;

import edu.brown.cs.student.main.Creator.CustomObjects.Star;
import java.util.List;

/** StarCreator class implementing CreatorFromRow interface to create Star objects. */
public class StarCreator implements CreatorFromRow<Star> {

  /** Constructor for StarCreator. */
  public StarCreator() {}

  /**
   * Creates a Star object from the provided row of data.
   *
   * @param row - list of strings representing a row of data
   * @return Star object created from the row input
   */
  @Override
  public Star create(List<String> row) {
    Star star = null;

    if (row.size() == 5) {
      star =
          new Star(
              Integer.parseInt(row.get(0)),
              row.get(1),
              Double.parseDouble(row.get(2)),
              Double.parseDouble(row.get(3)),
              Double.parseDouble(row.get(4)));
    } else {
      System.err.println("Please enter a valid file corresponding to your CreatorFromRow class.");
    }
    return star;
  }
}
