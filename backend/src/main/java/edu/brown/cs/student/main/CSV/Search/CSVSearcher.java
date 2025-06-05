package edu.brown.cs.student.main.CSV.Search;

import java.util.ArrayList;
import java.util.List;

/** Class that handles searching through CSV files. */
public class CSVSearcher {

  private final List<List<String>> parsedList;
  private final List<String> headers;

  /**
   * Constructor for CSVSearcher.
   *
   * @param parsedList the parsed CSV data
   */
  public CSVSearcher(List<List<String>> parsedList, List<String> headers) {

    this.parsedList = parsedList;
    this.headers = headers;
  }

  /**
   * Search for a target string without specifying a column.
   *
   * @param target the string to Search for
   * @return a list of rows containing the target string
   */
  public List<List<String>> searchT(String target) {
    if (target == null) {
      throw new IllegalArgumentException("Target cannot be null.");
    }

    List<List<String>> res = new ArrayList<>();

    for (List<String> row : parsedList) {
      for (String cell : row) {
        if (cell.contains(target)) {
          res.add(row);
          break;
        }
      }
    }

    if (res.isEmpty()) {
      System.out.println("No valid res found.");
    }

    System.out.println(res);
    return res;
  }

  /**
   * Search for a target string in a specific column.
   *
   * @param target the string to Search for <<<<<<< HEAD
   * @param col the column (number or header) to Search =======
   * @param col the column (number or header) to Search >>>>>>>
   *     169f6cfbd60f9ae2dc0e24d4622d600633061077
   * @return a list of rows containing the target string in the specified column
   */
  public List<List<String>> searchC(String target, String col) {
    List<List<String>> res = new ArrayList<>();

    for (List<String> row : parsedList) {
      // If the input is a number
      if (isNumeric(col)) {
        int columnNum = Integer.parseInt(col);
        if (columnNum >= 0 && columnNum < row.size() && row.get(columnNum).contains(target)) {
          res.add(row);
        }
      } else { // If the input is a column name
        int columnHeaderIndex = findColumnHeaderIndex(col);
        if (columnHeaderIndex != -1
            && columnHeaderIndex < row.size()
            && row.get(columnHeaderIndex).contains(target)) {
          res.add(row);
        }
      }
    }

    if (res.isEmpty()) {
      System.out.println("No valid res found.");
    }

    System.out.println(res);
    return res;
  }

  /**
   * Check if a string is numeric.
   *
   * @param s the string to check
   * @return true if the string is numeric, false otherwise
   */
  private boolean isNumeric(String s) {
    try {
      Integer.parseInt(s);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * Find the index of a column header in the header row.
   *
   * @param col the column header to Search for
   * @return the index of the column header, or -1 if not found
   */
  private int findColumnHeaderIndex(String col) {
    for (int i = 0; i < headers.size(); i++) {
      if (headers.get(i).equals(col)) {
        return i;
      }
    }

    return -1;
  }
}
