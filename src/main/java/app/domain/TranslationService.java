package app.domain;

import java.util.Locale;

public interface TranslationService {
  String translateText(String originalText, Locale targetLanguage);
}
