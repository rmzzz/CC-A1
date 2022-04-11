package app.service;

import app.domain.Heading;
import app.domain.Link;
import app.domain.Page;
import app.domain.Report;
import app.domain.ReportService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;

public class ReportServiceTest {

  ReportService reportService;
  CommandLine cli1;
  CommandLine cli2;
  URL googleURL;
  URL qwantURL;
  URL aauURL;
  URL stackOverFlowURL;
  Link googleLink;
  Link qwantLink;
  Link aauLink;
  Link stackOverFlowLink;
  Report report;
  Page page1;
  Page page2;
  List<Page> pageList;
  List<Heading> headingList1;
  List<Heading> headingList2;
  List<Link> linksList1;
  List<Link> linksList2;

  final String expectedResult = "Input:\n" +
          "<br><https://www.google.at/>\n" +
          "<br>depth: 1\n" +
          "<br>target language: en\n" +
          "<br>report:\n" +
          "# Heading1\n" +
          "## Heading2\n" +
          "## Heading3\n" +
          "<br>-->link to <https://www.google.at/>\n" +
          "<br>-->broken link <https://www.qwant.com/>\n" +
          "<br>\n" +
          "# Header A1\n" +
          "# Header A2\n" +
          "## Header B1\n" +
          "### Header C1\n" +
          "<br>-->link to <https://www.aau.at/>\n" +
          "<br>-->broken link <https://stackoverflow.com/>\n"+
          "<br>\n";

  @BeforeEach
  void setUp()throws MalformedURLException {
    reportService = new ReportServiceImpl();
    cli1 = mock(CommandLine.class);
    cli2 = mock(CommandLine.class);
    googleURL = new URL("https://www.google.at/");
    qwantURL = new URL("https://www.qwant.com/");
    aauURL = new URL("https://www.aau.at/");
    stackOverFlowURL = new URL("https://stackoverflow.com/");
    googleLink = new Link(googleURL, "Google", false);
    qwantLink = new Link(qwantURL, "Qwant", true);
    aauLink = new Link(aauURL, "AAU", false);
    stackOverFlowLink = new Link(stackOverFlowURL, "Stackoverflow", true);
    report = mock(Report.class);
    page1 = mock(Page.class);
    page2 = mock(Page.class);
    pageList = new LinkedList<>();
    headingList1 = new LinkedList<>();
    headingList2 = new LinkedList<>();
    linksList1 = new LinkedList<>();
    linksList2 = new LinkedList<>();

    when(cli1.getUrl()).thenReturn(googleURL);
    when(cli1.getDepth()).thenReturn(1);
    when(cli1.getTargetLanguage()).thenReturn(Locale.ENGLISH);
    when(cli2.getUrl()).thenReturn(qwantURL);
    when(cli2.getDepth()).thenReturn(3);
    when(cli2.getTargetLanguage()).thenReturn(Locale.FRENCH);

    when(report.getPageList()).thenReturn(pageList);
    when(page1.getHeadings()).thenReturn(headingList1);
    when(page1.streamLinks()).thenReturn(linksList1.stream());
    when(page2.getHeadings()).thenReturn(headingList2);
    when(page2.streamLinks()).thenReturn(linksList2.stream());

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

  }

  @Test
  void testcreateMarkdownReport() {

    reportService.createMarkdownReport(report, cli1);
    char[] resultReport = new char[350];
    Assertions.assertDoesNotThrow(()->{
      FileReader fileReader = new FileReader("google.md");
      fileReader.read(resultReport);
      fileReader.close();

      File resultFile = new File("google.md");
      if(!resultFile.delete()){
        throw new IOException();
      }
    });
    String resultReportString = new String(resultReport);
    Assertions.assertEquals(expectedResult, resultReportString);

    verify(cli1, times(2)).getUrl();
    verify(cli1, times(1)).getDepth();
    verify(cli1, times(1)).getTargetLanguage();

    verify(report, times(1)).getPageList();
    verify(page1, times(1)).streamLinks();
    verify(page1, times(1)).getHeadings();
    verify(page2, times(1)).streamLinks();
    verify(page2, times(1)).getHeadings();
  }
}
