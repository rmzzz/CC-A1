package app.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportTest {
  Page mainPage, subPage1, subPage2, leafPage11, leafPage12;
  Report report;

  @BeforeEach
  void setUp() throws Exception {
    mainPage = new Page(new URL("http://localhost/main"));
    subPage1 = new Page(new URL("http://localhost/sub1"));
    subPage2 = new Page(new URL("http://localhost/sub2"));
    leafPage11 = new Page(new URL("http://localhost/leaf11"));
    leafPage12 = new Page(new URL("http://localhost/leaf12"));
    mainPage.links.add(new Link(subPage1.pageUrl, "sub1"));
    mainPage.links.add(new Link(subPage2.pageUrl, "sub2"));
    subPage1.links.add(new Link(leafPage11.pageUrl, "leaf11"));
    subPage1.links.add(new Link(leafPage12.pageUrl, "leaf12"));
    subPage2.links.add(new Link(new URL("http://broken"), "broken", true));
    report = new Report();
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void addPage() {
    report.addPage(mainPage);
    assertEquals(mainPage, report.mainPage);
  }

  @Test
  void mergeSingle() {
    report.addPage(mainPage);
    Report subReport = new Report();
    subReport.addPage(subPage1);
    report.merge(subReport);

    assertEquals(1, report.subPages.size());
    assertEquals(subPage1, report.subPages.get(subPage1.pageUrl));
  }

  @Test
  void mergeMerged() {
    report.addPage(mainPage);
    Report mergedReport = new Report();
    mergedReport.addPage(subPage1);
    Report leafReport1 = new Report();
    leafReport1.addPage(leafPage11);
    mergedReport.merge(leafReport1);
    Report leafReport2 = new Report();
    leafReport2.addPage(leafPage12);
    mergedReport.merge(leafReport2);

    report.merge(mergedReport);

    assertEquals(3, report.subPages.size());
    assertEquals(subPage1, report.subPages.get(subPage1.pageUrl));
    assertEquals(leafPage11, report.subPages.get(leafPage11.pageUrl));
    assertEquals(leafPage12, report.subPages.get(leafPage12.pageUrl));
  }

  @Test
  void getPageList() {
    report.addPage(mainPage);

    Report subReport1 = new Report();
    subReport1.addPage(subPage1);

    Report leafReport1 = new Report();
    leafReport1.addPage(leafPage11);
    subReport1.merge(leafReport1);

    Report leafReport2 = new Report();
    leafReport2.addPage(leafPage12);
    subReport1.merge(leafReport2);

    report.merge(subReport1);

    Report subReport2 = new Report();
    subReport2.addPage(subPage2);
    report.merge(subReport2);

    List<Page> pageList = report.getPageList();
    assertEquals(5, pageList.size());
    assertEquals(mainPage, pageList.get(0));
    assertTrue(mainPage.streamLinks().allMatch(l -> l.depth == 1));
    assertEquals(subPage1, pageList.get(1));
    assertTrue(subPage1.streamLinks().allMatch(l -> l.depth == 2));
    assertEquals(leafPage11, pageList.get(2));
    assertEquals(leafPage12, pageList.get(3));
    assertEquals(subPage2, pageList.get(4));
    assertTrue(subPage2.streamLinks().allMatch(Link::isBroken));
  }

  @Test
  void getInputUrl() {
    report.addPage(mainPage);
    assertEquals(mainPage.pageUrl, report.getInputUrl());
  }

  @Test
  void getSourceLanguage() {
  }

  @Test
  void getTargetLanguage() {
  }
}