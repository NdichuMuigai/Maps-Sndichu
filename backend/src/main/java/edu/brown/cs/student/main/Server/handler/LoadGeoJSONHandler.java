package edu.brown.cs.student.main.Server.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.brown.cs.student.main.Server.ServerData;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

/** LoadGeoJSONHandler class for loading the GeoJSON file. */
public class LoadGeoJSONHandler implements Route {
  private ServerData data;

  /**
   * Constructor for LoadGeoJSONHandler.
   * @param data
   */
  public LoadGeoJSONHandler(ServerData data) {
    this.data = data;
  }

  /**
   * Loads the GeoJSON file into the server and checks that the input file is valid by seeing if the
   * file exists.
   *
   * @param request the HTTP request
   * @param response the HTTP response
   * @return the response object
   * @throws Exception if there is an error in processing the request
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {

    Session session = request.session(true); // Create a new session if one does not exist
    CreateResponse myResponse = new CreateResponse();
    QueryParamsMap qm = request.queryMap();
    String file = qm.value("file");

    if (file == null) {
      return myResponse.serializer(
          myResponse.makeFailureHm(
              "error_bad_request", "Missing 'file' query param. i.e. 'file=(filename)'."));
    }

    String filePath = "./data/" + file;

    try {
      // Check if file exists and is not a directory
      if (!Files.exists(Paths.get(filePath)) || Files.isDirectory(Paths.get(filePath))) {
        throw new FileNotFoundException("File does not exist or is a directory: " + filePath);
      }

      // reading the file
      Reader fReader = new FileReader(filePath);

      // Parse the JSON file using Gson
      JsonElement jsonElement = JsonParser.parseReader(fReader);
      if (!jsonElement.isJsonObject()) {
        return myResponse.serializer(
            myResponse.makeFailureHm("error_bad_json", "File content is not a valid JSON object."));
      }

      this.data.geojsonData = jsonElement.getAsJsonObject();
      // serialize the response
      return myResponse.serializer(myResponse.makeSuccessHm("file", file));
    } catch (FileNotFoundException e) {
      return myResponse.serializer(
          myResponse.makeFailureHm("error_file_not_found", "Could not read file: " + file));
    }
  }

  /**
   * Returns the GeoJSON data.
   * @return
   */
  public JsonObject getJsonData() {
    return this.data.geojsonData;
  }
}
