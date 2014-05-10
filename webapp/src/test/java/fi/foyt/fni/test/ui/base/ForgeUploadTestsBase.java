package fi.foyt.fni.test.ui.base;

import org.junit.Test;

public class ForgeUploadTestsBase extends AbstractUITest {

  @Test
  public void testTitle() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testTitle(getWebDriver(), "/forge/upload", "Forge - Import From My Computer");
  }

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), "/forge/upload");
  }

  @Test
  public void testNotFound() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testNotFound(getWebDriver(), "/forge/upload?parentFolderId=12345");
  }
}
