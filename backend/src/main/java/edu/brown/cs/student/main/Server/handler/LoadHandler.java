package edu.brown.cs.student.main.Server.handler;

import edu.brown.cs.student.main.CSV.Parse.CSVParser;
import edu.brown.cs.student.main.CSV.Parse.ParsedCSV;
import edu.brown.cs.student.main.Creator.ListOfStringCreator;
import edu.brown.cs.student.main.Server.ServerData;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

/** LoadHandler class for loading the input file. Use with loadcsv endpoint. */
public class LoadHandler implements Route {
  private final ServerData data;

  /**
   * LoadHandler constructor.
   *
   * @param data
   */
  public LoadHandler(ServerData data) {
    this.data = data;
  }

  /**
   * Loads the input file into the server and checks that the input file is valid by seeing if the
   * file exists and if the hasHeader parameter is valid.
   *
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    CreateResponse myResponse = new CreateResponse();
    QueryParamsMap qm = request.queryMap();
    String file = qm.value("file");

    // checking if the file path is valid
    if (request.queryParams("file") == null) {
      return myResponse.serializer(
          myResponse.makeFailureHm(
              "error_bad_request", "Missing filepath query param. i.e. 'filepath=(filepath)'."));
    }
    if (request.queryParams("hasHeader") == null) {
      return myResponse.serializer(
          myResponse.makeFailureHm(
              "error_bad_request",
              "Missing hasHeader query param. i.e. 'hasHeader=(false),(true)"));
    }
    if (!(request.queryParams("hasHeader").equalsIgnoreCase("false"))
        && !(request.queryParams("hasHeader").equalsIgnoreCase("true"))) {
      // System.out.println(request.queryParams("File hasHeader"));
      return myResponse.serializer(
          myResponse.makeFailureHm(
              "error_bad_json",
              "Request was ill-formed. Improper parameter for header. i.e. 'yes' or 'no'"));
    }
    // getting files only requires the file name
    // defensive programming because it isn't possible to access any files that aren't contained
    String filePath = "./data/" + request.queryParams("file");

    boolean hasHeader = Boolean.parseBoolean(request.queryParams("hasHeader").toLowerCase());

    try {

      // reading the file
      Reader fReader = new FileReader(filePath);

      ListOfStringCreator parsingCtr = new ListOfStringCreator();

      // parsing the file
      CSVParser<List<String>> parser = new CSVParser<>(fReader, parsingCtr);

      ParsedCSV<List<String>> parsedDT;

      List<List<String>> parsedBody = parser.parse();
      List<String> parsedHeader = parser.getHeader();

      // checking if the file has a header
      if (hasHeader) {
        parsedDT = new ParsedCSV<>(parsedHeader, parsedBody);
        //        parsedDT.getBody().add(0, parsedHeader);
        System.out.println(parsedDT.getHeader());
      } else {
        // if the file doesn't have a header, we create a header
        //        for (List<String> row : parsedBody) {
        //          System.out.println("row: " + row);
        //        }
        parsedDT = new ParsedCSV<>(parsedHeader, parsedBody);
      }
      this.data.parsedCSV = parsedDT;

      // serialize the response
      return myResponse.serializer(myResponse.makeSuccessHm("filepath", file));
    } catch (FileNotFoundException e) {
      return myResponse.serializer(
          myResponse.makeFailureHm("error_datasource", "Could not read file: " + file));
    }
  }
}
