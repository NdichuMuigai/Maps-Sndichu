package edu.brown.cs.student.main.Broadband;
//

//
/// **
// * Class that handles deserialization of the JSON response from the Broadband API. This class
// * contains wrapper classes that are used to deserialize the JSON response into Java objects.
// */
// public class BroadbandData {
//
//  /** Wrapper class contains the broadband coverage percentage and unit of measurement. */
//  public record Coverage(
//      @Json(name = "percentage") double percentage, @Json(name = "unit") String unit) {}
////      @Json(name = "NAME") String name,
////  "NAME","S2802_C03_022E","state","county"
//
//  /**
//   * Wrapper class contains the Coverage data and the time the JSON was generated.
//   *
//   * @param coverageList
//   * @param time
//   */
//  public record CoverageProperties(
//      @Json(name = "coverageList") List<Coverage> coverageList,
//      @Json(name = "generatedAt") String time) {}
//
//  /**
//   * Wrapper class contains the coordinates attribute of the grid point JSON.
//   *
//   * @param coordinates
//   */
//  public record Coordinates(@Json(name = "coordinates") List<Double> coordinates) {}
//
//  /**
//   * Wrapper class contains the CoverageProperties attribute of the Broadband JSON.
//   *
//   * @param properties
//   */
//  public record BroadbandResponse(@Json(name = "properties") CoverageProperties properties) {}
//
//  /** Wrapper class – contains the properties and geometry attributes of the grid point JSON. */
//  public record GridResponse(
//      @Json(name = "properties") GridProperties properties,
//      @Json(name = "geometry") Coordinates coordinates) {}
//
//  /**
//   * Wrapper class contains the broadband data endpoint attribute of the grid point JSON.
//   *
//   * @param endpoint
//   */
//  public record GridProperties(@Json(name = "broadbandData") String endpoint) {}
// }

public class BroadbandData {
  public String name;
  public float coverage;
  public String stateCode;
  public String countyCode;
}
