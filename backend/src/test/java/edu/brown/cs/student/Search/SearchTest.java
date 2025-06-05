package edu.brown.cs.student.Search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.brown.cs.student.main.CSV.Parse.CSVParser;
import edu.brown.cs.student.main.CSV.Search.CSVSearcher;
import edu.brown.cs.student.main.Creator.CustomObjects.Dog;
import edu.brown.cs.student.main.Creator.DogCreator;
import edu.brown.cs.student.main.Creator.ListOfStringCreator;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SearchTest {

  /**
   * Tests generic search.
   *
   * @throws IOException
   */
  @Test
  public void TestNormalSearch() throws IOException {
    Reader reader1 = new FileReader("data/dogs.csv");
    CSVParser parser1 = new CSVParser(reader1, new ListOfStringCreator());
    List<List<String>> parsedList1 = parser1.parse();
    CSVSearcher searcher1 = new CSVSearcher(parsedList1, parser1.getHeader());

    List<List<String>> list1 = List.of(List.of("poodle", "germany", "large", "yes", "high"));
    assertEquals(searcher1.searchT("poodle"), list1);

    List<List<String>> list2 = new ArrayList<>();
    List<String> item1 = List.of("german shepherd", "germany", "large", "no", "high");
    List<String> item2 = List.of("poodle", "germany", "large", "yes", "high");
    list2.add(item1);
    list2.add(item2);
    assertEquals(searcher1.searchT("germany"), list2);
  }

  /**
   * Tests search with a column specified by user
   *
   * @throws IOException
   */
  @Test
  public void TestSearchColumn() throws IOException {
    List<List<String>> emptyList = new ArrayList<>();
    Reader reader1 = new FileReader("data/newStar.csv");
    CSVParser parser1 = new CSVParser(reader1, new ListOfStringCreator());
    List<List<String>> parsedList1 = parser1.parse();
    CSVSearcher searcher1 = new CSVSearcher(parsedList1, parser1.getHeader());

    List<List<String>> list1 =
        List.of(List.of("70667", "Proxima Centauri", "-0.47175", "-0.36132", "-1.15037"));
    assertEquals(searcher1.searchC("Proxima", "1"), list1);
    assertEquals(searcher1.searchC("Proxima", "ProperName"), list1);

    List<List<String>> list2 = new ArrayList<>();
    List<String> item1 = List.of("71454", "Rigel Kentaurus B", "-0.50359", "-0.42128", "-1.1767");
    List<String> item2 = List.of("71457", "Rigel Kentaurus A", "-0.50362", "-0.42139", "-1.17665");
    list2.add(item1);
    list2.add(item2);
    assertEquals(searcher1.searchC("Kentaurus", "1"), list2);
    assertEquals(searcher1.searchC("Kentaurus", "ProperName"), list2);

    // Wrong column search should return an empty list
    assertEquals(searcher1.searchC("Proxima", "2"), emptyList);
    assertEquals(searcher1.searchC("Proxima", "StarID"), emptyList);
    assertEquals(searcher1.searchC("Kentaurus", "2"), emptyList);
    assertEquals(searcher1.searchC("Kentaurus", "StarID"), emptyList);
  }

  /**
   * Tests search on an empty file, if the search doesn't turn up a res, and target is null.
   *
   * @throws IOException
   */
  @Test
  public void TestEmptyOrInvalidSearch() throws IOException {
    List<List<String>> emptyList = new ArrayList<>();

    // Target isn't there
    Reader reader1 = new FileReader("data/tenStar.csv");
    CSVParser parser1 = new CSVParser(reader1, new ListOfStringCreator());
    List<List<String>> parsedList1 = parser1.parse();
    CSVSearcher searcher1 = new CSVSearcher(parsedList1, parser1.getHeader());
    assertEquals(searcher1.searchT("Frank"), emptyList);
    assertEquals(searcher1.searchC("Frank", "1"), emptyList);

    // CSV is empty
    Reader reader2 = new FileReader("data/empty.csv");
    CSVParser parser2 = new CSVParser(reader2, new ListOfStringCreator());
    List<List<String>> parsedList2 = parser2.parse();
    CSVSearcher searcher2 = new CSVSearcher(parsedList2, parser1.getHeader());
    assertEquals(searcher2.searchT("Frank"), emptyList);
    assertEquals(searcher2.searchC("Frank", "1"), emptyList);
    // Target is null
    assertThrows(IllegalArgumentException.class, () -> searcher2.searchT(null));
  }

  // Test with custom DogCreator class
  @Test
  public void testDogCreator() throws FileNotFoundException {
    Reader reader = new FileReader("data/dogs.csv");
    DogCreator dogC = new DogCreator();
    CSVParser<Dog> DogCreator = new CSVParser<>(reader, dogC);

    // expected to create 5 dogs
    assertEquals(DogCreator.parse().size(), 5);
  }
}
