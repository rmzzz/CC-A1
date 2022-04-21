package app;

import app.domain.InputParameters;
import app.domain.PageLoader;
import app.domain.Report;
import app.domain.ReportService;
import app.domain.TranslationService;
import app.domain.WebCrawler;
import app.service.CommandLine;
import app.service.DeeplTranslationService;
import app.service.MarkdownReportService;
import app.service.WebPageLoader;

import java.util.logging.Logger;

public class Main {
  static Logger logger = Logger.getLogger("app.Main");

  public static void main(String[] args) {
    CommandLine cli = CommandLine.fromCommandLine(args);
    if (cli.isValid()) {
      executeCommand(cli);
    } else {
      logger.warning("""
        Invalid parameters.
        Usage: java app.Main <parameters>
        """ + cli.getUsage());
    }
  }

  static void executeCommand(InputParameters commandInputParameters) {
    PageLoader webClient = new WebPageLoader();
    TranslationService deepl = new DeeplTranslationService();
    WebCrawler crawler = new WebCrawler(commandInputParameters, webClient, deepl);
    Report report = crawler.crawl();
    ReportService markdownReport = new MarkdownReportService();
    markdownReport.createReport(report, commandInputParameters);
  }
}
