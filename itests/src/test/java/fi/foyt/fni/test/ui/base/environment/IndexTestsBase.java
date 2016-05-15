package fi.foyt.fni.test.ui.base.environment;

import static org.junit.Assert.assertEquals;

import java.net.URLEncoder;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet (id = "upcoming-events", 
    before = {"basic-users-setup.sql","illusion-basic-setup.sql", "illusion-upcoming-events-setup.sql", "illusion-upcoming-unpublished-event-setup.sql"},
    after = {"illusion-upcoming-unpublished-event-teardown.sql", "illusion-upcoming-events-teardown.sql", "illusion-basic-teardown.sql","basic-users-teardown.sql"}
  ),
  @DefineSqlSet (id = "basic-gamelibrary", before = { "basic-users-setup.sql","basic-forum-setup.sql","basic-gamelibrary-setup.sql"}, after={"basic-gamelibrary-teardown.sql", "basic-forum-teardown.sql","basic-users-teardown.sql"}),
})
public class IndexTestsBase extends AbstractUITest {

  @Test
  public void testTitle() throws Exception {
    getWebDriver().get(getAppUrl() + "/");
    assertEquals("Forge & Illusion", getWebDriver().getTitle());
  }

  @Test
  public void testUnicodeGarbage() {
    testNotFound("/å®‰è£…è¯´æ˜Ž.txt");
  }

  @Test
  public void testTexts() {
    navigate("/");

    assertSelectorTextIgnoreCase("p.description-title", "WHAT IS THE FORGE & ILLUSION");
    assertSelectorTextIgnoreCase("p.description-text", "Forge & Illusion is an open platform built for roleplaying and roleplayers.");
    assertSelectorTextIgnoreCase("h3 a[href='/gamelibrary/']", "LATEST GAME LIBRARY PUBLICATIONS");
    assertSelectorTextIgnoreCase("h3 a[href='/forum/']", "LATEST FORUM TOPICS");
    assertSelectorTextIgnoreCase("h3 a[href='/news/archive/0/0']", "NEWS");
    assertSelectorTextIgnoreCase("h3 a[href='/illusion/']", "UPCOMING EVENTS");
    
    assertSelectorTextIgnoreCase("a.more-link[href='/gamelibrary/']", "More >>");
    assertSelectorTextIgnoreCase("a.more-link[href='/forum/']", "More >>");
    assertSelectorTextIgnoreCase("a.more-link[href='/news/archive/0/0']", "More >>");
    assertSelectorTextIgnoreCase("a.more-link[href='/illusion/']", "More >>");
  }

  @Test
  @SqlSets ("basic-gamelibrary")
  public void testPublicationTags() throws Exception {
    navigate("/", true);
    List<WebElement> tagLinks = findElementsBySelector(".publications .tag");
    for (WebElement tagLink : tagLinks) {
      String tag = tagLink.getText().toLowerCase();
      String expectedHref = String.format("%s/gamelibrary/tags/%s", getAppUrl(true), URLEncoder.encode(tag, "UTF-8").replaceAll("\\+", "%20"));
      assertEquals(expectedHref, tagLink.getAttribute("href"));
    }
  }

  @Test
  public void testNoUpcomingEvents() throws Exception {
    navigate("/");
    assertSelectorPresent(".index-illusion-no-events");
  }
  
  @Test
  @SqlSets ("upcoming-events")
  public void testUpcomingEvents() throws Exception {
    navigate("/");
    assertSelectorNotPresent(".index-illusion-no-events");
    assertSelectorCount(".index-illusion-event", 2);
    assertSelectorTextIgnoreCase(".index-illusion-event:nth-child(1) .index-illusion-event-title a", "Upcoming #2");
    assertSelectorPresent(".index-illusion-event:nth-child(1) .index-illusion-event-date");
    assertSelectorPresent(".index-illusion-event:nth-child(1) .index-illusion-event-desc");
    assertSelectorTextIgnoreCase(".index-illusion-event:nth-child(2) .index-illusion-event-title a", "Upcoming #1");
    assertSelectorPresent(".index-illusion-event:nth-child(2) .index-illusion-event-date");
    assertSelectorPresent(".index-illusion-event:nth-child(2) .index-illusion-event-desc");
  }
  
}
