package app.service;

import app.domain.Heading;
import app.domain.InputParameters;
import app.domain.Link;
import app.domain.Page;
import app.domain.Report;
import app.domain.ReportService;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MarkdownReportService implements ReportService {

  private static final String MARK_DOWN_BREAK = "<br>";

  @Override
  public void createReport(Report targetReport, InputParameters inputParameters) {
    //TODO: split
    String reportString = "";
    reportString += renderMetaInformation(inputParameters);
    reportString += renderPageContents(targetReport.getPageList());

    String fileName = extractDomainNameFromURL(inputParameters.getUrl()) + ".md";
    try (FileWriter fileWriter = new FileWriter(fileName)) {
      fileWriter.write(reportString);
    } catch (IOException ioException) {
      System.out.println("An error occurred during file writing.\n");
    }
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
    for (int i = 0; i < heading.getHeadingDepth(); i++) {
      headingString.append('#');
    }
    headingString.append(" ");
    headingString.append(heading.getHeadingTitle());
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
    return (MARK_DOWN_BREAK + "-->" + ((link.isBroken()) ? "broken link <" : "link to <") + link.getUrl() + ">\n");
  }

  public String extractDomainNameFromURL(URL targetURL) {
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
