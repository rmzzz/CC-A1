package app.service;

import app.domain.Page;
import app.domain.PageLoader;

import java.net.URI;

public class WebPageLoader implements PageLoader {
  @Override
  public Page loadPage(URI pageUrl) {
    // TODO complete impl
    return new Page(pageUrl);
  }
}
