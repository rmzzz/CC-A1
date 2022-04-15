package app.domain;

import java.net.URI;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebCrawler {
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
    return page;
  }

  Report translatePage(Page page) {
    Report resultReport = new Report();
    resultReport.addPage(page);
    Locale targetLanguage = inputParameters.getTargetLanguage();
    resultReport = translationService.translateReport(resultReport, targetLanguage);
    return resultReport;
  }

  Report crawlSubPages(Page page, Report resultReport, int subDepth, Set<URI> visitedUrls) {
    return page.streamLinks()
            .map(Link::getUrl)
            .filter(u -> !visitedUrls.contains(u))
            .map(u -> crawlUrl(u, subDepth, visitedUrls))
            .reduce(resultReport, Report::merge);
  }
}
