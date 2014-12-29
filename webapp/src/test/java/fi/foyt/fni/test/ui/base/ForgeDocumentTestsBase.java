package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-materials-users", before = {"basic-users-setup.sql","basic-materials-setup.sql"}, after = { "basic-materials-teardown.sql","basic-users-teardown.sql" }),
})
public class ForgeDocumentTestsBase extends AbstractUITest {

  private static final String DOCUMENT_IN_ROOT = "/forge/documents/2/document";
  private static final String DOCUMENT_IN_FOLDER = "/forge/documents/2/folder/document_in_folder";
  private static final String DOCUMENT_IN_SUBFOLDER = "/forge/documents/2/folder/subfolder/document_in_subfolder";

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), DOCUMENT_IN_ROOT);
    testLoginRequired(getWebDriver(), DOCUMENT_IN_FOLDER);
    testLoginRequired(getWebDriver(), DOCUMENT_IN_SUBFOLDER);
  }
  
  @Test
  @SqlSets ({"basic-materials-users"})
  public void testForbidden() throws Exception {
    loginInternal(getWebDriver(), "guest@foyt.fi", "pass");
    testAccessDenied(getWebDriver(), DOCUMENT_IN_ROOT);
    testAccessDenied(getWebDriver(), DOCUMENT_IN_FOLDER);
    testAccessDenied(getWebDriver(), DOCUMENT_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testNotFound() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testNotFound(getWebDriver(), "/forge/documents/2/folder/subfolder");
    testNotFound(getWebDriver(), "/forge/documents//document");
    testNotFound(getWebDriver(), "/forge/documents/a/document");
    testNotFound(getWebDriver(), "/forge/documents/2");
    testNotFound(getWebDriver(), "/forge/documents/2/");
    testNotFound(getWebDriver(), "/forge/documents/2/*");
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testMayView() {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    testMayViewDocument(getWebDriver(), DOCUMENT_IN_ROOT);
    testMayViewDocument(getWebDriver(), DOCUMENT_IN_FOLDER);
    testMayViewDocument(getWebDriver(), DOCUMENT_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testMayEdit() {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    testMayEditDocument(getWebDriver(), DOCUMENT_IN_ROOT);
    testMayEditDocument(getWebDriver(), DOCUMENT_IN_FOLDER);
    testMayEditDocument(getWebDriver(), DOCUMENT_IN_SUBFOLDER);
  }

  private void testMayViewDocument(RemoteWebDriver driver, String documentPath) {
    testDocumentEditable(driver, documentPath, false);
  }

  private void testMayEditDocument(RemoteWebDriver driver, String documentPath) {
    testDocumentEditable(driver, documentPath, true);
  }

  protected void testDocumentEditable(RemoteWebDriver driver, String documentPath, boolean expect) {
    driver.get(getAppUrl() + documentPath);

    WebDriverWait wait = new WebDriverWait(driver, 120);

    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cke_wysiwyg_frame")));
    wait.until(new ExpectedCondition<Boolean>() {
      @Override
      public Boolean apply(WebDriver webDriver) {
        if (webDriver.findElement(By.cssSelector(".forge-ckdocument-editor-status-loaded")).isDisplayed())
          return true;

        if (webDriver.findElement(By.cssSelector(".forge-ckdocument-editor-status-saved")).isDisplayed())
          return true;

        return false;
      }

    });

    driver.switchTo().frame(driver.findElement(By.cssSelector(".cke_wysiwyg_frame")));

    assertEquals(expect ? "true" : "false", driver.findElement(By.cssSelector("body.cke_editable")).getAttribute("contenteditable"));
  }

}
