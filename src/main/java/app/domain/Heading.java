package app.domain;

import java.util.Locale;
import java.util.concurrent.CompletionStage;

/**
 * Section heading element from HTML document
 *
 * @see <a href="https://www.w3.org/TR/2014/REC-html5-20141028/sections.html#the-h1,-h2,-h3,-h4,-h5,-and-h6-elements">HTML5 spec</a>
 */
public class Heading {
  /**
   * Heading text, i.e. as received from Element.innerText
   */
  final String originalText;

  volatile String translatedText;

  /**
   * Heading rank according to html5 specification
   */
  final int rank;

  volatile int depth;

  public Heading(String originalText, int rank) {
    this.originalText = originalText;
    this.rank = rank;
  }
  public Heading(String originalText, int rank, int depth) {
    this.originalText = originalText;
    this.rank = rank;
    this.depth = depth;
  }

  public String getText() {
    return translatedText != null ? translatedText : originalText;
  }

  public int getRank() {
    return rank;
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }

  public CompletionStage<Void> translate(TranslationService translationService, Locale sourceLanguage, Locale targetLanguage) {
    return translationService.translateText(originalText, sourceLanguage, targetLanguage, this::setTranslatedText);
  }

  synchronized void setTranslatedText(String text) {
    translatedText = text;
  }

}
