package edu.brown.cs.student.Server;

import static org.junit.jupiter.api.Assertions.*;

import com.squareup.moshi.Moshi;
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

    Spark.get("loadcsv", new LoadHandler(this.data));
    Spark.get("searchcsv", new SearchHandler(this.data));

    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void cleanup() {
    this.data.clearCSV();
    this.responseGen = null;

    Spark.unmap("/loadcsv");
    Spark.unmap("/searchcsv");
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
  public void randomSearchTest() throws IOException {
    HttpURLConnection clientConnection1 =
        initiateRequest("loadcsv?file=stardata.Csv&hasHeader=true");
    assertEquals(200, clientConnection1.getResponseCode());
    RandomStringGenerator randomGen = new RandomStringGenerator();
    String randomTarget = "";
    String randomCol = "";

    for (int i = 0; i < 100; i++) {
      randomTarget = randomGen.generateRandomString();
//      System.out.println(randomTarget);
      randomCol = randomGen.generateRandomValidCol();
      String csvSearch = "searchcsv?by=column&query=" + randomTarget + "&column=" + randomCol;
      HttpURLConnection clientConnection2 = initiateRequest(csvSearch);

      if (clientConnection2.getResponseCode() == 200) {
        assertTrue(true, "Connection Successful!");
        Map<String, Object> expectedResponse =
            responseGen.generateSuccessResponse("data", List.of());
        expectedResponse.put(
            "message", "No match for query " + randomTarget + " in column " + randomCol);
        expectedResponse.put("query", randomTarget);
        expectedResponse.put("column", randomCol);
        Map<String, Object> response = fetchResponse(clientConnection2);

        if (expectedResponse.equals(response)) {
          assertTrue(true, "Term not found!");
        } else {
          assertTrue(true, "Term was found!");
        }
      } else {
        fail(
            "Unexpected error occurred with response code: " + clientConnection2.getResponseCode());
      }
    }
    assertTrue(true, "Test completed successfully!");
  }

  @Test
  public void randomColumnSearchTest() throws IOException {
    HttpURLConnection clientConnection1 =
        initiateRequest("loadcsv?file=stardata.Csv&hasHeader=true");
    assertEquals(200, clientConnection1.getResponseCode());
    RandomStringGenerator randomGen = new RandomStringGenerator();
    String target = "Casey";
    String randomCol = "";

    for (int i = 0; i < 100; i++) {

      randomCol = randomGen.generateRandomValidCol();
//      System.out.println(randomCol);
      String csvSearch = "searchcsv?by=column&query=" + target + "&column=" + randomCol;
      HttpURLConnection clientConnection2 = initiateRequest(csvSearch);
      assertEquals(200, clientConnection2.getResponseCode());
    }
  }
}
