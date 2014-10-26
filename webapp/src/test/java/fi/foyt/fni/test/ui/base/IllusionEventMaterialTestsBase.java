package fi.foyt.fni.test.ui.base;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "illusion-open-materials-participant", 
    before = {"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-participant-setup.sql", "illusion-event-open-materials-to-participants-setup.sql", "illusion-event-open-materials-setup.sql"}, 
    after = {"illusion-event-open-materials-teardown.sql", "illusion-event-open-materials-to-participants-teardown.sql", "illusion-event-open-participant-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"}
  ),
  @DefineSqlSet(id = "illusion-open-materials-organizer", 
    before = {"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-organizer-setup.sql", "illusion-event-open-materials-to-participants-setup.sql", "illusion-event-open-materials-setup.sql"},
    after = {"illusion-event-open-materials-teardown.sql", "illusion-event-open-materials-to-participants-teardown.sql", "illusion-event-open-organizer-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"}
  )
})
public class IllusionEventMaterialTestsBase extends AbstractUITest {
  
  @Test
  @SqlSets ("illusion-open-materials-participant")
  public void testLoginRequired() throws Exception {
    testLoginRequired("/illusion/event/openevent/materials/document");
  }
  
  @Test
  @SqlSets ("illusion-open-materials-participant")
  public void testNotFound() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testNotFound("/illusion/event/openevent/materials/nothing");
    testNotFound("/illusion/event/noevent/materials/document");
    testNotFound("/illusion/event/noevent//materials/document");
    testNotFound("/illusion/event/noevent/*/materials/document");
    testNotFound("/illusion/event/1/materials/document");
    testNotFound("/illusion/event///materials/document");
    testNotFound("/illusion/event//*/materials/document");
    testNotFound("/illusion/event/~/materials/document");
  }
  
  @Test
  @SqlSets ("illusion-open-materials-participant")
  public void testLoggedInParticipant() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testTitle("/illusion/event/openevent/materials/document", "Illusion - Open Event");
    assertSelectorCount(".illusion-event-navigation>a", 2);
    assertSelectorCount(".illusion-event-navigation-item-active", 1);
    assertSelectorPresent(".illusion-event-material");    
    assertSelectorNotPresent(".illusion-event-navigation-admin-menu");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-item-active", "materials");
  }
  
  @Test
  @SqlSets ("illusion-open-materials-organizer")
  public void testLoggedInOrganizer() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    testTitle("/illusion/event/openevent/materials/document", "Illusion - Open Event");
    assertSelectorCount(".illusion-event-navigation>a", 2);
    assertSelectorCount(".illusion-event-navigation-item-active", 1);
    assertSelectorPresent(".illusion-event-material");
    assertSelectorPresent(".illusion-event-navigation-admin-menu");
    assertSelectorTextIgnoreCase(".illusion-event-navigation-item-active", "materials");
  }
  
}
