package app.domain;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

public interface TranslationService {
  String[] translateText(String[] originalText, Locale sourceLanguage, Locale targetLanguage);

  default String translateText(String originalText, Locale sourceLanguage, Locale targetLanguage) {
    return translateText(new String[]{originalText}, sourceLanguage, targetLanguage)[0];
  }

  /**
   * translate text asynchronously and then set result into translationReceiver
   * @param originalText
   * @param sourceLanguage
   * @param targetLanguage
   * @param translationReceiver
   * @return completion stage
   */
  default CompletionStage<Void> translateTextAsync(String originalText, Locale sourceLanguage, Locale targetLanguage,
                                                   Consumer<String> translationReceiver) {
    return translateTextAsync(originalText, sourceLanguage, targetLanguage)
            .thenAccept(translationReceiver);
  }

  // TODO javadoc
  default CompletionStage<String> translateTextAsync(String originalText, Locale sourceLanguage, Locale targetLanguage) {
    return CompletableFuture.supplyAsync(() -> translateText(originalText, sourceLanguage, targetLanguage));
  }
}
