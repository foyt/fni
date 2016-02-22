package fi.foyt.fni.test.ui.base.forge;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-materials-users", before = {"basic-users-setup.sql","basic-materials-setup.sql"}, after = { "basic-materials-teardown.sql","basic-users-teardown.sql" }),
})
public class ForgeDocumentTestsBase extends AbstractUITest {

  private static final String DOCUMENT_IN_ROOT = "/forge/documents/2/document";
  private static final String DOCUMENT_IN_FOLDER = "/forge/documents/2/folder/document_in_folder";
  private static final String DOCUMENT_IN_SUBFOLDER = "/forge/documents/2/folder/subfolder/document_in_subfolder";

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(DOCUMENT_IN_ROOT);
    testLoginRequired(DOCUMENT_IN_FOLDER);
    testLoginRequired(DOCUMENT_IN_SUBFOLDER);
  }
  
  @Test
  @SqlSets ({"basic-materials-users"})
  public void testForbidden() throws Exception {
    loginInternal("guest@foyt.fi", "pass");
    testAccessDenied(DOCUMENT_IN_ROOT);
    testAccessDenied(DOCUMENT_IN_FOLDER);
    testAccessDenied(DOCUMENT_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testNotFound() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testNotFound("/forge/documents/2/folder/subfolder");
    testNotFound("/forge/documents//document");
    testNotFound("/forge/documents/a/document");
    testNotFound("/forge/documents/2");
    testNotFound("/forge/documents/2/");
    testNotFound("/forge/documents/2/*");
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testMayView() {
    loginInternal("librarian@foyt.fi", "pass");
    testMayViewDocument(DOCUMENT_IN_ROOT);
    testMayViewDocument(DOCUMENT_IN_FOLDER);
    testMayViewDocument(DOCUMENT_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testMayEdit() {
    loginInternal("admin@foyt.fi", "pass");
    testMayEditDocument(DOCUMENT_IN_ROOT);
    testMayEditDocument(DOCUMENT_IN_FOLDER);
    testMayEditDocument(DOCUMENT_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-materials-users"})
  public void testWithHyphen() {
    loginInternal("user@foyt.fi", "pass");
    testMayEditDocument("/forge/documents/2/document-hyphen");
  }
  
  @Test
  @SqlSets ({"basic-materials-users"})
  public void textCreateSharedFolder() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    navigate("/forge/folders/2/folder");
    clickSelector(".forge-new-material-menu");
    clickSelector(".forge-new-document");
    assertDocumentEditable(true);
    executeSql("delete from CoOpsSession where material_id in (select id from Material where type='DOCUMENT' and parentFolder_id = 1)");
    executeSql("update MaterialRevision set checksum = 'DELETE' where id in (select id from DocumentRevision where document_id in (select id from Material where type='DOCUMENT' and parentFolder_id = 1))");
    executeSql("delete from DocumentRevision where document_id in (select id from Material where type='DOCUMENT' and parentFolder_id = 1)");
    executeSql("delete from MaterialRevision where checksum = 'DELETE'");
    executeSql("delete from Document where id in (select id from Material where type='DOCUMENT' and parentFolder_id = 1)");
    executeSql("delete from MaterialView where material_id in (select id from Material where type='DOCUMENT' and parentFolder_id = 1)");
    executeSql("delete from Material where type='DOCUMENT' and parentFolder_id = 1");
  }

  private void testMayViewDocument(String documentPath) {
    testDocumentEditable(documentPath, false);
  }

  private void testMayEditDocument(String documentPath) {
    testDocumentEditable(documentPath, true);
  }

  private void testDocumentEditable(String documentPath, boolean expect) {
    navigate(documentPath);
    assertDocumentEditable(expect);
  }

  private void assertDocumentEditable(boolean expect) {
    WebDriverWait wait = new WebDriverWait(getWebDriver(), 120);
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cke_wysiwyg_frame")));
    wait.until(new ExpectedCondition<Boolean>() {
      @Override
      public Boolean apply(WebDriver webDriver) {
        try {
          if (webDriver.findElement(By.cssSelector(".forge-ckdocument-editor-status-loaded")).isDisplayed())
            return true;
  
          if (webDriver.findElement(By.cssSelector(".forge-ckdocument-editor-status-saved")).isDisplayed())
            return true;
        } catch (Exception e) {
        }
        
        return false;
      }

    });
    
    switchFrame(".cke_wysiwyg_frame");
    assertEquals(expect ? "true" : "false", getWebDriver().findElement(By.cssSelector("body.cke_editable")).getAttribute("contenteditable"));
  }

}
