package app.service;

import app.domain.Heading;
import app.domain.Link;
import app.domain.Page;
import app.domain.Report;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
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
  Report report1;
  Report report2;
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
          <br>URLs:
          <br><https://www.google.at/>
          <br>depth: 1
          <br>target language: en
          <br>source language: null
          <br>report:
          #--> Heading1
          ##---> Heading2
          ##----> Heading3
          <br>-->link to <https://www.google.at/>
          <br>----->broken link <https://www.qwant.com/>
          <br>Error when loading qwant
          <br>
          #-> Header A1
          #-> Header A2
          ##--> Header B1
          ###--> Header C1
          <br>->link to <https://www.aau.at/>
          <br>--->broken link <https://stackoverflow.com/>
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
    qwantLink.setErrorMessage("Error when loading qwant");
    aauLink = new Link(aauURL, "AAU", false);
    stackOverFlowLink = new Link(stackOverFlowURL, "Stackoverflow", true);
    githubLink = new Link(githubURL, "GitHub", false);
    report1 = mock(Report.class);
    report2 = mock(Report.class);
    page1 = mock(Page.class);
    page2 = mock(Page.class);
    pageList = new LinkedList<>();
    headingList1 = new LinkedList<>();
    headingList2 = new LinkedList<>();
    linksList1 = new LinkedList<>();
    linksList2 = new LinkedList<>();
    linksList3 = new LinkedList<>();

    when(cli1.getUrls()).thenReturn(List.of(googleURL));
    when(cli1.getDepth()).thenReturn(1);
    when(cli1.getTargetLanguage()).thenReturn(Locale.ENGLISH);
    when(cli2.getUrls()).thenReturn(List.of(qwantURL));
    when(cli2.getDepth()).thenReturn(3);
    when(cli2.getTargetLanguage()).thenReturn(Locale.FRENCH);

    when(report1.getPageList()).thenReturn(pageList);
    List<URI> uriListReport1 = new ArrayList<>();
    uriListReport1.add(googleURL);
    when(report1.getInputUrls()).thenReturn(uriListReport1);
    when(report1.getMaxDepth()).thenReturn(1);
    when(report1.getTargetLanguage()).thenReturn(Locale.ENGLISH);
    List<URI> uriListReport2 = new ArrayList<>();
    uriListReport2.add(qwantURL);
    when(report2.getInputUrls()).thenReturn(uriListReport2);
    when(report2.getMaxDepth()).thenReturn(3);
    when(report2.getTargetLanguage()).thenReturn(Locale.FRENCH);
    when(page1.getHeadings()).thenReturn(headingList1);
    when(page1.getLinks()).thenReturn(linksList1);
    when(page2.getHeadings()).thenReturn(headingList2);
    when(page2.getLinks()).thenReturn(linksList2);

    pageList.add(page1);
    pageList.add(page2);
    headingList1.add(new Heading("Heading1", 1, 2));
    headingList1.add(new Heading("Heading2", 2, 3));
    headingList1.add(new Heading("Heading3", 2, 4));

    headingList2.add(new Heading("Header A1", 1, 1));
    headingList2.add(new Heading("Header A2", 1, 1));
    headingList2.add(new Heading("Header B1", 2, 2));
    headingList2.add(new Heading("Header C1", 3, 2));

    googleLink.setDepth(2);
    qwantLink.setDepth(5);
    aauLink.setDepth(1);
    stackOverFlowLink.setDepth(3);
    githubLink.setDepth(9);
    linksList1.add(googleLink);
    linksList1.add(qwantLink);
    linksList2.add(aauLink);
    linksList2.add(stackOverFlowLink);
    linksList3.add(githubLink);
  }

  @Test
  void testCreateMarkdownReport() {

    reportService.createReport(report1);
    String[] resultReports = new String[1];
    Assertions.assertDoesNotThrow(() -> {
      resultReports[0] = Files.readString(Paths.get("google.md"));

      File resultFile = new File("google.md");
      if (!resultFile.delete()) {
        throw new IOException();
      }
    });
    Assertions.assertEquals(expectedResult, resultReports[0]);

    verify(report1, times(2)).getInputUrls();
    verify(report1, times(1)).getMaxDepth();
    verify(report1, times(1)).getTargetLanguage();
    verify(report1, times(1)).getSourceLanguage();

    verify(report1, times(1)).getPageList();
    verify(page1, times(1)).getLinks();
    verify(page1, times(1)).getHeadings();
    verify(page2, times(1)).getLinks();
    verify(page2, times(1)).getHeadings();
    verifyNoMoreInteractions(report1, page1, page2, cli1);
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
    assertEquals("<br>->link to <https://www.aau.at/>\n", reportService.renderSingleLink(aauLink));
  }

  @Test
  void createSingleLinkTestBroken() {
    assertEquals("<br>--->broken link <https://stackoverflow.com/>\n", reportService.renderSingleLink(stackOverFlowLink));
  }

  @Test
  void createHeadingTest() {
    assertEquals("#-> Header A1\n#-> Header A2\n##--> Header B1\n###--> Header C1\n", reportService.renderHeadings(headingList2));
  }

  @Test
  void createLinksTest() {
    assertEquals("<br>--------->link to <https://github.com/rmzzz/CC-A1>\n<br>\n", reportService.renderLinks(linksList3));
  }

  @Test
  void createBrokenLinkTest(){
    Assertions.assertEquals("broken link <https://www.qwant.com/>\n<br>Error when loading qwant\n" ,reportService.renderBrokenLink(qwantLink));
  }

  @Test
  void createMetaInformationTest() {
    String expected = """
            Input:
            <br>URLs:
            <br><https://www.qwant.com/>
            <br>depth: 3
            <br>target language: fr
            <br>source language: null
            <br>report:
            """;
    assertEquals(expected, reportService.renderMetaInformation(report2));

    verify(report2, times(1)).getInputUrls();
    verify(report2, times(1)).getMaxDepth();
    verify(report2, times(1)).getTargetLanguage();
    verify(report2, times(1)).getSourceLanguage();
    verifyNoMoreInteractions(report2);
  }
}
