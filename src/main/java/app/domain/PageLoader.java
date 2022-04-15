package app.domain;

import java.net.URI;

public interface PageLoader {
  Page loadPage(URI pageUrl);
}
