package app.domain;

import java.net.URI;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Report {

  Locale sourceLanguage;
  Locale targetLanguage;
  int maxDepth;

  Page mainPage;
  Map<URI, Page> subPages = new LinkedHashMap<>();

  public Report(Page mainPage, int maxDepth, Locale targetLanguage) {
    this.mainPage = mainPage;
    this.maxDepth = maxDepth;
    this.sourceLanguage = mainPage.language;
    this.targetLanguage = targetLanguage;
  }

  public Report merge(Report report) {
    Page subPage = report.mainPage;
    subPages.put(subPage.pageUrl, subPage);
    subPages.putAll(report.subPages);
    return this;
  }

  public List<Page> getPageList() {
    List<Page> pageList = new LinkedList<>();
    Set<URI> addedUrls = new HashSet<>();
    addPageToList(mainPage, pageList, addedUrls, 1);
    return pageList;
  }

  void addPageToList(Page page, List<Page> pageList, Set<URI> addedUrls, int currentDepth) {
    if (!addedUrls.add(page.pageUrl)) {
      return;
    }
    pageList.add(page);
    page.setDepth(currentDepth);
    if (currentDepth < maxDepth) {
      for (Link link : page.getLinks()) {
        Page subPage = subPages.get(link.getUrl());
        if (subPage == null) {
          link.setBroken(true);
        } else {
          addPageToList(subPage, pageList, addedUrls, currentDepth + 1);
        }
      }
    }
  }

  public URI getInputUrl() {
    return mainPage.getPageUrl();
  }

  public Locale getSourceLanguage() {
    return sourceLanguage;
  }

  public Locale getTargetLanguage() {
    return targetLanguage;
  }

  public int getDepth() {
    return maxDepth;
  }
}
