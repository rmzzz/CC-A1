package app.domain;

import app.mock.MockTranslationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HeadingTest {
  Heading heading;

  MockTranslationService translationServiceMock = new MockTranslationService();

  @BeforeEach
  void setUp() {
    heading = new Heading("test", 1);
  }

  @AfterEach
  void tearDown() {
    translationServiceMock.reset();
  }

  @Test
  void getText() {
    assertEquals("test", heading.getText());
  }

  @Test
  void getRank() {
    assertEquals(1, heading.getRank());
  }

  @Test
  void translate() {
    translationServiceMock.mockTranslation("test", Locale.ENGLISH, Locale.GERMAN, "Test");

    heading.translate(translationServiceMock, Locale.ENGLISH, Locale.GERMAN)
            .toCompletableFuture().join();
    assertEquals("Test", heading.getText());
  }
}