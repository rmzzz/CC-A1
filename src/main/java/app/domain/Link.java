package app.domain;

import java.net.URL;

public record Link(URL url, String title, boolean broken) {

}
