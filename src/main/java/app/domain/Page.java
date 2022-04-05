package app.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class Page {
  List<Link> links = new LinkedList<>();

  public Stream<Link> streamLinks() {
    return links.stream();
  }

}
