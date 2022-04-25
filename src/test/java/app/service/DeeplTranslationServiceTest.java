package app.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeeplTranslationServiceTest {
  DeeplTranslationService deepl;
  HttpClient httpClientMock;
  HttpResponse<JsonElement> responseMock;
  JsonObject apiResponse;

  @BeforeEach
  void setUp() throws Exception {
    System.setProperty("apiKey", "test");
    deepl = new DeeplTranslationService();
    httpClientMock = mock(HttpClient.class);
    deepl.httpClient = httpClientMock;

    responseMock = mock(HttpResponse.class);
    apiResponse = new JsonObject();
    JsonArray translations = new JsonArray();
    apiResponse.add("translations", translations);
    JsonObject translation = new JsonObject();
    translation.addProperty("text", "Hallo Welt!");
    translation.addProperty("detected_source_language", "EN");
    translations.add(translation);
    doAnswer(invocation -> apiResponse).when(responseMock).body();
    doAnswer(invocation -> responseMock).when(httpClientMock).send(any(), any());
  }

  @AfterEach
  void tearDown() {
    reset(responseMock, httpClientMock);
  }

  @Test
  void translateText() {
    String text = deepl.translateText("Hello World!", Locale.ENGLISH, Locale.GERMAN);
    assertEquals("Hallo Welt!", text);
  }

  @Test
  void createApiRequest() {
    HttpRequest apiRequest = deepl.createApiRequest("test", Locale.ENGLISH, Locale.GERMAN);
    assertEquals("POST", apiRequest.method());
    assertEquals("https://api-free.deepl.com/v2/translate?api_key=test", apiRequest.uri().toString());
    assertTrue(apiRequest.bodyPublisher().isPresent());
  }

  @Test
  void extractTextFromApiResponse() {
    String defaultText = "test";
    String text = deepl.extractTextFromApiResponse(apiResponse, defaultText);
    assertEquals("Hallo Welt!", text);
  }
}