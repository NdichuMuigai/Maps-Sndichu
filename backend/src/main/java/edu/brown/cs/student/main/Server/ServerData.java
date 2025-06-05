package edu.brown.cs.student.main.Server;

import com.google.gson.JsonObject;
import edu.brown.cs.student.main.CSV.Parse.ParsedCSV;
import java.util.List;

/** Wrapper class for the Project2's data. This class is used to store the parsed CSV data. */
public class ServerData {

  public ParsedCSV<List<String>> parsedCSV;
  public JsonObject geojsonData;

  /** Clears the parsed CSV data. */
  public void clearCSV() {

    this.parsedCSV = null;
    this.geojsonData = null;
  }
}
