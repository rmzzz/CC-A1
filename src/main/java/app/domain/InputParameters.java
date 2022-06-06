package app.domain;

import java.net.URI;
import java.util.List;
import java.util.Locale;

public interface InputParameters {
  List<URI> getUrls();

  int getDepth();

  Locale getTargetLanguage();

  int getThreadsCount();
}
