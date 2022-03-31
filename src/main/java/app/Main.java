package app;

import app.domain.WebCrawler;
import app.service.CommandLine;

public class Main {
    public static void main(String[] args) {
        CommandLine cli = CommandLine.fromCommandLine(args);
        if(!cli.isValid()) {
            cli.printUsage();
            return;
        }

        new WebCrawler(cli).crawl();
    }
}
