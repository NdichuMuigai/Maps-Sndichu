package edu.brown.cs.student.main.Broadband.requests;

import java.io.IOException;

/**
 * Interface for edu.brown.cs.student.main.requests.Broadband.Requester that makes HTTP requests to
 * a specified URL and deserializes the response into Java objects.
 */
public interface Requester {
  public <T> T makeRequestAndDeserialize(String url, Class<T> goalClass) throws IOException;
}
