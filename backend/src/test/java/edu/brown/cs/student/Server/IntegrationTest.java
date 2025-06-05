package edu.brown.cs.student.Server;

import static org.junit.jupiter.api.Assertions.*;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Server.ServerData;
import edu.brown.cs.student.main.Server.handler.BroadbandHandler;
import edu.brown.cs.student.main.Server.handler.CreateResponse;
import edu.brown.cs.student.main.Server.handler.LoadHandler;
import edu.brown.cs.student.main.Server.handler.SearchHandler;
import edu.brown.cs.student.main.Server.handler.ViewHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class IntegrationTest {
  /** Set up the connection with the Project2 before testing connections */
  ServerData serverData;

  private CreateResponse createResponse;

  /**
   * Sets up the Spark server before all tests are run. Configures Spark to use an arbitrary
   * available port and sets the logging level to WARNING.
   */
  @BeforeAll
  public static void setupOnce() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  /**
   * Setup the Spark server before each test is run.
   *
   * @throws KeyException if the key is invalid
   */
  @BeforeEach
  public void setup() throws KeyException {
    // clear the data in the Csv file
    this.serverData = new ServerData();
    this.createResponse = new CreateResponse();

    // make all of the endpoints
    Spark.get("loadcsv", new LoadHandler(serverData));
    Spark.get("searchcsv", new SearchHandler(serverData));
    Spark.get("viewcsv", new ViewHandler(serverData));
    Spark.get("broadband", new BroadbandHandler(serverData));

    Spark.init();
    Spark.awaitInitialization();
  }

  /** Remove all test resources after each test */
  @AfterEach
  public void cleanup() {
    // clear the cache and data
    this.serverData.clearCSV();
    this.createResponse = null;

    // remove endpoints
    Spark.unmap("/loadcsv");
    Spark.unmap("/searchcsv");
    Spark.unmap("/viewcsv");
    Spark.unmap("/broadband");
    Spark.awaitStop();
  }

  /**
   * Helper to get the response from the connection's input stream, in the form of a map
   *
   * @param clientConnection the HttpURLConnection to read from
   * @return the Map representing the response to the request
   * @throws IOException if failed to read from input stream
   */
  private Map<String, Object> getResponse(HttpURLConnection clientConnection) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> map =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    return map;
  }

  /**
   * Attempts to connect to the Project2 through URL on local machine
   *
   * @param apiCall the endpoint you are targeting
   * @return a requester
   * @throws ExecutionException error thrown if does no execute properly
   */
  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Tests the response for loadcsv if given a proper request
   *
   * @throws IOException if the connection fails for some reason
   * @throws ExecutionException if the request fails to execute properly
   */
  @Test
  public void testLoadSuccess() throws IOException, ExecutionException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?file=dogs.Csv&hasHeader=false");
    assertEquals(200, clientConnection.getResponseCode());

    CreateResponse myResponse = new CreateResponse();
    Map<String, Object> expected = myResponse.makeSuccessHm("filepath", "dogs.Csv");

    Map<String, Object> response = getResponse(clientConnection);

    assertEquals(response, expected);
    clientConnection.disconnect();
  }

  /**
   * Tests that the program can load a generic Csv
   *
   * @throws IOException if the connection fails for some reason
   */
  @Test
  public void testValidLoadCSV() throws IOException, ExecutionException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?file=dogs.Csv&hasHeader=false");
    // checking that it connects to Project2 successfully
    assertEquals(200, clientConnection.getResponseCode());

    // checking what we actually got from the JSON when this happened
    this.createResponse.makeSuccessHm("filepath", "dogs.Csv");
    assertEquals(this.createResponse.getResponseHm().get("result"), "success");
  }
  /**
   * Tests the error message for loadcsv if no header indicator is given as a parameter
   *
   * @throws IOException if the connection fails for some reason
   */
  @Test
  public void testLoadNoHeader() throws IOException, ExecutionException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?file=ten-star.Csv");
    assertEquals(200, clientConnection.getResponseCode());

    CreateResponse myResponse = new CreateResponse();
    Map<String, Object> expected =
        myResponse.makeFailureHm(
            "error_bad_request", "Missing hasHeader query param. i.e. 'hasHeader=(false),(true)");

    Map<String, Object> response = getResponse(clientConnection);

    assertEquals(expected, response);
    clientConnection.disconnect();
  }

  /**
   * Tests the error message for loadcsv if a file that cannot be found is given as a parameter
   *
   * @throws IOException if the connection fails for some reason
   */
  @Test
  public void testLoadBadFile() throws IOException, ExecutionException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?file=missing.Csv&hasHeader=true");
    assertEquals(200, clientConnection.getResponseCode());

    CreateResponse myResponse = new CreateResponse();
    Map<String, Object> expected =
        myResponse.makeFailureHm("error_datasource", "Could not read file: missing.Csv");

    Map<String, Object> response = getResponse(clientConnection);

    assertEquals(expected, response);
    clientConnection.disconnect();
  }

  /**
   * Tests the error message for loadcsv if an ill-formatted header indicator is given as a
   * parameter
   *
   * @throws IOException if the connection fails for some reason
   */
  @Test
  public void testLoadBadHeader() throws IOException, ExecutionException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?file=ten-star.Csv&hasHeader=m");
    assertEquals(200, clientConnection.getResponseCode());

    CreateResponse myResponse = new CreateResponse();
    Map<String, Object> expected =
        myResponse.makeFailureHm(
            "error_bad_json",
            "Request was ill-formed. Improper parameter for header. i.e. 'yes' or 'no'");

    Map<String, Object> response = getResponse(clientConnection);

    assertEquals(response, expected);
    clientConnection.disconnect();
  }

  /**
   * Tests the response for viewcsv if given a proper request
   *
   * @throws IOException if the connection fails for some reason
   */
  @Test
  public void testViewSuccess() throws IOException, ExecutionException {
    HttpURLConnection clientConnection1 = tryRequest("loadcsv?file=dogs.Csv&hasHeader=true");
    assertEquals(200, clientConnection1.getResponseCode());
    HttpURLConnection clientConnection2 = tryRequest("viewcsv");
    assertEquals(200, clientConnection2.getResponseCode());
    List<List<String>> dogData =
        List.of(
            List.of("dogName", "origin", "size", "friendly", "intelligence"),
            List.of("german shepherd", "germany", "large", "no", "high"),
            List.of("poodle", "germany", "large", "yes", "high"),
            List.of("chihuahua", "mexico", "small", "no", "mid"),
            List.of("golden retriever", "uk", "large", "yes", "mid"),
            List.of("border collie", "scotland", "medium", "yes", "high"));

    CreateResponse myResponse = new CreateResponse();
    Map<String, Object> expected = myResponse.makeSuccessHm("data", dogData);

    Map<String, Object> response = getResponse(clientConnection2);

    assertEquals(response, expected);
    clientConnection1.disconnect();
    clientConnection2.disconnect();
  }

  /**
   * Tests the response for viewcsv if no file is loaded
   *
   * @throws IOException if the connection fails for some reason
   */
  @Test
  public void testViewNoFile() throws IOException, ExecutionException {
    HttpURLConnection clientConnection = tryRequest("viewcsv");
    assertEquals(200, clientConnection.getResponseCode());

    CreateResponse myResponse = new CreateResponse();
    Map<String, Object> expected = myResponse.makeFailureHm("error_datasource", "File not loaded.");

    Map<String, Object> response = getResponse(clientConnection);

    assertEquals(response, expected);
    clientConnection.disconnect();
  }

  /**
   * tests taht multiple calls can be made to the api
   *
   * @throws IOException in case connection fails
   */
  @Test
  public void testMultipleAPICalls() throws IOException {
    HttpURLConnection clientConnection1 = tryRequest("loadcsv?file=dogs.Csv&hasHeader=false");
    assertEquals(200, clientConnection1.getResponseCode());
    HttpURLConnection clientConnection2 = tryRequest("searchcsv?by=all&query=golden%20retriever");
    assertEquals(200, clientConnection2.getResponseCode());
  }

  /**
   * tests that basic Search without column identifiers works properly
   *
   * @throws IOException in case connection fails
   * @throws ExecutionException if request fails to execute properly
   */
  @Test
  public void testSearchBasicSuccess() throws IOException, ExecutionException {
    HttpURLConnection clientConnection1 = tryRequest("loadcsv?file=dogs.Csv&hasHeader=false");
    assertEquals(200, clientConnection1.getResponseCode());
    HttpURLConnection clientConnection2 = tryRequest("searchcsv?by=all&query=golden%20retriever");
    assertEquals(200, clientConnection2.getResponseCode());

    List<List<String>> dogData = List.of(List.of("golden retriever", "uk", "large", "yes", "mid"));

    Map<String, Object> expected = this.createResponse.makeSuccessHm("data", dogData);
    expected.put("query", "golden retriever");

    Map<String, Object> response = getResponse(clientConnection2);

    assertEquals(response, expected);
    clientConnection1.disconnect();
    clientConnection2.disconnect();
  }

  /**
   * Tests the success response for searchcsv if given a proper request (with a value and column to
   * Search for)
   *
   * @throws IOException if the connection fails for some reason
   */
  @Test
  public void testSearchColumnSuccess() throws IOException, ExecutionException {
    HttpURLConnection clientConnection1 = tryRequest("loadcsv?file=dogs.Csv&hasHeader=true");
    assertEquals(200, clientConnection1.getResponseCode());
    HttpURLConnection clientConnection2 =
        tryRequest("searchcsv?by=column&query=poodle&column=dogName");
    assertEquals(200, clientConnection2.getResponseCode());
    HttpURLConnection clientConnection3 = tryRequest("searchcsv?by=column&query=poodle&column=0");
    assertEquals(200, clientConnection3.getResponseCode());

    List<List<String>> poodleSpoodleScoodleDoodle =
        List.of(List.of("poodle", "germany", "large", "yes", "high"));

    CreateResponse myResponse = new CreateResponse();
    Map<String, Object> expected = myResponse.makeSuccessHm("data", poodleSpoodleScoodleDoodle);
    expected.put("query", "poodle");
    expected.put("column", "dogName");

    Map<String, Object> expected2 = myResponse.makeSuccessHm("data", poodleSpoodleScoodleDoodle);
    expected2.put("query", "poodle");
    expected2.put("column", "0");

    Map<String, Object> response = getResponse(clientConnection2);
    Map<String, Object> response2 = getResponse(clientConnection3);

    assertEquals(response, expected);
    assertEquals(response2, expected2);
    clientConnection1.disconnect();
    clientConnection2.disconnect();
  }

  /**
   * Tests the response for searchcsv if no file is loaded
   *
   * @throws IOException if the connection fails for some reason
   */
  @Test
  public void testSearchNoFile() throws IOException {
    HttpURLConnection clientConnection = tryRequest("searchcsv");
    assertEquals(200, clientConnection.getResponseCode());

    CreateResponse myResponse = new CreateResponse();
    Map<String, Object> expected =
        myResponse.makeFailureHm(
            "error_datasource", "No file was loaded before. Use loadcsv endpoint.");

    Map<String, Object> response = getResponse(clientConnection);

    assertEquals(response, expected);
    clientConnection.disconnect();
  }

  /**
   * Tests the response for searchcsv if no Search value is given as a parameter
   *
   * @throws IOException if the connection fails for some reason
   */
  @Test
  public void testSearchNoValue() throws IOException {
    HttpURLConnection clientConnection1 = tryRequest("loadcsv?file=stardata.Csv&hasHeader=true");
    assertEquals(200, clientConnection1.getResponseCode());
    HttpURLConnection clientConnection2 = tryRequest("searchcsv");
    assertEquals(200, clientConnection2.getResponseCode());

    CreateResponse myResponse = new CreateResponse();
    Map<String, Object> expected =
        myResponse.makeFailureHm("error_bad_request", "Missing the query or column");

    Map<String, Object> response = getResponse(clientConnection2);

    assertEquals(response, expected);
    clientConnection1.disconnect();
    clientConnection2.disconnect();
  }

  /**
   * Tests the response for searchcsv if the value was not found in the entire file (regular Search)
   *
   * @throws IOException if the connection fails for some reason
   */
  @Test
  public void testSearchBasicNoResult() throws IOException, ExecutionException {
    HttpURLConnection clientConnection1 = tryRequest("loadcsv?file=dogs.Csv&hasHeader=false");
    assertEquals(200, clientConnection1.getResponseCode());
    HttpURLConnection clientConnection2 = tryRequest("searchcsv?by=all&query=corgi");
    assertEquals(200, clientConnection2.getResponseCode());

    CreateResponse myResponse = new CreateResponse();
    Map<String, Object> expected = myResponse.makeSuccessHm("data", List.of());
    expected.put("query", "corgi");

    Map<String, Object> response = getResponse(clientConnection2);

    assertEquals(response, expected);
    clientConnection1.disconnect();
    clientConnection2.disconnect();
  }

  /**
   * Tests the response for searchcsv if the value was not found in the specified column (column
   * Search)
   *
   * @throws IOException if the connection fails for some reason
   */
  @Test
  public void testSearchColumnNoResult() throws IOException, ExecutionException {
    HttpURLConnection clientConnection1 = tryRequest("loadcsv?file=dogs.Csv&hasHeader=true");
    assertEquals(200, clientConnection1.getResponseCode());
    HttpURLConnection clientConnection2 =
        tryRequest("searchcsv?by=column&query=dragon&column=dogName");
    assertEquals(200, clientConnection2.getResponseCode());
    HttpURLConnection clientConnection3 =
        tryRequest("searchcsv?by=column&query=poodle&column=dragon");
    assertEquals(200, clientConnection3.getResponseCode());

    Map<String, Object> intSearchResponse = getResponse(clientConnection2);
    Map<String, Object> headerSearchResponse = getResponse(clientConnection3);

    CreateResponse myResponse = new CreateResponse();
    Map<String, Object> expected = myResponse.makeSuccessHm("data", List.of());
    expected.put("message", "Could not find query dragon in column dogName");
    expected.put("query", "dragon");
    expected.put("column", "dogName");

    Map<String, Object> expected2 = myResponse.makeSuccessHm("data", List.of());
    expected2.put("message", "Could not find query poodle in column dragon");
    expected2.put("query", "poodle");
    expected2.put("column", "dragon");

    assertEquals(intSearchResponse, expected);
    assertEquals(headerSearchResponse, expected2);
    clientConnection1.disconnect();
    clientConnection2.disconnect();
    clientConnection3.disconnect();
  }

  /**
   * Tests that the dogs.csv file can be loaded, viewed, and then searched properly
   *
   * @throws IOException in case connection fails
   */
  @Test
  public void testLoadViewSearchDogs() throws IOException {
    HttpURLConnection clientConnection1 = tryRequest("loadcsv?file=dogs.csv&hasHeader=true");
    assertEquals(200, clientConnection1.getResponseCode());
    HttpURLConnection clientConnection2 = tryRequest("viewcsv");
    assertEquals(200, clientConnection2.getResponseCode());
    HttpURLConnection clientConnection3 =
        tryRequest("searchcsv?by=column&column=origin&query=germany");
    assertEquals(200, clientConnection3.getResponseCode());

    // german shepherd and poodle are from Germany
    CreateResponse myResponse = new CreateResponse();
    Map<String, Object> viewExpected =
        myResponse.makeSuccessHm(
            "data",
            List.of(
                List.of("dogName", "origin", "size", "friendly", "intelligence"),
                List.of("german shepherd", "germany", "large", "no", "high"),
                List.of("poodle", "germany", "large", "yes", "high"),
                List.of("chihuahua", "mexico", "small", "no", "mid"),
                List.of("golden retriever", "uk", "large", "yes", "mid"),
                List.of("border collie", "scotland", "medium", "yes", "high")));
    Map<String, Object> searchExpected =
        myResponse.makeSuccessHm(
            "data",
            List.of(
                List.of("german shepherd", "germany", "large", "no", "high"),
                List.of("poodle", "germany", "large", "yes", "high")));
    searchExpected.put("query", "germany");
    searchExpected.put("column", "origin");

    Map<String, Object> viewResponse = getResponse(clientConnection2);
    Map<String, Object> searchResponse = getResponse(clientConnection3);

    assertEquals(viewExpected, viewResponse);
    assertEquals(searchExpected, searchResponse);
    clientConnection1.disconnect();
    clientConnection2.disconnect();
    clientConnection3.disconnect();
  }

  @Test
  public void testBroadbandSingleSuccess() throws IOException, ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    sdf.setTimeZone(TimeZone.getTimeZone("EDT"));

    Date start = new Date();
    HttpURLConnection clientConnection =
        this.tryRequest("broadband?state=Rhode%20Island&county=Providence");
    assertEquals(200, clientConnection.getResponseCode());
    Date end = new Date();

    Map<String, Object> map = this.getResponse(clientConnection);
    System.out.println(map);

    assertEquals("success", map.get("result"));
    assertEquals("Providence", map.get("county"));
    assertEquals("Rhode Island", map.get("state"));

    Date responseTime = sdf.parse((String) map.get("datetime"));
    assertTrue(responseTime.after(start) && responseTime.before(end));

    List<Map<String, Object>> dataList = (List<Map<String, Object>>) map.get("data");
    assertNotNull(dataList);
    assertEquals(1, dataList.size());

    Map<String, Object> dataItem = dataList.get(0);
    assertEquals(85.4, dataItem.get("coverage"));
    assertEquals("007", dataItem.get("countycode"));
    assertEquals("Providence County, Rhode Island", dataItem.get("name"));
    assertEquals("44", dataItem.get("statecode"));

    clientConnection.disconnect();
  }

  @Test
  public void testBroadbandInvalidState() throws IOException {
    HttpURLConnection clientConnection = this.tryRequest("broadband?state=InvalidState");

    Map<String, Object> map = this.getResponse(clientConnection);
    System.out.println(map);
    assertEquals(200, clientConnection.getResponseCode());

    assertEquals(
        "error_bad_request", map.get("result")); // Assuming an 'error' result for invalid input
    assertNotNull(map.get("message")); // Assuming there's an error message

    clientConnection.disconnect();
  }

  /**
   * tests that the Broadband API can handle an invalid county input
   *
   * @throws IOException
   */
  @Test
  public void testBroadbandInvalidCounty() throws IOException {
    HttpURLConnection clientConnection =
        this.tryRequest("broadband?state=Rhode%20Island&county=InvalidCounty");

    Map<String, Object> map = this.getResponse(clientConnection);
    System.out.println(map);
    assertEquals(200, clientConnection.getResponseCode());

    assertEquals(
        "error_bad_request",
        map.get("result")); // Assuming an 'error_bad_request' result for invalid input
    assertNotNull(map.get("message")); // Assuming there's an error message

    clientConnection.disconnect();
  }

  /**
   * tests that the Broadband api can work with multiple valid responses in a row
   *
   * @throws IOException in case the connection fails
   */
  @Test
  public void testBroadbandMultipleSucess() throws IOException, ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    sdf.setTimeZone(TimeZone.getTimeZone("EDT"));

    Date start1 = new Date();
    HttpURLConnection clientConnection = this.tryRequest("broadband?state=California&county=*");
    assertEquals(200, clientConnection.getResponseCode());
    Date end1 = new Date();

    Date start2 = new Date();
    HttpURLConnection clientConnection2 = this.tryRequest("broadband?state=California");
    assertEquals(200, clientConnection2.getResponseCode());
    Date end2 = new Date();

    Map<String, Object> map = this.getResponse(clientConnection);
    System.out.println(map);
    Map<String, Object> map2 = this.getResponse(clientConnection2);
    System.out.println(map2);

    assertNotEquals(map, map2);

    assertEquals("success", map.get("result"));
    assertEquals("success", map2.get("result"));

    Date responseTime1 = sdf.parse((String) map.get("datetime"));
    Date responseTime2 = sdf.parse((String) map2.get("datetime"));
    System.out.println(responseTime1);
    System.out.println(responseTime2);
    System.out.println(start1);
    System.out.println(end1);
    assertTrue(responseTime1.after(start1) && responseTime1.before(end1));
    assertTrue(responseTime2.after(start2) && responseTime2.before(end2));

    clientConnection.disconnect();
    clientConnection2.disconnect();
  }
}
