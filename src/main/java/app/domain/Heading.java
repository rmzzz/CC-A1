package app.domain;

public class Heading {
    private String headingTitle;
    private int headingDepth;

  public Heading(String headingTitle, int headingDepth){
      this.headingTitle = headingTitle;
      this.headingDepth = headingDepth;
    }

    public Heading(){};

  public String getHeadingTitle() {
    return headingTitle;
  }

  public int getHeadingDepth() {
    return headingDepth;
  }
}
