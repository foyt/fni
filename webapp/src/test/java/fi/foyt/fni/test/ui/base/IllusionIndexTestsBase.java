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
public class IllusionIndexTestsBase extends AbstractUITest {

  @Test
  public void testTitle() throws Exception {
    testTitle("/illusion", "Illusion");
  }

  @Test
  public void testNotCreateLinkNotLoggedIn() throws Exception {
    navigate("/illusion");
    assertSelectorNotPresent(".illusion-index-create-event-link");
  }
  
  @Test
  @SqlSets ("basic")
  public void testNotCreateLinkLoggedIn() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    navigate("/illusion");
    assertSelectorPresent(".illusion-index-create-event-link");
  }
  
}
