package app.domain;

import app.exception.BrokenLinkException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebCrawlerTest {
  WebCrawler crawler;
  InputParameters inputParametersMock;
  PageLoader pageLoaderMock;
  TranslationService translationServiceMock;
  URI targetUrl;
  int targetDepth;
  Locale targetLocale;
  Locale sourceLanguage;

  @BeforeEach
  void setUp() throws Exception {
    inputParametersMock = mock(InputParameters.class);
    targetUrl = new URI("http://localhost");
    targetDepth = 3;
    targetLocale = Locale.GERMAN;
    sourceLanguage = Locale.ENGLISH;
    when(inputParametersMock.getTargetLanguage()).thenReturn(targetLocale);
    when(inputParametersMock.getUrl()).thenReturn(targetUrl);
    when(inputParametersMock.getDepth()).thenReturn(targetDepth);

    pageLoaderMock = mock(PageLoader.class);
    translationServiceMock = mock(TranslationService.class);
    when(translationServiceMock.translateText(any(String.class), any(Locale.class), any(Locale.class))).then(i -> i.getArgument(0));
    crawler = new WebCrawler(inputParametersMock, pageLoaderMock, translationServiceMock);
  }

  @AfterEach
  void tearDown() {
    reset(inputParametersMock, pageLoaderMock, translationServiceMock);
  }

  @Test
  void crawlUrlSingleDepth() throws Exception {
    Set<URI> visited = new HashSet<>();
    Page page = new Page(targetUrl);
    page.language = sourceLanguage;
    Heading h1 = new Heading("h1", 1);
    page.addHeading(h1);
    Heading h2 = new Heading("h2", 2);
    page.addHeading(h2);
    doReturn(page).when(pageLoaderMock).loadPage(targetUrl);
    Report report = crawler.crawlUrl(targetUrl, 1, visited);

    assertNotNull(report);
    assertEquals(1, visited.size());
    assertTrue(visited.contains(targetUrl));
    verify(inputParametersMock).getTargetLanguage();
    verify(inputParametersMock).getDepth();
    verify(pageLoaderMock).loadPage(targetUrl);
    verify(translationServiceMock).translateText(h1.originalText, sourceLanguage, targetLocale);
    verify(translationServiceMock).translateText(h2.originalText, sourceLanguage, targetLocale);
    verifyNoMoreInteractions(inputParametersMock, pageLoaderMock, translationServiceMock);
  }

  @Test
  void crawlUrlDoubleDepth() throws Exception {
    URI linkUrl = targetUrl.resolve("/about.html");
    Set<URI> visited = new HashSet<>();
    Page mainPage = new Page(targetUrl);
    mainPage.language = sourceLanguage;
    mainPage.addLink(new Link(linkUrl, "About", false));
    Page subPage = new Page(linkUrl);
    subPage.language = sourceLanguage;
    mainPage.addHeading(new Heading("first", 1));
    mainPage.addHeading(new Heading("second", 1));
    subPage.addHeading(new Heading("sub 1", 1));
    subPage.addHeading(new Heading("sub 2", 1));

    doReturn(mainPage).when(pageLoaderMock).loadPage(targetUrl);
    doReturn(subPage).when(pageLoaderMock).loadPage(linkUrl);
    Report report = crawler.crawlUrl(targetUrl, 2, visited);
    assertFalse(report.mainPage.links.get(0).isBroken());

    assertNotNull(report);
    assertEquals(2, visited.size());
    assertTrue(visited.contains(targetUrl));
    assertTrue(visited.contains(linkUrl));
    verify(inputParametersMock, times(2)).getTargetLanguage();
    verify(inputParametersMock, times(2)).getDepth();
    verify(pageLoaderMock).loadPage(targetUrl);
    verify(pageLoaderMock).loadPage(linkUrl);
    verify(translationServiceMock, times(4)).translateText(any(String.class), any(Locale.class), any(Locale.class));
    verifyNoMoreInteractions(inputParametersMock, pageLoaderMock, translationServiceMock);
  }

  @Test
  void crawlUrlWithBrokenLink() throws Exception {
    URI linkUrl = targetUrl.resolve("/about.html");
    Set<URI> visited = new HashSet<>();
    Page mainPage = new Page(targetUrl);
    mainPage.language = sourceLanguage;
    mainPage.addLink(new Link(linkUrl, "About", false));
    mainPage.addHeading(new Heading("first", 1));
    mainPage.addHeading(new Heading("second", 1));

    doReturn(mainPage).when(pageLoaderMock).loadPage(targetUrl);
    doThrow(new BrokenLinkException(linkUrl, new IOException())).when(pageLoaderMock).loadPage(linkUrl);
    Report report = crawler.crawlUrl(targetUrl, 2, visited);
    assertTrue(report.mainPage.links.get(0).isBroken());

    assertNotNull(report);
    assertEquals(2, visited.size());
    assertTrue(visited.contains(targetUrl));
    assertTrue(visited.contains(linkUrl));
    verify(inputParametersMock, times(1)).getTargetLanguage();
    verify(inputParametersMock, times(1)).getDepth();
    verify(pageLoaderMock).loadPage(targetUrl);
    verify(pageLoaderMock).loadPage(linkUrl);
    verify(translationServiceMock, times(2)).translateText(any(String.class), any(Locale.class), any(Locale.class));
    verifyNoMoreInteractions(inputParametersMock, pageLoaderMock, translationServiceMock);
  }

  @Test
  void crawl() {
    URI aboutUrl = targetUrl.resolve("/about.html");
    URI termsUrl = targetUrl.resolve("/terms.html");
    Map<URI, Page> pages = Map.of(targetUrl, new Page(targetUrl),
            aboutUrl, new Page(aboutUrl),
            termsUrl, new Page(termsUrl));
    pages.get(targetUrl).links.add(new Link(aboutUrl, "About", false));
    pages.get(targetUrl).links.add(new Link(termsUrl, "Terms", false));
    pages.get(aboutUrl).links.add(new Link(targetUrl, "Back", false));
    pages.get(termsUrl).links.add(new Link(targetUrl, "Home", false));
    pages.get(targetUrl).headings.add(new Heading("h1", 1));
    pages.get(targetUrl).headings.add(new Heading("h2", 2));
    pages.get(targetUrl).headings.add(new Heading("h3", 3));
    pages.values().forEach(p -> p.language = sourceLanguage);
    when(pageLoaderMock.loadPage(any(URI.class))).then(i -> pages.get((URI) i.getArgument(0)));

    Report report = crawler.crawl();

    assertNotNull(report);
    verify(inputParametersMock).getUrl();
    verify(inputParametersMock, times(4)).getDepth();
    verify(inputParametersMock, times(3)).getTargetLanguage();
    verify(pageLoaderMock).loadPage(eq(targetUrl));
    verify(pageLoaderMock).loadPage(eq(aboutUrl));
    verify(pageLoaderMock).loadPage(eq(termsUrl));
    verify(translationServiceMock, times(3)).translateText(any(String.class), eq(sourceLanguage), eq(Locale.GERMAN));
    verifyNoMoreInteractions(inputParametersMock, pageLoaderMock, translationServiceMock);
  }
}