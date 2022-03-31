package app.domain;

import java.net.URL;
import java.util.Locale;

public interface InputParameters {
    URL getUrl();

    int getDepth();

    Locale getTargetLanguage();
}
