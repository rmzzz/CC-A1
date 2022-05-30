package app.service.provider;

import app.domain.InputParameters;
import app.service.JsoupPageLoader;
import app.service.MarkdownReportService;
import app.service.task.MultiThreadTaskExecutor;
import app.service.translation.BufferingTranslationService;
import app.service.translation.CachingTranslationService;
import app.service.translation.DeeplTranslationService;

public class MultiThreadServiceProvider extends BaseServiceProvider {
  public MultiThreadServiceProvider(InputParameters parameters) {
    super(new MultiThreadTaskExecutor(),
            new JsoupPageLoader(),
            new CachingTranslationService(
                    new BufferingTranslationService(
                            new DeeplTranslationService())),
            new MarkdownReportService());
  }
}
