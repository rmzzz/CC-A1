package app.domain;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class Page {
  final URL pageUrl;
  final List<Heading> headings = new LinkedList<>();
  final List<Link> links = new LinkedList<>();

  public Page(URL pageUrl) {
    this.pageUrl = pageUrl;
  }

  public URL getPageUrl() {
    return pageUrl;
  }

  public Stream<Heading> streamHeadings() {
    return headings.stream();
  }

  public Stream<Link> streamLinks() {
    return links.stream();
  }

}
