package app.domain;

import app.mock.MockTranslationService;
import app.mock.TestPageLoader;
import app.mock.TestServiceProvider;
import app.service.task.SameThreadTaskExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebCrawlerTest {
  WebCrawler crawler;
  InputParameters inputParametersMock;
  TestPageLoader pageLoader;

  volatile List<URI> targetUrls;
  volatile int targetDepth;
  volatile Locale targetLocale;
  volatile Locale sourceLanguage;
  TestServiceProvider serviceProvider;

  @BeforeEach
  void setUp() throws Exception {
    inputParametersMock = mock(InputParameters.class);
    targetUrls = List.of(URI.create("http://localhost"));
    targetDepth = 3;
    targetLocale = Locale.GERMAN;
    sourceLanguage = Locale.ENGLISH;
    when(inputParametersMock.getTargetLanguage()).then(invocation -> targetLocale);
    when(inputParametersMock.getUrls()).then(invocation -> targetUrls);
    when(inputParametersMock.getDepth()).then(invocation -> targetDepth);

    serviceProvider = new TestServiceProvider();
    pageLoader = new TestPageLoader();
    serviceProvider.pageLoader = pageLoader;
    serviceProvider.translationService = new MockTranslationService();
    serviceProvider.taskExecutor = new SameThreadTaskExecutor();
    crawler = new WebCrawler(inputParametersMock, serviceProvider);
  }

  Map<URI, Page> mockPagesForTargetUrls(List<URI> urls) {
    Map<URI, Page> pages = new ConcurrentHashMap<>();
    for (URI targetUrl : urls) {
      URI aboutUrl = targetUrl.resolve("about.html");
      URI termsUrl = targetUrl.resolve("terms.html");
      pages.put(targetUrl, new Page(targetUrl));
      pages.put(aboutUrl, new Page(aboutUrl));
      pages.put(termsUrl, new Page(termsUrl));
      pages.get(targetUrl).links.add(new Link(aboutUrl, "About", false));
      pages.get(targetUrl).links.add(new Link(termsUrl, "Terms", false));
      pages.get(aboutUrl).links.add(new Link(targetUrl, "Back", false));
      pages.get(termsUrl).links.add(new Link(targetUrl, "Home", false));
      pages.get(targetUrl).headings.add(new Heading("h1", 1));
      pages.get(targetUrl).headings.add(new Heading("h2", 2));
      pages.get(targetUrl).headings.add(new Heading("h3", 3));
    }
    pages.values().forEach(p -> p.language = sourceLanguage);
    pages.values().forEach(pageLoader::mockPage);
    return pages;
  }

  @AfterEach
  void tearDown() {
    reset(inputParametersMock);
  }

  @Test
  void crawlUrlSingleDepth() throws Exception {
    URI targetUrl = targetUrls.get(0);
    Page page = new Page(targetUrl);
    page.language = sourceLanguage;
    Heading h1 = new Heading("h1", 1);
    page.addHeading(h1);
    Heading h2 = new Heading("h2", 2);
    page.addHeading(h2);
    pageLoader.mockPage(page);

    Task<Report> report = crawler.crawlUrl(targetUrl, 1);
    assertNotNull(report);
    assertEquals(1, crawler.visitedUrls.size());
    assertTrue(crawler.visitedUrls.contains(targetUrl));
    //assertTrue(crawler.visitedUrls.contains(linkUrl));
  }

  @Test
  void crawlUrlDoubleDepth() throws Exception {
    URI targetUrl = targetUrls.get(0);
    URI linkUrl = targetUrl.resolve("/about.html");
    Page mainPage = new Page(targetUrl);
    mainPage.language = sourceLanguage;
    mainPage.addLink(new Link(linkUrl, "About", false));
    Page subPage = new Page(linkUrl);
    subPage.language = sourceLanguage;
    mainPage.addHeading(new Heading("first", 1));
    mainPage.addHeading(new Heading("second", 1));
    subPage.addHeading(new Heading("sub 1", 1));
    subPage.addHeading(new Heading("sub 2", 1));
    pageLoader.mockPage(mainPage);
    pageLoader.mockPage(subPage);
    Task<Report> report = crawler.crawlUrl(targetUrl, 2);
    assertNotNull(report);
    assertEquals(1, crawler.visitedUrls.size());
    assertTrue(crawler.visitedUrls.contains(targetUrl));
    assertFalse(crawler.visitedUrls.contains(linkUrl));
  }

  @Test
  void crawlSingleTarget() {
    Map<URI, Page> pages = mockPagesForTargetUrls(targetUrls);

    Report report = crawler.crawl();

    assertEquals(1, report.mainPages.size());
    assertEquals(targetUrls.get(0), report.mainPages.get(0).pageUrl);
  }

  @Test
  void crawlMultipleTargets() {
    targetUrls = List.of(URI.create("http://localhost/v1/"), URI.create("http://localhost/v2/"));
    Map<URI, Page> pages = mockPagesForTargetUrls(targetUrls);
    assertTrue(pages.keySet().containsAll(targetUrls));

    Report report = new WebCrawler(inputParametersMock, serviceProvider).crawl();
    assertEquals(2, report.mainPages.size());
  }

  @Test
  void crawlUrlShouldDetectBrokenLinks() throws Exception {
    Map<URI, Page> pages = mockPagesForTargetUrls(targetUrls);
    Page mainPage = pages.get(targetUrls.get(0));
    Link brokenLink = new Link(URI.create("http://broken.url"), "Test", false);
    mainPage.getLinks().set(0, brokenLink);
    assertFalse(brokenLink.isBroken());

    Report report = crawler.crawl();
    assertTrue(report.mainPages.get(0).links.get(0).isBroken());
    assertSame(brokenLink, report.mainPages.get(0).links.get(0));
    assertFalse(report.mainPages.get(0).links.get(1).isBroken());
  }

}