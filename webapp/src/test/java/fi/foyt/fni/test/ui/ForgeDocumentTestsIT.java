package fi.foyt.fni.test.ui;

import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class ForgeDocumentTestsIT extends AbstractUITest {
 
  private static final String DOCUMENT_IN_ROOT = "/forge/documents/2/document";
  private static final String DOCUMENT_IN_FOLDER = "/forge/documents/2/folder/document_in_folder";
  private static final String DOCUMENT_IN_SUBFOLDER = "/forge/documents/2/subfolder/document_in_folder";
    
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
  
}
