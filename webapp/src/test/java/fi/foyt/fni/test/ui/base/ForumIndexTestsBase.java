package fi.foyt.fni.test.ui.base;

import org.junit.Test;

public class ForumIndexTestsBase extends AbstractUITest {

  @Test
  public void testTitle() {
    testTitle(getWebDriver(), "/forum/", "Forum");
  }

}
