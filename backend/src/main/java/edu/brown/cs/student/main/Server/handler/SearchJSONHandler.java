package edu.brown.cs.student.main.Server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import edu.brown.cs.student.main.Server.ServerData;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import spark.Request;
import spark.Response;
import spark.Route;

/** SearchGeoJSONHandler class for searching GeoJSON data based on a keyword. */
public class SearchJSONHandler implements Route {
  private ServerData data;
  private ArrayDeque<String> searchHistory;

  public SearchJSONHandler(ServerData data) {
    this.data = data;
    this.searchHistory = new ArrayDeque<>(10);
  }

  public ArrayDeque<String> getSearchHistory() {
    return searchHistory;
  }

  @Override
  public Object handle(Request request, Response response) {
    CreateResponse myResponse = new CreateResponse();

    try {
      if (this.data.geojsonData == null) {
        return myResponse.serializer(
            myResponse.makeFailureHm("error_datasource", "GeoJSON data not loaded."));
      }

      String searchKeyword = request.queryParams("keyword").toLowerCase();
      // update search history
      if(searchHistory.size() == 10) {
        searchHistory.removeFirst();
      }
      searchHistory.addLast(searchKeyword);

      JsonArray features = data.geojsonData.getAsJsonArray("features");

      JsonArray matchedFeatures = new JsonArray();
      for (JsonElement featureElement : features) {
        if (featureElement == null || !featureElement.isJsonObject()) {
          continue;
        }

        JsonObject feature = featureElement.getAsJsonObject();
        JsonObject properties = feature.getAsJsonObject("properties");
        boolean isMatch = properties.entrySet().stream()
            .anyMatch(entry -> entry.getValue().toString().toLowerCase().contains(searchKeyword.toLowerCase()));


        if (isMatch) {
          matchedFeatures.add(feature);
        }
      }

      JsonObject result = new JsonObject();
      result.addProperty("type", "FeatureCollection");
      result.add("features", matchedFeatures);

      // convert success to json
      JsonObject actualResult = new JsonObject();
      actualResult.addProperty("result", "success");

      actualResult.add("data", result);
      // Serialize the result and return it
      Gson gson = new Gson();
      return gson.toJson(actualResult);

    } catch (Exception e) {
      // Handle exceptions and return an error response
      return myResponse.serializer(
          myResponse.makeFailureHm("error_processing_request", e.getMessage()));
    }
  }
}
