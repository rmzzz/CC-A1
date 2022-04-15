package app.domain;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

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

  public Stream<Heading> streamHeadings() {
    return headings.stream();
  }

  public Stream<Link> streamLinks() {
    return links.stream();
  }

  public List<Link> getLinks(){
    return links;
  }

  public List<Heading> getHeadings(){
    return headings;
  }
}
