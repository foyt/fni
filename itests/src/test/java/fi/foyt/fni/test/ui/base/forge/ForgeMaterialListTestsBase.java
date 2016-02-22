package fi.foyt.fni.test.ui.base.forge;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
    loginInternal("user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/");
    assertMaterialExists(16l);
  }

  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateRootMaterialMayEdit() {
    loginInternal("admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/");
    assertMaterialExists(3l);
  }

  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateRootMaterialMayView() {
    loginInternal("librarian@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/");
    assertMaterialExists(3l);
  }
  
  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateRootMaterialNoPermission() {
    loginInternal("noshares@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/");
    assertMaterialNotExists(16l);
    assertMaterialNotExists(3l);
  }
  
  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateFolderMaterialOwner() {
    loginInternal("user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/prifol");
    assertMaterialExists(17l);
  }

  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateFolderMaterialMayEdit() {
    loginInternal("admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/folder");
    assertMaterialExists(4l);
  }

  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateFolderMaterialMayView() {
    loginInternal("librarian@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/folder");
    assertMaterialExists(4l);
  }
  
  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateFolderMaterialNoPermission() {
    loginInternal("noshares@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/prifol");
    assertEquals("Access Denied!", getWebDriver().getTitle());
  }
  
  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateSubfolderMaterialOwner() {
    loginInternal("user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/prifol/prisubfol");
    assertMaterialExists(18l);
  }

  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateSubfolderMaterialMayEdit() {
    loginInternal("admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/folder/subfolder");
    assertMaterialExists(5l);
  }

  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateSubfolderMaterialMayView() {
    loginInternal("librarian@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/folder/subfolder");
    assertMaterialExists(5l);
  }
  
  @Test 
  @SqlSets ({"basic-materials-users"})
  public void testPrivateSubfolderMaterialNoPermission() {
    loginInternal("noshares@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/prifol/prisubfol");
    assertEquals("Access Denied!", getWebDriver().getTitle());
  }
  
  private void assertMaterialExists(Long id) {
    String selector = ".forge-material[data-material-id=\"" + id + "\"]";
    waitForSelectorVisible(selector);
    assertSelectorPresent(selector);
  }
  
  private void assertMaterialNotExists(Long id) {
    String selector = ".forge-material[data-material-id=\"" + id + "\"]";
    assertSelectorNotPresent(selector);
  }

}
