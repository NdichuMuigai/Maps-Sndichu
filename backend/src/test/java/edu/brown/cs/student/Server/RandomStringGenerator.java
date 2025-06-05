package edu.brown.cs.student.Server;

import java.util.Random;

public class RandomStringGenerator {

  private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private static final String[] VALID_COLUMNS = {
    "Column1", "Column2", "Column3", "Column4", "Column5"
  };
  private final Random random;

  public RandomStringGenerator() {
    this.random = new Random();
  }

  public String generateRandomString() {
    StringBuilder builder = new StringBuilder();
    int length = random.nextInt(10) + 1; // Random length between 1 and 10
    for (int i = 0; i < length; i++) {
      builder.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
    }
    return builder.toString();
  }

  public double randomCoordinate() {
    return random.nextDouble() * 180 - 90;
  }

  public double randomDouble() {
    return random.nextDouble() * 1000 - 500;
  }

  public String generateRandomValidCol() {
    return VALID_COLUMNS[random.nextInt(VALID_COLUMNS.length)];
  }
}
