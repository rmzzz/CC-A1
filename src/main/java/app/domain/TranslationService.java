package app.domain;

public interface TranslationService {
  Heading translateHeading(Heading headingToTranslate);

  Report translateReport(Report report);
}
