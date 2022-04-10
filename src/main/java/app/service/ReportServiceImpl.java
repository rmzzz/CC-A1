package app.service;

import app.domain.Link;
import app.domain.Report;
import app.domain.ReportService;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

public class ReportServiceImpl implements ReportService {

    @Override
    public void createMarkdownReport(Report targetReport, CommandLine commandLineInput) {
      try{
        FileWriter fileWriter = new FileWriter(transformURLintoName(commandLineInput.getUrl()) + ".md");
        //TODO: Write Meta information

        //TODO Write contents of targetReport
        fileWriter.close();

      }catch (IOException ioException){
        System.out.println("An error occurred during file writing.");
        ioException.printStackTrace();
      }
    }

    private String transformURLintoName(URL targetURL){
      // TODO transform URL into String
      return targetURL.toString();
    }
}
