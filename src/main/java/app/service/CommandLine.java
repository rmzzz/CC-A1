package app.service;

import app.domain.InputParameters;

import java.net.URL;
import java.util.Locale;

public class CommandLine implements InputParameters {
  public static final String PARAM_URL = "url";
  public static final String PARAM_DEPTH = "depth";
  public static final String PARAM_LANGUAGE = "lang";

  private URL url;
  private int depth;
  private Locale targetLanguage;
  private boolean valid;

  public static CommandLine fromCommandLine(String... args) {
    CommandLine commandLine = new CommandLine();
    // TODO parse args
    return commandLine;
  }

  @Override
  public URL getUrl() {
    return url;
  }

  @Override
  public int getDepth() {
    return depth;
  }

  @Override
  public Locale getTargetLanguage() {
    return targetLanguage;
  }

  public boolean isValid() {
    return valid;
  }

  public String getUsage() {
    return String.format("""
                    Input parameters:
                      -%c <url>, --%s=<url> - URL to crawl, e.g. "https://www.aau.at" or just "www.aau.at"
                      -%c <depth>, --%s=<depth> - the depth of websites to crawl, e.g. 3
                      -%c <lang>, --%s=<lang> - target language as IETF BCP 47 language tag, e.g. "de-AT" or "de"
                    """,
            PARAM_URL.charAt(0), PARAM_URL,
            PARAM_DEPTH.charAt(0), PARAM_DEPTH,
            PARAM_LANGUAGE.charAt(0), PARAM_LANGUAGE);
  }
}
