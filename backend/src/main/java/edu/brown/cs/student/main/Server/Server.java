package edu.brown.cs.student.main.Server;

import static spark.Spark.after;

import edu.brown.cs.student.main.Server.handler.BroadbandHandler;
import edu.brown.cs.student.main.Server.handler.LoadGeoJSONHandler;
import edu.brown.cs.student.main.Server.handler.LoadHandler;
import edu.brown.cs.student.main.Server.handler.FilterGeoJSONHandler;
import edu.brown.cs.student.main.Server.handler.SearchHandler;
import edu.brown.cs.student.main.Server.handler.SearchJSONHandler;
import edu.brown.cs.student.main.Server.handler.ViewGeoJSONHandler;
import edu.brown.cs.student.main.Server.handler.ViewHandler;
import java.io.IOException;
import spark.Spark;

/** The Server class sets up the Spark server and the endpoints for the Project2. */
public class Server {
  public static void main(String[] args) throws IOException {
    int PORT = 8080;
    Spark.port(PORT);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
          response.header("Content-Type", "application/json");
        });

    ServerData data = new ServerData();
    // Setting up Spark Project2 on search, view, and load endpoints
    Spark.get("searchcsv", new SearchHandler(data));
    Spark.get("viewcsv", new ViewHandler(data));
    Spark.get("loadcsv", new LoadHandler(data));
    Spark.get("broadband", new BroadbandHandler(data));
    Spark.get("loadjson", new LoadGeoJSONHandler(data));
    Spark.get("viewjson", new ViewGeoJSONHandler(data));
    Spark.get("filterjson", new FilterGeoJSONHandler(data));
    Spark.get("searchjson", new SearchJSONHandler(data));

    Spark.init();
    Spark.awaitInitialization();
    System.out.println("Project2 started at http://localhost:" + PORT);
  }
}
