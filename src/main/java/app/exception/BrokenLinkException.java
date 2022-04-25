package app.exception;

import java.net.URI;

public class BrokenLinkException extends RuntimeException {
  public BrokenLinkException(URI url, Throwable cause) {
    super("Broken link: " + url, cause);
  }
}
