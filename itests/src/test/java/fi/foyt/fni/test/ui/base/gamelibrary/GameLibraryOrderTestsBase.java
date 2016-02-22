package fi.foyt.fni.test.ui.base.gamelibrary;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets({
  @DefineSqlSet (id = "basic-gamelibrary", before = { "basic-users-setup.sql","basic-forum-setup.sql","basic-gamelibrary-setup.sql"}, after={"basic-gamelibrary-teardown.sql", "basic-forum-teardown.sql","basic-users-teardown.sql"}),
})
public class GameLibraryOrderTestsBase extends AbstractUITest {

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testAccessKey() throws Exception {
    navigateAndWait("/gamelibrary/orders/1?key=bogus-access-key", true);
    testOrderDetails();
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testInvalidAccessKey() throws Exception {
    testAccessDenied("/gamelibrary/orders/1?key=invalid-access-key", true);
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testNotFound() throws Exception {
    testNotFound("/gamelibrary/orders/~", true);
    testNotFound("/gamelibrary/orders/-1", true);
    testNotFound("/gamelibrary/orders/", true);
    testNotFound("/gamelibrary/orders/asd", true);
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testAccessDenied() throws Exception {
    loginInternal("guest@foyt.fi", "pass");
    testAccessDenied("/gamelibrary/orders/1", true);
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testUser() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    navigateAndWait("/gamelibrary/orders/1", true);
    testOrderDetails();
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testLibrarian() throws Exception {
    loginInternal("librarian@foyt.fi", "pass");
    navigateAndWait("/gamelibrary/orders/1", true);
    testOrderDetails();
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testAdmin() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    navigateAndWait("/gamelibrary/orders/1", true);
    testOrderDetails();
  }

  private void testOrderDetails() {
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

    assertSelectorText(".gamelibrary-order-status", "Status: Paid, Waiting for Delivery", true, true);

    assertSelectorText(".gamelibrary-order-customer-company", company, true, true);
    assertSelectorText(".gamelibrary-order-customer-name", firstName + " " + lastName, true, true);
    assertSelectorText(".gamelibrary-order-customer-email", email, true, true);
    assertSelectorText(".gamelibrary-order-customer-mobile", mobile, true, true);
    assertSelectorText(".gamelibrary-order-customer-phone", phone, true, true);

    assertSelectorText(".gamelibrary-order-delivery-address-street", addressStreet, true, true);
    assertSelectorText(".gamelibrary-order-delivery-address-postal-code", addressPostalCode + " " + addressPostalOffice, true, true);
    assertSelectorText(".gamelibrary-order-delivery-address-country", "Antarctica", true, true);
        
    assertSelectorText(".gamelibrary-order-notes p", notes, true, true);

    assertSelectorCount(".gamelibrary-order-item", 1);
    
    assertSelectorText(".gamelibrary-order-item[data-order-item-index='0'] div:nth-child(1)", "10 X TEST BOOK #1", true, true);
    assertSelectorText(".gamelibrary-order-item[data-order-item-index='0'] div:nth-child(2)", "EUR10.00", true, true);
    assertSelectorText(".gamelibrary-order-item[data-order-item-index='0'] div:nth-child(3)", "EUR100.00", true, true);

    assertSelectorText(".gamelibrary-order-total div", "EUR100.00", true, true);
    assertSelectorText(".gamelibrary-order-tax-label label", "TAX (0% - NOT VAT REGISTERED)", true, true);
    assertSelectorText(".gamelibrary-order-tax-amount div", "EUR0.00", true, true);
  }

}
