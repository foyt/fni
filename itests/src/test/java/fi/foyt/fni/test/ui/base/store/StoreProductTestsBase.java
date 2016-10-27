package fi.foyt.fni.test.ui.base.store;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-users", before = { "basic-users-setup.sql" }, after = { "basic-users-teardown.sql" }),
  @DefineSqlSet (id = "basic-forum", before = { "basic-forum-setup.sql" }, after = { "basic-forum-teardown.sql" }),
  @DefineSqlSet (id = "store-products", before = { "store-products-setup.sql" }, after = { "store-products-teardown.sql" })
})
public class StoreProductTestsBase extends AbstractUITest {
  
  @Test
  @SqlSets ({"basic-users", "basic-forum", "store-products"})
  public void testMiniCartTexts() {
    navigate("/store/test_product_1");
    assertSelectorText(".mini-shopping-cart-title", "SHOPPING CART", true, true);
    assertEquals(getAppUrl() + "/gamelibrary/cart/", getWebDriver().findElement(By.cssSelector(".mini-shopping-cart-view")).getAttribute("href"));
    assertSelectorText(".mini-shopping-cart-empty","Shopping cart is empty", true, true);
    assertSelectorText(".mini-shopping-cart-summary label","Total", true, true);
    assertSelectorText(".mini-shopping-cart-summary span","EUR0.00", true, true);
  }
  
  @Test
  @SqlSets ({"basic-users", "basic-forum", "store-products"})
  public void testDetails() {
    navigate("/store/test_product_1");
    waitForSelectorPresent(".store-product");
    assertSelectorCount(".store-product", 1);
    testProductDetails(".store-product", "4", "/store/test_product_1", "Fat hag dwarves quickly zap jinx mob", new String[] {"test", "with whitespace"}, "Fat hag dwarves quickly zap jinx mob", "EUR20.00", "immutable/test_product_1", 0);
  }

  @Test
  public void testPublicationNotFound() {
    testNotFound("/store/bogus");
    testNotFound("/store/~");
    testNotFound("/store/12345");
    testNotFound("/store/-1");
  }

  private void testProductDetails(String productSelector, String productId, String path, String title, String[] tags, String description, 
      String price, String commentUrl, int comments) {

    assertSelectorText(String.format("%s h3 a", productSelector), title, true, true);
    assertEquals(getAppUrl() + path, getWebDriver().findElement(By.cssSelector(productSelector + " h3 a")).getAttribute("href"));

    for (int i = 0, l = tags.length; i < l; i++) {
      String tag = tags[i];
      assertSelectorText(String.format("%s .tags a:nth-child(%d)", productSelector, i + 1), tag, true, true);
    }
    
    assertSelectorText(String.format("%s .description", productSelector), description, true, true);

    if (price != null) {
      assertSelectorText(String.format("%s .store-product-detail-price span", productSelector), price, true, true);
    }

    assertSelectorPresent(String.format("%s .store-product-action-add-to-cart", productSelector));
    
    assertSelectorText(String.format("%s .store-product-comments", productSelector), String.format("COMMENTS (%d)", comments), true, true);
    assertEquals(getAppUrl() + "/forum/" + commentUrl, getWebDriver().findElement(By.cssSelector(productSelector + " .store-product-comments")).getAttribute("href"));
    
    assertShareButtonsHidden(productSelector);
    waitAndClick(productSelector + " .store-product-share-button label");
    assertShareButtonsVisible(productSelector);
    waitAndClick(productSelector + " .store-product-share-button label");
    assertShareButtonsHidden(productSelector);
    waitAndClick(productSelector + " .store-product-share-button label");
    assertShareButtonsVisible(productSelector);
    waitAndClick(productSelector + " .store-product-detail-price");
    assertShareButtonsHidden(productSelector);
  }

  private void assertShareButtonsHidden(String publicationSelector) {
    new WebDriverWait(getWebDriver(), 10).until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(publicationSelector + " .store-product-share-button .entypo-twitter")));
    assertSelectorNotVisible(String.format("%s .store-product-share-button .entypo-twitter", publicationSelector));
    assertSelectorNotVisible(String.format("%s .store-product-share-button .entypo-facebook", publicationSelector));
    assertSelectorNotVisible(String.format("%s .store-product-share-button .entypo-gplus", publicationSelector));
  }

  private void assertShareButtonsVisible(String publicationSelector) {
    waitForSelectorVisible(String.format("%s .store-product-share-button .entypo-twitter", publicationSelector));
    assertSelectorVisible(String.format("%s .store-product-share-button .entypo-twitter", publicationSelector));
    assertSelectorVisible(String.format("%s .store-product-share-button .entypo-facebook", publicationSelector));
    assertSelectorVisible(String.format("%s .store-product-share-button .entypo-gplus", publicationSelector));
  }
}
