package app.service;

import app.domain.TranslationService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Console;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TranslationService backed with DeepL API free web service.
 *
 * @see <a href="https://www.deepl.com/docs-api">DeepL API Docs</a>
 */
public class DeeplTranslationService implements TranslationService {
  static Logger logger = Logger.getLogger(DeeplTranslationService.class.getName());

  String apiKey;
  HttpClient httpClient;

  public DeeplTranslationService() {
    String configApiKey = System.getProperty("apiKey");
    if (configApiKey == null || configApiKey.isBlank()) {
      String inputApiKey = null;
      Console console = System.console();
      if (console != null) {
        inputApiKey = console.readLine("DeepL API Key: ");
      }
      if (inputApiKey == null || inputApiKey.isBlank()) {
        throw new RuntimeException("No API Key provided!");
      }
      apiKey = inputApiKey;
    } else {
      apiKey = configApiKey;
    }
    httpClient = HttpClient.newHttpClient();
  }

  @Override
  public String translateText(String originalText, Locale targetLanguage) {
    String text = originalText;
    try {
      HttpRequest request = createApiRequest(originalText, targetLanguage);
      HttpResponse<JsonElement> httpResponse = httpClient.send(request, this::getApiResponseBodyParser);
      JsonElement body = httpResponse.body();
      text = extractTextFromApiResponse(body, originalText);
    } catch (IOException ex) {
      logger.log(Level.WARNING, "Error translating text", ex);
    } catch (InterruptedException ex) {
      logger.fine("interrupted");
      Thread.currentThread().interrupt();
    }
    return text;
  }

  HttpRequest createApiRequest(String originalText, Locale targetLanguage) {
    String requestBody = String.format("auth_key=%s&text=%s&target_lang=%s",
            apiKey, originalText, targetLanguage.getLanguage().toUpperCase(Locale.ROOT));
    return HttpRequest.newBuilder(URI.create("https://api-free.deepl.com/v2/translate?api_key=" + apiKey))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();
  }

  HttpResponse.BodySubscriber<JsonElement> getApiResponseBodyParser(HttpResponse.ResponseInfo responseInfo) {
    //return HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
    return HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8),
            JsonParser::parseString);
  }

  String extractTextFromApiResponse(JsonElement json, String defaultText) {
    JsonArray translations = json.getAsJsonObject().getAsJsonArray("translations");
    if (translations.isEmpty()) {
      return defaultText;
    }
    JsonObject translation = translations.get(0).getAsJsonObject();
    String text = translation.get("text").getAsString();
    return text;
  }

}
