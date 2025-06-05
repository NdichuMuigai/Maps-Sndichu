package edu.brown.cs.student.main.CSV.Parse;

import java.util.List;

/**
 * A new class added for Sprint Project2. Storing parsed CSV from the parser was necessary for the
 * Project2 sprint. Here we have created a simple ParsedCSV class that stores CSV file's header and
 * body content data information. This will be used for view, load_file, and Search Handlers.
 */
public class ParsedCSV<T> {

  private List<String> header;
  private final List<List<String>> body;

  /**
   * Constructor for the ParsedCSV class.
   *
   * @param bd - list of lists of strings representing the body portion of the csv file
   */
  public ParsedCSV(List<List<String>> bd) {
    this.body = bd;
  }

  /**
   * Second constructor for the ParsedCSV class for csv files with headers.
   *
   * @param bd - list of lists of strings representing the body portion of the csv file
   * @param hd - list of strings representing the header row
   */
  public ParsedCSV(List<String> hd, List<List<String>> bd) {
    this.header = hd;
    this.body = bd;
  }

  /**
   * A getter method returning just the header of the file.
   *
   * @return list of String (header)
   */
  public List<String> getHeader() {
    return this.header;
  }

  /**
   * A getter method returning just the body portion of the file.
   *
   * @return list of lists of strings representing the main contents of the csv file
   */
  public List<List<String>> getBody() {
    return this.body;
  }
}
