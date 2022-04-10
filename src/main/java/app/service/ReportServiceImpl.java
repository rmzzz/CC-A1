package app.service;

import app.domain.Heading;
import app.domain.Link;
import app.domain.Page;
import app.domain.Report;
import app.domain.ReportService;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class ReportServiceImpl implements ReportService {

  private final String br = "<br>";

    @Override
    public void createMarkdownReport(Report targetReport, CommandLine commandLineInput) {
      try{
        FileWriter fileWriter = new FileWriter(transformURLintoName(commandLineInput.getUrl()) + ".md");
        //TODO: Write Meta information
        fileWriter.write("Input:\n");
        fileWriter.write(br + "<"+ commandLineInput.getUrl() +">\n");
        fileWriter.write(br + "depth: " + commandLineInput.getDepth() + "\n");
        fileWriter.write(br + "target language: " + commandLineInput.getTargetLanguage() + "\n");
        fileWriter.write(br + "report:\n");

        List<Page> pageList = targetReport.getPageList();


        for(Page page: pageList){

          StringBuilder urlLineToWrite = new StringBuilder();

          List<Heading> headingList = page.getHeadings();
          headingList.sort(new Comparator<Heading>() {
            @Override
            public int compare(Heading o1, Heading o2) {
              return o1.getHeadingDepth() - o2.getHeadingDepth();
            }
          });

          for(Heading heading : headingList){
            for(int i = 0; i< heading.getHeadingDepth();i++){
              urlLineToWrite.append("#");
            }
            urlLineToWrite.append(" "+heading.getHeadingTitle() +"\n");
          }

          urlLineToWrite.append(br+"--> ");

          page.streamLinks()
                  .forEach(l->
                          urlLineToWrite.append(br+"-->"+
                                  ((l.broken())?"broken link ":"link to <") +
                                  l.url() + ">\n"));
          urlLineToWrite.append(br+ "\n");
          fileWriter.write(urlLineToWrite.toString());
        }

        fileWriter.close();

      }catch (IOException ioException){
        System.out.println("An error occurred during file writing.\n");
        ioException.printStackTrace();
      }
    }

    private String transformURLintoName(URL targetURL){
      // TODO transform URL into String
      return targetURL.toString();
    }
}
