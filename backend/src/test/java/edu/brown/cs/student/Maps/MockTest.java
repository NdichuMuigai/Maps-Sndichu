package edu.brown.cs.student.Maps;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.brown.cs.student.main.Server.ServerData;
import edu.brown.cs.student.main.Server.handler.FilterGeoJSONHandler;
import edu.brown.cs.student.main.Server.handler.LoadGeoJSONHandler;
import javax.xml.crypto.Data;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Session;

public class MockTest {

  @Mock
  Request mockRequest;

  @Mock
  Response mockResponse;

  @Mock
  Request firstMockRequest;

  @Mock
  Response firstMockResponse;

  @Test
  public void testWithMock() throws Exception {

    mockRequest = mock(Request.class);
    Session mockSession = mock(Session.class);
    QueryParamsMap mockQueryParamsMap = mock(QueryParamsMap.class);
    mockResponse = mock(Response.class);
    // Setup mock behavior
    // When request.session(true) is called, return the mockSession
    when(mockRequest.session(true)).thenReturn(mockSession);

    // When request.queryMap() is called, return the mockQueryParamsMap
    when(mockRequest.queryMap()).thenReturn(mockQueryParamsMap);
    String expectedFileName = "map.geojson";

    when(mockQueryParamsMap.value("file")).thenReturn(expectedFileName);
        ServerData data = new ServerData();
    LoadGeoJSONHandler loadHandler = new LoadGeoJSONHandler(data); // assuming null is acceptable for this constructor
loadHandler.handle(mockRequest, mockResponse);
    System.out.println(data.geojsonData);

    // When mockQueryParamsMap.value("file") is called, return a specific file name
    verify(mockQueryParamsMap, times(1)).value("file");
  verify(mockRequest, times(1)).session(true);
  verify(mockRequest, times(1)).queryMap();
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    // Capture the response body written by your method
//    verify(mockResponse).body(captor.capture());
//    String responseBody = captor.getValue();

    firstMockRequest = mock(Request.class);
    firstMockResponse = mock(Response.class);
//    ServerData data = new ServerData();
    FilterGeoJSONHandler handler = new FilterGeoJSONHandler(data); // assuming null is acceptable for this constructor
//    // Create mock objects
//    QueryParamsMap mockQueryParamsMap = mock(QueryParamsMap.class);
//    when(firstMockRequest.queryMap()).thenReturn(mockQueryParamsMap);
//
//    when(firstMockRequest.queryParams("file")).thenReturn("map.geojson");
    when(mockQueryParamsMap.value("keyword")).thenReturn("Alabama");

    when(firstMockRequest.session(true)).thenReturn(mockSession);

    // When request.queryMap() is called, return the mockQueryParamsMap

    when(firstMockRequest.queryMap()).thenReturn(mockQueryParamsMap);



//    mockRequest = mock(Request.class);
//    mockResponse = mock(Response.class);
    handler.handle(firstMockRequest, firstMockResponse);
//    // Define behavior for mock objects
//    when(firstMockRequest.queryParams("keyword")).thenReturn("Alabama");
//
//    // Create instance of the class to be tested
//    FilterGeoJSONHandler handler = new FilterGeoJSONHandler(null); // assuming null is acceptable for this constructor
//
//    // Call the method to be tested
//    handler.handle(mockRequest, mockResponse);
//
//    // Verify behavior
    verify(firstMockRequest, times(1)).queryParams("keyword");
    verify(firstMockRequest, times(1)).session(true);
  }


  @Test
  public void testBadFile() throws Exception {

    mockRequest = mock(Request.class);
    Session mockSession = mock(Session.class);
    QueryParamsMap mockQueryParamsMap = mock(QueryParamsMap.class);
    mockResponse = mock(Response.class);
    // Setup mock behavior
    // When request.session(true) is called, return the mockSession
    when(mockRequest.session(true)).thenReturn(mockSession);

    // When request.queryMap() is called, return the mockQueryParamsMap
    when(mockRequest.queryMap()).thenReturn(mockQueryParamsMap);
    String expectedFileName = "bad_file";

    when(mockQueryParamsMap.value("file")).thenReturn(expectedFileName);
    ServerData data = new ServerData();
    LoadGeoJSONHandler loadHandler = new LoadGeoJSONHandler(
        data); // assuming null is acceptable for this constructor
    loadHandler.handle(mockRequest, mockResponse);
    System.out.println(data.geojsonData);

    // When mockQueryParamsMap.value("file") is called, return a specific file name
    verify(mockQueryParamsMap, times(1)).value("file");
    verify(mockRequest, times(1)).session(true);
    verify(mockRequest, times(1)).queryMap();
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    // Capture the response body written by your method
  }
}
