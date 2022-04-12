package app.domain;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.stream.Stream;

public class Report {

  Locale sourceLanguage;
  Locale targetLanguage;

  Page mainPage;
  Map<URL, Page> subPages = new LinkedHashMap<>();

  private ArrayList<Heading> heading;

  public void addPage(Page page) {
    mainPage = page;
  }

  public Report merge(Report report) {
    Page subPage = report.mainPage;
    subPages.put(subPage.pageUrl, subPage);
    subPages.putAll(report.subPages);
    return this;
  }

  public List<Page> getPageList(){
    //TODO implement page list
    return null;
  }


  public URL getInputUrl() {
    return mainPage.getPageUrl();
  }

  public Locale getSourceLanguage() {
    return sourceLanguage;
  }

  public Locale getTargetLanguage() {
    return targetLanguage;
  }
}
