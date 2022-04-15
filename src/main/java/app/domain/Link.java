package app.domain;

import java.net.URL;

public class Link {
  final URL url;
  final String title;
  boolean broken;

  int depth;

  public Link(URL url, String title) {
    this(url, title, false);
  }
  public Link(URL url, String title, boolean broken) {
    this.url = url;
    this.title = title;
    this.broken = broken;
  }

  public URL getUrl() {
    return url;
  }

  public String getTitle() {
    return title;
  }

  public boolean isBroken() {
    return broken;
  }

  public void setBroken(boolean broken) {
    this.broken = broken;
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }
}
