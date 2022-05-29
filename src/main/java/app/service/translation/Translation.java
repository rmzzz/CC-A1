package app.service.translation;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Translation {
  final String originalText;
  final Locale sourceLanguage;
  final Locale targetLanguage;

  final CompletableFuture<String> translationFuture;

  public Translation(String originalText, Locale sourceLanguage, Locale targetLanguage) {
    this.originalText = originalText;
    this.sourceLanguage = sourceLanguage;
    this.targetLanguage = targetLanguage;
    translationFuture = new CompletableFuture<>();
  }

  @Override
  public boolean equals(Object other) {
    if (this == other)
      return true;
    if (other instanceof Translation that) {
      return originalText.equals(that.originalText)
              && sourceLanguage.equals(that.sourceLanguage)
              && targetLanguage.equals(that.targetLanguage);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(originalText, sourceLanguage, targetLanguage);
  }

  public CompletableFuture<String> getTranslationFuture() {
    return translationFuture;
  }

  String getTranslationDirection() {
    return sourceLanguage + "->" + targetLanguage;
  }
}
