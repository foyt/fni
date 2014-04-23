package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ForgeDocumentTestsIT extends AbstractUITest {
 
  private static final String DOCUMENT_IN_ROOT = "/forge/documents/2/document";
  private static final String DOCUMENT_IN_FOLDER = "/forge/documents/2/folder/document_in_folder";
  private static final String DOCUMENT_IN_SUBFOLDER = "/forge/documents/2/folder/subfolder/document_in_subfolder";
    
  @Test
  public void testLoginRedirect() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testLoginRequired(driver, DOCUMENT_IN_ROOT);
      testLoginRequired(driver, DOCUMENT_IN_FOLDER);
      testLoginRequired(driver, DOCUMENT_IN_SUBFOLDER);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testForbidden() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "guest@foyt.fi", "pass");
      testAccessDenied(driver, DOCUMENT_IN_ROOT);
      testAccessDenied(driver, DOCUMENT_IN_FOLDER);
      testAccessDenied(driver, DOCUMENT_IN_SUBFOLDER);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testNotFound() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "user@foyt.fi", "pass");
      testNotFound(driver, "/forge/documents/2/folder/subfolder");
      testNotFound(driver, "/forge/documents//document");
      testNotFound(driver, "/forge/documents/a/document");
      testNotFound(driver, "/forge/documents/2");
      testNotFound(driver, "/forge/documents/2/");
      testNotFound(driver, "/forge/documents/2/*");
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testMayView() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "librarian@foyt.fi", "pass");
      testMayViewDocument(driver, DOCUMENT_IN_ROOT);
      testMayViewDocument(driver, DOCUMENT_IN_FOLDER);
      testMayViewDocument(driver, DOCUMENT_IN_SUBFOLDER);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testMayEdit() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "admin@foyt.fi", "pass");
      testMayEditDocument(driver, DOCUMENT_IN_ROOT);
      testMayEditDocument(driver, DOCUMENT_IN_FOLDER);
      testMayEditDocument(driver, DOCUMENT_IN_SUBFOLDER);
    } finally {
      driver.close();
    }
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
