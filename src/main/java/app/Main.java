package app;

import app.domain.Link;
import app.domain.PageLoader;
import app.domain.Report;
import app.domain.ReportService;
import app.domain.TranslationService;
import app.domain.WebCrawler;
import app.service.CommandLine;
import app.service.DeeplTranslationService;
import app.service.ReportServiceImpl;
import app.service.WebPageLoader;

public class Main {
  public static void main(String[] args) {
    CommandLine cli = CommandLine.fromCommandLine(args);
    if (cli.isValid()) {
      executeCommand(cli);
    } else {
      cli.printUsage();
    }
  }

  static void executeCommand(CommandLine cli) {
    PageLoader webClient = new WebPageLoader();
    TranslationService deepl = new DeeplTranslationService();
    WebCrawler crawler = new WebCrawler(cli, webClient, deepl);
    Report report = crawler.crawl();
    ReportService markdownReport = new ReportServiceImpl();
    markdownReport.createMarkdownReport(report, cli);
  }
}
