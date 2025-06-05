package edu.brown.cs.student.main.Server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import edu.brown.cs.student.main.Json.JsonUtility;
import edu.brown.cs.student.main.Server.ServerData;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import org.jetbrains.annotations.NotNull;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

/** FilterGeoJSONHandler class for filtering GeoJSON data based on a geographic bounding box. */
public class FilterGeoJSONHandler implements Route {
  private ServerData data;
  private ArrayDeque<String> searchHistory;


  /**
   * Constructor for FilterGeoJSONHandler.
   * @param data
   */
  public FilterGeoJSONHandler(ServerData data) {
    this.data = data;
    this.searchHistory = new ArrayDeque<>(10);

  }

  /**
   * Handles requests to the /filterjson endpoint.
   * @param request  The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   * @return
   */
  @Override
  public Object handle(Request request, Response response) {
    Session session = request.session(true); // Create a new session if one does not exist

    CreateResponse myResponse = new CreateResponse();
    try {
      if(this.data.geojsonData == null) {
        return myResponse.serializer(
            myResponse.makeFailureHm("error_datasource", "GeoJSON data not loaded."));
      }
      JsonArray features;
      try {
        features = data.geojsonData.getAsJsonArray("features");
      } catch(Exception e) {
        return myResponse.serializer(
            myResponse.makeFailureHm("error_datasource", "GeoJSON data not loaded."));
      }

      // Extract bounding box parameters from the request

      if(request.queryParams("minLat") != null && request.queryParams("minLng") != null && request.queryParams("maxLat") != null && request.queryParams("maxLng") != null) {
        double minLat = Double.parseDouble(request.queryParams("minLat"));
        double minLng = Double.parseDouble(request.queryParams("minLng"));
        double maxLat = Double.parseDouble(request.queryParams("maxLat"));
        double maxLng = Double.parseDouble(request.queryParams("maxLng"));
        // Filter features based on the bounding box
        features = getFilteredFeatures(features, minLat, minLng, maxLat,
            maxLng);
      }

      if(request.queryParams("keyword") != null) {
        String searchKeyword = request.queryParams("keyword").toLowerCase();
        features = getMatchedFeatures(searchKeyword, features);
        this.updateSearchHistory(searchKeyword);
      }

      // Create a new GeoJSON object with the filtered features
      JsonObject filteredGeoJson = new JsonObject();
      filteredGeoJson.addProperty("type", "FeatureCollection");
      filteredGeoJson.add("features", features);
      // add result: success to filteredGeoJson
      JsonObject result = new JsonObject();
      result.addProperty("result", "success");
      result.add("data", filteredGeoJson);
      Gson gson = new Gson();

      return gson.toJson(result);
      // Serialize the filtered GeoJSON to a string and return it
//      return myResponse.serializer(myResponse.makeSuccessHm("data", filteredGeoJson));

    } catch (NumberFormatException e) {
      // Handle number format exceptions for bounding box parameters
      return myResponse.serializer(
          myResponse.makeFailureHm("error_invalid_parameters", "Invalid bounding box parameters."));
    } catch (Exception e) {
      // Handle other exceptions and return an error response
      return myResponse.serializer(
          myResponse.makeFailureHm("error_processing_request", e.getMessage()));
    }
  }

  /**
   * Updates the search history.
   * @param searchKeyword
   */
  private void updateSearchHistory(String searchKeyword) {
    if(searchHistory.size() == 10) {
      searchHistory.removeFirst();
    }
    searchHistory.addLast(searchKeyword);
    appendToSearchHistoryFile(searchKeyword); // Write to file

  }

  /**
   * Appends the search keyword to the search history file.
   * @param searchKeyword
   */
  private void appendToSearchHistoryFile(String searchKeyword) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("history/search_history.txt", true))) {
      writer.write(searchKeyword + "\n"); // Append the keyword and a new line
    } catch (IOException e) {
      // Handle exceptions (you could log this or print a message)
      System.err.println("Error writing to search history file: " + e.getMessage());
    }
  }


  /**
   * Returns the features that match the search keyword.
   * @param searchKeyword
   * @param features
   * @return
   */
  @NotNull
  private static JsonArray getMatchedFeatures(String searchKeyword, JsonArray features) {
    // update search history
    JsonArray matchedFeatures = new JsonArray();

    for (JsonElement featureElement : features) {
      if (featureElement == null || !featureElement.isJsonObject()) {
        continue;
      }

      JsonObject feature = featureElement.getAsJsonObject();
      JsonObject properties = feature.getAsJsonObject("properties");
      JsonObject areaDescriptions = properties.getAsJsonObject("area_description_data");
      boolean isMatch = areaDescriptions.entrySet().stream()
          .anyMatch(entry -> entry.getValue().toString().toLowerCase().contains(searchKeyword.toLowerCase()));


      if (isMatch) {
        matchedFeatures.add(feature);
      }
    }
    features = matchedFeatures;
    return features;
  }

  /**
   * Returns the features that are within the bounding box.
   * @param features
   * @param minLat
   * @param minLng
   * @param maxLat
   * @param maxLng
   * @return
   */
  @NotNull
  public static JsonArray getFilteredFeatures(JsonArray features, double minLat, double minLng,
      double maxLat, double maxLng) {
    JsonArray filteredFeatures = new JsonArray();

    for (JsonElement featureElement : features) {
      // Check if the feature element is indeed a JsonObject
      if (featureElement == null || !featureElement.isJsonObject()) {
        continue; // Skip if it's not a JsonObject
      }
      JsonObject feature = featureElement.getAsJsonObject();

      // Extract the geometry object
      JsonElement geometryElement = feature.get("geometry");
      if (geometryElement == null || !geometryElement.isJsonObject()) {
        continue; // Skip if there is no geometry or it's not a JsonObject
      }
      JsonObject geometry = geometryElement.getAsJsonObject();
//      System.out.println(geometry);

      // Extract the coordinates array
      JsonElement coordinatesElement = geometry.get("coordinates");
      if (coordinatesElement == null || !coordinatesElement.isJsonArray()) {
        continue; // Skip if there are no coordinates or it's not a JsonArray
      }
      JsonArray coordinates = coordinatesElement.getAsJsonArray();
//      System.out.println(coordinates);

      // Check if the Polygon is within the bounding box
      if (JsonUtility.isPolygonInBounds(coordinates, minLat, minLng, maxLat, maxLng)) {
        // Add the feature to the list of filtered features
        filteredFeatures.add(feature);
      }
    }
    return filteredFeatures;
  }
}
