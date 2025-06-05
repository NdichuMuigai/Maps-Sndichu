package edu.brown.cs.student.main.Exceptions;

/**
 * Exception class for situations where the specified string-based column ID is not present in the
 * csv file being processed by the Search class.
 */
public class HeaderNotFoundException extends Exception {

  /** Exception when header input is not in the existing file. */
  public HeaderNotFoundException(String message) {
    super(message);
  }
}
