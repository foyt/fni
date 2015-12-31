package fi.foyt.fni.test.ui.base.environment;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-users", before = "basic-users-setup.sql", after = "basic-users-teardown.sql")
})
public class AdminArchiveUserTestsBase extends AbstractUITest {
  
  private static final Long USER_ID = 1024l;
  
  @Before
  public void addTestData() throws Exception {
    createUser(USER_ID, "Test", "Archiving", "test.archiving@foyt.fi", "pass", "en_US", "GRAVATAR", "USER");
  }
  
  @After
  public void cleanTestData() throws Exception {
    deleteUser(USER_ID);
  }
  
  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), "/admin/archive-user/" + USER_ID);
  }
  
  @Test
  @SqlSets ({"basic-users"})
  public void testNotFound() throws Exception {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    testNotFound("/admin/archive-user");
    testNotFound("/admin/archive-user/");
    testNotFound("/admin/archive-user/");
    testNotFound("/admin/archive-user/-1");
    testNotFound("/admin/archive-user/2048");
  }
  
  @Test
  @SqlSets ({"basic-users"})
  public void testArchive() throws Exception {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/admin/archive-user/" + USER_ID);
    assertEquals("Forge & Illusion", getWebDriver().getTitle());
  }
  
  @Test
  @SqlSets ({"basic-users"})
  public void testUnauthorized() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testAccessDenied("/admin/archive-user/" + USER_ID);
  }
  
}
