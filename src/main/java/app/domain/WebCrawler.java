package app.domain;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.Queue;

public class WebCrawler {
  InputParameters inputParameters;
  PageLoader pageLoader;
  TranslationService translationService;

  private Queue<URL> urlQueue;

  public WebCrawler(InputParameters inputParameters, PageLoader pageLoader, TranslationService translationService) {
    this.inputParameters = inputParameters;
    this.pageLoader = pageLoader;
    this.translationService = translationService;

    urlQueue = new ArrayDeque<URL>();
    urlQueue.add(inputParameters.getUrl());
  }

  public Report crawl() {
    URL url = inputParameters.getUrl();
    Page page = pageLoader.loadPage(url);

    Report resultReport = new Report();

    for (int i = 0; i < inputParameters.getDepth(); i++) {

      //TODO crawl website
      urlQueue.remove();

      //TODO translate heading

      //TODO update report

    }
    return resultReport;
  }

  public void addURLtoQueue(URL urlToAdd) {

    urlQueue.add(urlToAdd);
  }
}
