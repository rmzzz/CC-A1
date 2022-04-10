package app.service;

import app.domain.Heading;
import app.domain.Link;
import app.domain.Page;
import app.domain.Report;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Locale;

public class ReportServiceTest {



  @Test
  void testcreateMarkdownReport() {
    CommandLine cli = Mockito.mock(CommandLine.class);
    URL testURL = null;
    try {
      testURL = new URL("http://localhost");
    }catch (MalformedURLException malformedURLException){
      malformedURLException.printStackTrace();
    }

    Mockito.when(cli.getUrl()).thenReturn(testURL);
    Mockito.when(cli.getDepth()).thenReturn(1);
    Mockito.when(cli.getTargetLanguage()).thenReturn(new Locale("de"));

    Report report = Mockito.mock(Report.class);
    LinkedList<Page> pages = new LinkedList<>();
    Page page1 = Mockito.mock(Page.class);
    LinkedList<Heading> headingList = new LinkedList<>();
    headingList.add(new Heading("Heading1", 1));
    headingList.add(new Heading("Heading2", 2));
    headingList.add(new Heading("Heading3", 2));
    Link link = null;
    try{
      link = new Link(new URL("http://localhost"), "Google", false);
    }catch (MalformedURLException malformedURLException){
      malformedURLException.printStackTrace();
    }

    LinkedList<Link> links = new LinkedList<>();
    links.add(link);

    Mockito.when(page1.streamLinks()).thenReturn(links.stream());
    Mockito.when(page1.getHeadings()).thenReturn(headingList);
    pages.add(page1);
    Mockito.when(report.getPageList()).thenReturn(pages);

    new ReportServiceImpl().createMarkdownReport(report, cli);

  }
}
