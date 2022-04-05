package app.domain;

import java.net.URL;

public interface PageLoader {
  Page loadPage(URL pageUrl);
}
