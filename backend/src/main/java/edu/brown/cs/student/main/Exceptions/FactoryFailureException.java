package edu.brown.cs.student.main.Exceptions;

import java.util.ArrayList;
import java.util.List;

/** Interface for error-handling for the case when the data type cannot be created from the row. */
public class FactoryFailureException extends Exception {

  final List<String> row;

  public FactoryFailureException(String message, List<String> row) {
    super(message);
    this.row = new ArrayList<>(row);
  }
}
