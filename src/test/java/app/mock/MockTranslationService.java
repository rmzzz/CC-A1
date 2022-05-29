package app.mock;

import app.domain.TranslationService;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class MockTranslationService implements TranslationService {
  final ConcurrentHashMap<String, String> translations = new ConcurrentHashMap<>();

  public void mockTranslation(String originalText, Locale sourceLanguage, Locale targetLanguage, String translatedText) {
    translations.put(translationKey(originalText, sourceLanguage, targetLanguage), translatedText);
  }

  public void reset() {
    translations.clear();
  }

  private static String translationKey(String originalText, Locale sourceLanguage, Locale targetLanguage) {
    return originalText + "_" + sourceLanguage + "_" + targetLanguage;
  }

  @Override
  public String[] translateText(String[] originalText, Locale sourceLanguage, Locale targetLanguage) {
    String[] text = Arrays.copyOf(originalText, originalText.length);
    for (int i = 0; i < text.length; i++) {
      text[i] = translations.getOrDefault(
              translationKey(originalText[i], sourceLanguage, targetLanguage),
              originalText[i]);
    }
    return text;
  }
}
