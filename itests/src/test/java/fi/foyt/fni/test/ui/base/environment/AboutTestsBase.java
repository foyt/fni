package fi.foyt.fni.test.ui.base.environment;

import java.io.IOException;

import org.junit.Test;

import fi.foyt.fni.test.ui.base.AbstractUITest;

public abstract class AboutTestsBase extends AbstractUITest {
  
  @Test
  public void testTitle() throws IOException {
    testTitle("/about", "About Forge & Illusion");
  }
  
}
