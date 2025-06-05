package edu.brown.cs.student.Server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import edu.brown.cs.student.main.Creator.CustomObjects.Dog;
import edu.brown.cs.student.main.Creator.CustomObjects.Person;
import edu.brown.cs.student.main.Json.JsonUtility;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class JsonUtilTest {

  @Test
  public void testLoadJsonFromStream() throws Exception {
    // Sample JSON string to be tested
    String json = "{\"name\":\"John Doe\",\"age\":30}";

    // Convert string to InputStream
    InputStream stream = new ByteArrayInputStream(json.getBytes());

    // Call the method to test
    JsonUtility utility = new JsonUtility();
    Person person = utility.loadJsonFromStream(stream, Person.class);

    // Assertions to check the deserialization
    assertNotNull(person);
    assertEquals("John Doe", person.name);
    assertEquals(30, person.age);
  }

  @Test
  public void testLoadDogJsonFromStream() throws Exception {
    // Sample JSON string for Dog object
    String dogJson =
        "{\"name\":\"Buddy\",\"origin\":\"USA\",\"size\":\"Medium\",\"friendly\":true,\"intelligence\":\"High\"}";

    // Convert string to InputStream
    InputStream stream = new ByteArrayInputStream(dogJson.getBytes(StandardCharsets.UTF_8));

    // Call the method to test
    JsonUtility utility = new JsonUtility();
    Dog dog = utility.loadJsonFromStream(stream, Dog.class);

    // Assertions to check the deserialization
    assertNotNull(dog);
    assertEquals("Buddy", dog.name);
    assertEquals("USA", dog.origin);
    assertEquals("Medium", dog.size);
    assertEquals(true, dog.friendly);
    assertEquals("High", dog.intelligence);
  }
}
