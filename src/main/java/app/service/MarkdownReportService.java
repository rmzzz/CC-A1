package app.service;

import app.domain.Heading;
import app.domain.InputParameters;
import app.domain.Link;
import app.domain.Page;
import app.domain.Report;
import app.domain.ReportService;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;

public class MarkdownReportService implements ReportService {

  private static final String MARK_DOWN_BREAK = "<br>";
  static final Logger logger = Logger.getLogger("app.service.MarkDownReportService");

  @Override
  public void createReport(Report targetReport) {
    String reportString = renderReport(targetReport);
    createReportFile(reportString, renderFileName(targetReport));
  }

  String renderReport(Report targetReport){
    String reportString = "";
    reportString += renderMetaInformation(targetReport);
    reportString += renderPageContents(targetReport.getPageList());
    return reportString;
  }

  void createReportFile(String reportString, String fileName){
    try (FileWriter fileWriter = new FileWriter(fileName, StandardCharsets.UTF_8)) {
      fileWriter.write(reportString);
    } catch (IOException ioException) {
      logger.warning("An error occurred during file writing.");
    }
    logger.info("Report file created: " + fileName);
  }

  String renderFileName(Report targetReport){
    String filename = extractDomainNameFromURL(targetReport.getInputUrl()) + ".md";
    return filename;
  }

  String renderMetaInformation(Report targetReport) {
    return "Input:\n"
            + MARK_DOWN_BREAK + "<" + targetReport.getInputUrl() + ">\n"
            + MARK_DOWN_BREAK + "depth: " + targetReport.getDepth() + "\n"
            + MARK_DOWN_BREAK + "target language: " + targetReport.getTargetLanguage() + "\n"
            + MARK_DOWN_BREAK + "source language: " + targetReport.getSourceLanguage() + "\n"
            + MARK_DOWN_BREAK + "report:\n";
  }

  String renderPageContents(List<Page> pageList) {
    StringBuilder pageContent = new StringBuilder();

    for (Page page : pageList) {
      pageContent.append(renderHeadings(page.getHeadings()));
      pageContent.append(renderLinks(page.getLinks()));
    }
    return pageContent.toString();
  }

  String renderHeadings(List<Heading> headingList) {

    StringBuilder headingString = new StringBuilder();
    for (Heading heading : headingList) {
      headingString.append(renderSingleHeading(heading));
    }
    return headingString.toString();
  }

  String renderSingleHeading(Heading heading) {
    StringBuilder headingString = new StringBuilder();
    for (int i = 0; i < heading.getRank(); i++) {
      headingString.append('#');
    }
    for(int i = 0; i < heading.getDepth();i++){
      headingString.append('-');
    }
    headingString.append("> ");
    headingString.append(heading.getText());
    headingString.append("\n");
    return headingString.toString();
  }

  String renderLinks(List<Link> linksList) {
    StringBuilder linksAsString = new StringBuilder();

    for (Link link : linksList) {
      linksAsString.append(renderSingleLink(link));
    }
    linksAsString.append(MARK_DOWN_BREAK + "\n");

    return linksAsString.toString();
  }

  String renderSingleLink(Link link) {
    StringBuilder singleLinkString = new StringBuilder();
    singleLinkString.append(MARK_DOWN_BREAK);
    for(int i =0;i< link.getDepth();i++){
      singleLinkString.append('-');
    }
    singleLinkString.append('>');
    if(link.isBroken()){
      singleLinkString.append("broken link ");
    }
    else {
      singleLinkString.append("link to ");
    }
    singleLinkString.append("<");
    singleLinkString.append(link.getUrl());
    singleLinkString.append(">\n");

    return singleLinkString.toString();
  }

  public String extractDomainNameFromURL(URI targetURL) {
    String result = null;
    String host = targetURL.getHost();

    String[] urlSubdomains = host.split("\\.");

    for (String substring : urlSubdomains) {
      if (!substring.matches("www")) {
        result = substring;
        break;
      }
    }
    if (result == null) {
      return "report";
    }
    return result;
  }
}
