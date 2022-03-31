package app.service;

import app.domain.InputParameters;

import java.net.URL;
import java.util.Locale;

public class CommandLine implements InputParameters {
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

    public void printUsage() {
        System.out.println("""
                Input parameters:
                  -u <url>, --url=<url> - URL to crawl, e.g. "https://www.aau.at" or just "www.aau.at"
                  -d <depth>, --depth=<depth> - the depth of websites to crawl, e.g. 3
                  -l <lang>, --lang=<lang> - target language as IETF BCP 47 language tag, e.g. "de-AT" or "de"
                """);
    }
}
