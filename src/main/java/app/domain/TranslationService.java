package app.domain;

import java.util.Locale;

public interface TranslationService {
  Heading translateHeading(Heading headingToTranslate);

  Report translateReport(Report report, Locale targetLanguage);
}
