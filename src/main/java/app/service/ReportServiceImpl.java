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

  private final String markDownBreak = "<br>";

    @Override
    public void createMarkdownReport(Report targetReport, CommandLine commandLineInput) {

      String reportString = "";
      reportString += createMetaInformationAsString(commandLineInput);
      reportString += createPageContentAsString(targetReport.getPageList());

      try{
        FileWriter fileWriter = new FileWriter(transformURLintoName(commandLineInput.getUrl()) + ".md");
        fileWriter.write(reportString);
        fileWriter.close();
      }catch (IOException ioException){
        System.out.println("An error occurred during file writing.\n");
        ioException.printStackTrace();
      }
    }

    private String createMetaInformationAsString(CommandLine commandLineInput){
      return "Input:\n"
              + markDownBreak + "<"+ commandLineInput.getUrl() +">\n"
              + markDownBreak + "depth: " + commandLineInput.getDepth() + "\n"
              + markDownBreak + "target language: " + commandLineInput.getTargetLanguage() + "\n"
              + markDownBreak + "report:\n";
    }

    private String createPageContentAsString(List<Page> pageList){
      StringBuilder pageContent = new StringBuilder();

      for(Page page: pageList){
       pageContent.append(createHeadingsAsString(page.getHeadings()));
       pageContent.append(createLinksAsString(page.streamLinks()));
      }
      return  pageContent.toString();
    }

    private String createHeadingsAsString(List<Heading> headingList){
      headingList.sort((o1, o2) -> o1.getHeadingDepth() - o2.getHeadingDepth());

      StringBuilder headingString = new StringBuilder();
      for(Heading heading : headingList){
        for(int i = 0; i< heading.getHeadingDepth();i++){
          headingString.append('#');
        }
        headingString.append(" ");
        headingString.append(heading.getHeadingTitle());
        headingString.append("\n");
      }
      return headingString.toString();
    }

    private String createLinksAsString(Stream<Link> linkStream){
      StringBuilder linksAsString = new StringBuilder();

      linkStream.forEach(link-> linksAsString.append(createSingleLinkAsString(link)));
      linksAsString.append(markDownBreak + "\n");

      return linksAsString.toString();
    }

    private String createSingleLinkAsString(Link link){
      return (markDownBreak + "-->"+ ((link.broken())?"broken link ":"link to <") + link.url() + ">\n");
    }

    private String transformURLintoName(URL targetURL){
      // TODO transform URL into String
      return "stub";
    }
}
