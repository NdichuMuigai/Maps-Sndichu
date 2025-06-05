package edu.brown.cs.student.main.Creator.CustomObjects;

/** Represents a custom Star object. */
public class Star {

  private final Integer starID;
  private final String properName;
  private final double xCoordinate;
  private final double yCoordinate;
  private final double zCoordinate;

  /**
   * Constructor for the Star class.
   *
   * @param starID - numeric ID for the star
   * @param properName - name of the star
   * @param xCoordinate - X-coordinate of the star
   * @param yCoordinate - Y-coordinate of the star
   * @param zCoordinate - Z-coordinate of the star
   */
  public Star(
      Integer starID,
      String properName,
      double xCoordinate,
      double yCoordinate,
      double zCoordinate) {
    this.starID = starID;
    this.properName = properName;
    this.xCoordinate = xCoordinate;
    this.yCoordinate = yCoordinate;
    this.zCoordinate = zCoordinate;
  }

  /**
   * Returns a formatted string representing a Star object.
   *
   * @return formatted string representing a Star object
   */
  @Override
  public String toString() {
    return "[Star ID: "
        + this.starID
        + "; Proper Name: "
        + this.properName
        + "; X-Coordinate: "
        + this.xCoordinate
        + "; Y-Coordinate: "
        + this.yCoordinate
        + "; Z-Coordinate: "
        + this.zCoordinate
        + "]";
  }
}
