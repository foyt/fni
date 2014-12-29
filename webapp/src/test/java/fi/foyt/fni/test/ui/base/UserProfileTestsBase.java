package fi.foyt.fni.test.ui.base;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic", 
    before = {"basic-users-setup.sql"},
    after = {"basic-users-teardown.sql"}
  )
})
public class UserProfileTestsBase extends AbstractUITest {

  @Test
  @SqlSets ("basic")
  public void testTitle() {
    testTitle(getWebDriver(), "/profile/1", "User Profile");
  }

  @Test
  @SqlSets ("basic")
  public void testNotFound() throws Exception {
    testNotFound(getWebDriver(), "/profile/~");
    testNotFound(getWebDriver(), "/profile/12345");
    testNotFound(getWebDriver(), "/profile/-1");
    testNotFound(getWebDriver(), "/profile/");
    testNotFound(getWebDriver(), "/profile/asd");
  }

}
