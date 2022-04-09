package app.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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

  @BeforeEach
  void setUp() {
    inputParametersMock = mock(InputParameters.class);
    pageLoaderMock = mock(PageLoader.class);
    translationServiceMock = mock(TranslationService.class);
    when(translationServiceMock.translateReport(any(Report.class))).then(i -> i.getArgument(0));
    crawler = new WebCrawler(inputParametersMock, pageLoaderMock, translationServiceMock);
  }

  @AfterEach
  void tearDown() {
    reset(inputParametersMock, pageLoaderMock, translationServiceMock);
  }

  @Test
  void crawlUrlSingleDepth() throws Exception {
    URL url = createUrl("http://localhost");
    Set<URL> visited = new HashSet<>();
    Page page = new Page();
    doReturn(page).when(pageLoaderMock).loadPage(eq(url));
    Report report = crawler.crawlUrl(url, 1, visited);

    assertNotNull(report);
    assertEquals(1, visited.size());
    assertTrue(visited.contains(url));
    verify(pageLoaderMock).loadPage(eq(url));
    verify(translationServiceMock).translateReport(eq(report));
    verifyNoMoreInteractions(inputParametersMock, pageLoaderMock, translationServiceMock);
  }

  @Test
  void crawlUrlDoubleDepth() throws Exception {
    URL mainUrl = createUrl("http://localhost");
    URL linkUrl = createUrl("http://localhost/about.html");
    Set<URL> visited = new HashSet<>();
    Page mainPage = new Page();
    mainPage.links.add(new Link(linkUrl, "About"));
    Page subPage = new Page();
    doReturn(mainPage).when(pageLoaderMock).loadPage(eq(mainUrl));
    doReturn(subPage).when(pageLoaderMock).loadPage(eq(linkUrl));
    Report report = crawler.crawlUrl(mainUrl, 2, visited);

    assertNotNull(report);
    assertEquals(2, visited.size());
    assertTrue(visited.contains(mainUrl));
    assertTrue(visited.contains(linkUrl));
    verify(pageLoaderMock).loadPage(eq(mainUrl));
    verify(pageLoaderMock).loadPage(eq(linkUrl));
    verify(translationServiceMock, times(2)).translateReport(any(Report.class));
    verifyNoMoreInteractions(inputParametersMock, pageLoaderMock, translationServiceMock);
  }

  @Test
  void crawl() throws Exception {
    URL mainUrl = createUrl("http://localhost");
    URL aboutUrl = createUrl("http://localhost/about.html");
    URL termsUrl = createUrl("http://localhost/terms.html");
    Map<URL, Page> pages = Map.of(mainUrl, new Page(), aboutUrl, new Page(), termsUrl, new Page());
    pages.get(mainUrl).links.add(new Link(aboutUrl, "About"));
    pages.get(mainUrl).links.add(new Link(termsUrl, "Terms"));
    pages.get(aboutUrl).links.add(new Link(mainUrl, "Back"));
    pages.get(termsUrl).links.add(new Link(mainUrl, "Home"));
    when(pageLoaderMock.loadPage(any(URL.class))).then(i -> pages.get((URL)i.getArgument(0)));
    when(inputParametersMock.getUrl()).thenReturn(mainUrl);
    when(inputParametersMock.getDepth()).thenReturn(3);
    when(inputParametersMock.getTargetLanguage()).thenReturn(Locale.GERMAN);

    Report report = crawler.crawl();

    assertNotNull(report);
    verify(inputParametersMock).getUrl();
    verify(inputParametersMock).getDepth();
    verify(pageLoaderMock).loadPage(eq(mainUrl));
    verify(pageLoaderMock).loadPage(eq(aboutUrl));
    verify(pageLoaderMock).loadPage(eq(termsUrl));
    verify(translationServiceMock, times(3)).translateReport(any(Report.class));
    verifyNoMoreInteractions(inputParametersMock, pageLoaderMock, translationServiceMock);

  }

  static URL createUrl(String urlSpec) throws MalformedURLException {
    return new URL(urlSpec);
  }
}