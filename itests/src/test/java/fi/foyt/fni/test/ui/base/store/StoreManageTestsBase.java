package fi.foyt.fni.test.ui.base.store;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-users", before = { "basic-users-setup.sql" }, after = { "basic-users-teardown.sql" })
})
public class StoreManageTestsBase extends AbstractUITest {

  @Test
  @SqlSets ("basic-users")
  public void testLoginRedirect() throws Exception {
    testLoginRequired("/store/manage/");
  }

  @Test
  @SqlSets ("basic-users")
  public void testUnauthorized() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/store/manage/");
  }

  @Test
  @SqlSets ("basic-users")
  public void testAdmin() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    navigate("/store/manage/");
    waitTitle("Store - Management");
    assertTitle("Store - Management");
  }

}
