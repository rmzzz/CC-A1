package app.domain;

import app.exception.BrokenLinkException;

import java.net.URI;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebCrawler {
  static Logger logger = Logger.getLogger("app.domain.WebCrawler");
  final List<URI> urls;
  final int maxDepth;
  final Locale targetLanguage;

  PageLoader pageLoader;
  TranslationService translationService;
  TaskExecutor taskExecutor;
  Set<URI> visitedUrls;

  Deque<Task<Report>> tasksQueue;

  public WebCrawler(InputParameters inputParameters, ServiceProvider serviceFactory) {
    this.urls = inputParameters.getUrls();
    this.maxDepth = inputParameters.getDepth();
    this.targetLanguage = inputParameters.getTargetLanguage();

    this.pageLoader = serviceFactory.getPageLoader();
    this.translationService = serviceFactory.getTranslationService();
    this.taskExecutor = serviceFactory.getTaskExecutor();
    this.visitedUrls = ConcurrentHashMap.newKeySet();
    this.tasksQueue = new ConcurrentLinkedDeque<>();
  }

  public Report crawl() {
    for (URI url : urls) {
      Task<Report> task = crawlUrl(url);
      tasksQueue.add(task);
    }
    Report report = taskExecutor.executeAllTasksThenMergeResult(tasksQueue,
            Report.EMPTY, Report::merge);
    return report;
  }

  Task<Report> crawlUrl(URI url) {
    return crawlUrl(url, 0);
  }

  Task<Report> crawlUrl(URI url, int depth) {
    logger.fine(() -> "crawling URL " + url);
    visitedUrls.add(url);
    Task<Page> loadPageTask = taskExecutor.createTask(url, pageLoader::loadPage);
    if (depth < maxDepth) {
      loadPageTask = loadPageTask.addStep(page -> enqueueSubPages(page, depth + 1));
    }
    Task<Page> translatePageTask = loadPageTask.addStep(
            page -> page.translate(translationService, targetLanguage));
    Task<Report> createReportTask = translatePageTask.addStep(
            page -> new Report(page, depth, maxDepth, targetLanguage));
    return createReportTask;
  }

  Page enqueueSubPages(Page page, int subDepth) {
    for (Link link : page.getLinks()) {
      URI uri = link.getUrl();
      if (!visitedUrls.contains(uri)) {
        Task<Report> task = crawlUrl(uri, subDepth)
                .addErrorHandler(((report, error) -> {
                  if (error instanceof BrokenLinkException blx) {
                    logger.log(Level.FINE, "Broken link: " + uri, blx);
                    link.setBroken(true);
                    return Report.EMPTY; // TODO maybe add Report.ERROR
                  }
                  return null;
                }));
        tasksQueue.add(task);
      }
    }
    return page;
  }
}
