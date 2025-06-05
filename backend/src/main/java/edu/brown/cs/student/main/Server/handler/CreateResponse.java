package edu.brown.cs.student.main.Server.handler;

import com.squareup.moshi.Moshi;
import java.util.LinkedHashMap;
import java.util.Map;

/** Class that creates the JSON response to be sent to the client. */
public class CreateResponse {

  private Map<String, Object> responseHm;

  /**
   * Makes the failure map, which indicates failure and whatever else to be displayed on failure.
   *
   * @param typeError
   * @param msgError
   * @return respHm
   */
  public Map<String, Object> makeFailureHm(String typeError, Object msgError) {
    Map<String, Object> respHm = new LinkedHashMap<>();
    respHm.put("result", typeError);
    respHm.put("message", msgError);
    this.responseHm = respHm;
    return respHm;
  }

  /**
   * Makes the success map, which indicates success and whatever else to be displayed on success.
   *
   * @param responseTitle
   * @param responseData
   * @return myResponse
   */
  public Map<String, Object> makeSuccessHm(String responseTitle, Object responseData) {
    Map<String, Object> myResponse = new LinkedHashMap<>();
    myResponse.put("result", "success");
    myResponse.put(responseTitle, responseData);
    this.responseHm = myResponse;
    return myResponse;
  }

  /**
   * Returns the response map.
   *
   * @return this.responseHm
   */
  public Map<String, Object> getResponseHm() {
    return this.responseHm;
  }

  /**
   * Serializes the response map into a JSON string.
   *
   * @param myResponse
   * @return
   */
  public String serializer(Map<String, Object> myResponse) {
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(Map.class).toJson(myResponse);
  }
}
