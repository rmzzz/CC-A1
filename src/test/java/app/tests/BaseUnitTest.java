package app.tests;

import org.junit.jupiter.api.BeforeEach;

public class BaseUnitTest {
  @BeforeEach
  protected void init() {
    mockApiKey();
  }

  protected void mockApiKey() {
    System.setProperty("apiKey", "test");
  }
}
