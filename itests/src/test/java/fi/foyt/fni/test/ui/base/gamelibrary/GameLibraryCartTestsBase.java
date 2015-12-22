package fi.foyt.fni.test.ui.base.gamelibrary;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets({
  @DefineSqlSet (id = "basic-gamelibrary", before = { "basic-users-setup.sql","basic-forum-setup.sql","basic-gamelibrary-setup.sql"}, after={"basic-gamelibrary-teardown.sql", "basic-forum-teardown.sql","basic-users-teardown.sql"}),
})
public class GameLibraryCartTestsBase extends AbstractUITest {

  @Test
  public void testEmptyCart() throws Exception {
    navigate("/gamelibrary/cart/", true);
    waitTitle("Forge & Illusion - Game Library");
    assertSelectorText(".gamelibrary-cart-empty-message", "Shopping Cart is Empty", true, true);
    assertSelectorDisabled(".gamelibrary-cart-submit");
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testSingleItemPurchase() throws Exception {
    String firstName = "Test";
    String lastName = "Orderer";
    String email = "test.orderer@foyt.fi";
    String mobile = "+111 23 567 4444";
    String addressStreet = "Test Orderer Street 18 F22";
    String addressPostalCode = "12345";
    String addressPostalOffice = "Town of Test";
    String notes = "This is an automated test order";
    
    acceptCookieDirective();
    
    navigate("/gamelibrary/testbook_1", true);
   
    // We have to wait for share button animation to end
    Thread.sleep(500);
    
    waitAndClick(".gamelibrary-publication-action-add-to-cart");
    waitForSelectorCount(".mini-shopping-cart-item", 1);
    navigate("/gamelibrary/cart/", true);
    waitTitle("Forge & Illusion - Game Library");

    getWebDriver().findElement(By.id("cart-form:payerFirstName")).sendKeys(firstName);
    getWebDriver().findElement(By.id("cart-form:payerLastName")).sendKeys(lastName);
    getWebDriver().findElement(By.id("cart-form:payerEmail")).sendKeys(email);
    getWebDriver().findElement(By.id("cart-form:payerMobile")).sendKeys(mobile);
    getWebDriver().findElement(By.id("cart-form:payerStreetAddress")).sendKeys(addressStreet);
    getWebDriver().findElement(By.id("cart-form:payerPostalCode")).sendKeys(addressPostalCode);
    getWebDriver().findElement(By.id("cart-form:payerPostalOffice")).sendKeys(addressPostalOffice);
    getWebDriver().findElement(By.id("cart-form:notes")).sendKeys(notes);
    getWebDriver().findElement(By.cssSelector(".gamelibrary-cart-submit")).click();

    acceptPaytrailPayment();
    waitTitle("Forge & Illusion - Game Library");
    waitForSelectorPresent(".gamelibrary-order-status");
    
    assertSelectorText(".gamelibrary-order-status", "Status: Paid, Waiting for Delivery", true, true);
    assertSelectorText(".gamelibrary-order-customer-name", firstName + " " + lastName, true, true);
    assertSelectorText(".gamelibrary-order-customer-email", email, true, true);
    assertSelectorText(".gamelibrary-order-customer-mobile", mobile, true, true);
  
    assertSelectorText(".gamelibrary-order-delivery-address-street", addressStreet, true, true);
    assertSelectorText(".gamelibrary-order-delivery-address-postal-code", addressPostalCode + " " + addressPostalOffice, true, true);
    assertSelectorText(".gamelibrary-order-delivery-address-country", "Finland", true, true);
    
    assertSelectorText(".gamelibrary-order-notes p", notes, true, true);
    assertSelectorCount(".gamelibrary-order-item", 1);
    
    assertSelectorText(".gamelibrary-order-item[data-order-item-index='0'] div:nth-child(1)", "1 X FAT HAG DWARVES QUICKLY ZAP JINX MOB", true, true);
    assertSelectorText(".gamelibrary-order-item[data-order-item-index='0'] div:nth-child(2)", "EUR10.00", true, true);
    assertSelectorText(".gamelibrary-order-item[data-order-item-index='0'] div:nth-child(3)", "EUR10.00", true, true);
    
    assertSelectorText(".gamelibrary-order-total div", "EUR10.00", true, true);
    assertSelectorText(".gamelibrary-order-tax-label label", "TAX (0% - NOT VAT REGISTERED)", true, true);
    assertSelectorText(".gamelibrary-order-tax-amount div", "EUR0.00", true, true);
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testMultiItemPurchase() throws Exception {
    String firstName = "Ärri";
    String lastName = "Pörri";
    String email = "arri.porri@foyt.fi";
    String mobile = "+222 33 444 5555";
    String addressStreet = "Ärri Pörri Katu 18 F22";
    String addressPostalCode = "12345";
    String addressPostalOffice = "Mäkkylä";
    String notes = "Tämä on automaattinen testitilaus";

    if ("microsoftedge".equals(getBrowser())) {
      firstName = "Arri";
      lastName = "Porri";
      addressStreet = "Arri Porri Katu 18 F22";
      addressPostalOffice = "Makkylä";
      notes = "Tama on automaattinen testitilaus";
    }
    
    navigate("/gamelibrary/testbook_1", true);
   
    // We have to wait for share button animation to end
    Thread.sleep(500);
    
    waitAndClick(".gamelibrary-publication-action-add-to-cart");
    waitForSelectorCount(".mini-shopping-cart-item", 1);
    navigate("/gamelibrary/pangram_fi", true);
    
    // We have to wait for share button animation to end
    Thread.sleep(500);
    
    waitAndClick(".gamelibrary-publication-action-add-to-cart");
    waitForSelectorCount(".mini-shopping-cart-item", 2);
    navigate("/gamelibrary/cart/", true);
    waitTitle("Forge & Illusion - Game Library");
    
    waitAndClick(String.format(".gamelibrary-cart-item[data-item-index='%d'] .gamelibrary-cart-action-inc-count", 0));
    waitForSelectorText(String.format(".gamelibrary-cart-item[data-item-index='%d'] div:first-child", 0), "2", true, true);

    getWebDriver().findElement(By.id("cart-form:payerFirstName")).sendKeys(firstName);
    getWebDriver().findElement(By.id("cart-form:payerLastName")).sendKeys(lastName);
    getWebDriver().findElement(By.id("cart-form:payerEmail")).sendKeys(email);
    getWebDriver().findElement(By.id("cart-form:payerMobile")).sendKeys(mobile);
    getWebDriver().findElement(By.id("cart-form:payerStreetAddress")).sendKeys(addressStreet);
    getWebDriver().findElement(By.id("cart-form:payerPostalCode")).sendKeys(addressPostalCode);
    getWebDriver().findElement(By.id("cart-form:payerPostalOffice")).sendKeys(addressPostalOffice);
    getWebDriver().findElement(By.id("cart-form:notes")).sendKeys(notes);
    getWebDriver().findElement(By.cssSelector(".gamelibrary-cart-submit")).click();

    acceptPaytrailPayment();
    waitTitle("Forge & Illusion - Game Library");
    waitForSelectorPresent(".gamelibrary-order-status");

    assertSelectorTextIgnoreCase(".gamelibrary-order-status", "Status: Paid, Waiting for Delivery");
    assertSelectorTextIgnoreCase(".gamelibrary-order-customer-name", firstName + " " + lastName);
    assertSelectorTextIgnoreCase(".gamelibrary-order-customer-email", email);
    assertSelectorTextIgnoreCase(".gamelibrary-order-customer-mobile", mobile);

    assertSelectorTextIgnoreCase(".gamelibrary-order-delivery-address-street", addressStreet);
    assertSelectorTextIgnoreCase(".gamelibrary-order-delivery-address-postal-code", addressPostalCode + " " + addressPostalOffice);
    assertSelectorTextIgnoreCase(".gamelibrary-order-delivery-address-country", "Finland");
    
    assertSelectorTextIgnoreCase(".gamelibrary-order-notes p", notes);
    assertSelectorCount(".gamelibrary-order-item", 2);
    
    assertEquals("2 X FAT HAG DWARVES QUICKLY ZAP JINX MOB", findElementsBySelector(".gamelibrary-order-item div:nth-child(1)").get(0).getText());
    assertEquals("EUR10.00", findElementsBySelector(".gamelibrary-order-item div:nth-child(2)").get(0).getText());
    assertEquals("EUR20.00", findElementsBySelector(".gamelibrary-order-item div:nth-child(3)").get(0).getText());
    assertEquals("1 X BEOWULF POHTI ZULUJA JA ÅNGSTRÖM-YKSIKKÖÄ KATSELLESSAAN Q-STONE- JA CMX-YHTYEITÄ VIDEOLTA.", findElementsBySelector(".gamelibrary-order-item div:nth-child(1)").get(1).getText());
    assertEquals("EUR7.50", findElementsBySelector(".gamelibrary-order-item div:nth-child(2)").get(1).getText());
    assertEquals("EUR7.50", findElementsBySelector(".gamelibrary-order-item div:nth-child(3)").get(1).getText());

    assertSelectorTextIgnoreCase(".gamelibrary-order-item[data-order-item-index='0'] div:nth-child(1)", "2 X FAT HAG DWARVES QUICKLY ZAP JINX MOB");
    assertSelectorTextIgnoreCase(".gamelibrary-order-item[data-order-item-index='0'] div:nth-child(2)", "EUR10.00");
    assertSelectorTextIgnoreCase(".gamelibrary-order-item[data-order-item-index='0'] div:nth-child(3)", "EUR20.00");
    assertSelectorTextIgnoreCase(".gamelibrary-order-item[data-order-item-index='1'] div:nth-child(1)", "1 X BEOWULF POHTI ZULUJA JA ÅNGSTRÖM-YKSIKKÖÄ KATSELLESSAAN Q-STONE- JA CMX-YHTYEITÄ VIDEOLTA.");
    assertSelectorTextIgnoreCase(".gamelibrary-order-item[data-order-item-index='1'] div:nth-child(2)", "EUR7.50");
    assertSelectorTextIgnoreCase(".gamelibrary-order-item[data-order-item-index='1'] div:nth-child(3)", "EUR7.50");
    
    assertSelectorTextIgnoreCase(".gamelibrary-order-total div", "EUR27.50");
    assertSelectorTextIgnoreCase(".gamelibrary-order-tax-label label", "TAX (0% - NOT VAT REGISTERED)");
    assertSelectorTextIgnoreCase(".gamelibrary-order-tax-amount div", "EUR0.00");
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testCartDelete() throws Exception {
    acceptCookieDirective();
    
    navigate("/gamelibrary/testbook_1", true);
    
    // We have to wait for share button animation to end
    Thread.sleep(500);
    
    waitAndClick(".gamelibrary-publication-action-add-to-cart");
    waitForSelectorCount(".mini-shopping-cart-item", 1);
    navigate("/gamelibrary/pangram_fi", true);

    // We have to wait for share button animation to end
    Thread.sleep(500);

    waitAndClick(".gamelibrary-publication-action-add-to-cart");
    waitForSelectorCount(".mini-shopping-cart-item", 2);
    navigate("/gamelibrary/cart/", true);
    waitTitle("Forge & Illusion - Game Library");

    assertSelectorText(".gamelibrary-cart-summary-field-total-value", "EUR17.50", true, true);
    assertSelectorClickable(".gamelibrary-cart-submit");
    
    findElementsBySelector(".gamelibrary-cart-item .gamelibrary-cart-action-remove").get(0).click();
    waitForPageLoad();
    assertSelectorText(".gamelibrary-cart-summary-field-total-value", "EUR7.50", true, true);
    assertSelectorClickable(".gamelibrary-cart-submit");

    findElementsBySelector(".gamelibrary-cart-item .gamelibrary-cart-action-remove").get(0).click();
    waitForPageLoad();

    assertSelectorText(".gamelibrary-cart-summary-field-total-value", "EUR0.00", true, true);
    assertSelectorNotClickable(".gamelibrary-cart-submit");
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testCartLoggedIn() throws Exception {
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

    if ("microsoftedge".equals(getBrowser())) {
      addressStreet = "Arri Porri Katu 18 F22";
      addressPostalOffice = "Makkylä";
    }
    
    createUser(userId, firstName, lastName, email, password, "en_US", "GRAVATAR", "USER");
    try {
      try {
        acceptCookieDirective();
        loginInternal(email, password);
        
        navigate("/gamelibrary/testbook_1", true);
        
        // We have to wait for share button animation to end
        Thread.sleep(500);

        waitAndClick(".gamelibrary-publication-action-add-to-cart");
        waitForSelectorCount(".mini-shopping-cart-item", 1);
        navigate("/gamelibrary/cart/", true);
        waitTitle("Forge & Illusion - Game Library");
        
        assertEquals(firstName, getWebDriver().findElement(By.id("cart-form:payerFirstName")).getAttribute("value"));
        assertEquals(lastName, getWebDriver().findElement(By.id("cart-form:payerLastName")).getAttribute("value"));
        assertEquals(email, getWebDriver().findElement(By.id("cart-form:payerEmail")).getAttribute("value"));

        assertEquals("", getWebDriver().findElement(By.id("cart-form:payerMobile")).getAttribute("value"));
        assertEquals("", getWebDriver().findElement(By.id("cart-form:payerTelephone")).getAttribute("value"));
        assertEquals("", getWebDriver().findElement(By.id("cart-form:payerStreetAddress")).getAttribute("value"));
        assertEquals("", getWebDriver().findElement(By.id("cart-form:payerPostalCode")).getAttribute("value"));
        assertEquals("", getWebDriver().findElement(By.id("cart-form:payerPostalOffice")).getAttribute("value"));

        getWebDriver().findElement(By.id("cart-form:payerMobile")).sendKeys(mobile);
        getWebDriver().findElement(By.id("cart-form:payerTelephone")).sendKeys(telephone);
        getWebDriver().findElement(By.id("cart-form:payerStreetAddress")).sendKeys(addressStreet);
        getWebDriver().findElement(By.id("cart-form:payerPostalCode")).sendKeys(addressPostalCode);
        getWebDriver().findElement(By.id("cart-form:payerPostalOffice")).sendKeys(addressPostalOffice);

        assertEquals(mobile, getWebDriver().findElement(By.id("cart-form:payerMobile")).getAttribute("value"));
        assertEquals(telephone, getWebDriver().findElement(By.id("cart-form:payerTelephone")).getAttribute("value"));
        assertEquals(addressStreet, getWebDriver().findElement(By.id("cart-form:payerStreetAddress")).getAttribute("value"));
        assertEquals(addressPostalCode, getWebDriver().findElement(By.id("cart-form:payerPostalCode")).getAttribute("value"));
        assertEquals(addressPostalOffice, getWebDriver().findElement(By.id("cart-form:payerPostalOffice")).getAttribute("value"));
        getWebDriver().findElement(By.cssSelector(".gamelibrary-cart-submit")).click();

        acceptPaytrailPayment();
        logout();
        loginInternal(getWebDriver(), email, password);

        navigate("/gamelibrary/cart/", true);
        waitTitle("Forge & Illusion - Game Library");
        
        assertEquals(firstName, getWebDriver().findElement(By.id("cart-form:payerFirstName")).getAttribute("value"));
        assertEquals(lastName, getWebDriver().findElement(By.id("cart-form:payerLastName")).getAttribute("value"));
        assertEquals(email, getWebDriver().findElement(By.id("cart-form:payerEmail")).getAttribute("value"));
        assertEquals(mobile, getWebDriver().findElement(By.id("cart-form:payerMobile")).getAttribute("value"));
        assertEquals(telephone, getWebDriver().findElement(By.id("cart-form:payerTelephone")).getAttribute("value"));
        assertEquals(addressStreet, getWebDriver().findElement(By.id("cart-form:payerStreetAddress")).getAttribute("value"));
        assertEquals(addressPostalCode, getWebDriver().findElement(By.id("cart-form:payerPostalCode")).getAttribute("value"));
        assertEquals(addressPostalOffice, getWebDriver().findElement(By.id("cart-form:payerPostalOffice")).getAttribute("value"));
      } finally {
        executeSql("delete from OrderItem where order_id in (select id from Order_ where customer_id = ?)", userId);
        executeSql("delete from Order_ where customer_id = ?", userId);
        executeSql("delete from ShoppingCartItem where cart_id in (select id from ShoppingCart where customer_id = ?)", userId);
        executeSql("delete from ShoppingCart where customer_id = ?", userId);
      }
    } finally {
      deleteUser(userId);
    }
  }

}
