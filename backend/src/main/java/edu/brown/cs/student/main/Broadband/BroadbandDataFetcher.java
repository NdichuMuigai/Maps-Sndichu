package edu.brown.cs.student.main.Broadband;

import edu.brown.cs.student.main.Broadband.requests.PlainRequester;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BroadbandDataFetcher {

  private final Map<String, String> stateCodes = new HashMap<>(); // Cache state codes

  public BroadbandDataFetcher() throws IOException {
    fetchStateCodes(); // Initialize state codes
  }

  private void fetchStateCodes() throws IOException {
    PlainRequester requester = new PlainRequester();
    String codesUrl = "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*";
    List<List<String>> responseBody = requester.makeRequestAndDeserialize(codesUrl, List.class);
    //    System.out.println(responseBody);
    //
    for (List<String> row : responseBody) {
      // Assuming the state name is the first element and the state code is the second element in
      // each row
      String stateName = row.get(0);
      String stateCode = row.get(1);
      stateCodes.put(stateName, stateCode);
    }
  }

  private List<BroadbandData> processBroadbandResponseBody(List<List<String>> responseBody) {
    List<BroadbandData> broadbandData = new ArrayList<>();
    int i = 0;
    for (List<String> row : responseBody) {
      if (i != 0) {
        BroadbandData data = new BroadbandData();
        data.name = row.get(0);
        data.coverage = Float.parseFloat(row.get(1));
        data.stateCode = row.get(2);
        data.countyCode = row.get(3);
        broadbandData.add(data);
      }
      i++;
    }

    return broadbandData;
  }

  public List<BroadbandData> mockFetchBroadbandResponseBody(List<List<String>> responseBody) {
    List<BroadbandData> data = new ArrayList<>();
    BroadbandData data1 = new BroadbandData();
    data1.name = "Alameda County, California";
    data1.coverage = 0.9f;
    data1.stateCode = "06";
    data1.countyCode = "001";
    data.add(data1);
    return data;
  }

  public List<BroadbandData> fetchDataForStateAndCounty(String state, String county)
      throws IOException, IllegalArgumentException {
    // Get the codes for the specified state and county
    String stateCode;
    if (state == "*") {
      stateCode = "*";
    } else {
      this.fetchStateCodes();
      stateCode = stateCodes.get(state);
      if (stateCode == null) {

        throw new IllegalArgumentException("Invalid state: " + state);
      }
    }
    String countyCode;

    if (Objects.equals(county, "*")) {
      countyCode = "*";
    } else {
      countyCode = getCountyCode(stateCode, county);
      if (countyCode == null) {
        throw new IllegalArgumentException("Invalid county: " + county);
      }
    }

    // Construct the URL for the API endpoint using the state and county codes
    // (Assuming there's a specific format for the endpoint URL)
    String dataUrl =
        String.format(
            "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:%s&in=state:%s",
            countyCode, stateCode);

    // Use PlainRequester to make the request and deserialize the response
    PlainRequester requester = new PlainRequester();
    List responseBody = requester.makeRequestAndDeserialize(dataUrl, List.class);

    // Process the response body to create a BroadbandResponse object
    List<BroadbandData> broadbandResponse = this.processBroadbandResponseBody(responseBody);

    return broadbandResponse;
  }

  private List<BroadbandData> processCountyResponseBody(List<List<String>> responseBody) {
    List<BroadbandData> broadbandData = new ArrayList<>();
    Integer i = 0;
    for (List<String> row : responseBody) {
      if (i != 0) {
        BroadbandData data = new BroadbandData();
        data.name = row.get(0);
        data.stateCode = row.get(1);
        data.countyCode = row.get(2);
        broadbandData.add(data);
      }
      i++;
    }

    return broadbandData;
  }

  private String findCountyCode(List<BroadbandData> data, String county) {
    if (county == "*") {
      return "*";
    }
    for (BroadbandData broadbandData : data) {
      if (broadbandData.name.contains(county)) {
        return broadbandData.countyCode;
      }
    }
    return null;
  }

  private String getCountyCode(String stateCode, String county) throws IOException {
    String dataUrl =
        String.format(
            "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:%s",
            stateCode);
    // Use PlainRequester to make the request and deserialize the response
    PlainRequester requester = new PlainRequester();
    List<List<String>> responseBody = requester.makeRequestAndDeserialize(dataUrl, List.class);
    List<BroadbandData> data = processCountyResponseBody(responseBody);
    String countyCode = findCountyCode(data, county);
    return countyCode;
  }

  public String mockGetCountyCode(String statecode, String county) {
    return "06";
  }
}
