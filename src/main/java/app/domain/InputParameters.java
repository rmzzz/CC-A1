package app.domain;

import java.net.URI;
import java.util.Locale;

public interface InputParameters {
    URI getUrl();

    int getDepth();

    Locale getTargetLanguage();
}
