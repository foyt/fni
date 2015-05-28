package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-materials-users", before = {"basic-users-setup.sql","basic-materials-setup.sql"}, after = { "basic-materials-teardown.sql","basic-users-teardown.sql" }),
})
public class ForgeUploadTestsBase extends AbstractUITest {

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testTitle() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testTitle(getWebDriver(), "/forge/upload", "Forge - Import From My Computer");
  }

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), "/forge/upload");
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testNotFound() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testNotFound(getWebDriver(), "/forge/upload?parentFolderId=12345");
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testBreadcrumbs() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    
    getWebDriver().get(getAppUrl() + "/forge/upload");
    List<WebElement> breadcrumbs = getWebDriver().findElements(By.cssSelector(".view-header-navigation .view-header-navigation-item"));
    assertEquals(2, breadcrumbs.size());
    assertEquals(getAppUrl() + "/", breadcrumbs.get(0).getAttribute("href"));
    assertEquals(getAppUrl() + "/forge/", breadcrumbs.get(1).getAttribute("href"));
    
    getWebDriver().get(getAppUrl() + "/forge/upload?parentFolderId=1");
    breadcrumbs = getWebDriver().findElements(By.cssSelector(".view-header-navigation .view-header-navigation-item"));
    assertEquals(3, breadcrumbs.size());
    assertEquals(getAppUrl() + "/", breadcrumbs.get(0).getAttribute("href"));
    assertEquals(getAppUrl() + "/forge/", breadcrumbs.get(1).getAttribute("href"));
    assertEquals(getAppUrl() + "/forge/folders/2/folder", breadcrumbs.get(2).getAttribute("href"));
    
    getWebDriver().get(getAppUrl() + "/forge/upload?parentFolderId=2");
    breadcrumbs = getWebDriver().findElements(By.cssSelector(".view-header-navigation .view-header-navigation-item"));
    assertEquals(4, breadcrumbs.size());
    assertEquals(getAppUrl() + "/", breadcrumbs.get(0).getAttribute("href"));
    assertEquals(getAppUrl() + "/forge/", breadcrumbs.get(1).getAttribute("href"));
    assertEquals(getAppUrl() + "/forge/folders/2/folder", breadcrumbs.get(2).getAttribute("href"));
    assertEquals(getAppUrl() + "/forge/folders/2/folder/subfolder", breadcrumbs.get(3).getAttribute("href"));
  }
}
