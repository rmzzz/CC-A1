package app.domain;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

public interface TranslationService {
  /**
   * translate an array of text strings synchronously
   * @param originalText An original text to translate
   * @param sourceLanguage A source language to translate from
   * @param targetLanguage A target language to translate to
   * @return an array of translated texts
   */
  String[] translateText(String[] originalText, Locale sourceLanguage, Locale targetLanguage);

  /**
   * translate single string of text synchronously
   * @param originalText An original text to translate
   * @param sourceLanguage A source language to translate from
   * @param targetLanguage A target language to translate to
   * @return translated text
   */
  default String translateText(String originalText, Locale sourceLanguage, Locale targetLanguage) {
    return translateText(new String[]{originalText}, sourceLanguage, targetLanguage)[0];
  }

  /**
   * translate text asynchronously and then set result into translationReceiver
   * @param originalText An original text to translate
   * @param sourceLanguage A source language to translate from
   * @param targetLanguage A target language to translate to
   * @param translationReceiver A consumer of translated text
   * @return completion stage that completes when consumer receives the translated text
   */
  default CompletionStage<Void> translateTextAsync(String originalText, Locale sourceLanguage, Locale targetLanguage,
                                                   Consumer<String> translationReceiver) {
    return translateTextAsync(originalText, sourceLanguage, targetLanguage)
            .thenAccept(translationReceiver);
  }

  /**
   * translate text asynchronously and then returns result as completion stage
   * @param originalText An original text to translate
   * @param sourceLanguage A source language to translate from
   * @param targetLanguage A target language to translate to
   * @return completion stage that completes when translated text is ready
   */
  default CompletionStage<String> translateTextAsync(String originalText, Locale sourceLanguage, Locale targetLanguage) {
    return CompletableFuture.supplyAsync(() -> translateText(originalText, sourceLanguage, targetLanguage));
  }
}
