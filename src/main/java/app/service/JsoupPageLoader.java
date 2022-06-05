package app.service;

import app.domain.Heading;
import app.domain.Link;
import app.domain.Page;
import app.domain.PageLoader;
import app.exception.BrokenLinkException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;


/**
 * PageLoader backed by Jsoup.
 *
 * @see <a href="https://jsoup.org/apidocs/">Jsoup API docs</a>
 */
public class JsoupPageLoader implements PageLoader {
  static final Logger logger = LoggerFactory.getLogger(JsoupPageLoader.class);

  @Override
  public Page loadPage(URI pageUrl) {
    Document document = loadDocument(pageUrl);
    Page page = new Page(pageUrl);
    updateLanguage(page, document);
    updateHeaders(page, document);
    updateLinks(page, document);
    return page;
  }

  Document loadDocument(URI url) {
    try {
      Document document = Jsoup.connect(url.toString()).get();
      logger.info("Loaded URL " + url);
      return document;
    } catch (IOException ex) {
      logger.warn("Error loading URL " + url, ex);
      throw new BrokenLinkException(url, ex);
    }
  }

  void updateLanguage(Page page, Document document) {
    Elements htmlElement = document.getElementsByTag("html");
    String langAttribute = htmlElement.attr("lang");
    if (!langAttribute.isBlank()) {
      page.setLanguage(langAttribute);
    }
  }

  void updateHeaders(Page page, Document document) {
    Elements headerElements = document.select("h1,h2,h3,h4,h5,h6");
    for (var header : headerElements) {
      int rank = parseHeadingRank(header);
      String text = header.text();
      page.addHeading(new Heading(text, rank));
    }
  }

  int parseHeadingRank(Element heading) {
    String tagName = heading.tagName();
    if (tagName.matches("[Hh][1-6]")) {
      return heading.tagName().charAt(1) - '0';
    }
    throw new IllegalArgumentException("Invalid heading element: " + heading);
  }

  void updateLinks(Page page, Document document) {
    Elements linkElements = document.select("a");
    for (var link : linkElements) {
      String text = link.text();
      String href = link.attr("abs:href");
      if (!href.startsWith("http")) {
        logger.debug("skipping non-http link {}", href);
        continue;
      }
      page.addLink(new Link(URI.create(href), text));
    }
  }
}
