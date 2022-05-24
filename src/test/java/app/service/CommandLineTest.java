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
  String[] commands1;
  String[] commands2;
  String[] commands3;
  String[] commands4;

  @BeforeEach
  void setUp() {
    cli = CommandLine.fromCommandLine();
    commands1 = new String[6];
    commands1[0] = "-d";
    commands1[1] = "3";
    commands1[2] = "-u";
    commands1[3] = "www.website.com";
    commands1[4] = "-l";
    commands1[5] = "de";

    commands2 = new String[2];
    commands2[0] = "--url";
    commands2[1] = "https://campus.aau.at/home";

    commands3 = new String[4];
    commands3[0] = "--url";
    commands3[1] = "www.aau.at";
    commands3[2] = "-u";
    commands3[3] = "www.stackoverflow.com";

    commands4 = new String[2];
    commands4[0] = "5";
    commands4[1] = "-d";
  }

  @Test
  void fromCommandLineTestFull() throws URISyntaxException {
    CommandLine commandLine = CommandLine.fromCommandLine(commands1);
    assertEquals(3, commandLine.getDepth());
    assertEquals(new Locale("de"), commandLine.getTargetLanguage());
    assertEquals(new URI("www.website.com"), commandLine.getUrl());
    assertTrue(commandLine.isValid());
  }

  @Test
  void commandLineTestPartly() throws URISyntaxException {
    CommandLine commandLine = CommandLine.fromCommandLine(commands2);
    assertEquals(2, commandLine.getDepth());
    assertNull(commandLine.getTargetLanguage());
    assertEquals(new URI("https://campus.aau.at/home"), commandLine.getUrl());
    assertTrue(commandLine.isValid());
  }

  @Test
  void commandLineTestTooManyArguments() throws URISyntaxException {
    CommandLine commandLine = CommandLine.fromCommandLine(commands3);
    assertEquals(2, commandLine.getDepth());
    assertNull(commandLine.getTargetLanguage());
    assertEquals(new URI("www.aau.at"), commandLine.getUrls().get(0));
    assertFalse(commandLine.isValid());
  }

  @Test
  void commandLineTestWrongOrder(){
    CommandLine commandLine = CommandLine.fromCommandLine(commands4);
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
              -u <url>, --url <url> - URL to crawl, e.g. "https://www.aau.at" or just "www.aau.at"
              -d <depth>, --depth <depth> - the depth of websites to crawl, e.g. 3
              -l <lang>, --lang <lang> - target language as IETF BCP 47 language tag, e.g. "de-AT" or "de"
            """, usage);
  }
}