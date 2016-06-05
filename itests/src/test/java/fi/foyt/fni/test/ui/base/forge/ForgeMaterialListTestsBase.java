package fi.foyt.fni.test.ui.base.forge;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-materials", before = { "basic-materials-setup.sql" }, after = { "basic-materials-teardown.sql" }),
  @DefineSqlSet (id = "basic-users", before = { "basic-users-setup.sql" }, after = { "basic-users-teardown.sql" }),
  @DefineSqlSet (id = "illusion-events", before = "illusion-event-oai-setup.sql", after = "illusion-event-oai-teardown.sql"),
  @DefineSqlSet (id = "illusion-participants", before = "illusion-event-oai-participants-setup.sql", after = "illusion-event-oai-participants-teardown.sql"),
  @DefineSqlSet (id = "illusion-groups", before = "illusion-event-oai-groups-setup.sql", after = "illusion-event-oai-groups-teardown.sql"),
  @DefineSqlSet (id = "illusion-group-members", before = "illusion-event-oai-group-members-setup.sql", after = "illusion-event-oai-group-members-teardown.sql")
})
public class ForgeMaterialListTestsBase extends AbstractUITest {

  @Test 
  @SqlSets ({"basic-users", "basic-materials"})
  public void testPrivateRootMaterialOwner() {
    loginInternal("user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/");
    assertMaterialExists(16l);
  }

  @Test 
  @SqlSets ({"basic-users", "basic-materials"})
  public void testPrivateRootMaterialMayEdit() {
    loginInternal("admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/");
    assertMaterialExists(3l);
  }

  @Test 
  @SqlSets ({"basic-users", "basic-materials"})
  public void testPrivateRootMaterialMayView() {
    loginInternal("librarian@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/");
    assertMaterialExists(3l);
  }
  
  @Test 
  @SqlSets ({"basic-users", "basic-materials"})
  public void testPrivateRootMaterialNoPermission() {
    loginInternal("noshares@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/");
    assertMaterialNotExists(16l);
    assertMaterialNotExists(3l);
  }
  
  @Test 
  @SqlSets ({"basic-users", "basic-materials"})
  public void testPrivateFolderMaterialOwner() {
    loginInternal("user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/prifol");
    assertMaterialExists(17l);
  }

  @Test 
  @SqlSets ({"basic-users", "basic-materials"})
  public void testPrivateFolderMaterialMayEdit() {
    loginInternal("admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/folder");
    assertMaterialExists(4l);
  }

  @Test 
  @SqlSets ({"basic-users", "basic-materials"})
  public void testPrivateFolderMaterialMayView() {
    loginInternal("librarian@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/folder");
    assertMaterialExists(4l);
  }
  
  @Test 
  @SqlSets ({"basic-users", "basic-materials"})
  public void testPrivateFolderMaterialNoPermission() {
    loginInternal("noshares@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/prifol");
    assertEquals("Access Denied!", getWebDriver().getTitle());
  }
  
  @Test 
  @SqlSets ({"basic-users", "basic-materials"})
  public void testPrivateSubfolderMaterialOwner() {
    loginInternal("user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/prifol/prisubfol");
    assertMaterialExists(18l);
  }

  @Test 
  @SqlSets ({"basic-users", "basic-materials"})
  public void testPrivateSubfolderMaterialMayEdit() {
    loginInternal("admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/folder/subfolder");
    assertMaterialExists(5l);
  }

  @Test 
  @SqlSets ({"basic-users", "basic-materials"})
  public void testPrivateSubfolderMaterialMayView() {
    loginInternal("librarian@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/folder/subfolder");
    assertMaterialExists(5l);
  }
  
  @Test 
  @SqlSets ({"basic-users", "basic-materials"})
  public void testPrivateSubfolderMaterialNoPermission() {
    loginInternal("noshares@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/prifol/prisubfol");
    assertEquals("Access Denied!", getWebDriver().getTitle());
  }
  
