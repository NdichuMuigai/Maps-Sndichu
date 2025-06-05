package edu.brown.cs.student.Parse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.brown.cs.student.main.CSV.Parse.CSVParser;
import edu.brown.cs.student.main.Creator.CustomObjects.Dog;
import edu.brown.cs.student.main.Creator.ListOfStringCreator;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests the CSV parsing implementation on various User Stories. */
public class ParseTest {

  /**
   * Tests CSV parsing on a basic case (User Story 1).
   *
   * @throws FileNotFoundException
   */
  @Test
  public void testBasicCSVParse() throws FileNotFoundException {
    try {
      FileReader reader = new FileReader("data/simple1.csv");
      CSVParser<List<String>> parser = new CSVParser<>(reader, new ListOfStringCreator());

      // Parse the CSV data
      List<List<String>> parsedList = parser.parse();

      // Define the expected test data
      List<List<String>> list = new ArrayList<>();
      list.add(List.of("Frank", "2025", "CS-Econ"));
      list.add(List.of("Emil", "2025", "CS"));
      list.add(List.of("Jakobi", "2025", "CS"));
      list.add(List.of("Matthew", "2025", "History-IAPA"));

      // Compare the parsed data with the expected data
      for (int r = 0; r < list.size() && r < parsedList.size(); r++) {
        for (int c = 0; c < list.get(0).size(); c++) {
          assertEquals(list.get(r).get(c), parsedList.get(r).get(c));
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println(e);
    }
  }

  /**
   * Tests CSV parsing on an empty file.
   *
   * @throws FileNotFoundException
   */
  @Test
  public void testEmptyCSVParse() throws FileNotFoundException {
    FileReader reader = new FileReader("data/empty.csv");
    CSVParser<List<String>> parser = new CSVParser<>(reader, new ListOfStringCreator());
    List<List<String>> parsedList = parser.parse();
    List<List<String>> emptyList = new ArrayList<>();
    assertEquals(parsedList, emptyList);
  }

  /** Tests CSV parsing with a different type of reader. */
  @Test
  public void testCSVParseWithDifferentReader() {
    StringReader reader = new StringReader("Name,Year,Major\n" + "Frank,2025,CS-Econ");
    CSVParser<List<String>> parser = new CSVParser<>(reader, new ListOfStringCreator());
    List<List<String>> parsedList = parser.parse();
    ArrayList<Object> list = new ArrayList<>();
    list.add(Arrays.asList("Frank", "2025", "CS-Econ"));
    assertEquals(parsedList, list);
  }

  /**
   * Tests CSV parsing with custom objects.
   *
   * @throws FileNotFoundException
   */
  @Test
  public void testNewObject() throws FileNotFoundException {
    FileReader reader = new FileReader("data/dogs.csv");
    CSVParser parser = new CSVParser(reader, new ListOfStringCreator());
    List<Dog> list = parser.parse();
    System.out.println(list.toString());
  }
}
