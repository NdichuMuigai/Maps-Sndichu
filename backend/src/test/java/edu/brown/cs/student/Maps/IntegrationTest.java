package edu.brown.cs.student.Maps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.Server.RandomStringGenerator;
import edu.brown.cs.student.Server.ResponseGenerator;
import edu.brown.cs.student.main.Server.ServerData;
import edu.brown.cs.student.main.Server.handler.FilterGeoJSONHandler;
import edu.brown.cs.student.main.Server.handler.LoadGeoJSONHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import com.google.gson.Gson;


/**
 * This class tests the SearchHandler class by inputting random strings and columns to search for
 */
  public class IntegrationTest {

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

    @Test
    public void loadAndSearchDifferentResultsTest() throws IOException {
      // Load the first JSON file
      HttpURLConnection loadConnection1 = initiateRequest("loadjson?file=map.geojson");
      assertEquals(200, loadConnection1.getResponseCode());
      Map<String, Object> loadResponse1 = fetchResponse(loadConnection1);
      assertEquals("success", loadResponse1.get("result"));
      assertEquals("map.geojson", loadResponse1.get("file"));

      // Perform a search after loading the first JSON file
      HttpURLConnection searchConnection1 = initiateRequest("filterjson?keyword=Alabama");
      assertEquals(200, searchConnection1.getResponseCode());
      Map<String, Object> searchResponse1 = fetchResponse(searchConnection1);
      assertTrue(searchResponse1.containsKey("data"));

      // Load an empty JSON file
      HttpURLConnection loadConnection2 = initiateRequest("loadjson?file=empty.geojson");
      assertEquals(200, loadConnection2.getResponseCode());
      Map<String, Object> loadResponse2 = fetchResponse(loadConnection2);
      assertEquals("success", loadResponse2.get("result"));
      assertEquals("empty.geojson", loadResponse2.get("file"));

      // Perform a search after loading the empty JSON file
      HttpURLConnection searchConnection2 = initiateRequest("filterjson?keyword=Alabama");
      assertEquals(200, searchConnection2.getResponseCode());
      Map<String, Object> searchResponse2 = fetchResponse(searchConnection2);
    Gson gson = new Gson();
      // Assert that the search results are different
      assertNotEquals(gson.toJson(searchResponse1), gson.toJson(searchResponse2));
    }

    @Test
    public void testSearchHistorySavedToFile() throws IOException {

      HttpURLConnection loadConnection1 = initiateRequest("loadjson?file=map.geojson");
      assertEquals(200, loadConnection1.getResponseCode());
      Map<String, Object> loadResponse1 = fetchResponse(loadConnection1);
      assertEquals("success", loadResponse1.get("result"));
      assertEquals("map.geojson", loadResponse1.get("file"));
      // Perform a few search requests
      performSearch("keyword1");
      performSearch("keyword2");
      performSearch("keyword3");

      // Shutdown the server
      Spark.stop();
      Spark.awaitStop();

      // Read the contents of the search history file
      List<String> fileContents = Files.readAllLines(Paths.get("history/search_history.txt"));

      // Assert the contents of the file
      assertTrue(fileContents.contains("keyword1"));
      assertTrue(fileContents.contains("keyword2"));
      assertTrue(fileContents.contains("keyword3"));
    }

  @Test
  public void testMalformedInputResponse() throws IOException {
    // Load a JSON file to ensure the server is ready to process requests
    HttpURLConnection loadConnection = initiateRequest("loadjson?file=map.geojson");
    assertEquals(200, loadConnection.getResponseCode());
    loadConnection.disconnect();

    // Make a request with malformed input (minLat but no other params)
    HttpURLConnection errorConnection = initiateRequest("filterjson?minLat=34.0");
    assertEquals(200,
        errorConnection.getResponseCode()); // Assuming server still responds with 200 OK but with an error message

    // Fetch and parse the response
    Map<String, Object> errorResponse = fetchResponse(errorConnection);
    Gson gson = new Gson();
    String jsonResponse = gson.toJson(errorResponse);
    // Check if the response contains an error
    assertTrue(errorResponse.get("result").equals("success"), "Response should not indicate an error");
    // You can add more specific assertions to check the nature of the error

    errorConnection.disconnect();
  }

  @Test
  public void testMalformedFile() throws IOException {
    // Load a JSON file to ensure the server is ready to process requests
    HttpURLConnection loadConnection = initiateRequest("loadjson?file=bad_file");
    assertEquals(200, loadConnection.getResponseCode());

    Map<String, Object> jsonResponse = fetchResponse(loadConnection);
    // Check if the response contains an error
    assertTrue(jsonResponse.get("result").equals("error_file_not_found"), "Response should indicate an error");
    // You can add more specific assertions to check the nature of the error

    loadConnection.disconnect();
  }


    private void performSearch(String keyword) throws IOException {
      HttpURLConnection connection = initiateRequest("filterjson?keyword=" + keyword);
      assertEquals(200, connection.getResponseCode());
      connection.disconnect();
    }

  }
