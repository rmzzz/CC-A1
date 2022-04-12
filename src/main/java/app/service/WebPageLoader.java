package app.service;

import app.domain.Page;
import app.domain.PageLoader;

import java.net.URL;

public class WebPageLoader implements PageLoader {
  @Override
  public Page loadPage(URL pageUrl) {
    // TODO complete impl
    return new Page(pageUrl);
  }
}
