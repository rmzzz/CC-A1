package app.service.translation;

import app.domain.TranslationService;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CachingTranslationService implements TranslationService {
  final TranslationService delegate;
  final ConcurrentMap<String, String> cache = new ConcurrentHashMap<>();

  public CachingTranslationService(TranslationService delegate) {
    this.delegate = delegate;
  }

  protected static String translationKey(String originalText, Locale sourceLanguage, Locale targetLanguage) {
    return originalText + "_" + sourceLanguage + "_" + targetLanguage;
  }

  @Override
  public String[] translateText(String[] originalText, Locale sourceLanguage, Locale targetLanguage) {
    CompletableFuture<String>[] translations = Arrays.stream(originalText)
            .map(text -> translateTextAsync(text, sourceLanguage, targetLanguage))
            .map(CompletionStage::toCompletableFuture)
            .toArray(size -> new CompletableFuture[size]);
    CompletableFuture.allOf(translations)
            .join();
    String[] translatedText = new String[originalText.length];
    for (int i = 0; i < translations.length; i++) {
      String text = translations[i]
              .getNow(originalText[i]); // already joined all the stages, thus, we can use getNow instead of get
      translatedText[i] = text;
    }
    return translatedText;
  }

  @Override
  public CompletionStage<String> translateTextAsync(String originalText, Locale sourceLanguage, Locale targetLanguage) {
    String key = translationKey(originalText, sourceLanguage, targetLanguage);
    String text = cache.get(key);
    if (text != null) {
      return CompletableFuture.completedFuture(text);
    }
    return delegate.translateTextAsync(originalText, sourceLanguage, targetLanguage)
            .whenComplete((result, error) -> {
              if (result != null && error == null) {
                cache.put(key, result);
              }
            });
  }
}
