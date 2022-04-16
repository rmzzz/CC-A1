package app.domain;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Page {
  final URI pageUrl;
  final List<Heading> headings = new LinkedList<>();
  final List<Link> links = new LinkedList<>();

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
    for (Heading heading : headings) {
      heading.translate(translationService, targetLanguage);
    }
    return this;
  }
}
