package app.domain;

import app.service.ReportServiceImpl;
import app.service.TranslationServiceImpl;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.Queue;

public class WebCrawler {
    final InputParameters inputParameters;

    private Queue<URL> urlQueue;
    private ReportService reportService;

    public WebCrawler(InputParameters inputParameters) {
        this.inputParameters = inputParameters;

        urlQueue = new ArrayDeque<URL>();
        urlQueue.add(inputParameters.getUrl());
    }

    public void crawl() {

        Report resultReport = new Report();

        for(int i = 0; i < inputParameters.getDepth();i++){

            //TODO crawl website
            urlQueue.remove();

            //TODO translate heading

            //TODO update report

        }
        reportService.createMarkdownReport(resultReport);

    }

    public void addURLtoQueue(URL urlToAdd){

        urlQueue.add(urlToAdd);
    }
}
