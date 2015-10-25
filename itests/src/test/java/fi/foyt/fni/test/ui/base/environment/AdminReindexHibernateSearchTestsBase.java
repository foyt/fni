package fi.foyt.fni.test.ui.base.environment;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-users", before = "basic-users-setup.sql", after = "basic-users-teardown.sql")
})
public class AdminReindexHibernateSearchTestsBase extends AbstractUITest {
  
  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), "/admin/reindex-hibernate-search");
  }
  
  @Test
  @SqlSets ({"basic-users"})
  public void testReindex() throws Exception {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/admin/reindex-hibernate-search");
    assertEquals("Forge & Illusion", getWebDriver().getTitle());
  }
  
  @Test
  @SqlSets ({"basic-users"})
  public void testUnauthorized() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testAccessDenied("/admin/reindex-hibernate-search");
  }
  
}
