package app.tests;

import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseUnitTest {
  protected Logger logger = LoggerFactory.getLogger(getClass());

  @BeforeEach
  protected void init() {
    mockApiKey();
  }

  protected void mockApiKey() {
    System.setProperty("apiKey", "test");
  }

  protected void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
