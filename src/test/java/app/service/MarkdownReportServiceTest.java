package app.service;

import app.domain.Heading;
import app.domain.Link;
import app.domain.Page;
import app.domain.Report;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;

public class MarkdownReportServiceTest {

  MarkdownReportService reportService;
  CommandLine cli1;
  CommandLine cli2;
  URI googleURL;
  URI qwantURL;
  URI aauURL;
  URI stackOverFlowURL;
  URI githubURL;
  Link googleLink;
  Link qwantLink;
  Link aauLink;
  Link stackOverFlowLink;
  Link githubLink;
  Report report;
  Page page1;
  Page page2;
  List<Page> pageList;
  List<Heading> headingList1;
  List<Heading> headingList2;
  List<Link> linksList1;
  List<Link> linksList2;
  List<Link> linksList3;

  final String expectedResult = """
          Input:
          <br><https://www.google.at/>
          <br>depth: 1
          <br>target language: en
          <br>report:
          # Heading1
          ## Heading2
          ## Heading3
          <br>-->link to <https://www.google.at/>
          <br>-->broken link <https://www.qwant.com/>
          <br>
          # Header A1
          # Header A2
          ## Header B1
          ### Header C1
          <br>-->link to <https://www.aau.at/>
          <br>-->broken link <https://stackoverflow.com/>
          <br>
          """;

  @BeforeEach
  void setUp() throws Exception {
    reportService = new MarkdownReportService();
    cli1 = mock(CommandLine.class);
    cli2 = mock(CommandLine.class);
    googleURL = new URI("https://www.google.at/");
    qwantURL = new URI("https://www.qwant.com/");
    aauURL = new URI("https://www.aau.at/");
    stackOverFlowURL = new URI("https://stackoverflow.com/");
    githubURL = new URI("https://github.com/rmzzz/CC-A1");
    googleLink = new Link(googleURL, "Google", false);
    qwantLink = new Link(qwantURL, "Qwant", true);
    aauLink = new Link(aauURL, "AAU", false);
    stackOverFlowLink = new Link(stackOverFlowURL, "Stackoverflow", true);
    githubLink = new Link(githubURL, "GitHub", false);
    report = mock(Report.class);
    page1 = mock(Page.class);
    page2 = mock(Page.class);
    pageList = new LinkedList<>();
    headingList1 = new LinkedList<>();
    headingList2 = new LinkedList<>();
    linksList1 = new LinkedList<>();
    linksList2 = new LinkedList<>();
    linksList3 = new LinkedList<>();

    when(cli1.getUrl()).thenReturn(googleURL);
    when(cli1.getDepth()).thenReturn(1);
    when(cli1.getTargetLanguage()).thenReturn(Locale.ENGLISH);
    when(cli2.getUrl()).thenReturn(qwantURL);
    when(cli2.getDepth()).thenReturn(3);
    when(cli2.getTargetLanguage()).thenReturn(Locale.FRENCH);

    when(report.getPageList()).thenReturn(pageList);
    when(page1.getHeadings()).thenReturn(headingList1);
    when(page1.getLinks()).thenReturn(linksList1);
    when(page2.getHeadings()).thenReturn(headingList2);
    when(page2.getLinks()).thenReturn(linksList2);

    pageList.add(page1);
    pageList.add(page2);
    headingList1.add(new Heading("Heading1", 1));
    headingList1.add(new Heading("Heading2", 2));
    headingList1.add(new Heading("Heading3", 2));

    headingList2.add(new Heading("Header A1", 1));
    headingList2.add(new Heading("Header A2", 1));
    headingList2.add(new Heading("Header B1", 2));
    headingList2.add(new Heading("Header C1", 3));

    linksList1.add(googleLink);
    linksList1.add(qwantLink);
    linksList2.add(aauLink);
    linksList2.add(stackOverFlowLink);
    linksList3.add(githubLink);
  }

  @Test
  void testCreateMarkdownReport() {

    reportService.createReport(report, cli1);
    char[] resultReport = new char[350];
    Assertions.assertDoesNotThrow(() -> {
      FileReader fileReader = new FileReader("google.md");
      fileReader.read(resultReport);
      fileReader.close();

      File resultFile = new File("google.md");
      if (!resultFile.delete()) {
        throw new IOException();
      }
    });
    String resultReportString = new String(resultReport);
    Assertions.assertEquals(expectedResult, resultReportString);

    verify(cli1, times(2)).getUrl();
    verify(cli1, times(1)).getDepth();
    verify(cli1, times(1)).getTargetLanguage();

    verify(report, times(1)).getPageList();
    verify(page1, times(1)).getLinks();
    verify(page1, times(1)).getHeadings();
    verify(page2, times(1)).getLinks();
    verify(page2, times(1)).getHeadings();
    verifyNoMoreInteractions(report, page1, page2, cli1);
  }

  @Test
  void testExtractDomainFromURLgoogle() {
    assertEquals("google", reportService.extractDomainNameFromURL(googleURL));
  }

  @Test
  void testExtractDomainFromURLstackoverflow() {
    assertEquals("stackoverflow", reportService.extractDomainNameFromURL(stackOverFlowURL));
  }

  @Test
  void testExtractDomainFromURLaau() {
    assertEquals("aau", reportService.extractDomainNameFromURL(aauURL));
  }

  @Test
  void testExtractDomainFromURLgitHub() {
    assertEquals("github", reportService.extractDomainNameFromURL(githubURL));
  }

  @Test
  void createSingleLinkTestValid() {
    assertEquals("<br>-->link to <https://www.aau.at/>\n", reportService.renderSingleLink(aauLink));
  }

  @Test
  void createSingleLinkTestBroken() {
    assertEquals("<br>-->broken link <https://stackoverflow.com/>\n", reportService.renderSingleLink(stackOverFlowLink));
  }

  @Test
  void createHeadingTest() {
    assertEquals("# Header A1\n# Header A2\n## Header B1\n### Header C1\n", reportService.renderHeadings(headingList2));
  }

  @Test
  void createLinksTest() {
    assertEquals("<br>-->link to <https://github.com/rmzzz/CC-A1>\n<br>\n", reportService.renderLinks(linksList3));
  }

  @Test
  void createMetaInformationTest() {
    String expected = """
            Input:
            <br><https://www.qwant.com/>
            <br>depth: 3
            <br>target language: fr
            <br>report:
            """;
    assertEquals(expected, reportService.renderMetaInformation(cli2));

    verify(cli2, times(1)).getUrl();
    verify(cli2, times(1)).getDepth();
    verify(cli2, times(1)).getTargetLanguage();
    verifyNoMoreInteractions(cli2);
  }
}
