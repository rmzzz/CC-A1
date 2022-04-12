package app;

import app.domain.PageLoader;
import app.domain.Report;
import app.domain.ReportService;
import app.domain.TranslationService;
import app.domain.WebCrawler;
import app.service.CommandLine;
import app.service.DeeplTranslationService;
import app.service.MarkdownReportService;
import app.service.WebPageLoader;

public class Main {
  public static void main(String[] args) {
    CommandLine cli = CommandLine.fromCommandLine(args);
    if (cli.isValid()) {
      executeCommand(cli);
    } else {
      System.out.println(cli.getUsage());
    }
  }

  static void executeCommand(CommandLine cli) {
    PageLoader webClient = new WebPageLoader();
    TranslationService deepl = new DeeplTranslationService();
    WebCrawler crawler = new WebCrawler(cli, webClient, deepl);
    Report report = crawler.crawl();
    ReportService markdownReport = new MarkdownReportService();
    markdownReport.createReport(report, cli);
  }
}
