package app.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class Page {
  List<Link> links = new LinkedList<>();
  List<Heading> headings = new LinkedList<>();

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
