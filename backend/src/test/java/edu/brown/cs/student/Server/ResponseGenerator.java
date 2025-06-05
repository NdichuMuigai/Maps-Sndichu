package edu.brown.cs.student.Server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseGenerator {

  /**
   * Generates a success response map.
   *
   * @param key the key for the data.
   * @param data the data to be included in the response.
   * @return a map representing a success response.
   */
  public Map<String, Object> generateSuccessResponse(String key, List<Object> data) {
    Map<String, Object> response = new HashMap<>();
    response.put("status", "success");
    response.put(key, data);
    return response;
  }

  /**
   * Generates an error response map.
   *
   * @param errorMessage the error message to be included in the response.
   * @return a map representing an error response.
   */
  public Map<String, Object> generateErrorResponse(String errorMessage) {
    Map<String, Object> response = new HashMap<>();
    response.put("result", "error");
    response.put("message", errorMessage);
    return response;
  }
}
