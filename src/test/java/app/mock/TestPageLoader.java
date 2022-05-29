package app.mock;

import app.domain.Page;
import app.domain.PageLoader;
import app.exception.BrokenLinkException;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

public class TestPageLoader implements PageLoader {
  final ConcurrentHashMap<URI, Page> pages = new ConcurrentHashMap<>();

  public void mockPage(Page page) {
    pages.put(page.getPageUrl(), page);
  }

  @Override
  public Page loadPage(URI pageUrl) {
    Page page = pages.get(pageUrl);
    if (page == null) {
      throw new BrokenLinkException(pageUrl, new IOException("Page not found"));
    }
    return page;
  }
}
