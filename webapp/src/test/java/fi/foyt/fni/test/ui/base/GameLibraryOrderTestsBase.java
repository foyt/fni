package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets({
  @DefineSqlSet (id = "basic-gamelibrary", before = { "basic-users-setup.sql","basic-forum-setup.sql","basic-gamelibrary-setup.sql"}, after={"basic-gamelibrary-teardown.sql", "basic-forum-teardown.sql","basic-users-teardown.sql"}),
})
public class GameLibraryOrderTestsBase extends AbstractUITest {

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testAccessKey() throws Exception {
    getWebDriver().get(getAppUrl(true) + "/gamelibrary/orders/1?key=bogus-access-key");
    testOrderDetails(getWebDriver());
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testInvalidAccessKey() throws Exception {
    testAccessDenied(getWebDriver(), "/gamelibrary/orders/1?key=invalid-access-key", true);
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testNotFound() throws Exception {
    testNotFound(getWebDriver(), "/gamelibrary/orders/~", true);
    testNotFound(getWebDriver(), "/gamelibrary/orders/-1", true);
    testNotFound(getWebDriver(), "/gamelibrary/orders/", true);
    testNotFound(getWebDriver(), "/gamelibrary/orders/asd", true);
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testAccessDenied() throws Exception {
    loginInternal(getWebDriver(), "guest@foyt.fi", "pass");
    testAccessDenied(getWebDriver(), "/gamelibrary/orders/1", true);
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testUser() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl(true) + "/gamelibrary/orders/1");
    testOrderDetails(getWebDriver());
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testLibrarian() throws Exception {
    loginInternal(getWebDriver(), "librarian@foyt.fi", "pass");
    getWebDriver().get(getAppUrl(true) + "/gamelibrary/orders/1");
    testOrderDetails(getWebDriver());
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testAdmin() throws Exception {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl(true) + "/gamelibrary/orders/1");
    testOrderDetails(getWebDriver());
  }

  private void testOrderDetails(RemoteWebDriver driver) {
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

    assertEquals("Status: Paid, Waiting for Delivery", getWebDriver().findElement(By.cssSelector(".gamelibrary-order-status")).getText());
    assertEquals(company, getWebDriver().findElement(By.cssSelector(".gamelibrary-order-customer-company")).getText());
    assertEquals(firstName + " " + lastName, getWebDriver().findElement(By.cssSelector(".gamelibrary-order-customer-name")).getText());
    assertEquals(email, getWebDriver().findElement(By.cssSelector(".gamelibrary-order-customer-email")).getText());
    assertEquals(mobile, getWebDriver().findElement(By.cssSelector(".gamelibrary-order-customer-mobile")).getText());
    assertEquals(phone, getWebDriver().findElement(By.cssSelector(".gamelibrary-order-customer-phone")).getText());

    assertEquals(addressStreet, getWebDriver().findElement(By.cssSelector(".gamelibrary-order-delivery-address-street")).getText());
    assertEquals(addressPostalCode + " " + addressPostalOffice, getWebDriver().findElement(By.cssSelector(".gamelibrary-order-delivery-address-postal-code")).getText());
    assertEquals("Antarctica", getWebDriver().findElement(By.cssSelector(".gamelibrary-order-delivery-address-country")).getText());

    assertEquals(notes, getWebDriver().findElement(By.cssSelector(".gamelibrary-order-notes p")).getText());

    assertEquals("10 X TEST BOOK #1", getWebDriver().findElement(By.cssSelector(".gamelibrary-order-items tbody tr td:nth-child(1)")).getText());
    assertEquals("EUR10.00", getWebDriver().findElement(By.cssSelector(".gamelibrary-order-items tbody tr td:nth-child(2)")).getText());
    assertEquals("EUR100.00", getWebDriver().findElement(By.cssSelector(".gamelibrary-order-items tbody tr td:nth-child(3)")).getText());
    assertEquals("EUR100.00", getWebDriver().findElement(By.cssSelector(".gamelibrary-order-total")).getText());

    assertEquals("TAX (0% - NOT VAT REGISTERED)", getWebDriver().findElement(By.cssSelector(".gamelibrary-order-tax-container label")).getText());
    assertEquals("EUR0.00", getWebDriver().findElement(By.cssSelector(".gamelibrary-order-tax")).getText());
  }

}
