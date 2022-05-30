package app.service.translation;

import app.domain.TranslationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class BufferingTranslationService implements TranslationService {
  final TranslationService delegate;

  final ConcurrentLinkedDeque<Translation> translationQueue = new ConcurrentLinkedDeque<>();
  ExecutorService executor;

  public BufferingTranslationService(TranslationService delegate) {
    this.delegate = delegate;
    // use single thread executor in order to protect delegate service from concurrent requests
    executor = Executors.newSingleThreadExecutor();
  }

  @Override
  public String[] translateText(String[] originalText, Locale sourceLanguage, Locale targetLanguage) {
    // only async methods can be buffered!
    return delegate.translateText(originalText, sourceLanguage, targetLanguage);
  }

  @Override
  public CompletionStage<String> translateTextAsync(String originalText, Locale sourceLanguage, Locale targetLanguage) {
    Translation translation = new Translation(originalText, sourceLanguage, targetLanguage);
    translationQueue.add(translation);
    runUpdateTranslationsSequentially();
    return translation.getTranslationFuture();
  }

  void runUpdateTranslationsSequentially() {
    CompletableFuture.runAsync(this::updateTranslations, executor);
  }

  void updateTranslations() {
    var translationsMap = pollAllTranslationFromQueue();
    for(var translations : translationsMap.values()) {
      String[] originalTexts = translations.stream()
              .map(t -> t.originalText)
              .toArray(String[]::new);
      Translation firstTranslation = translations.get(0);
      Locale sourceLanguage = firstTranslation.sourceLanguage;
      Locale targetLanguage = firstTranslation.targetLanguage;
      String[] translatedTexts = delegate.translateText(originalTexts, sourceLanguage, targetLanguage);
      for(int i = 0; i < translatedTexts.length; i++) {
        CompletableFuture<String> future = translations.get(i).getTranslationFuture();
        String text = translatedTexts[i];
        future.complete(text);
      }
    }
  }

  Map<String, List<Translation>> pollAllTranslationFromQueue() {
    List<Translation> queueSnapshot = new ArrayList<>();
    synchronized (translationQueue) {
      for (Translation translation = translationQueue.poll();
           translation != null;
           translation = translationQueue.poll()) {
        queueSnapshot.add(translation);
      }
    }
    return queueSnapshot.stream()
            .collect(Collectors.groupingBy(Translation::getTranslationDirection));
  }
}
