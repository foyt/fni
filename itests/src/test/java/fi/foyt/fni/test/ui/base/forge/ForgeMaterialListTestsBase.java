package fi.foyt.fni.test.ui.base.forge;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.remote.RemoteWebDriver;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet (
    id = "basic-materials-users", 
    before = {"basic-users-setup.sql","basic-materials-setup.sql"}, 
    after = { "basic-materials-teardown.sql","basic-users-teardown.sql" }
  )
})
public class ForgeMaterialListTestsBase extends AbstractUITest {

  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateRootMaterialOwner() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/");
    assertMaterialExists(getWebDriver(), 16l);
  }

  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateRootMaterialMayEdit() {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/");
    assertMaterialExists(getWebDriver(), 3l);
  }

  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateRootMaterialMayView() {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/");
    assertMaterialExists(getWebDriver(), 3l);
  }
  
  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateRootMaterialNoPermission() {
    loginInternal(getWebDriver(), "noshares@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/");
    assertMaterialNotExists(getWebDriver(), 16l);
    assertMaterialNotExists(getWebDriver(), 3l);
  }
  
  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateFolderMaterialOwner() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/prifol");
    assertMaterialExists(getWebDriver(), 17l);
  }

  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateFolderMaterialMayEdit() {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/folder");
    assertMaterialExists(getWebDriver(), 4l);
  }

  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateFolderMaterialMayView() {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/folder");
    assertMaterialExists(getWebDriver(), 4l);
  }
  
  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateFolderMaterialNoPermission() {
    loginInternal(getWebDriver(), "noshares@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/prifol");
    assertEquals("Access Denied!", getWebDriver().getTitle());
  }
  
  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateSubfolderMaterialOwner() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/prifol/prisubfol");
    assertMaterialExists(getWebDriver(), 18l);
  }

  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateSubfolderMaterialMayEdit() {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/folder/subfolder");
    assertMaterialExists(getWebDriver(), 5l);
  }

  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateSubfolderMaterialMayView() {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/folder/subfolder");
    assertMaterialExists(getWebDriver(), 5l);
  }
  
  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateSubfolderMaterialNoPermission() {
    loginInternal(getWebDriver(), "noshares@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/prifol/prisubfol");
    assertEquals("Access Denied!", getWebDriver().getTitle());
  }
  
  private void assertMaterialExists(RemoteWebDriver driver, Long id) {
    String selector = ".forge-material[data-material-id=\"" + id + "\"]";
    waitForSelectorVisible(selector);
    assertSelectorPresent(selector);
  }
  
  private void assertMaterialNotExists(RemoteWebDriver driver, Long id) {
    String selector = ".forge-material[data-material-id=\"" + id + "\"]";
    assertSelectorNotPresent(selector);
  }

}
