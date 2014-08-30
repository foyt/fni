package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;


import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;

public class ForgeMaterialListTestsBase extends AbstractUITest {

  @Test 
  public void testPrivateRootMaterialOwner() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/");
    assertMaterialExists(getWebDriver(), 16l);
  }

  @Test 
  public void testPrivateRootMaterialMayEdit() {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/");
    assertMaterialExists(getWebDriver(), 3l);
  }

  @Test 
  public void testPrivateRootMaterialMayView() {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/");
    assertMaterialExists(getWebDriver(), 3l);
  }
  
  @Test 
  public void testPrivateRootMaterialNoPermission() {
    loginInternal(getWebDriver(), "noshares@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/");
    assertMaterialNotExists(getWebDriver(), 16l);
    assertMaterialNotExists(getWebDriver(), 3l);
  }
  
  @Test 
  public void testPrivateFolderMaterialOwner() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/prifol");
    assertMaterialExists(getWebDriver(), 17l);
  }

  @Test 
  public void testPrivateFolderMaterialMayEdit() {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/folder");
    assertMaterialExists(getWebDriver(), 4l);
  }

  @Test 
  public void testPrivateFolderMaterialMayView() {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/folder");
    assertMaterialExists(getWebDriver(), 4l);
  }
  
  @Test 
  public void testPrivateFolderMaterialNoPermission() {
    loginInternal(getWebDriver(), "noshares@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/prifol");
    assertEquals("Access Denied!", getWebDriver().getTitle());
  }
  
  @Test 
  public void testPrivateSubfolderMaterialOwner() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/prifol/prisubfol");
    assertMaterialExists(getWebDriver(), 18l);
  }

  @Test 
  public void testPrivateSubfolderMaterialMayEdit() {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/folder/subfolder");
    assertMaterialExists(getWebDriver(), 5l);
  }

  @Test 
  public void testPrivateSubfolderMaterialMayView() {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/folder/subfolder");
    assertMaterialExists(getWebDriver(), 5l);
  }
  
  @Test 
  public void testPrivateSubfolderMaterialNoPermission() {
    loginInternal(getWebDriver(), "noshares@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forge/folders/2/prifol/prisubfol");
    assertEquals("Access Denied!", getWebDriver().getTitle());
  }
  
  private void assertMaterialExists(RemoteWebDriver driver, Long id) {
    assertFalse(driver.findElements(By.cssSelector(".forge-material[data-material-id=\"" + id + "\"]")).isEmpty());
  }
  
  private void assertMaterialNotExists(RemoteWebDriver driver, Long id) {
    assertTrue(driver.findElements(By.cssSelector(".forge-material[data-material-id=\"" + id + "\"]")).isEmpty());
  }

}
