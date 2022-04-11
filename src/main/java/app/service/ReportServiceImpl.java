package app.service;

import app.domain.Heading;
import app.domain.Link;
import app.domain.Page;
import app.domain.Report;
import app.domain.ReportService;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Stream;

public class ReportServiceImpl implements ReportService {

  private static final String MARK_DOWN_BREAK = "<br>";

    @Override
    public void createMarkdownReport(Report targetReport, CommandLine commandLineInput) {

      String reportString = "";
      reportString += createMetaInformationAsString(commandLineInput);
      reportString += createPageContentAsString(targetReport.getPageList());

      try{
        FileWriter fileWriter = new FileWriter(extractDomainNameFromURL(commandLineInput.getUrl()) + ".md");
        fileWriter.write(reportString);
        fileWriter.close();
      }catch (IOException ioException){
        System.out.println("An error occurred during file writing.\n");
        ioException.printStackTrace();
      }
    }

    private String createMetaInformationAsString(CommandLine commandLineInput){
      return "Input:\n"
              + MARK_DOWN_BREAK + "<"+ commandLineInput.getUrl() +">\n"
              + MARK_DOWN_BREAK + "depth: " + commandLineInput.getDepth() + "\n"
              + MARK_DOWN_BREAK + "target language: " + commandLineInput.getTargetLanguage() + "\n"
              + MARK_DOWN_BREAK + "report:\n";
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
      linksAsString.append(MARK_DOWN_BREAK + "\n");

      return linksAsString.toString();
    }

    private String createSingleLinkAsString(Link link){
      return (MARK_DOWN_BREAK + "-->"+ ((link.broken())?"broken link ":"link to <") + link.url() + ">\n");
    }

    public String extractDomainNameFromURL(URL targetURL){

      String result = null;
      try{
        String URLstring = targetURL.getHost();
        String[] URLsubstrings = URLstring.split("\\.");

        for(String substring : URLsubstrings){
          if(!substring.matches("www")){
            result = substring;
            break;
          }
        }
        if(result == null){
          throw new NullPointerException();
        }
      }catch (NullPointerException nullPointerException){
        nullPointerException.printStackTrace();
        System.out.println("URL could not be transformed into domain name. The file will be called \"report.md\" instead.");
        return "report";
      }
      return result;
    }
}
