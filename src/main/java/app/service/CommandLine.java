package app.service;

import app.domain.InputParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class CommandLine implements InputParameters {
  public static final String PARAM_URL = "url";
  public static final String PARAM_DEPTH = "depth";
  public static final String PARAM_LANGUAGE = "lang";

  static Logger logger = LoggerFactory.getLogger(CommandLine.class);

  private URI url;

  private List<URI> urlList;
  private int depth;
  private Locale targetLanguage;
  private boolean valid;

  private CommandLine() {
    valid = true;
    urlList = new ArrayList<>();
    depth = 2;
  }

  public static CommandLine fromCommandLine(String... args) {
    CommandLine commandLine = new CommandLine();
    parseArguments(commandLine, args);
    return commandLine;
  }

  private static void parseArguments(CommandLine commandLine, String[] args){
    boolean urlFlag = false;
    boolean depthFlag = false;
    boolean langFlag = false;

    for (int i = 0; i < args.length; i = i + 2) {
      if (isArgumentAFlagOfType(args[i], PARAM_URL)) {
        urlFlag = commandLine.checkFlag(urlFlag);
        i += parseURLandReturnNumberOfURLs(commandLine, args, i) - 1; //-1 to compensate for the usual increment by 2 which shouldn't happen after URL parsing

      } else if (isArgumentAFlagOfType(args[i], PARAM_DEPTH)) {
        depthFlag = commandLine.checkFlag(depthFlag);
        parseDepth(commandLine, args[i+1]);

      } else if (isArgumentAFlagOfType(args[i], PARAM_LANGUAGE)) {
        langFlag = commandLine.checkFlag(langFlag);
        parseLanguage(commandLine, args[i+1]);

      } else {
        commandLine.valid = false;
      }
    }
  }



  private static int parseURLandReturnNumberOfURLs(CommandLine commandLine, String[] args, int urlFlagIndex){

    try {
      urlFlagIndex++;

      while(urlFlagIndex < args.length && !isArgumentAnyFlag(args[urlFlagIndex])){
        commandLine.urlList.add(new URI(args[urlFlagIndex]));
        urlFlagIndex++;
      }

    } catch (URISyntaxException exception) {
      logger.warn("Error when trying to parse URL from command line");
    }
    return commandLine.urlList.size();
  }

  private static void parseLanguage(CommandLine commandLine,String langString){
    commandLine.targetLanguage = new Locale(langString);
  }

  private static void parseDepth(CommandLine commandLine,String depthString){
    commandLine.depth = Integer.parseInt(depthString);
    commandLine.checkForValidDepth();
  }

  static boolean isArgumentAnyFlag(String argument){
    return isArgumentAFlagOfType(argument, PARAM_URL)
            || isArgumentAFlagOfType(argument, PARAM_DEPTH)
            || isArgumentAFlagOfType(argument, PARAM_LANGUAGE);
  }


  private boolean checkFlag(boolean flag) {
    if (flag) {
      valid = false;
    }
    return true;
  }

  void checkForValidDepth() {
    if (depth < 1) {
      valid = false;
    }
  }

  private static boolean isArgumentAFlagOfType(String argument, String flagType) {
    return (argument.equals("--" + flagType) || argument.equals("-" + flagType.charAt(0)));
  }

  @Override
  public URI getUrl() {
    return url;
  }

  @Override
  public List<URI> getUrls() {
    return Collections.unmodifiableList(urlList);
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
                      -%c <url>, --%s <url> - URLs to crawl, e.g. "https://www.aau.at" or just "www.aau.at" separated with space
                      -%c <depth>, --%s <depth> - the depth of websites to crawl, e.g. 3
                      -%c <lang>, --%s <lang> - target language as IETF BCP 47 language tag, e.g. "de-AT" or "de"
                    """,
            PARAM_URL.charAt(0), PARAM_URL,
            PARAM_DEPTH.charAt(0), PARAM_DEPTH,
            PARAM_LANGUAGE.charAt(0), PARAM_LANGUAGE);
  }
}
