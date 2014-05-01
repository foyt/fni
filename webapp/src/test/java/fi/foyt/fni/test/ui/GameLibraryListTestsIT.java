package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import fi.foyt.fni.utils.licenses.CreativeCommonsLicense;
import fi.foyt.fni.utils.licenses.CreativeCommonsUtils;

public class GameLibraryListTestsIT extends AbstractUITest {
  
  private static final String UML_ID = "3";
  private static final boolean UML_PURCHASABLE = true;
  private static final String UML_LICENSE = "http://creativecommons.org/licenses/by-nc/3.0";
  private static final String UML_PAGES = "15";
  private static final String UML_PRICE = "EUR7.50";
  private static final String UML_DESC = "Beowulf pohti zuluja ja ångström-yksikköä katsellessaan Q-stone- ja CMX-yhtyeitä videolta.";
  private static final String[] UML_TAGS = new String[] { "törkylempijävongahdus" };
  private static final String UML_TITLE = UML_DESC;
  private static final String UML_PATH = "/gamelibrary/pangram_fi";
  private static final String[] UML_AUTHOR_NAMES = new String[] {"Test Librarian"};
  private static final Long[] UML_AUTHOR_IDS = new Long[] { 3l };
  private static final String UML_COMMENT_URL = "immutable/pangram_fi";
  private static final int UML_COMMENTS = 0;
  
  private static final String BYSARUS_ID = "2";
  private static final String BYSARUS_PATH = "/gamelibrary/testbook_2";
  private static final boolean BYSARUS_PURCHASABLE = false;
  private static final String BYSARUS_LICENSE = "http://www.example.com/custom/license";
  private static final String BYSARUS_PAGES = "200";
  private static final String BYSARUS_PRICE = null;
  private static final String BYSARUS_DESC = "Эх, чужак, общий съём цен шляп (юфть) – вдрызг";
  private static final String[] BYSARUS_TAGS = new String[] { "test", "with whitespace" };
  private static final String BYSARUS_TITLE = "Эх, чужак, общий съём цен шляп (юфть) – вдрызг";
  private static final String[] BYSARUS_AUTHOR_NAMES = new String[] {"Test Guest", "Test User"};
  private static final Long[] BYSARUS_AUTHOR_IDS = new Long[] { 1l, 2l };
  private static final String BYSARUS_COMMENT_URL = "immutable/testbook_2";
  private static final int BYSARUS_COMMENTS = 1;
  
  private static final String SIMPLE_ID = "1";
  private static final String SIMPLE_PATH = "/gamelibrary/testbook_1";
  private static final boolean SIMPLE_PURCHASABLE = true;
  private static final String SIMPLE_LICENSE = "http://creativecommons.org/licenses/by-sa/3.0";
  private static final String SIMPLE_PAGES = "100";
  private static final String SIMPLE_PRICE = "EUR10.00";
  private static final String SIMPLE_DESC = "Fat hag dwarves quickly zap jinx mob";
  private static final String[] SIMPLE_TAGS = new String[] { "test" };
  private static final String SIMPLE_TITLE = SIMPLE_DESC;
  private static final String[] SIMPLE_AUTHOR_NAMES = null;
  private static final Long[] SIMPLE_AUTHOR_IDS = null;
  private static final String SIMPLE_COMMENT_URL = "immutable/testbook_1";
  private static final int SIMPLE_COMMENTS = 2;
  
  @Test
  public void testHttps() {
    ChromeDriver driver = new ChromeDriver();
    try {
      driver.get(getAppUrl() + "/gamelibrary/"); 
      assertTrue(StringUtils.startsWith(driver.getCurrentUrl(), "https://"));
    } finally {
      driver.close();
    } 
  }
  
  @Test
  public void testTagNotFound() {
    ChromeDriver driver = new ChromeDriver();
    try {
      testNotFound(driver, "/gamelibrary/tags/bogus", true);
      testNotFound(driver, "/gamelibrary/tags/~", true);
      testNotFound(driver, "/gamelibrary/tags/12345", true);
      testNotFound(driver, "/gamelibrary/tags/-1", true);
      testNotFound(driver, "/gamelibrary/tags/", true);
    } finally {
      driver.close();
    } 
  }
  
  @Test
  public void testPublicationNotFound() {
    ChromeDriver driver = new ChromeDriver();
    try {
      testNotFound(driver, "/gamelibrary/bogus", true);
      testNotFound(driver, "/gamelibrary/~", true);
      testNotFound(driver, "/gamelibrary/12345", true);
      testNotFound(driver, "/gamelibrary/-1", true);
    } finally {
      driver.close();
    } 
  }

