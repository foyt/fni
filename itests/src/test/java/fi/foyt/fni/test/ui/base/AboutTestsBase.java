package fi.foyt.fni.test.ui.base;

import java.io.IOException;

import org.junit.Test;

public abstract class AboutTestsBase extends AbstractUITest {
  
  @Test
  public void testTitle() throws IOException {
    testTitle(getWebDriver(), "/about", "About Forge & Illusion");
  }
  
}
