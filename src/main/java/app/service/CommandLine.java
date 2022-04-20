package app.service;

import app.domain.InputParameters;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.logging.Logger;

public class CommandLine implements InputParameters {
  public static final String PARAM_URL = "url";
  public static final String PARAM_DEPTH = "depth";
  public static final String PARAM_LANGUAGE = "lang";

  static Logger commandLineLogger = Logger.getLogger("app.service.CommandLine");

  private URI url;
  private int depth;
  private Locale targetLanguage;
  private boolean valid;

  public CommandLine(){
    setDefaultValues();
  }

  public static CommandLine fromCommandLine(String... args) {
    CommandLine commandLine = new CommandLine();

    commandLine.setDefaultValues();
    boolean urlFlag = false;
    boolean depthFlag = false;
    boolean langFlag = false;

    for (int i = 0; i < args.length; i=i+2) {
      try{
        if(commandLine.checkArgument(args[i], PARAM_URL)){
          urlFlag = commandLine.checkFlag(urlFlag);
          commandLine.url= new URI(args[i+1]);
        }
        else if(commandLine.checkArgument(args[i], PARAM_DEPTH)){
          depthFlag = commandLine.checkFlag(depthFlag);
          commandLine.depth = Integer.parseInt(args[i+1]);
          commandLine.checkForValidDepth();
        }
        else if(commandLine.checkArgument(args[i], PARAM_LANGUAGE)){
          langFlag = commandLine.checkFlag(langFlag);
          commandLine.targetLanguage = new Locale(args[i+1]);
        }
        else{
          commandLine.valid = false;
        }
      }catch(URISyntaxException exception){
        commandLineLogger.warning("Error when trying to parse URL from command line");
      }
    }
    return commandLine;
  }

  private void setDefaultValues() {
    valid = true;
    depth = 2;
    targetLanguage = new Locale("en");
    try {
      url = new URI("http://histo.io/");
    } catch (URISyntaxException e) {
      commandLineLogger.warning("Error while parsing. Default URL could not be parsed.");
      throw new RuntimeException(e);
    }
  }

  private boolean checkFlag(boolean flag){
    if(flag){
      valid = false;
    }
    return true;
  }

  private void checkForValidDepth(){
    if(depth < 1){
      valid = false;
    }
  }

  private boolean checkArgument(String argument, String commandName){
    return (argument.equals("--"+commandName) || argument.equals("-"+commandName.charAt(0)));
  }

  @Override
  public URI getUrl() {
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
