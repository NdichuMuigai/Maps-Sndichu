package edu.brown.cs.student.Maps;


import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.Server.RandomStringGenerator;
import edu.brown.cs.student.Server.ResponseGenerator;
import edu.brown.cs.student.main.Server.ServerData;
import edu.brown.cs.student.main.Server.handler.*;
import edu.brown.cs.student.main.Server.handler.LoadHandler;
import edu.brown.cs.student.main.Server.handler.SearchHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/**
 * This class tests the SearchHandler class by inputting random strings and columns to search for
 * and checking if the response is correct. This is known as fuzz testing.
 */
public class FuzzTest {

  final ServerData data = new ServerData();
  private ResponseGenerator responseGen;

  @BeforeAll
  public static void setupOnce() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  @BeforeEach
  public void setupBeforeEach() {
    this.data.clearCSV();
    this.responseGen = new ResponseGenerator();

    Spark.get("loadjson", new LoadGeoJSONHandler(this.data));
    Spark.get("filterjson", new FilterGeoJSONHandler(this.data));

    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void cleanup() {
    this.data.clearCSV();
    this.responseGen = null;

    Spark.unmap("/loadjson");
    Spark.unmap("/filterjson");
    Spark.awaitStop();
  }

  private Map<String, Object> fetchResponse(HttpURLConnection connection) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> map =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(connection.getInputStream()));
    return map;
  }

  private HttpURLConnection initiateRequest(String endpoint) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + endpoint);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * This test checks if the loadjson endpoint returns the correct response.
   * @throws IOException
   */
  @Test
  public void randomBoundingBoxSearchTest() throws IOException {
    // call loadjson endpoint
    HttpURLConnection clientConnection1 =
        initiateRequest("loadjson?file=map.geojson");
    assertEquals(200, clientConnection1.getResponseCode());
    Map<String, Object> response = fetchResponse(clientConnection1);
    assertEquals("success",response.get("result"));
    assertEquals("map.geojson", response.get("file"));
    RandomStringGenerator randomGen = new RandomStringGenerator();

    for (int i = 0; i < 100; i++) {
      // Generate random bounding box coordinates
      double minLat = randomGen.randomCoordinate();
      double minLng = randomGen.randomCoordinate();
      double maxLat = randomGen.randomCoordinate();
      double maxLng = randomGen.randomCoordinate();

//      // Ensure minLat/maxLat and minLng/maxLng are in the correct order
//      if (minLat > maxLat) {
//        double temp = minLat;
//        minLat = maxLat;
//        maxLat = temp;
//      }
//      if (minLng > maxLng) {
//        double temp = minLng;
//        minLng = maxLng;
//        maxLng = temp;
//      }

      String bboxSearch =
          "filterjson?minLat=" + minLat + "&minLng=" + minLng + "&maxLat=" + maxLat + "&maxLng="
              + maxLng;
      HttpURLConnection clientConnection = initiateRequest(bboxSearch);
      assertEquals(200, clientConnection.getResponseCode());
      // assert that the data returned is a JsonArray
      Map<String, Object> response2 = fetchResponse(clientConnection);
      assertTrue(response2.containsKey("data"));
      assertEquals(response2.get("result"), "success");
    }
  }

  /**
   * This test checks if the bounding box search returns the correct results.
   * @throws IOException
   */
  @Test
  public void randomBoundsUnitTest() throws IOException {

    HttpURLConnection clientConnection1 =
        initiateRequest("loadjson?file=map.geojson");
    assertEquals(200, clientConnection1.getResponseCode());
    Map<String, Object> response = fetchResponse(clientConnection1);
    assertEquals("success", response.get("result"));
    assertEquals("map.geojson", response.get("file"));

    // get data as JSONArray
    HttpURLConnection clientConnection2 = initiateRequest("filterjson");
    assertEquals(200, clientConnection2.getResponseCode());
    Map<String, Object> dataResponse = fetchResponse(clientConnection2);
    assertEquals("success", dataResponse.get("result"));
    Gson gson = new Gson();
    JsonObject data = gson.fromJson(gson.toJson(dataResponse.get("data")), JsonObject.class);
    JsonArray features = data.getAsJsonArray("features");

    double minLat = -30;
    double minLng = -80;
    double maxLat = 60;
    double maxLng = 60;
    JsonArray filteredFeatures = FilterGeoJSONHandler.getFilteredFeatures(features, minLat, minLng, maxLat, maxLng);
// ensure that all features are within the bounds
    for (JsonElement featureElement : filteredFeatures) {
      JsonObject feature = featureElement.getAsJsonObject();
      JsonObject geometry = feature.getAsJsonObject("geometry");
      JsonArray polygons = geometry.getAsJsonArray("coordinates");

      for (JsonElement polygonElement : polygons) {
        JsonArray polygon = polygonElement.getAsJsonArray().get(0).getAsJsonArray(); // Assuming the first array is the outer boundary.

        for (JsonElement ringElement : polygon) {
          JsonArray coord = ringElement.getAsJsonArray();

//          for (JsonElement coordElement : ring) {

            double lng = coord.get(0).getAsDouble();
            double lat = coord.get(1).getAsDouble();
            if(lng >= -81.0 && lng < -80.0) {
              System.out.println("lng");
            }

            if ((lng >= minLng && lng <= maxLng && lat >= minLat && lat <= maxLat)) {
              System.out.println("true");
              assertTrue(true);
            } else {
              System.out.println(lng);
              System.out.println(lat);

              System.out.println("false");
              assertTrue(false);
            }
//          }
        }
      }
    }
    System.out.println("true");
    assertTrue(true);

  }

  @Test
  public void randomKeywordSearchTest() throws IOException {
    HttpURLConnection clientConnection1 =
        initiateRequest("loadjson?file=map.geojson");
    assertEquals(200, clientConnection1.getResponseCode());
    Map<String, Object> response = fetchResponse(clientConnection1);
    assertEquals("success", response.get("result"));
    assertEquals("map.geojson", response.get("file"));

    RandomStringGenerator randomGen = new RandomStringGenerator();

    for (int i = 0; i < 100; i++) {
      // Generate a random keyword
      String randomKeyword = randomGen.generateRandomString();

      String keywordSearch = "filterjson?keyword=" + randomKeyword;
      HttpURLConnection clientConnection = initiateRequest(keywordSearch);
      assertEquals(200, clientConnection.getResponseCode());
      Map<String, Object> keywordResp = fetchResponse(clientConnection);
      assertEquals("success", keywordResp.get("result"));
      assertTrue(keywordResp.containsKey("data"));

    }
  }
}