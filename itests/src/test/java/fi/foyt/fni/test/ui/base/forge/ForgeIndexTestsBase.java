package fi.foyt.fni.test.ui.base.forge;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-materials-users", before = {"basic-users-setup.sql","basic-materials-setup.sql"}, after = { "basic-materials-teardown.sql","basic-users-teardown.sql" }),
})
public class ForgeIndexTestsBase extends AbstractUITest {

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testTitle() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testTitle(getWebDriver(), "/forge/", "Forge");
  }

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), "/forge/");
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testRemoveDialogLongText() {
    loginInternal("user@foyt.fi", "pass");
    navigate("/forge/");
    waitAndClick(".forge-material[data-material-id=\"15\"] .forge-material-info");
    waitAndClick(".forge-material-action-delete a[data-material-id=\"15\"]");
    waitForSelectorPresent(".forge-remove-material-dialog");
    assertSelectorTextIgnoreCase(".ui-dialog-title", "Remove 'Beowulf pohti zuluja ja ång...'?");
    assertSelectorTextIgnoreCase(".forge-remove-material-dialog p", "Do you really wish to remove 'Beowulf pohti zuluja ja ångström-yksikköä katsellessaan Q-stone- ja CMX-yhtyeitä videolta.'");
  }
  

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testOpenShareDialog() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    navigate("/forge");
    waitForSelectorVisible(".forge-material[data-material-id=\"16\"] .forge-material-icon");
    clickSelector(".forge-material[data-material-id=\"16\"] .forge-material-icon");
    waitSelectorToBeClickable(".forge-material[data-material-id=\"16\"] .forge-material-action-share a");
    clickSelector(".forge-material[data-material-id=\"16\"] .forge-material-action-share a");
    waitForSelectorVisible(".forge-share-material-dialog");
    assertSelectorPresent(".forge-share-material-dialog");
  }
  
}
