package edu.brown.cs.student.main.Creator.CustomObjects;

/** Represents a custom Dog object. */
public class Dog {

  public final String name;
  public final String origin;
  public final String size;
  public final boolean friendly;
  public final String intelligence;

  /**
   * Constructor for the Dog class.
   *
   * @param name - the name of the dog
   * @param origin - the origin of the dog
   * @param size - rough estimate of the dog's size
   * @param friendly - measure of how friendly the dog is
   * @param intelligence - rough metric for the dog's intelligence
   */
  public Dog(String name, String origin, String size, boolean friendly, String intelligence) {
    this.name = name;
    this.origin = origin;
    this.size = size;
    this.friendly = friendly;
    this.intelligence = intelligence;
  }

  /**
   * Returns a formatted string representing a Dog object.
   *
   * @return formatted string representing a Dog object
   */
  @Override
  public String toString() {
    return "[Dog Name: "
        + this.name
        + "; Origin: "
        + this.origin
        + "; Average Size: "
        + this.size
        + "; Friendly: "
        + this.friendly
        + "; Intelligence: "
        + this.intelligence
        + "] ";
  }
}