  @Test
  public void testMiniCartTexts() {
    ChromeDriver driver = new ChromeDriver();
    try {
      driver.get(getAppUrl(true) + "/gamelibrary/"); 
      assertEquals("SHOPPING CART", driver.findElement(By.cssSelector(".gamelibrary-mini-shopping-cart-title")).getText());
      assertEquals(getAppUrl(true) + "/gamelibrary/cart/", driver.findElement(By.cssSelector(".gamelibrary-mini-shopping-cart-view")).getAttribute("href"));
      assertEquals("Shopping cart is empty", driver.findElement(By.cssSelector(".gamelibrary-mini-shopping-cart-empty")).getText());
      assertEquals("Total", driver.findElement(By.cssSelector(".gamelibrary-mini-shopping-cart-summary label")).getText());
      assertEquals("EUR0.00", driver.findElement(By.cssSelector(".gamelibrary-mini-shopping-cart-summary span")).getText());
    } finally {
      driver.close();
    } 
  }
  
  @Test
  public void testMostRecentList() {
    ChromeDriver driver = new ChromeDriver();
    try {
      driver.get(getAppUrl(true) + "/gamelibrary/"); 

      testPublicationDetails(driver, ".gamelibrary-publications form:nth-child(1) .gamelibrary-publication", UML_ID, UML_PATH, UML_TITLE, UML_TAGS, 
          UML_DESC, UML_PRICE, UML_PAGES, UML_AUTHOR_NAMES, UML_AUTHOR_IDS, UML_LICENSE, UML_PURCHASABLE, UML_COMMENT_URL, UML_COMMENTS);
      
      testPublicationDetails(driver, ".gamelibrary-publications form:nth-child(2) .gamelibrary-publication", BYSARUS_ID, BYSARUS_PATH, BYSARUS_TITLE, BYSARUS_TAGS, 
          BYSARUS_DESC, BYSARUS_PRICE, BYSARUS_PAGES, BYSARUS_AUTHOR_NAMES, BYSARUS_AUTHOR_IDS, BYSARUS_LICENSE, BYSARUS_PURCHASABLE, BYSARUS_COMMENT_URL, BYSARUS_COMMENTS);
      
      testPublicationDetails(driver, ".gamelibrary-publications form:nth-child(3) .gamelibrary-publication", SIMPLE_ID, SIMPLE_PATH, SIMPLE_TITLE, SIMPLE_TAGS, 
          SIMPLE_DESC, SIMPLE_PRICE, SIMPLE_PAGES, SIMPLE_AUTHOR_NAMES, SIMPLE_AUTHOR_IDS, SIMPLE_LICENSE, SIMPLE_PURCHASABLE, SIMPLE_COMMENT_URL, SIMPLE_COMMENTS);
    } finally {
      driver.close();
    } 
  }
  
