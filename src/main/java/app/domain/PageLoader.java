package app.domain;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public interface PageLoader {
  Page loadPage(URI pageUrl);
}
