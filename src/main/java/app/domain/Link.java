package app.domain;

import java.net.URI;

public class Link {
  final URI url;
  final String title;
  volatile boolean broken;

  volatile String errorMessage;
  volatile int depth;

  public Link(URI url, String title) {
    this(url, title, false);
  }
  public Link(URI url, String title, boolean broken) {
    this.url = url;
    this.title = title;
    this.broken = broken;
  }

  public URI getUrl() {
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

  public String getErrorMessage(){
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage){
    this.errorMessage = errorMessage;
  }
}
