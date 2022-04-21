package app.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HeadingTest {
  Heading heading;

  @BeforeEach
  void setUp() {
    heading = new Heading("test", 1);
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
    TranslationService translationServiceMock = mock(TranslationService.class);
    doReturn("Test").when(translationServiceMock).translateText("test", Locale.GERMAN);

    heading.translate(translationServiceMock, Locale.GERMAN);
    assertEquals("Test", heading.getText());

    verify(translationServiceMock).translateText("test", Locale.GERMAN);
    verifyNoMoreInteractions(translationServiceMock);

  }
}