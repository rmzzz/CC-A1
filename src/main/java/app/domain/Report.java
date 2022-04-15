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

  Page mainPage;
  Map<URI, Page> subPages = new LinkedHashMap<>();

  public void addPage(Page page) {
    mainPage = page;
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

  void addPageToList(Page page, List<Page> pageList, Set<URI> addedUrls, int depth) {
    if (!addedUrls.add(page.pageUrl)) {
      return;
    }
    pageList.add(page);
    page.streamLinks()
            .peek(link -> link.setDepth(depth))
            .peek(link -> link.setBroken(!subPages.containsKey(link.getUrl())))
            .filter(link -> !link.isBroken())
            .map(Link::getUrl)
            .map(subPages::get)
            .forEach(p -> addPageToList(p, pageList, addedUrls, depth + 1));
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
}
