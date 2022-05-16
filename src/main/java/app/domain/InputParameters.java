package app.domain;

import java.net.URI;
import java.util.List;
import java.util.Locale;

public interface InputParameters {
  @Deprecated(forRemoval = true)
  URI getUrl();

  default List<URI> getUrls() {
    return List.of(getUrl());
  }

  int getDepth();

  Locale getTargetLanguage();
}
