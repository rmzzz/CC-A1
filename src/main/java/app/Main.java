package app;

import app.domain.InputParameters;
import app.domain.Report;
import app.domain.ReportService;
import app.domain.ServiceProvider;
import app.domain.WebCrawler;
import app.service.CommandLine;
import app.service.ServiceProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
  static Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    CommandLine cli = CommandLine.fromCommandLine(args);
    if (cli.isValid()) {
      executeCommand(cli);
    } else {
      logger.warn("""
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
