package app.service;

import app.domain.Heading;
import app.domain.Link;
import app.domain.Page;
import app.domain.Report;
import app.domain.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class MarkdownReportService implements ReportService {

  private static final String MARK_DOWN_BREAK = "<br>";
  static final Logger logger = LoggerFactory.getLogger(MarkdownReportService.class);

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
      logger.warn("An error occurred during file writing.", ioException);
    }
    logger.info("Report file created: " + fileName);
  }

  String renderFileName(Report targetReport){
    String filename = extractDomainNameFromURL(targetReport.getInputUrls().get(0)) + ".md";
    return filename;
  }

  String renderMetaInformation(Report targetReport) {
    return "Input:\n"
            + renderURLMetaInformation(targetReport.getInputUrls())
            + MARK_DOWN_BREAK + "depth: " + targetReport.getMaxDepth() + "\n"
            + MARK_DOWN_BREAK + "target language: " + targetReport.getTargetLanguage() + "\n"
            + MARK_DOWN_BREAK + "source language: " + targetReport.getSourceLanguage() + "\n"
            + MARK_DOWN_BREAK + "report:\n";
  }

  String renderURLMetaInformation(List<URI> urlList){

    StringBuilder resultString = new StringBuilder();
    resultString.append(MARK_DOWN_BREAK);
    resultString.append("URLs:\n");

    for(URI url : urlList){
      resultString.append(MARK_DOWN_BREAK);
      resultString.append("<");
      resultString.append(url);
      resultString.append(">\n");
    }
    return resultString.toString();
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
      singleLinkString.append(renderBrokenLink(link));
    }
    else {
      singleLinkString.append(renderNonBrokenLink(link));
    }
    return singleLinkString.toString();
  }

  String renderBrokenLink(Link link){
    String brokenLinkErrorMessage = "broken link <" + link.getUrl() + ">\n";
    if(link.getErrorMessage() != null){
      brokenLinkErrorMessage += MARK_DOWN_BREAK + link.getErrorMessage() + "\n";
    }
    return brokenLinkErrorMessage;
  }

  String renderNonBrokenLink(Link link){
    String linkString = "link to <" + link.getUrl() + ">\n";
    return linkString;
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
