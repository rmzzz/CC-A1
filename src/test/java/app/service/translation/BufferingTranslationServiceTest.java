package app.service.translation;

import app.domain.TranslationService;
import app.tests.BaseUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

class BufferingTranslationServiceTest extends BaseUnitTest {
  public static final int TRANSLATIONS_COUNT = 32;

  BufferingTranslationService service;
  TranslationService delegateMock;
  Locale sourceLanguage;
  Locale targetLanguage;

  @BeforeEach
  void setUp() {
    delegateMock = mock(TranslationService.class);
    service = new BufferingTranslationService(delegateMock);
    doAnswer(invocation -> {
      String[] original = invocation.getArgument(0);
      // simulate remote service delay
      sleep(500);
      return Arrays.stream(original)
              .map(s -> s + "-translated")
              .toArray(String[]::new);
    }).when(delegateMock)
            .translateText(any(String[].class), any(Locale.class), any(Locale.class));
    sourceLanguage = Locale.GERMAN;
    targetLanguage = Locale.ENGLISH;
  }

  @Test
  void translateTextAsyncShouldBufferRequests() {
    CompletableFuture<String>[] futures = new CompletableFuture[TRANSLATIONS_COUNT];
    for (int i = 0; i < TRANSLATIONS_COUNT; i++) {
      futures[i] = service.translateTextAsync("test " + i, sourceLanguage, targetLanguage)
              .toCompletableFuture();
    }
    CompletableFuture.allOf(futures).join();
    verify(delegateMock, atMost(TRANSLATIONS_COUNT/2))
            .translateText(any(String[].class), any(Locale.class), any(Locale.class));
  }
}