package app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class CommandLineTest {

  CommandLine cli;
  String[] commandsFull;
  String[] commandsWithoutLanguage;
  String[] commandsTooManyURLFlags;
  String[] commandsArgumentBeforeFlag;

  String[] commandsThreeURLs;

  String[] commandsNegativeDepth;

  ArrayList<URI> expectedURLListForCommands5;

  @BeforeEach
  void setUp() throws URISyntaxException {
    cli = CommandLine.fromCommandLine();
    commandsFull = new String[6];
    commandsFull[0] = "-d";
    commandsFull[1] = "3";
    commandsFull[2] = "-u";
    commandsFull[3] = "www.website.com";
    commandsFull[4] = "-l";
    commandsFull[5] = "de";

    commandsWithoutLanguage = new String[2];
    commandsWithoutLanguage[0] = "--url";
    commandsWithoutLanguage[1] = "https://campus.aau.at/home";

    commandsTooManyURLFlags = new String[4];
    commandsTooManyURLFlags[0] = "--url";
    commandsTooManyURLFlags[1] = "www.aau.at";
    commandsTooManyURLFlags[2] = "-u";
    commandsTooManyURLFlags[3] = "www.stackoverflow.com";

    commandsArgumentBeforeFlag = new String[2];
    commandsArgumentBeforeFlag[0] = "5";
    commandsArgumentBeforeFlag[1] = "-d";

    commandsThreeURLs = new String[6];
    commandsThreeURLs[0] = "--url";
    commandsThreeURLs[1] = "https://campus.aau.at/home";
    commandsThreeURLs[2] = "www.stackoverflow.com";
    commandsThreeURLs[3] = "www.aau.at";
    commandsThreeURLs[4] = "--depth";
    commandsThreeURLs[5] = "2";

    commandsNegativeDepth = new String[2];
    commandsNegativeDepth[0] = "-d";
    commandsNegativeDepth[1] = "-3";

    expectedURLListForCommands5 = new ArrayList<>();
    expectedURLListForCommands5.add(new URI("https://campus.aau.at/home"));
    expectedURLListForCommands5.add(new URI("www.stackoverflow.com"));
    expectedURLListForCommands5.add(new URI("www.aau.at"));
  }

  @Test
  void fromCommandLineTestFull() throws URISyntaxException {
    CommandLine commandLine = CommandLine.fromCommandLine(commandsFull);
    assertEquals(3, commandLine.getDepth());
    assertEquals(new Locale("de"), commandLine.getTargetLanguage());
    assertEquals(new URI("www.website.com"), commandLine.getUrls().get(0));
    assertTrue(commandLine.isValid());
  }

  @Test
  void commandLineTestPartly() throws URISyntaxException {
    CommandLine commandLine = CommandLine.fromCommandLine(commandsWithoutLanguage);
    assertEquals(2, commandLine.getDepth());
    assertNull(commandLine.getTargetLanguage());
    assertEquals(new URI("https://campus.aau.at/home"), commandLine.getUrls().get(0));
    assertTrue(commandLine.isValid());
  }

  @Test
  void commandLineTestTooManyArguments() throws URISyntaxException {
    CommandLine commandLine = CommandLine.fromCommandLine(commandsTooManyURLFlags);
    assertEquals(2, commandLine.getDepth());
    assertNull(commandLine.getTargetLanguage());
    assertEquals(new URI("www.aau.at"), commandLine.getUrls().get(0));
    assertFalse(commandLine.isValid());
  }

  @Test
  void commandLineTestWrongOrder(){
    CommandLine commandLine = CommandLine.fromCommandLine(commandsArgumentBeforeFlag);
    assertFalse(commandLine.isValid());
  }

  @Test
  void commandLineTestMultipleURLs(){
    CommandLine commandLine = CommandLine.fromCommandLine(commandsThreeURLs);
    assertEquals(2, commandLine.getDepth());
    assertEquals(expectedURLListForCommands5, commandLine.getUrls());
  }

  @Test
  void testInvalidDepth(){
    CommandLine commandLine = CommandLine.fromCommandLine(commandsNegativeDepth);
    assertFalse(commandLine.isValid());
  }

  @Test
  void getDepth() {
    assertEquals(2, cli.getDepth());
  }

  @Test
  void getTargetLanguage() {
    assertNull(cli.getTargetLanguage());
  }

  @Test
  void isValid() {
    assertTrue(cli.isValid());
  }

  @Test
  void getUsage() {
    String usage = cli.getUsage();

    assertEquals("""
            Input parameters:
              -u <url>, --url <url> - URLs to crawl, e.g. "https://www.aau.at" or just "www.aau.at" separated with space
              -d <depth>, --depth <depth> - the depth of websites to crawl, e.g. 3
              -l <lang>, --lang <lang> - target language as IETF BCP 47 language tag, e.g. "de-AT" or "de"
              -t <count> --threads <count> - set number of threads. By default number will be selected based on available CPU cores.
            """, usage);
  }
}