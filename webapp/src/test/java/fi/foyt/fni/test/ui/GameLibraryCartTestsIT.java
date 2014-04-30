package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GameLibraryCartTestsIT extends AbstractUITest {
  
  @Test
  public void testEmptyCart() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      driver.get(getAppUrl(true) + "/gamelibrary/cart/");
      assertEquals("Shopping Cart is Empty", driver.findElement(By.cssSelector(".gamelibrary-cart-empty-message")).getText());
      assertEquals("true", driver.findElement(By.cssSelector(".gamelibrary-cart-submit")).getAttribute("disabled"));
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testSingleItemPurchase() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      String firstName = "Test";
      String lastName = "Orderer";
      String email = "test.orderer@foyt.fi";
      String mobile = "+111 23 567 4444";
      String addressStreet = "Test Orderer Street 18 F22";
      String addressPostalCode = "12345";
      String addressPostalOffice = "Town of Test";
      String notes = "This is an automated test order";

      driver.get(getAppUrl(true) + "/gamelibrary/");
      driver.manage().addCookie(new Cookie("cookiesDirective", "1", getHost(), "/", null));
      driver.get(getAppUrl(true) + "/gamelibrary/testbook_1");

      driver.findElement(By.cssSelector(".gamelibrary-publication-action-add-to-cart")).click();
      driver.findElement(By.cssSelector("a.gamelibrary-mini-shopping-cart-view")).click();
      driver.findElement(By.id("cart-form:payerFirstName")).sendKeys(firstName);
      driver.findElement(By.id("cart-form:payerLastName")).sendKeys(lastName);
      driver.findElement(By.id("cart-form:payerEmail")).sendKeys(email);
      driver.findElement(By.id("cart-form:payerMobile")).sendKeys(mobile);
      driver.findElement(By.id("cart-form:payerStreetAddress")).sendKeys(addressStreet);
      driver.findElement(By.id("cart-form:payerPostalCode")).sendKeys(addressPostalCode);
      driver.findElement(By.id("cart-form:payerPostalOffice")).sendKeys(addressPostalOffice);
      driver.findElement(By.id("cart-form:notes")).sendKeys(notes);
      driver.findElement(By.cssSelector(".gamelibrary-cart-submit")).click();
      
      acceptPaytrailPayment(driver);
      
      new WebDriverWait(driver, 60).until(ExpectedConditions.titleIs("Forge & Illusion - Game Library"));
      
      assertEquals("Status: Paid, Waiting for Delivery", driver.findElement(By.cssSelector(".gamelibrary-order-status")).getText());
      assertEquals(firstName + " " + lastName, driver.findElement(By.cssSelector(".gamelibrary-order-customer-name")).getText());
      assertEquals(email, driver.findElement(By.cssSelector(".gamelibrary-order-customer-email")).getText());
      assertEquals(mobile, driver.findElement(By.cssSelector(".gamelibrary-order-customer-mobile")).getText());

      assertEquals(addressStreet, driver.findElement(By.cssSelector(".gamelibrary-order-delivery-address-street")).getText());
      assertEquals(addressPostalCode + " " + addressPostalOffice, driver.findElement(By.cssSelector(".gamelibrary-order-delivery-address-postal-code")).getText());
      assertEquals("Finland", driver.findElement(By.cssSelector(".gamelibrary-order-delivery-address-country")).getText());

      assertEquals(notes, driver.findElement(By.cssSelector(".gamelibrary-order-notes p")).getText());
      
      assertEquals("1 X FAT HAG DWARVES QUICKLY ZAP JINX MOB", driver.findElement(By.cssSelector(".gamelibrary-order-items tbody tr td:nth-child(1)")).getText());
      assertEquals("EUR10.00", driver.findElement(By.cssSelector(".gamelibrary-order-items tbody tr td:nth-child(2)")).getText());
      assertEquals("EUR10.00", driver.findElement(By.cssSelector(".gamelibrary-order-items tbody tr td:nth-child(3)")).getText());
      assertEquals("EUR10.00", driver.findElement(By.cssSelector(".gamelibrary-order-total")).getText());
      
      assertEquals("TAX (0% - NOT VAT REGISTERED)", driver.findElement(By.cssSelector(".gamelibrary-order-tax-container label")).getText());
      assertEquals("EUR0.00", driver.findElement(By.cssSelector(".gamelibrary-order-tax")).getText());
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testMultiItemPurchase() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      String firstName = "Ärri";
      String lastName = "Pörri";
      String email = "arri.porri@foyt.fi";
      String mobile = "+222 33 444 5555";
      String addressStreet = "Ärri Pörri Katu 18 F22";
      String addressPostalCode = "12345";
      String addressPostalOffice = "Mäkkylä";
      String notes = "Tämä on automaattinen testitilaus";
  
      driver.get(getAppUrl(true) + "/gamelibrary/");
      driver.manage().addCookie(new Cookie("cookiesDirective", "1", getHost(), "/", null));
      driver.get(getAppUrl(true) + "/gamelibrary/testbook_1");
      driver.findElement(By.cssSelector(".gamelibrary-publication-action-add-to-cart")).click();
      driver.get(getAppUrl(true) + "/gamelibrary/pangram_fi");
      driver.findElement(By.cssSelector(".gamelibrary-publication-action-add-to-cart")).click();
      driver.findElement(By.cssSelector("a.gamelibrary-mini-shopping-cart-view")).click();
      driver.findElement(By.cssSelector(".gamelibrary-cart-items-inner-container tr:first-child .gamelibrary-cart-action-inc-count")).click();
      
      driver.findElement(By.id("cart-form:payerFirstName")).sendKeys(firstName);
      driver.findElement(By.id("cart-form:payerLastName")).sendKeys(lastName);
      driver.findElement(By.id("cart-form:payerEmail")).sendKeys(email);
      driver.findElement(By.id("cart-form:payerMobile")).sendKeys(mobile);
      driver.findElement(By.id("cart-form:payerStreetAddress")).sendKeys(addressStreet);
      driver.findElement(By.id("cart-form:payerPostalCode")).sendKeys(addressPostalCode);
      driver.findElement(By.id("cart-form:payerPostalOffice")).sendKeys(addressPostalOffice);
      driver.findElement(By.id("cart-form:notes")).sendKeys(notes);
      driver.findElement(By.cssSelector(".gamelibrary-cart-submit")).click();
      
      acceptPaytrailPayment(driver);
      
      new WebDriverWait(driver, 60).until(ExpectedConditions.titleIs("Forge & Illusion - Game Library"));
      
      assertEquals("Status: Paid, Waiting for Delivery", driver.findElement(By.cssSelector(".gamelibrary-order-status")).getText());
      assertEquals(firstName + " " + lastName, driver.findElement(By.cssSelector(".gamelibrary-order-customer-name")).getText());
      assertEquals(email, driver.findElement(By.cssSelector(".gamelibrary-order-customer-email")).getText());
      assertEquals(mobile, driver.findElement(By.cssSelector(".gamelibrary-order-customer-mobile")).getText());
  
      assertEquals(addressStreet, driver.findElement(By.cssSelector(".gamelibrary-order-delivery-address-street")).getText());
      assertEquals(addressPostalCode + " " + addressPostalOffice, driver.findElement(By.cssSelector(".gamelibrary-order-delivery-address-postal-code")).getText());
      assertEquals("Finland", driver.findElement(By.cssSelector(".gamelibrary-order-delivery-address-country")).getText());
  
      assertEquals(notes, driver.findElement(By.cssSelector(".gamelibrary-order-notes p")).getText());
      
      assertEquals("2 X FAT HAG DWARVES QUICKLY ZAP JINX MOB", driver.findElement(By.cssSelector(".gamelibrary-order-items tbody tr:nth-child(1) td:nth-child(1)")).getText());
      assertEquals("EUR10.00", driver.findElement(By.cssSelector(".gamelibrary-order-items tbody tr:nth-child(1) td:nth-child(2)")).getText());
      assertEquals("EUR20.00", driver.findElement(By.cssSelector(".gamelibrary-order-items tbody tr:nth-child(1) td:nth-child(3)")).getText());

      assertEquals("1 X BEOWULF POHTI ZULUJA JA ÅNGSTRÖM-YKSIKKÖÄ KATSELLESSAAN Q-STONE- JA CMX-YHTYEITÄ VIDEOLTA.", driver.findElement(By.cssSelector(".gamelibrary-order-items tbody tr:nth-child(2) td:nth-child(1)")).getText());
      assertEquals("EUR7.50", driver.findElement(By.cssSelector(".gamelibrary-order-items tbody tr:nth-child(2) td:nth-child(2)")).getText());
      assertEquals("EUR7.50", driver.findElement(By.cssSelector(".gamelibrary-order-items tbody tr:nth-child(2) td:nth-child(3)")).getText());

      assertEquals("EUR27.50", driver.findElement(By.cssSelector(".gamelibrary-order-total")).getText());
      assertEquals("TAX (0% - NOT VAT REGISTERED)", driver.findElement(By.cssSelector(".gamelibrary-order-tax-container label")).getText());
      assertEquals("EUR0.00", driver.findElement(By.cssSelector(".gamelibrary-order-tax")).getText());
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testCartDelete() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      driver.get(getAppUrl(true) + "/gamelibrary/");
      driver.manage().addCookie(new Cookie("cookiesDirective", "1", getHost(), "/", null));
      driver.get(getAppUrl(true) + "/gamelibrary/testbook_1");
      driver.findElement(By.cssSelector(".gamelibrary-publication-action-add-to-cart")).click();
      driver.get(getAppUrl(true) + "/gamelibrary/pangram_fi");
      driver.findElement(By.cssSelector(".gamelibrary-publication-action-add-to-cart")).click();
      driver.findElement(By.cssSelector("a.gamelibrary-mini-shopping-cart-view")).click();
      assertEquals("EUR17.50", driver.findElement(By.cssSelector(".gamelibrary-cart-summary-total-field .gamelibrary-cart-summary-value")).getText());
      assertNotEquals("true", driver.findElement(By.cssSelector(".gamelibrary-cart-submit")).getAttribute("disabled"));
      driver.findElement(By.cssSelector(".gamelibrary-cart-items table tr:nth-child(1) .gamelibrary-cart-action-remove")).click(); 
      assertEquals("EUR7.50", driver.findElement(By.cssSelector(".gamelibrary-cart-summary-total-field .gamelibrary-cart-summary-value")).getText());
      assertNotEquals("true", driver.findElement(By.cssSelector(".gamelibrary-cart-submit")).getAttribute("disabled"));
      driver.findElement(By.cssSelector(".gamelibrary-cart-items table tr:nth-child(1) .gamelibrary-cart-action-remove")).click(); 
      assertEquals("EUR0.00", driver.findElement(By.cssSelector(".gamelibrary-cart-summary-total-field .gamelibrary-cart-summary-value")).getText());
      assertEquals("true", driver.findElement(By.cssSelector(".gamelibrary-cart-submit")).getAttribute("disabled"));
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testCartLoggedIn() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      Long userId = 2048l;
      String firstName = "Cart";
      String lastName = "Tester";
      String email = "cart.tester@foyt.fi";
      String password = "pass";
      String mobile = "+222 33 444 5555";
      String telephone = "+123 45 678 909";
      String addressStreet = "Ärri Pörri Katu 18 F22";
      String addressPostalCode = "12345";
      String addressPostalOffice = "Mäkkylä";
      
      createUser(userId, firstName, lastName, email, password, "en_US", "GRAVATAR", "USER");
      try {
        try {
          driver.get(getAppUrl(true) + "/gamelibrary/");
          driver.manage().addCookie(new Cookie("cookiesDirective", "1", getHost(), "/", null));
          loginInternal(driver, email, password);
  
          driver.get(getAppUrl(true) + "/gamelibrary/testbook_1");
          driver.findElement(By.cssSelector(".gamelibrary-publication-action-add-to-cart")).click();
          
          driver.get(getAppUrl(true) + "/gamelibrary/cart/");
          assertEquals(firstName, driver.findElement(By.id("cart-form:payerFirstName")).getAttribute("value"));
          assertEquals(lastName, driver.findElement(By.id("cart-form:payerLastName")).getAttribute("value"));
          assertEquals(email, driver.findElement(By.id("cart-form:payerEmail")).getAttribute("value"));
  
          assertEquals("", driver.findElement(By.id("cart-form:payerMobile")).getAttribute("value"));
          assertEquals("", driver.findElement(By.id("cart-form:payerTelephone")).getAttribute("value"));
          assertEquals("", driver.findElement(By.id("cart-form:payerStreetAddress")).getAttribute("value"));
          assertEquals("", driver.findElement(By.id("cart-form:payerPostalCode")).getAttribute("value"));
          assertEquals("", driver.findElement(By.id("cart-form:payerPostalOffice")).getAttribute("value"));
  
          driver.findElement(By.id("cart-form:payerMobile")).sendKeys(mobile);
          driver.findElement(By.id("cart-form:payerTelephone")).sendKeys(telephone);
          driver.findElement(By.id("cart-form:payerStreetAddress")).sendKeys(addressStreet);
          driver.findElement(By.id("cart-form:payerPostalCode")).sendKeys(addressPostalCode);
          driver.findElement(By.id("cart-form:payerPostalOffice")).sendKeys(addressPostalOffice);
  
          assertEquals(mobile, driver.findElement(By.id("cart-form:payerMobile")).getAttribute("value"));
          assertEquals(telephone, driver.findElement(By.id("cart-form:payerTelephone")).getAttribute("value"));
          assertEquals(addressStreet, driver.findElement(By.id("cart-form:payerStreetAddress")).getAttribute("value"));
          assertEquals(addressPostalCode, driver.findElement(By.id("cart-form:payerPostalCode")).getAttribute("value"));
          assertEquals(addressPostalOffice, driver.findElement(By.id("cart-form:payerPostalOffice")).getAttribute("value"));
          driver.findElement(By.cssSelector(".gamelibrary-cart-submit")).click();
          
          acceptPaytrailPayment(driver);
          logout(driver);
          loginInternal(driver, email, password);
          
          driver.get(getAppUrl(true) + "/gamelibrary/cart/");
          assertEquals(firstName, driver.findElement(By.id("cart-form:payerFirstName")).getAttribute("value"));
          assertEquals(lastName, driver.findElement(By.id("cart-form:payerLastName")).getAttribute("value"));
          assertEquals(email, driver.findElement(By.id("cart-form:payerEmail")).getAttribute("value"));
          assertEquals(mobile, driver.findElement(By.id("cart-form:payerMobile")).getAttribute("value"));
          assertEquals(telephone, driver.findElement(By.id("cart-form:payerTelephone")).getAttribute("value"));
          assertEquals(addressStreet, driver.findElement(By.id("cart-form:payerStreetAddress")).getAttribute("value"));
          assertEquals(addressPostalCode, driver.findElement(By.id("cart-form:payerPostalCode")).getAttribute("value"));
          assertEquals(addressPostalOffice, driver.findElement(By.id("cart-form:payerPostalOffice")).getAttribute("value"));
        } finally {
          executeSql("delete from OrderItem where order_id in (select id from Order_ where customer_id = ?)", userId);
          executeSql("delete from Order_ where customer_id = ?", userId);
          executeSql("delete from ShoppingCartItem where cart_id in (select id from ShoppingCart where customer_id = ?)", userId);
          executeSql("delete from ShoppingCart where customer_id = ?", userId);
        }
      } finally {
        deleteUser(userId);
      }
    } finally {
      driver.close();
    }  
  }

  private void acceptPaytrailPayment(ChromeDriver driver) {
    driver.findElement(By.cssSelector("form[action=\"https://kultaraha.op.fi/cgi-bin/krcgi\"] input[type=\"submit\"]")).click();
 
    new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        return "https://kultaraha.op.fi/cgi-bin/krcgi".equals(driver.getCurrentUrl());
      }
    });
  
    driver.findElement(By.name("id")).sendKeys("123456");
    driver.findElement(By.name("pw")).sendKeys("7890");
    driver.findElement(By.name("ktunn")).click();
    driver.findElement(By.name("avainluku")).sendKeys("1234");
    driver.findElement(By.name("avainl")).click();
    driver.findElement(By.id("Toiminto")).click();
  }
  
}
