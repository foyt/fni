package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ForgeIndexTestsIT extends AbstractUITest {
  
  @Test
  public void testTitle() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "user@foyt.fi", "pass");
      testTitle(driver, "/forge/", "Forge");
    } finally {
      driver.close();
    }
  }
 
  @Test
  public void testLoginRedirect() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testLoginRequired(driver, "/forge/");
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testRemoveDialogLongText() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "user@foyt.fi", "pass");
      driver.get(getAppUrl() + "/forge/");
      
      new Actions(driver).moveToElement(driver.findElement(By.cssSelector(".forge-material[data-material-id=\"15\"] .forge-material-info"))).build().perform();
      WebElement deleteLink = driver.findElement(By.cssSelector(".forge-material-action-delete a[data-material-id=\"15\"]"));
      new WebDriverWait(driver, 60).until(ExpectedConditions.visibilityOf(deleteLink));
      deleteLink.click();
      
      new WebDriverWait(driver, 60).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".forge-remove-material-dialog")));

      assertEquals("Remove 'Beowulf pohti zuluja ja ång...'?", driver.findElement(By.cssSelector(".ui-dialog-title")).getText());
      assertEquals("Do you really wish to remove 'Beowulf pohti zuluja ja ångström-yksikköä katsellessaan Q-stone- ja CMX-yhtyeitä videolta.'", 
          driver.findElement(By.cssSelector(".forge-remove-material-dialog p")).getText());

      
    } finally {
      driver.close();
    }
    
     
  }
  
}
