package app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandLineTest {

  CommandLine cli;

  @BeforeEach
  void setUp() {
    cli = new CommandLine();
  }

  @Test
  void fromCommandLine() {
  }

  @Test
  void getUrl() {
  }

  @Test
  void getDepth() {
  }

  @Test
  void getTargetLanguage() {
  }

  @Test
  void isValid() {
  }

  @Test
  void getUsage() {
    String usage = cli.getUsage();

    assertEquals("""
            Input parameters:
              -u <url>, --url=<url> - URL to crawl, e.g. "https://www.aau.at" or just "www.aau.at"
              -d <depth>, --depth=<depth> - the depth of websites to crawl, e.g. 3
              -l <lang>, --lang=<lang> - target language as IETF BCP 47 language tag, e.g. "de-AT" or "de"
            """, usage);
  }
}