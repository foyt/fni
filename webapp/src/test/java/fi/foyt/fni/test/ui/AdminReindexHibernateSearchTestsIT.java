package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class AdminReindexHibernateSearchTestsIT extends AbstractUITest {
  
  @Test
  public void testLoginRedirect() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testLoginRequired(driver, "/admin/reindex-hibernate-search");
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testReindex() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "admin@foyt.fi", "pass");
      driver.get(getAppUrl() + "/admin/reindex-hibernate-search");
      assertEquals("Forge & Illusion", driver.getTitle());
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testUnauthorized() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "user@foyt.fi", "pass");
      testAccessDenied(driver, "/admin/reindex-hibernate-search");
    } finally {
      driver.close();
    }
  }
  
}
