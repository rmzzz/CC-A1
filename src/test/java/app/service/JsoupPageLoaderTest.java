package app.service;

import app.domain.Page;
import app.exception.BrokenLinkException;
import com.sun.net.httpserver.HttpServer;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JsoupPageLoaderTest {

  JsoupPageLoader pageLoader;
  Page page;
  Document document;

  @BeforeEach
  void setUp() {
    pageLoader = new JsoupPageLoader();
    document = mock(Document.class);
    page = new Page(URI.create("http://localhost"));
  }

  @AfterEach
  void tearDown() {
    reset(document);
  }

  @Test
  void loadPage() throws Throwable {
    String html = """
            <html lang="de-AT">
            <body>
            <p>
            <h1>Kapitel 1</h1>
            <h2>Kapitel 1.2</h2>
            <a href="http://localhost:8888/test.html">Test</a>
            </p>
            <p>
            <h1>Kapitel 2</h1>
            <a href="javascript:document.write('')">nicht klicken!</a>
            </p>
            </body>
            </html>
            """;
    testWithHttpServer(html, () -> {
      page = pageLoader.loadPage(URI.create("http://localhost:8888/test.html"));
      assertNotNull(page);
      assertEquals(Locale.forLanguageTag("de-AT"), page.getLanguage());
      assertEquals(3, page.getHeadings().size());
      assertEquals(1, page.getLinks().size());
    });
  }

  @Test
  void loadBrokenLink() throws Throwable {
    testWithHttpServer("", () -> {
      assertThrows(BrokenLinkException.class,
              () -> pageLoader.loadPage(URI.create("http://localhost:8888/broken-link.html")));
    });
  }

  @Test
  void loadDocument() throws Throwable {
    String html = """
            <html lang="en-US">
            <body>
            <p>
            <h1>header 1</h1>
            <h2>header 2</h2>
            <a href="http://localhost:8888/test.html">self ref</a>
            </p>
            </body>
            </html>
            """;
    testWithHttpServer(html, () -> {
      Document doc = pageLoader.loadDocument(URI.create("http://localhost:8888/test.html"));
      assertNotNull(doc);
      assertEquals("en-US", doc.getElementsByTag("html").attr("lang"));
      assertEquals("header 1", doc.getElementsByTag("h1").text());
      assertEquals("header 2", doc.getElementsByTag("h2").text());
      assertEquals("http://localhost:8888/test.html", doc.getElementsByTag("a").attr("href"));
    });
  }

  void testWithHttpServer(String html, Executable test) throws Throwable {
    HttpServer httpServer = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 8888), 3);
    try {
      httpServer.createContext("/test.html", exchange -> {
        InputStream in = exchange.getRequestBody();
        System.out.println("httpd: " + exchange.getRequestMethod() + " " + exchange.getRequestURI() + " " + exchange.getProtocol() + "\n"
                + new String(in.readAllBytes()));

        exchange.getResponseHeaders().add("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, html.length());
        try (OutputStream out = exchange.getResponseBody()) {
          out.write(html.getBytes(StandardCharsets.UTF_8));
          out.flush();
        }
      });
      httpServer.start();
      test.execute();
    } finally {
      httpServer.stop(1);
    }
  }

  @Test
  void updateLanguage() {
    doReturn(new Elements(new Element("html").attr("lang", "de-DE")))
            .when(document).getElementsByTag("html");
    pageLoader.updateLanguage(page, document);
    assertEquals(Locale.forLanguageTag("de-DE"), page.getLanguage());
  }

  @Test
  void updateHeaders() {
    Elements headersElements = new Elements(new Element("h1").text("chapter 1"),
            new Element("h2").text("chapter 1.1"),
            new Element("h3").text("chapter 1.1.1"),
            new Element("h2").text("chapter 1.2"),
            new Element("h1").text("chapter 2")
    );
    doReturn(headersElements).when(document).select("h1,h2,h3,h4,h5,h6");
    pageLoader.updateHeaders(page, document);
    assertEquals(headersElements.size(), page.getHeadings().size());
    assertEquals("chapter 1", page.getHeadings().get(0).getText());
    assertEquals("chapter 1.1", page.getHeadings().get(1).getText());
    assertEquals("chapter 1.1.1", page.getHeadings().get(2).getText());
    assertEquals("chapter 1.2", page.getHeadings().get(3).getText());
    assertEquals("chapter 2", page.getHeadings().get(4).getText());
  }

  @Test
  void parseHeadingRank1() {
    assertEquals(1, pageLoader.parseHeadingRank(new Element("h1")));
  }

  @Test
  void parseHeadingRank2() {
    assertEquals(2, pageLoader.parseHeadingRank(new Element("H2")));
  }

  @Test
  void parseHeadingRank6() {
    assertEquals(6, pageLoader.parseHeadingRank(new Element("h6")));
  }

  @Test
  void parseHeadingRankError() {
    assertThrows(IllegalArgumentException.class, () -> pageLoader.parseHeadingRank(new Element("h7")));
    assertThrows(IllegalArgumentException.class, () -> pageLoader.parseHeadingRank(new Element("a")));
    assertThrows(IllegalArgumentException.class, () -> pageLoader.parseHeadingRank(new Element("html")));
  }

  @Test
  void updateLinks() {
    doReturn(new Elements(
            new Element("a").attr("href", "http://localhost/1.html"),
            new Element("a").attr("href", "http://localhost/2.html"),
            new Element("a").attr("href", "javascript:alert('test')"),
            new Element("a").attr("href", "http://localhost/3.html")
    )).when(document).select("a");
    pageLoader.updateLinks(page, document);
    assertEquals(3, page.getLinks().size());
    assertEquals("http://localhost/1.html", page.getLinks().get(0).getUrl().toString());
    assertEquals("http://localhost/3.html", page.getLinks().get(2).getUrl().toString());
  }
}