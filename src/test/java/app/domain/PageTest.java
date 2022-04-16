package app.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PageTest {

  Page page;

  @BeforeEach
  void setUp() {
    page = new Page(URI.create("http://localhost"));
  }

  @Test
  void getPageUrl() {
    assertEquals(URI.create("http://localhost"), page.getPageUrl());
  }

  @Test
  void addHeading() {
    page.addHeading(new Heading("h1", 1));
    assertEquals(1, page.getHeadings().size());
    assertEquals(1, page.getHeadings().get(0).getRank());
  }

  @Test
  void addLink() {
    page.addLink(new Link(URI.create("http://localhost"), "self"));
    assertEquals(1, page.getLinks().size());
    assertEquals(URI.create("http://localhost"), page.getLinks().get(0).url);
  }

  @Test
  void translate() {
    page.addHeading(new Heading("one", 1));
    TranslationService translationServiceMock = mock(TranslationService.class);
    doReturn("ein").when(translationServiceMock).translateText("one", Locale.GERMAN);

    page.translate(translationServiceMock, Locale.GERMAN);
    assertEquals("ein", page.getHeadings().get(0).getText());

    verify(translationServiceMock).translateText("one", Locale.GERMAN);
    verifyNoMoreInteractions(translationServiceMock);
  }
}