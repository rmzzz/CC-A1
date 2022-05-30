package app.service.provider;

import app.domain.InputParameters;
import app.domain.ServiceProvider;
import app.service.JsoupPageLoader;
import app.service.MarkdownReportService;
import app.service.task.SingleThreadTaskExecutor;
import app.service.translation.CachingTranslationService;
import app.service.translation.DeeplTranslationService;

public class SingleThreadServiceProvider extends BaseServiceProvider implements ServiceProvider {
  public SingleThreadServiceProvider(InputParameters inputParameters) {
    super(new SingleThreadTaskExecutor(),
            new JsoupPageLoader(),
            new CachingTranslationService(
                    new DeeplTranslationService()),
            new MarkdownReportService());
  }
}
