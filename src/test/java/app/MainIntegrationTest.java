package app;

import app.domain.InputParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("because it crawls real web pages, thus, not suitable for automated tests")
class MainIntegrationTest {
  InputParameters testInputParameters;
  List<URI> urls;
  int depth;
  Locale targetLanguage;

  @BeforeEach
  void setUp() {
    urls = new ArrayList<>();
    testInputParameters = new InputParameters() {

      @Override
      public List<URI> getUrls() {
        return urls;
      }

      @Override
      public int getDepth() {
        return depth;
      }

      @Override
      public Locale getTargetLanguage() {
        return targetLanguage;
      }

      @Override
      public int getThreadsCount() {
        return 0;
      }
    };
  }

  @Test
  void executeCommandOnAAUwithDepth1() throws IOException {
    urls.add(URI.create("https://www.aau.at"));
    depth = 1;
    targetLanguage = Locale.ENGLISH;
    Main.executeCommand(testInputParameters);
    Path reportFile = Path.of("aau.md");
    assertTrue(Files.exists(reportFile));
    String report = Files.readString(reportFile);
    System.out.println(report);
  }

  @Test
  void executeCommandOnAAUwithRU() throws IOException {
    urls.add(URI.create("https://www.aau.at"));
    depth = 1;
    targetLanguage = new Locale("ru");
    Main.executeCommand(testInputParameters);
    Path reportFile = Path.of("aau.md");
    assertTrue(Files.exists(reportFile));
    String report = Files.readString(reportFile);
    System.out.println(report);
  }

}