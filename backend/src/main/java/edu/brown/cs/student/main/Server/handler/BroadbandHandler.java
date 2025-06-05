package edu.brown.cs.student.main.Server.handler;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Broadband.BroadbandData;
import edu.brown.cs.student.main.Broadband.BroadbandDataFetcher;
import edu.brown.cs.student.main.Server.ServerData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler class for the broadband API endpoint. This class handles the request and response for the
 * broadband API endpoint. This class is responsible for deserializing the request, calling the
 * BroadbandDataFetcher class to fetch the data, and serializing the response.
 */
public class BroadbandHandler implements Route {

  private final ServerData data;

  /** Constructor for Broadband handler */
  public BroadbandHandler(ServerData data) {
    this.data = data;
  }

  @Override
  public Object handle(Request request, Response response) {
    String state = request.queryParams("state");
    String county = request.queryParams("county");
    CreateResponse createResponse = new CreateResponse();
    Map<String, Object> map = new HashMap<>();
    try {
      if (state == null || state.isEmpty()) {
        return createResponse.makeFailureHm("error_bad_request", "State is missing");
      }
      if (county == null || county.isEmpty()) {
        county = "*";
      }

      map = this.BroadbandResponseHelper(state, county, createResponse);

      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(Map.class).toJson(map);

    } catch (Exception e) {
      map.put("message", e.getMessage());
      return createResponse.serializer(map); // Handle exception and return error response
    }
  }

  public Map<String, Object> BroadbandResponseHelper(
      String state, String county, CreateResponse myResponse) throws IOException {

    Map<String, Object> response = new HashMap<>();
    BroadbandDataFetcher fetcher = new BroadbandDataFetcher();
    List<BroadbandData> data;
    try {
      data = fetcher.fetchDataForStateAndCounty(state, county);

    } catch (IllegalArgumentException e) {
      return myResponse.makeFailureHm("error_bad_request", e.getMessage());
    } catch (IOException e) {
      return myResponse.makeFailureHm("error_datasource", e.getMessage());
    }
    response.put("state", state);
    response.put("county", county);

    List<Map<String, Object>> dataMapList = new ArrayList<>();
    for (BroadbandData broadbandData : data) {
      Map<String, Object> dataMap = new HashMap<>();
      dataMap.put("name", broadbandData.name);
      dataMap.put("coverage", broadbandData.coverage);
      dataMap.put("statecode", broadbandData.stateCode);
      dataMap.put("countycode", broadbandData.countyCode);
      dataMapList.add(dataMap);
    }

    response.put("datetime", String.valueOf(new Date()));
    response.put("data", dataMapList);
    response.put("result", "success");

    return response;
  }
}
