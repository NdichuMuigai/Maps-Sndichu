package edu.brown.cs.student.main.Server.handler;

import edu.brown.cs.student.main.Server.ServerData;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler class for the viewcsv API endpoint.
 *
 * <p>Allows the user to view the CSV file that has been loaded into the server.
 */
public class ViewHandler implements Route {

  // shared state
  private final ServerData data;

  /**
   * Constructor for the ViewHandler class. Takes in the serverData as shared state.
   *
   * @param data
   */
  public ViewHandler(ServerData data) {
    this.data = data;
  }

  /**
   * Handles the viewcsv endpoint. Returns the CSV file that has been loaded into the server if it
   * has been loaded. Otherwise, returns an error message.
   *
   * @param request the request
   * @param response the response
   * @return the CSV file that has been loaded into the server if it has been loaded. Otherwise,
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    CreateResponse myResponse = new CreateResponse();
    if (this.data.parsedCSV == null) {
      return myResponse.serializer(
          myResponse.makeFailureHm("error_datasource", "File not loaded."));
    }
    List<List<String>> dataDisplay = this.data.parsedCSV.getBody();
    // check if the file has headers and if so, add them to the dataDisplay
    if (this.data.parsedCSV.getHeader() != null
        && !this.data.parsedCSV.getBody().isEmpty()
        && this.data.parsedCSV.getBody().get(0) != this.data.parsedCSV.getHeader()) {
      dataDisplay.add(0, this.data.parsedCSV.getHeader());
    }

    return myResponse.serializer(myResponse.makeSuccessHm("data", dataDisplay));
  }
}
