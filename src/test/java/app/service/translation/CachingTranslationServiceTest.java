package app.service.translation;

import app.domain.TranslationService;
import app.tests.BaseUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CachingTranslationServiceTest extends BaseUnitTest {

  TranslationService delegate;
  CachingTranslationService service;
  Locale sourceLanguage;
  Locale targetLanguage;

  @BeforeEach
  void setUp() {
    delegate = mock(TranslationService.class);
    doAnswer(invocation -> CompletableFuture.completedFuture("translated-" + invocation.getArgument(0)))
            .when(delegate)
            .translateTextAsync(anyString(), any(Locale.class), any(Locale.class));

    service = new CachingTranslationService(delegate);
    sourceLanguage = Locale.GERMAN;
    targetLanguage = Locale.ENGLISH;
  }

  @Test
  void translationKey() {
    String key = CachingTranslationService.translationKey("text", sourceLanguage, targetLanguage);
    assertEquals("text_de_fr", key);
  }

  @Test
  void translateTextShouldUseCache() {
    String[] originalText = {"one", "two"};
    service.cache.put(CachingTranslationService.translationKey("one", sourceLanguage, targetLanguage), "cached");
    String[] translatedText = service.translateText(originalText, sourceLanguage, targetLanguage);
    assertArrayEquals(new String[]{"cached", "translated-two"}, translatedText);
  }

  @Test
  void translateTextShouldFillCache() {
    String[] originalText = {"one", "two"};
    assertTrue(service.cache.isEmpty());
    String[] translatedText = service.translateText(originalText, sourceLanguage, targetLanguage);
    assertEquals(Set.of(translatedText), new HashSet<>(service.cache.values()));
  }

  @Test
  void translateTextAsync() throws Exception {
    CompletionStage<String> future = service.translateTextAsync("text", sourceLanguage, targetLanguage);
    assertEquals("translated-text", future.toCompletableFuture().get());
  }
}