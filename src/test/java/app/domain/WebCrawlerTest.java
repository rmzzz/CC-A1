package app.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

  @BeforeEach
  void setUp() throws Exception {
    inputParametersMock = mock(InputParameters.class);
    targetUrl = new URI("http://localhost");
    targetDepth = 3;
    targetLocale = Locale.GERMAN;
    when(inputParametersMock.getTargetLanguage()).thenReturn(targetLocale);
    when(inputParametersMock.getUrl()).thenReturn(targetUrl);
    when(inputParametersMock.getDepth()).thenReturn(targetDepth);

    pageLoaderMock = mock(PageLoader.class);
    translationServiceMock = mock(TranslationService.class);
    when(translationServiceMock.translateText(any(String.class), any(Locale.class))).then(i -> i.getArgument(0));
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
    Heading h1 = new Heading("h1", 1);
    page.addHeading(h1);
    Heading h2 = new Heading("h2", 2);
    page.addHeading(h2);
    doReturn(page).when(pageLoaderMock).loadPage(eq(targetUrl));
    Report report = crawler.crawlUrl(targetUrl, 1, visited);

    assertNotNull(report);
    assertEquals(1, visited.size());
    assertTrue(visited.contains(targetUrl));
    verify(inputParametersMock).getTargetLanguage();
    verify(pageLoaderMock).loadPage(eq(targetUrl));
    verify(translationServiceMock).translateText(h1.originalText, targetLocale);
    verify(translationServiceMock).translateText(h2.originalText, targetLocale);
    verifyNoMoreInteractions(inputParametersMock, pageLoaderMock, translationServiceMock);
  }

  @Test
  void crawlUrlDoubleDepth() throws Exception {
    URI linkUrl = targetUrl.resolve("/about.html");
    Set<URI> visited = new HashSet<>();
    Page mainPage = new Page(targetUrl);
    mainPage.addLink(new Link(linkUrl, "About", false));
    Page subPage = new Page(linkUrl);
    mainPage.addHeading(new Heading("first", 1));
    mainPage.addHeading(new Heading("second", 1));
    doReturn(mainPage).when(pageLoaderMock).loadPage(eq(targetUrl));
    doReturn(subPage).when(pageLoaderMock).loadPage(eq(linkUrl));
    Report report = crawler.crawlUrl(targetUrl, 2, visited);

    assertNotNull(report);
    assertEquals(2, visited.size());
    assertTrue(visited.contains(targetUrl));
    assertTrue(visited.contains(linkUrl));
    verify(inputParametersMock, times(2)).getTargetLanguage();
    verify(pageLoaderMock).loadPage(eq(targetUrl));
    verify(pageLoaderMock).loadPage(eq(linkUrl));
    verify(translationServiceMock, times(2)).translateText(any(String.class), any(Locale.class));
    verifyNoMoreInteractions(inputParametersMock, pageLoaderMock, translationServiceMock);
  }

  @Test
  void crawl() throws Exception {
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
    when(pageLoaderMock.loadPage(any(URI.class))).then(i -> pages.get((URI)i.getArgument(0)));

    Report report = crawler.crawl();

    assertNotNull(report);
    verify(inputParametersMock).getUrl();
    verify(inputParametersMock).getDepth();
    verify(inputParametersMock, times(3)).getTargetLanguage();
    verify(pageLoaderMock).loadPage(eq(targetUrl));
    verify(pageLoaderMock).loadPage(eq(aboutUrl));
    verify(pageLoaderMock).loadPage(eq(termsUrl));
    verify(translationServiceMock, times(3)).translateText(any(String.class), eq(Locale.GERMAN));
    verifyNoMoreInteractions(inputParametersMock, pageLoaderMock, translationServiceMock);
  }
}