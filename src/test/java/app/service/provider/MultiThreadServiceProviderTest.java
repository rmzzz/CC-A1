package app.service.provider;

import app.domain.InputParameters;
import app.domain.PageLoader;
import app.domain.ReportService;
import app.domain.TaskExecutor;
import app.domain.TranslationService;
import app.service.JsoupPageLoader;
import app.service.MarkdownReportService;
import app.service.task.MultiThreadTaskExecutor;
import app.service.translation.CachingTranslationService;
import app.tests.BaseUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MultiThreadServiceProviderTest extends BaseUnitTest {

  MultiThreadServiceProvider provider;

  @BeforeEach
  void setUp() {
    InputParameters parameters = mock(InputParameters.class);
    provider = new MultiThreadServiceProvider(parameters);
  }

  @Test
  void getPageLoader() {
    PageLoader pageLoader = provider.getPageLoader();
    assertTrue(pageLoader instanceof JsoupPageLoader);
  }

  @Test
  void getTranslationService() {
    TranslationService service = provider.getTranslationService();
    assertTrue(service instanceof CachingTranslationService);
  }

  @Test
  void getTaskExecutor() {
    TaskExecutor executor = provider.getTaskExecutor();
    assertTrue(executor instanceof MultiThreadTaskExecutor);
  }

  @Test
  void getReportService() {
    ReportService service = provider.getReportService();
    assertTrue(service instanceof MarkdownReportService);
  }
}