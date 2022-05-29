package app.domain;

public interface ServiceProvider {
  PageLoader getPageLoader();

  TranslationService getTranslationService();

  TaskExecutor getTaskExecutor();

  ReportService getReportService();
}
