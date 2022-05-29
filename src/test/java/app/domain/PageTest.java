package app.domain;

import app.mock.MockTranslationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PageTest {

  Page page;
  MockTranslationService translationServiceMock;

  @BeforeEach
  void setUp() {
    page = new Page(URI.create("http://localhost"));
    translationServiceMock = new MockTranslationService();
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
    page.language = Locale.ENGLISH;
    translationServiceMock.mockTranslation("one", Locale.ENGLISH, Locale.GERMAN,"ein");

    page.translate(translationServiceMock, Locale.GERMAN);
    assertEquals("ein", page.getHeadings().get(0).getText());
  }
}