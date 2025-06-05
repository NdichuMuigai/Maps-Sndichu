package edu.brown.cs.student.main.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.io.InputStream;
import okio.Buffer;

public class JsonUtility {

  private final Moshi moshi = new Moshi.Builder().build();

  /**
   * Load JSON content from an InputStream and deserialize it into an object of the given class.
   *
   * @param clientConnection InputStream containing the JSON content
   * @param clazz The class of the target object
   * @param <T> The type of the target object
   * @return An instance of the specified class containing the deserialized data
   * @throws IOException if there's an issue reading from the InputStream or deserializing the JSON
   */
  public <T> T loadJsonFromStream(InputStream clientConnection, Class<T> clazz) throws IOException {
    // Specify the type of the target object using the provided class
    JsonAdapter<T> jsonAdapter = moshi.adapter(clazz);

    // Deserialize the JSON content to an object of the specified class
    T obj = jsonAdapter.fromJson(new Buffer().readFrom(clientConnection));

    return obj;
  }

  /**
   * Checks if any coordinate of a polygon is within the given bounding box.
   *
   * @param multiPolygon      The polygon as a JsonArray of coordinates.
   * @param minLat       Minimum latitude of the bounding box.
   * @param minLng       Minimum longitude of the bounding box.
   * @param maxLat       Maximum latitude of the bounding box.
   * @param maxLng       Maximum longitude of the bounding box.
   * @return             True if any coordinate of the polygon is within the bounding box, false otherwise.
   */
  public static boolean isPolygonInBounds(JsonArray multiPolygon, double minLat, double minLng, double maxLat, double maxLng) {
    if (multiPolygon == null || multiPolygon.size() == 0) {
      return false;
    }
    for (JsonElement polygonElement : multiPolygon) {
//      if (!polygonElement.isJsonArray() || polygonElement.getAsJsonArray().size() == 0) {
//        continue; // Skip if not a JsonArray or empty
//      }
      JsonArray polygon = polygonElement.getAsJsonArray().get(0).getAsJsonArray(); // Assuming the first array is the outer boundary.
//      if (polygon == null || polygon.size() == 0) {
//        continue;
//      }
      for (JsonElement ringElement : polygon) {
//        if (!ringElement.isJsonArray()) {
//          continue; // Skip if not a JsonArray
//        }
        JsonArray ring = ringElement.getAsJsonArray();
//        if (ring.size() < 2) {
//          continue; // Skip if there are not enough elements for a coordinate
//        }
//        for (JsonElement coordElement : ring) {
          JsonArray coord = ring.getAsJsonArray();
//        if (coord == null || coord.size() < 2) {
//          continue;
//        }

        double lng = coord.get(0).getAsDouble();
          double lat = coord.get(1).getAsDouble();

          if (lng >= minLng && lng <= maxLng && lat >= minLat && lat <= maxLat) {
          } else {
            return false; // all of polygon has to be in bounds
          }
//        }
      }
    }
    return true; //
  }
}
