package app.mock;

import app.domain.PageLoader;
import app.domain.ReportService;
import app.domain.ServiceProvider;
import app.domain.TaskExecutor;
import app.domain.TranslationService;

public class TestServiceProvider implements ServiceProvider {
  public PageLoader pageLoader;
  public TranslationService translationService;
  public TaskExecutor taskExecutor;
  public ReportService reportService;

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
