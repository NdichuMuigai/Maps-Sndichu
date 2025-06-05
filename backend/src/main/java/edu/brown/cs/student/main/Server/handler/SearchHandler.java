package edu.brown.cs.student.main.Server.handler;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.CSV.Search.CSVSearcher;
import edu.brown.cs.student.main.Server.ServerData;
import java.util.List;
import java.util.Map;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler for the searchcsv API endpoint. Searches a loaded csv file for a specified keyword. Can
 * search across all columns or, if a column id or header is provided, returns the relevant search
 * result from the Search class.
 */
public class SearchHandler implements Route {

  private final ServerData data;

  /**
   * Constructor takes data as shared state.
   *
   * @param data the result from Search in Project2 Data.
   */
  public SearchHandler(ServerData data) {
    this.data = data;
  }

  /**
   * @param response used to alter response properties
   * @return content of the response
   * @throws Exception Part of the interface; no obligation to throw anything
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    List<List<String>> searchResults;
    CreateResponse myResponse = new CreateResponse();
    Map<String, Object> map;

    if (this.data.parsedCSV == null) {
      return myResponse.serializer(
          myResponse.makeFailureHm(
              "error_datasource", "No file was loaded before. Use loadcsv endpoint."));
    }
    QueryParamsMap qm = request.queryMap();
    String val = qm.value("query");
    if (this.data.parsedCSV.getBody().isEmpty()) {
      // succeed, return empty array
      map = myResponse.makeSuccessHm("data", this.data.parsedCSV.getBody());
      map.put("query", val);
      map.put("message", "CSV file is empty");
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(Map.class).toJson(map);
    } else {
      CSVSearcher searcher =
          new CSVSearcher(this.data.parsedCSV.getBody(), this.data.parsedCSV.getHeader());

      // checking for invalid input queries
      if (request.queryParams("by") == null) {
        return myResponse.serializer(
            myResponse.makeFailureHm("error_bad_request", "Missing the query or column"));
      }

      // checking for invalid input queries, and handling search
      try {
        String col = qm.value("column");
        switch (request.queryParams("by")) {
          case "all" -> {
            if (request.queryParams("query") == null) {
              return myResponse.serializer(
                  myResponse.makeFailureHm("error_datasource", "\"query\" field missing"));
            }

            searchResults = searcher.searchT(request.queryParams("query"));
            map = myResponse.makeSuccessHm("data", searchResults);
            map.put("query", val);
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(Map.class).toJson(map);
          }
          case "column" -> {
            if (request.queryParams("query") == null) {
              return myResponse.serializer(
                  myResponse.makeFailureHm("error_datasource", "\"query\" field missing"));
            }
            if (request.queryParams("column") == null) {
              return myResponse.serializer(
                  myResponse.makeFailureHm("error_datasource", "\"column\" field missing"));
            }
            searchResults =
                searcher.searchC(request.queryParams("query"), request.queryParams("column"));
            if (searchResults.isEmpty()) {
              map = myResponse.makeSuccessHm("data", searchResults);
              map.put("message", "Could not find query " + val + " in column " + col);
              map.put("query", val);
              map.put("column", col);
              Moshi moshi = new Moshi.Builder().build();
              return moshi.adapter(Map.class).toJson(map);
            }
            map = myResponse.makeSuccessHm("data", searchResults);
            map.put("query", val);
            map.put("column", col);
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(Map.class).toJson(map);
          }
          default -> {
            return myResponse.serializer(
                myResponse.makeFailureHm("error_bad_request", "invalid input"));
          }
        }
      } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
        return myResponse.serializer(myResponse.makeFailureHm("error_bad_request", e.getMessage()));
      }
    }
  }
}
