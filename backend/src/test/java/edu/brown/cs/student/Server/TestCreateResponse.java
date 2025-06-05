package edu.brown.cs.student.Server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.brown.cs.student.main.Server.handler.CreateResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests the CreateResponse class, which is responsible for creating the JSON response to be sent to
 * the client.
 */
public class TestCreateResponse {

  /** Tests makeFailureMap to see if correct map is produced for failure. */
  @Test
  public void testFailureMap() {
    Map<String, Object> map = new CreateResponse().makeFailureHm("error_bad_request", "no request");
    Map<String, Object> expected = new HashMap<>();
    expected.put("result", "error_bad_request");
    expected.put("cause", "no request");
    assertEquals(map, expected);
  }

  /** /** Tests makeFailureMap to see if correct map is produced for failure */
  @Test
  public void testSuccessMap() {
    Map<String, Object> map = new CreateResponse().makeSuccessHm("data", List.of(List.of("hi")));
    Map<String, Object> expected = new HashMap<>();
    expected.put("result", "success");
    expected.put("data", List.of(List.of("hi")));
    assertEquals(map, expected);
  }

  /** Tests serializer to see if correct JSON string is produced. */
  @Test
  public void testSerializer() {
    Map<String, Object> map = new CreateResponse().makeFailureHm("error_bad_request", "no request");
    String result = new CreateResponse().serializer(map);
    assertEquals("{\"result\":\"error_bad_request\",\"cause\":\"no request\"}", result);

    Map<String, Object> map2 = new CreateResponse().makeSuccessHm("data", List.of(List.of("hi")));
    String result2 = new CreateResponse().serializer(map2);
    assertEquals("{\"result\":\"success\",\"data\":[[\"hi\"]]}", result2);
  }
}
