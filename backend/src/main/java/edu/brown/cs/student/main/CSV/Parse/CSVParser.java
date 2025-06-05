package edu.brown.cs.student.main.CSV.Parse;

import edu.brown.cs.student.main.Creator.CreatorFromRow;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A CSVParser for parsing CSV data into a list of generic objects of type T.
 *
 * @param <T> - the generic type T
 */
public class CSVParser<T> {

  // BufferedReader to read from the input source
  private final BufferedReader br;

  // CreatorFromRow object to create objects of type T from a row of CSV data
  private final CreatorFromRow<T> type;
  private List<String> header;

  /**
   * Constructor for CSVParser.
   *
   * @param reader - the input Reader
   * @param type - the CreatorFromRow implementation for creating objects of type T
   */
  public CSVParser(Reader reader, CreatorFromRow<T> type) {
    this.br = new BufferedReader(reader);
    this.type = type;
  }

  /**
   * Parse the CSV data and return a list of objects of type T.
   *
   * @return list of objects of type T
   */
  public List<T> parse() {
    List<T> res = new ArrayList<>();

    try {
      String line;
      boolean isHeader = true; // Use this flag to identify the header row
      while ((line = br.readLine()) != null) {
        // Split the line into columns using commas as the delimiter
        String[] col = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
        List<String> parsedCol = new ArrayList<>();

        // Iterate through the columns and add non-empty values to the parsedCol list
        for (String s : col) {
          if (!s.isEmpty()) {
            parsedCol.add(s);
          }
        }

        if (isHeader) {
          header = parsedCol; // Set headers here
          isHeader = false; // Set flag to false after processing header row
        } else {
          // Create an object of type T from the parsed column data and add it to the res list
          res.add(this.type.create(parsedCol));
        }
      }
    } catch (IOException e) {
      // Handle exceptions and print them
      System.err.println(e);
    } catch (FactoryFailureException e) {
      throw new RuntimeException(e);
    }

    return res;
  }

  public List<String> getHeader() {
    return this.header;
  }
}
