package app;

import app.domain.InputParameters;
import app.domain.Report;
import app.domain.ReportService;
import app.domain.ServiceProvider;
import app.domain.WebCrawler;
import app.service.CommandLine;
import app.service.ServiceProviderFactory;

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
    ServiceProvider serviceProvider = ServiceProviderFactory.create(commandInputParameters);
    WebCrawler crawler = new WebCrawler(commandInputParameters, serviceProvider);
    Report report = crawler.crawl();
    ReportService reportService = serviceProvider.getReportService();
    reportService.createReport(report);
  }
}