  @Test
  public void testTagList() {
    ChromeDriver driver = new ChromeDriver();
    try {
      driver.get(getAppUrl(true) + "/gamelibrary/tags/test");
      
      testPublicationDetails(driver, ".gamelibrary-publications form:nth-child(1) .gamelibrary-publication", SIMPLE_ID, SIMPLE_PATH, SIMPLE_TITLE, SIMPLE_TAGS, 
          SIMPLE_DESC, SIMPLE_PRICE, SIMPLE_PAGES, SIMPLE_AUTHOR_NAMES, SIMPLE_AUTHOR_IDS, SIMPLE_LICENSE, SIMPLE_PURCHASABLE, SIMPLE_COMMENT_URL, SIMPLE_COMMENTS);

      testPublicationDetails(driver, ".gamelibrary-publications form:nth-child(2) .gamelibrary-publication", BYSARUS_ID, BYSARUS_PATH, BYSARUS_TITLE, BYSARUS_TAGS, 
          BYSARUS_DESC, BYSARUS_PRICE, BYSARUS_PAGES, BYSARUS_AUTHOR_NAMES, BYSARUS_AUTHOR_IDS, BYSARUS_LICENSE, BYSARUS_PURCHASABLE, BYSARUS_COMMENT_URL, BYSARUS_COMMENTS);
    } finally {
      driver.close();
    } 
  }
  
  
  @Test
  public void testPublicationDetailsSimple() {
    ChromeDriver driver = new ChromeDriver();
    try {
      driver.get(getAppUrl(true) + SIMPLE_PATH);
      testPublicationDetails(driver, ".gamelibrary-publication", SIMPLE_ID, SIMPLE_PATH, SIMPLE_TITLE, SIMPLE_TAGS, 
          SIMPLE_DESC, SIMPLE_PRICE, SIMPLE_PAGES, SIMPLE_AUTHOR_NAMES, SIMPLE_AUTHOR_IDS, SIMPLE_LICENSE, SIMPLE_PURCHASABLE, SIMPLE_COMMENT_URL, SIMPLE_COMMENTS);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testPublicationDetailsBySaAndRus() {
    ChromeDriver driver = new ChromeDriver();
    try {
      driver.get(getAppUrl(true) + BYSARUS_PATH);
      testPublicationDetails(driver, ".gamelibrary-publication", BYSARUS_ID, BYSARUS_PATH,  BYSARUS_TITLE, BYSARUS_TAGS, 
          BYSARUS_DESC, BYSARUS_PRICE, BYSARUS_PAGES, BYSARUS_AUTHOR_NAMES, BYSARUS_AUTHOR_IDS, BYSARUS_LICENSE, BYSARUS_PURCHASABLE, BYSARUS_COMMENT_URL, BYSARUS_COMMENTS);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testPublicationDetailsUmlaut() {
    ChromeDriver driver = new ChromeDriver();
    try {
      driver.get(getAppUrl(true) + UML_PATH);
      testPublicationDetails(driver, ".gamelibrary-publication", UML_ID, UML_PATH,  UML_TITLE, UML_TAGS, 
          UML_DESC, UML_PRICE, UML_PAGES, UML_AUTHOR_NAMES, UML_AUTHOR_IDS, UML_LICENSE, UML_PURCHASABLE, UML_COMMENT_URL, UML_COMMENTS);
    } finally {
      driver.close();
    }
  }
  
  private void testPublicationDetails(RemoteWebDriver driver, String publicationSelector, String publicationId, String path, String title, String[] tags, String description, String price, String numberOfPages, String[] authorNames, Long[] authorIds, String license, boolean purchasable, String commentUrl, int comments) {
    CreativeCommonsLicense creativeCommonsLicense = CreativeCommonsUtils.parseLicenseUrl(license);

    assertEquals(title.toUpperCase(), driver.findElement(By.cssSelector(publicationSelector + " h3 a")).getText());
    assertEquals(getAppUrl(true) + path, driver.findElement(By.cssSelector(publicationSelector + " h3 a")).getAttribute("href"));

    for (int i = 0, l = tags.length; i < l; i++) {
      String tag = tags[i];
      assertEquals(tag.toUpperCase(), driver.findElement(By.cssSelector(publicationSelector + " .gamelibrary-publication-tags a:nth-child(" + (i + 1) + ")")).getText());
    }
   
    assertEquals(description, driver.findElement(By.cssSelector(publicationSelector + " .gamelibrary-publication-description")).getText());
    
    if (price != null) {
      assertEquals(price, driver.findElement(By.cssSelector(publicationSelector + " .gamelibrary-publication-detail-price span")).getText());
    }
    
    assertEquals(numberOfPages, driver.findElement(By.cssSelector(publicationSelector + " .gamelibrary-publication-detail-number-of-pages span")).getText());
    
    if (authorIds == null || authorNames == null) {
      assertEquals(0, driver.findElements(By.cssSelector(publicationSelector + " .gamelibrary-publication-author")).size());
    } else {
      List<WebElement> authorLinks = driver.findElements(By.cssSelector(publicationSelector + " .gamelibrary-publication-author"));
      assertEquals(authorIds.length, authorNames.length);
      assertEquals(authorIds.length, authorLinks.size());
      
      for (int i = 0, l = authorIds.length; i < l; i++) {
        String authorName = authorNames[i];
        Long authorId = authorIds[i];
        WebElement authorLink = authorLinks.get(i);
        assertEquals(authorLink.getText(), authorName.toUpperCase());
        assertEquals(authorLink.getAttribute("href"), getAppUrl(true) + "/profile/" + authorId);
      }
    }
    
    assertEquals(getAppUrl(true) + "/gamelibrary/publicationFiles/" + publicationId, driver.findElement(By.cssSelector(publicationSelector + " .gamelibrary-publication-action-download-pdf")).getAttribute("href"));
    
    assertEquals(purchasable ? 1 : 0, driver.findElements(By.cssSelector(publicationSelector + " .gamelibrary-publication-action-add-to-cart")).size());
    
    if (creativeCommonsLicense != null) {
      assertEquals(1, driver.findElements(By.cssSelector(publicationSelector + " .gamelibrary-publication-creative-commans-license-container")).size());
      assertEquals(creativeCommonsLicense.getUrl(), driver.findElement(By.cssSelector(publicationSelector + " .gamelibrary-publication-creative-commans-license-container a")).getAttribute("href"));
      assertEquals(creativeCommonsLicense.getIconUrl(), driver.findElement(By.cssSelector(publicationSelector + " .gamelibrary-publication-creative-commans-license-container img")).getAttribute("src"));
    } else {
      assertEquals(license, driver.findElement(By.cssSelector(publicationSelector + " .gamelibrary-publication-detail-license a")).getAttribute("href"));
    }
    
    assertEquals("COMMENTS (" + comments + ")", driver.findElement(By.cssSelector(publicationSelector + " .gamelibrary-publication-comments")).getText());
    assertEquals(getAppUrl(true) + "/forum/" + commentUrl, driver.findElement(By.cssSelector(publicationSelector + " .gamelibrary-publication-comments")).getAttribute("href"));
  }
  
}
