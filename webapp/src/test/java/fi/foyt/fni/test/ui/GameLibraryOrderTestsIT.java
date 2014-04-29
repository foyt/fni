package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class GameLibraryOrderTestsIT extends AbstractUITest {
  
  @Test
  public void testAccessKey() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      driver.get(getAppUrl(true) + "/gamelibrary/orders/1?key=bogus-access-key"); 
      testOrderDetails(driver);
    } finally {
      driver.close();
    }
  }

  @Test
  public void testInvalidAccessKey() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testAccessDenied(driver, "/gamelibrary/orders/1?key=invalid-access-key", true);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testNotFound() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testNotFound(driver, "/gamelibrary/orders/~", true);
      testNotFound(driver, "/gamelibrary/orders/-1", true);
      testNotFound(driver, "/gamelibrary/orders/", true);
      testNotFound(driver, "/gamelibrary/orders/asd", true);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testAccessDenied() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "guest@foyt.fi", "pass");
      testAccessDenied(driver, "/gamelibrary/orders/1", true);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testUser() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "user@foyt.fi", "pass");
      driver.get(getAppUrl(true) + "/gamelibrary/orders/1"); 
      testOrderDetails(driver);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testLibrarian() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "librarian@foyt.fi", "pass");
      driver.get(getAppUrl(true) + "/gamelibrary/orders/1"); 
      testOrderDetails(driver);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testAdmin() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "admin@foyt.fi", "pass");
      driver.get(getAppUrl(true) + "/gamelibrary/orders/1"); 
      testOrderDetails(driver);
    } finally {
      driver.close();
    }
  }

  private void testOrderDetails(ChromeDriver driver) {
    String firstName = "Bogus";
    String lastName = "Person";
    String company = "Bogus Company";
    String email = "bogus.order@foyt.fi";
    String mobile = "+123-456-789-0123";
    String phone = "+098-765-432-1098";
    String addressPostalOffice = "Bogus City";
    String addressStreet = "12 Bogus Street";
    String addressPostalCode = "12345";
    String notes = "This is a test order";
    
    assertEquals("Status: Paid, Waiting for Delivery", driver.findElement(By.cssSelector(".gamelibrary-order-status")).getText());
    assertEquals(company, driver.findElement(By.cssSelector(".gamelibrary-order-customer-company")).getText());
    assertEquals(firstName + " " + lastName, driver.findElement(By.cssSelector(".gamelibrary-order-customer-name")).getText());
    assertEquals(email, driver.findElement(By.cssSelector(".gamelibrary-order-customer-email")).getText());
    assertEquals(mobile, driver.findElement(By.cssSelector(".gamelibrary-order-customer-mobile")).getText());
    assertEquals(phone, driver.findElement(By.cssSelector(".gamelibrary-order-customer-phone")).getText());

    assertEquals(addressStreet, driver.findElement(By.cssSelector(".gamelibrary-order-delivery-address-street")).getText());
    assertEquals(addressPostalCode + " " + addressPostalOffice, driver.findElement(By.cssSelector(".gamelibrary-order-delivery-address-postal-code")).getText());
    assertEquals("Antarctica", driver.findElement(By.cssSelector(".gamelibrary-order-delivery-address-country")).getText());

    assertEquals(notes, driver.findElement(By.cssSelector(".gamelibrary-order-notes p")).getText());
    
    assertEquals("10 X TEST BOOK #1", driver.findElement(By.cssSelector(".gamelibrary-order-items tbody tr td:nth-child(1)")).getText());
    assertEquals("EUR10.00", driver.findElement(By.cssSelector(".gamelibrary-order-items tbody tr td:nth-child(2)")).getText());
    assertEquals("EUR100.00", driver.findElement(By.cssSelector(".gamelibrary-order-items tbody tr td:nth-child(3)")).getText());
    assertEquals("EUR100.00", driver.findElement(By.cssSelector(".gamelibrary-order-total")).getText());
    
    assertEquals("TAX (0% - NOT VAT REGISTERED)", driver.findElement(By.cssSelector(".gamelibrary-order-tax-container label")).getText());
    assertEquals("EUR0.00", driver.findElement(By.cssSelector(".gamelibrary-order-tax")).getText());
  }
  
}
