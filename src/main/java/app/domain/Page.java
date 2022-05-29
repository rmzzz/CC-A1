package app.domain;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class Page {
  public static final Page EMPTY = new Page(null);

  final URI pageUrl;
  final List<Heading> headings = new LinkedList<>();
  final List<Link> links = new LinkedList<>();
  Locale language;

  public Page(URI pageUrl) {
    this.pageUrl = pageUrl;
  }

  public URI getPageUrl() {
    return pageUrl;
  }

  public List<Link> getLinks() {
    return links;
  }

  public List<Heading> getHeadings() {
    return headings;
  }

  public void addHeading(Heading heading) {
    headings.add(heading);
  }

  public void addLink(Link link) {
    links.add(link);
  }

  public Page translate(TranslationService translationService, Locale targetLanguage) {
    translateAsync(translationService, targetLanguage)
            .join();
    return this;
  }

  public CompletableFuture<Void> translateAsync(TranslationService translationService, Locale targetLanguage) {
    var translationFutures = headings.stream()
            .map(heading -> heading.translate(translationService, language, targetLanguage)
                    .toCompletableFuture())
            .toArray(CompletableFuture[]::new);
    return CompletableFuture.allOf(translationFutures);
  }

  public Locale getLanguage() {
    return language;
  }

  public void setLanguage(String languageTag) {
    this.language = Locale.forLanguageTag(languageTag);
  }

  void setDepth(int depth) {
    headings.forEach(heading -> heading.setDepth(depth));
    links.forEach(link -> link.setDepth(depth));
  }
}
