package edu.brown.cs.student.main.Creator;

import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.util.ArrayList;
import java.util.List;

public class ListOfStringCreator implements CreatorFromRow<List<String>> {

  /**
   * Creates a List<String> from a given CSV row represented as a List<String>.
   *
   * @param row List<String> representing a row from a CSV file
   * @return new List<String> where each element corresponds to a column in the CSV row
   */
  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    if (row == null) {
      String msg = "Row is null";
      throw new FactoryFailureException(msg, row);
    }

    List<String> newList = new ArrayList<>();
    for (String column : row) {
      // For example purposes, directly adding the column.
      // You can perform any modifications you like here.
      newList.add(column);
    }

    return newList;
  }
}
