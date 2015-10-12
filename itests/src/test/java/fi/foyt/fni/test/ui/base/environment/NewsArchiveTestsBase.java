package fi.foyt.fni.test.ui.base.environment;

import java.io.IOException;

import org.junit.Test;

import fi.foyt.fni.test.ui.base.AbstractUITest;

public abstract class NewsArchiveTestsBase extends AbstractUITest {
  
  @Test
  public void testTitle() throws IOException {
    testTitle("/news/archive/2015/1", "News Archive");
  }
  
}
