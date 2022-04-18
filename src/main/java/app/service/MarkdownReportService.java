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
import java.util.List;
import java.util.logging.Logger;

public class MarkdownReportService implements ReportService {

  private static final String MARK_DOWN_BREAK = "<br>";
  static Logger logger = Logger.getLogger("app.service.MarkDownReportService");

  @Override
  public void createReport(Report targetReport, InputParameters inputParameters) {
    String reportString = renderReport(targetReport, inputParameters);
    createReportFile(reportString, renderFileName(inputParameters));
  }

  String renderReport(Report targetReport, InputParameters inputParameters){
    String reportString = "";
    reportString += renderMetaInformation(inputParameters);
    reportString += renderPageContents(targetReport.getPageList());
    return reportString;
  }

  void createReportFile(String reportString, String fileName){
    try (FileWriter fileWriter = new FileWriter(fileName)) {
      fileWriter.write(reportString);
    } catch (IOException ioException) {
      logger.warning("An error occurred during file writing.\n");
    }
  }

  String renderFileName(InputParameters inputParameters){
    String filename = extractDomainNameFromURL(inputParameters.getUrl()) + ".md";
    return filename;
  }

  String renderMetaInformation(InputParameters inputParameters) {
    return "Input:\n"
            + MARK_DOWN_BREAK + "<" + inputParameters.getUrl() + ">\n"
            + MARK_DOWN_BREAK + "depth: " + inputParameters.getDepth() + "\n"
            + MARK_DOWN_BREAK + "target language: " + inputParameters.getTargetLanguage() + "\n"
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
    headingString.append(" ");
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
