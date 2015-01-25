package fi.foyt.fni.test.ui.base;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic", 
    before = {"basic-users-setup.sql"},
    after = {"basic-users-teardown.sql"}
  ),
  @DefineSqlSet (id = "events", 
    before = {"basic-users-setup.sql","illusion-basic-setup.sql", "illusion-upcoming-events-setup.sql", "illusion-upcoming-unpublished-event-setup.sql", "illusion-event-open-setup.sql"},
    after = {"illusion-event-open-teardown.sql", "illusion-upcoming-unpublished-event-teardown.sql", "illusion-upcoming-events-teardown.sql", "illusion-basic-teardown.sql","basic-users-teardown.sql"}
  )
})
public class IllusionIndexTestsBase extends AbstractUITest {

  @Test
  public void testTitle() throws Exception {
    testTitle("/illusion", "Illusion");
  }

  @Test
  public void testNotCreateLinkNotLoggedIn() throws Exception {
    navigate("/illusion");
    assertSelectorNotPresent(".illusion-index-create-event-link");
  }
  
  @Test
  @SqlSets ("basic")
  public void testNotCreateLinkLoggedIn() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    navigate("/illusion");
    assertSelectorPresent(".illusion-index-create-event-link");
  }

  @Test
  @SqlSets ("events")
  public void testListsNotLoggedIn() throws Exception {
    navigate("/illusion");
    assertSelectorPresent("a[href='#upcoming']");
    assertSelectorPresent("#upcoming");
    assertSelectorCount("#upcoming .illusion-index-event", 2);
    assertSelectorTextIgnoreCase("#upcoming .illusion-index-event:nth-of-type(1) .illusion-index-event-name", "Upcoming #1");
    assertSelectorTextIgnoreCase("#upcoming .illusion-index-event:nth-of-type(2) .illusion-index-event-name", "Upcoming #2");
    
    assertSelectorPresent("a[href='#pastevents']");
    clickSelector("a[href='#pastevents']");
    assertSelectorPresent("#pastevents");
    assertSelectorCount("#pastevents .illusion-index-event", 1);
    assertSelectorTextIgnoreCase("#pastevents .illusion-index-event:nth-of-type(1) .illusion-index-event-name", "Open Event");

    assertSelectorNotPresent("a[href='#unpublished']");
    assertSelectorNotPresent("#unpublished");
  }

  @Test
  @SqlSets ("events")
  public void testListsLoggedIn() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    
    navigate("/illusion");
    assertSelectorPresent("a[href='#upcoming']");
    assertSelectorPresent("#upcoming");
    assertSelectorCount("#upcoming .illusion-index-event", 2);
    assertSelectorTextIgnoreCase("#upcoming .illusion-index-event:nth-of-type(1) .illusion-index-event-name", "Upcoming #1");
    assertSelectorTextIgnoreCase("#upcoming .illusion-index-event:nth-of-type(2) .illusion-index-event-name", "Upcoming #2");
    
    assertSelectorPresent("a[href='#pastevents']");
    clickSelector("a[href='#pastevents']");
    assertSelectorPresent("#pastevents");
    assertSelectorCount("#pastevents .illusion-index-event", 1);
    assertSelectorTextIgnoreCase("#pastevents .illusion-index-event:nth-of-type(1) .illusion-index-event-name", "Open Event");

    assertSelectorPresent("a[href='#unpublished']");
    clickSelector("a[href='#unpublished']");
    assertSelectorPresent("#unpublished");
    assertSelectorCount("#unpublished .illusion-index-event", 1);
    assertSelectorTextIgnoreCase("#unpublished .illusion-index-event:nth-of-type(1) .illusion-index-event-name", "Upcoming unpublished");
  }
  
}
