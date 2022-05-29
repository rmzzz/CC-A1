package app.domain;

import java.net.URI;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Report {

  public static final Report EMPTY = new Report() {
    @Override
    public Report merge(Report report) {
      return report;
    }
  };

  Locale sourceLanguage;
  Locale targetLanguage;

  int depth;
  int maxDepth;

  final List<Page> mainPages = new LinkedList<>();
  Map<URI, Page> subPages = new LinkedHashMap<>();

  private Report() {
  }

  public Report(Page mainPage, int maxDepth, Locale targetLanguage) {
    this(mainPage, 0, maxDepth, targetLanguage);
  }
  public Report(Page mainPage, int depth, int maxDepth, Locale targetLanguage) {
    this.mainPages.add(mainPage);
    this.depth = depth;
    this.maxDepth = maxDepth;
    this.sourceLanguage = mainPage.language;
    this.targetLanguage = targetLanguage;
  }

  public Report merge(Report report) {
    if (this.depth > report.depth) {
      for(var page : mainPages) {
        report.subPages.put(page.getPageUrl(), page);
      }
      report.subPages.putAll(subPages);
      return report;
    }
    if (this.depth == report.depth) {
      this.mainPages.addAll(report.mainPages);
    } else {
      for(var page : report.mainPages) {
        this.subPages.put(page.getPageUrl(), page);
      }
    }
    subPages.putAll(report.subPages);
    return this;
  }

  public List<Page> getPageList() {
    List<Page> pageList = new LinkedList<>();
    Set<URI> addedUrls = new HashSet<>();
    for(Page mainPage : mainPages) {
      addPageToList(mainPage, pageList, addedUrls, 1);
    }
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

  @Deprecated
  public URI getInputUrl() {
    return mainPages.get(0).getPageUrl();
  }

  public List<URI> getInputUrls() {
    return mainPages.stream()
            .map(Page::getPageUrl)
            .collect(Collectors.toList());
  }

  public Locale getSourceLanguage() {
    return sourceLanguage;
  }

  public Locale getTargetLanguage() {
    return targetLanguage;
  }

  public int getMaxDepth() {
    return maxDepth;
  }
}
