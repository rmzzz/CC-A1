package app.domain;

import java.net.URI;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class WebCrawler {
  static Logger logger = Logger.getLogger("app.domain.WebCrawler");

  InputParameters inputParameters;
  PageLoader pageLoader;
  TranslationService translationService;

  public WebCrawler(InputParameters inputParameters, PageLoader pageLoader, TranslationService translationService) {
    this.inputParameters = inputParameters;
    this.pageLoader = pageLoader;
    this.translationService = translationService;
  }

  public Report crawl() {
    URI url = inputParameters.getUrl();
    int depth = inputParameters.getDepth();
    Set<URI> visitedUrls = ConcurrentHashMap.newKeySet();
    return crawlUrl(url, depth, visitedUrls);
  }

  Report crawlUrl(URI url, int depth, Set<URI> visitedUrls) {
    Page page = loadPage(url, visitedUrls);
    Report resultReport = translatePage(page);
    if (depth <= 1) {
      return resultReport;
    }
    return crawlSubPages(page, resultReport, depth - 1, visitedUrls);
  }

  Page loadPage(URI url, Set<URI> visitedUrls) {
    visitedUrls.add(url);
    Page page = pageLoader.loadPage(url);
    logger.fine(() -> "loaded page " + page.pageUrl);
    return page;
  }

  Report translatePage(Page page) {
    Locale targetLanguage = inputParameters.getTargetLanguage();
    Page translatedPage = page.translate(translationService, targetLanguage);
    Report resultReport = new Report(translatedPage, inputParameters.getDepth());
    logger.fine(() -> "translated page " + translatedPage.pageUrl);
    return resultReport;
  }

  Report crawlSubPages(Page page, Report resultReport, int subDepth, Set<URI> visitedUrls) {
    return page.getLinks().stream()
            .map(Link::getUrl)
            .filter(uri -> !visitedUrls.contains(uri))
            .map(uri -> crawlUrl(uri, subDepth, visitedUrls))
            .reduce(resultReport, Report::merge);
  }
}
