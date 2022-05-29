package app.service.provider;

import app.domain.PageLoader;
import app.domain.ReportService;
import app.domain.ServiceProvider;
import app.domain.TaskExecutor;
import app.domain.TranslationService;

public class BaseServiceProvider implements ServiceProvider {
  TaskExecutor taskExecutor;
  PageLoader pageLoader;
  TranslationService translationService;
  ReportService reportService;

  protected BaseServiceProvider(TaskExecutor taskExecutor, PageLoader pageLoader, TranslationService translationService, ReportService reportService) {
    this.taskExecutor = taskExecutor;
    this.pageLoader = pageLoader;
    this.translationService = translationService;
    this.reportService = reportService;
  }

  @Override
  public PageLoader getPageLoader() {
    return pageLoader;
  }

  @Override
  public TranslationService getTranslationService() {
    return translationService;
  }

  @Override
  public TaskExecutor getTaskExecutor() {
    return taskExecutor;
  }

  @Override
  public ReportService getReportService() {
    return reportService;
  }

}
