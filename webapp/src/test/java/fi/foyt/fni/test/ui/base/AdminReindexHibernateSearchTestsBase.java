package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AdminReindexHibernateSearchTestsBase extends AbstractUITest {
  
  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), "/admin/reindex-hibernate-search");
  }
  
  @Test
  public void testReindex() throws Exception {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/admin/reindex-hibernate-search");
    assertEquals("Forge & Illusion", getWebDriver().getTitle());
  }
  
  @Test
  public void testUnauthorized() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testAccessDenied(getWebDriver(), "/admin/reindex-hibernate-search");
  }
  
}
