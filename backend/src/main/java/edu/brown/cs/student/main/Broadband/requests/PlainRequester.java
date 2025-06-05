package edu.brown.cs.student.main.Broadband.requests;

import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import okio.Buffer;

/**
 * The edu.brown.cs.student.main.requests.Broadband.PlainRequester class encapsulates the
 * functionality for sending HTTP requests to a specified URL and deserializing the response into
 * Java objects. It provides a reusable method, 'requestToInstantiate', which accepts a URL string
 * and a target class type for deserialization. Inside this method, it establishes a connection to
 * the remote resource, checks for a successful response (HTTP code 200), and utilizes the Moshi
 * library to transform the response data into a Java object of the specified type. This class
 * serves as a valuable component for making HTTP requests and handling responses in a
 * straightforward manner.
 */
public class PlainRequester implements Requester {

  public <T> T makeRequestAndDeserialize(String url, Class<T> goalClass) throws IOException {
    URL urlReq = new URL(url);
    HttpURLConnection clientConnection = (HttpURLConnection) urlReq.openConnection();
    clientConnection.connect();
    T response = null;
    // Check for successful response
    if (clientConnection.getResponseCode() == 200) {
      Moshi moshi = new Moshi.Builder().build();
      response =
          moshi
              .adapter(goalClass)
              .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    }
    clientConnection.disconnect();
    return response;
  }
}
