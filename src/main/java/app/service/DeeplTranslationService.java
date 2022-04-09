package app.service;

import app.domain.Heading;
import app.domain.Report;
import app.domain.TranslationService;

public class DeeplTranslationService implements TranslationService {
  @Override
  public Heading translateHeading(Heading headingToTranslate) {
    // TODO implement translation
    return new Heading();
  }

  @Override
  public Report translateReport(Report report) {
    // TODO implement translation
    return report;
  }
}
