package edu.brown.cs.student.main.Server.handler;

import com.google.gson.JsonObject;
import edu.brown.cs.student.main.Server.ServerData;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;

/**
 * Handler class for the viewgeojson API endpoint.
 *
 * <p>Allows the user to view the GeoJSON data that has been loaded into the server.
 */
public class ViewGeoJSONHandler implements Route {

  // shared state
  private ServerData data;
  /**
   * Constructor for the ViewGeoJSONHandler class.
   *
   * @param data Shared state of the loaded GeoJSON data.
   */
  public ViewGeoJSONHandler(ServerData data) {
    this.data = data;
  }

  /**
   * Handles the viewgeojson endpoint. Returns the GeoJSON data that has been loaded into the server
   * if it has been loaded. Otherwise, returns an error message.
   *
   * @param request the request
   * @param response the response
   * @return the GeoJSON data that has been loaded into the server if it has been loaded. Otherwise,
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    CreateResponse myResponse = new CreateResponse();
//    System.out.println(this.data.geojsonData);
    if (this.data.geojsonData == null) {
      return myResponse.serializer(
          myResponse.makeFailureHm("error_datasource", "GeoJSON data not loaded."));
    }
  Gson gson = new Gson();
    JsonObject actualResult = new JsonObject();
    actualResult.addProperty("result", "success");
    actualResult.add("data", this.data.geojsonData);
    return gson.toJson(actualResult);
//    return myResponse.serializer(myResponse.makeSuccessHm("data", this.data.geojsonData));
  }

  // Setter method in case you want to update the GeoJSON data from another handler
  public void setGeoJsonData(JsonObject geoJsonData) {
    this.data.geojsonData = geoJsonData;
  }
}
