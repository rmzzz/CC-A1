package app.service.translation;

import java.util.Locale;
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

  public CompletableFuture<String> getTranslationFuture() {
    return translationFuture;
  }

  String getTranslationDirection() {
    return sourceLanguage + "->" + targetLanguage;
  }

  @Override
  public String toString() {
    return "Translation(" + getTranslationDirection() + ", \"" + originalText + "\")";
  }
}
