package fi.foyt.fni.test.ui.base.store;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.openqa.selenium.By;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-users", before = { "basic-users-setup.sql" }, after = { "basic-users-teardown.sql" }),
  @DefineSqlSet (id = "basic-forum", before = { "basic-forum-setup.sql" }, after = { "basic-forum-teardown.sql" }),
  @DefineSqlSet (id = "store-products", before = { "store-products-setup.sql" }, after = { "store-products-teardown.sql" })
})
public class StoreCartTestsBase extends AbstractUITest {

  @Test
  @SqlSets ({"basic-users", "basic-forum", "store-products"})
  public void testSingleItemPurchase() throws Exception {
    String firstName = "Test";
    String lastName = "Orderer";
    String email = "test.orderer@foyt.fi";
    String mobile = "+111 23 567 4444";
    String addressStreet = "Test Orderer Street 18 F22";
    String addressPostalCode = "12345";
    String addressPostalOffice = "Town of Test";
    String notes = "This is an automated test order";
    
    navigate("/store/test_product_1", true);
    
    // We have to wait for share button animation to end
    Thread.sleep(500);
    
    waitAndClick(".store-product-action-add-to-cart");
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
    assertSelectorText(".gamelibrary-order-item[data-order-item-index='0'] div:nth-child(2)", "EUR20.00", true, true);
    assertSelectorText(".gamelibrary-order-item[data-order-item-index='0'] div:nth-child(3)", "EUR20.00", true, true);
    
    assertSelectorText(".gamelibrary-order-total div", "EUR20.00", true, true);
    assertSelectorText(".gamelibrary-order-tax-label label", "TAX (0% - NOT VAT REGISTERED)", true, true);
    assertSelectorText(".gamelibrary-order-tax-amount div", "EUR0.00", true, true);
  }

  @Test
  @SqlSets ({"basic-users", "basic-forum", "store-products"})
  public void testMultiItemPurchase() throws Exception {
    String firstName = "Ärri";
    String lastName = "Pörri";
    String email = "arri.porri@foyt.fi";
    String mobile = "+222 33 444 5555";
    String addressStreet = "Ärri Pörri Katu 18 F22";
    String addressPostalCode = "12345";
    String addressPostalOffice = "Mäkkylä";
    String notes = "Tämä on automaattinen testitilaus";
    
    navigate("/store/", true);
    
    // We have to wait for share button animation to end
    Thread.sleep(500);
    
    waitAndClick(".store-product[data-index='0'] .store-product-action-add-to-cart");
    waitForSelectorCount(".mini-shopping-cart-item", 1);
    
    // We have to wait for share button animation to end
    Thread.sleep(500);

    waitAndClick(".store-product[data-index='1'] .store-product-action-add-to-cart");
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
    
    assertEquals("2 X FAT HAG DWARVES QUICKLY ZAP JINX MOB", StringUtils.upperCase(findElementsBySelector(".gamelibrary-order-item div:nth-child(1)").get(0).getText()));
    assertEquals("EUR20.00", StringUtils.upperCase(findElementsBySelector(".gamelibrary-order-item div:nth-child(2)").get(0).getText()));
    assertEquals("EUR40.00", StringUtils.upperCase(findElementsBySelector(".gamelibrary-order-item div:nth-child(3)").get(0).getText()));
    assertEquals("1 X ЭХ, ЧУЖАК, ОБЩИЙ СЪЁМ ЦЕН ШЛЯП (ЮФТЬ) – ВДРЫЗГ", StringUtils.upperCase(findElementsBySelector(".gamelibrary-order-item div:nth-child(1)").get(1).getText()));
    assertEquals("EUR20.00", StringUtils.upperCase(findElementsBySelector(".gamelibrary-order-item div:nth-child(2)").get(1).getText()));
    assertEquals("EUR20.00", StringUtils.upperCase(findElementsBySelector(".gamelibrary-order-item div:nth-child(3)").get(1).getText()));

    assertSelectorTextIgnoreCase(".gamelibrary-order-item[data-order-item-index='0'] div:nth-child(1)", "2 X FAT HAG DWARVES QUICKLY ZAP JINX MOB");
    assertSelectorTextIgnoreCase(".gamelibrary-order-item[data-order-item-index='0'] div:nth-child(2)", "EUR20.00");
    assertSelectorTextIgnoreCase(".gamelibrary-order-item[data-order-item-index='0'] div:nth-child(3)", "EUR40.00");
    assertSelectorTextIgnoreCase(".gamelibrary-order-item[data-order-item-index='1'] div:nth-child(1)", "1 X ЭХ, ЧУЖАК, ОБЩИЙ СЪЁМ ЦЕН ШЛЯП (ЮФТЬ) – ВДРЫЗГ");
    assertSelectorTextIgnoreCase(".gamelibrary-order-item[data-order-item-index='1'] div:nth-child(2)", "EUR20.00");
    assertSelectorTextIgnoreCase(".gamelibrary-order-item[data-order-item-index='1'] div:nth-child(3)", "EUR20.00");
    
    assertSelectorTextIgnoreCase(".gamelibrary-order-total div", "EUR60.00");
    assertSelectorTextIgnoreCase(".gamelibrary-order-tax-label label", "TAX (0% - NOT VAT REGISTERED)");
    assertSelectorTextIgnoreCase(".gamelibrary-order-tax-amount div", "EUR0.00");
  }

}