  @Test 
  @SqlSets ({"basic-users", "basic-materials"})
  public void testShareDialogAddUser() {
    reindexHibernateSearch();
    
    // Login as noshares and verify the material is not present
    loginInternal("noshares@foyt.fi", "pass");
    navigate("/forge");
    waitForSelectorVisible(".forge-new-material-menu");
    assertSelectorNotPresent("div[data-material-id='3']");
    logout();
    
    // Login as admin and share the material
    loginInternal("admin@foyt.fi", "pass");
    navigate("/forge");
    waitAndClick(".forge-material[data-material-id=\"3\"] .forge-material-icon");
    waitAndClick(".forge-material[data-material-id=\"3\"] .forge-material-action-share a");
    waitAndClick(".forge-share-material-dialog a[href='#share']");
    scrollWaitAndType(".forge-share-material-dialog .forge-share-material-invite input", "Shares");
    waitAndClick(".ui-autocomplete .ui-menu-item:first-child");
    waitForSelectorVisible(".forge-share-material-dialog .forge-share-material-collaborator[data-user-id='6']");
    scrollWaitAndClick(".ui-dialog .save-button");
    waitForSelectorNotPresent(".forge-share-material-dialog");
    logout();
    
    // Login as noshares and verify the material is now present
    loginInternal("noshares@foyt.fi", "pass");
    navigate("/forge");
    waitForSelectorVisible(".forge-new-material-menu");
    assertSelectorPresent("div[data-material-id='3']");
    logout();
    
    // Login as admin and unshare the material
    loginInternal("admin@foyt.fi", "pass");
    navigate("/forge");
    waitAndClick(".forge-material[data-material-id=\"3\"] .forge-material-icon");
    waitAndClick(".forge-material[data-material-id=\"3\"] .forge-material-action-share a");
    waitAndClick(".forge-share-material-dialog a[href='#share']");
    waitAndSelect(".forge-share-material-dialog .forge-share-material-collaborator[data-user-id='6'] select[name='role']", "NONE");
    scrollWaitAndClick(".ui-dialog .save-button");
    waitForSelectorNotPresent(".forge-share-material-dialog");
    logout();
    
    // Login as noshares and verify the material is no longer present
    loginInternal("noshares@foyt.fi", "pass");
    navigate("/forge");
    waitForSelectorVisible(".forge-new-material-menu");
    assertSelectorNotPresent("div[data-material-id='3']");
  }

  @Test 
  @SqlSets ({"basic-users", "basic-materials", "illusion-events", "illusion-participants", "illusion-groups", "illusion-group-members"})
  public void testShareDialogAddGroup() {
    reindexHibernateSearch();
    
    // Login as noshares and verify the material is not present
    loginInternal("noshares@foyt.fi", "pass");
    navigate("/forge");
    waitForSelectorVisible(".forge-new-material-menu");
    assertSelectorNotPresent("div[data-material-id='3']");
    logout();
    
    // Login as admin and share the material
    loginInternal("admin@foyt.fi", "pass");
    navigate("/forge");
    waitAndClick(".forge-material[data-material-id=\"3\"] .forge-material-icon");
    waitAndClick(".forge-material[data-material-id=\"3\"] .forge-material-action-share a");
    waitAndClick(".forge-share-material-dialog a[href='#share']");
    scrollWaitAndType(".forge-share-material-dialog .forge-share-material-invite input", "Approve");
    waitAndClick(".ui-autocomplete .ui-menu-item:first-child");
    waitForSelectorVisible(".forge-share-material-dialog .forge-share-material-collaborator[data-user-group-id='2']");
    scrollWaitAndClick(".ui-dialog .save-button");
    waitForSelectorNotPresent(".forge-share-material-dialog");
    logout();
    
    // Login as noshares and verify the material is now present
    loginInternal("noshares@foyt.fi", "pass");
    navigate("/forge");
    waitForSelectorVisible(".forge-new-material-menu");
    assertSelectorPresent("div[data-material-id='3']");
    logout();
    
    // Login as admin and unshare the material
    loginInternal("admin@foyt.fi", "pass");
    navigate("/forge");
    waitAndClick(".forge-material[data-material-id=\"3\"] .forge-material-icon");
    waitAndClick(".forge-material[data-material-id=\"3\"] .forge-material-action-share a");
    waitAndClick(".forge-share-material-dialog a[href='#share']");
    waitAndSelect(".forge-share-material-dialog .forge-share-material-collaborator[data-user-group-id='2'] select[name='role']", "NONE");
    scrollWaitAndClick(".ui-dialog .save-button");
    waitForSelectorNotPresent(".forge-share-material-dialog");
    logout();
    
    // Login as noshares and verify the material is no longer present
    loginInternal("noshares@foyt.fi", "pass");
    navigate("/forge");
    waitForSelectorVisible(".forge-new-material-menu");
    assertSelectorNotPresent("div[data-material-id='3']");
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
