package app.service.translation;

import app.domain.TranslationService;
import app.exception.ConfigurationException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TranslationService backed with DeepL API free web service.
 *
 * @see <a href="https://www.deepl.com/docs-api">DeepL API Docs</a>
 */
public class DeeplTranslationService implements TranslationService {
  static Logger logger = LoggerFactory.getLogger(DeeplTranslationService.class);

  static final String EMPTY = "";

  String apiKey;
  HttpClient httpClient;

  public DeeplTranslationService() {
    apiKey = getApiKey();
    httpClient = HttpClient.newHttpClient();
  }

  static String getApiKey() {
    String configApiKey = System.getProperty("apiKey");
    if (configApiKey == null || configApiKey.isBlank()) {
      String inputApiKey = null;
      Console console = System.console();
      if (console != null) {
        inputApiKey = console.readLine("DeepL API Key: ");
      }
      if (inputApiKey == null || inputApiKey.isBlank()) {
        throw new ConfigurationException("No API Key provided!");
      }
      return inputApiKey;
    }
    return configApiKey;
  }

  @Override
  public String[] translateText(String[] originalText, Locale sourceLanguage, Locale targetLanguage) {
    String[] text = Arrays.copyOf(originalText, originalText.length);
    try {
      HttpRequest request = createApiRequest(originalText, sourceLanguage, targetLanguage);
      HttpResponse<JsonElement> httpResponse = httpClient.send(request, this::getApiResponseBodyParser);
      JsonElement body = httpResponse.body();
      extractTextFromApiResponse(body, text);
    } catch (InterruptedException ex) {
      logger.trace("interrupted");
      Thread.currentThread().interrupt();
    } catch (IOException ex) {
      logger.warn("Error translating text", ex);
    } catch (RuntimeException ex) {
      logger.error("Error translating text", ex);
      throw ex;
    }
    return text;
  }

  HttpRequest createApiRequest(String[] originalText, Locale sourceLanguage, Locale targetLanguage) {
    String requestBody = Stream.concat(
                    Stream.of(encodeParameter("auth_key", apiKey),
                                    encodeParameter("target_lang",
                                            mapLocaleToDeeplLanguage(targetLanguage)),
                                    encodeParameterIfNotEmpty("source_lang",
                                            mapLocaleToDeeplLanguage(sourceLanguage)))
                            .filter(parameter -> !parameter.isEmpty()),
                    Arrays.stream(originalText)
                            .map(text -> encodeParameter("text", text)))
            .collect(Collectors.joining("&"));

    return HttpRequest.newBuilder(URI.create("https://api-free.deepl.com/v2/translate?api_key=" + apiKey))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();
  }

  String encodeParameter(String key, String value) {
    return key + '=' + value;
  }

  String encodeParameterIfNotEmpty(String key, String value) {
    return key != null && value != null && !key.isBlank() && !value.isBlank() ? encodeParameter(key, value) : EMPTY;
  }

  String mapLocaleToDeeplLanguage(Locale locale) {
    return locale != null ? locale.getLanguage().toUpperCase(Locale.ROOT) : EMPTY;
  }

  HttpResponse.BodySubscriber<JsonElement> getApiResponseBodyParser(HttpResponse.ResponseInfo responseInfo) {
    return HttpResponse.BodySubscribers.mapping(
            HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8),
            JsonParser::parseString);
  }

  void extractTextFromApiResponse(JsonElement json, String[] text) {
    JsonArray translations = json.getAsJsonObject().getAsJsonArray("translations");
    if (translations.isEmpty()) {
      return;
    }
    int size = translations.size();
    if (size != text.length) {
      logger.warn("Received {} translations, but requested {}", size, text.length);
      // resilience
      size = Math.min(size, text.length);
    }
    for(int i = 0; i < size; i++) {
      JsonObject translation = translations.get(i).getAsJsonObject();
      String translatedText = translation.get("text").getAsString();
      logger.debug("translated text: {}", translatedText);
      if (translatedText != null && !translatedText.isBlank()) {
        text[i] = translatedText;
      }
    }
  }
}
