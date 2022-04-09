package app.domain;

import java.net.URL;
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
    URL url = inputParameters.getUrl();
    int depth = inputParameters.getDepth();
    Set<URL> visitedUrls = ConcurrentHashMap.newKeySet();
    return crawlUrl(url, depth, visitedUrls);
  }

  Report crawlUrl(URL url, int depth, Set<URL> visitedUrls) {
    visitedUrls.add(url);
    Page page = pageLoader.loadPage(url);
    Report resultReport = new Report();
    resultReport.addPage(page);
    resultReport = translationService.translateReport(resultReport);
    if (depth <= 1) {
      return resultReport;
    }
    return page.streamLinks()
            .map(Link::url)
            .filter(u -> !visitedUrls.contains(u))
            .map(u -> crawlUrl(u, depth - 1, visitedUrls))
            .reduce(resultReport, Report::merge);
  }
}
