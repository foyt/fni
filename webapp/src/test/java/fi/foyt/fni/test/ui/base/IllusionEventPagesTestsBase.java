package fi.foyt.fni.test.ui.base;

import org.junit.Test;

import fi.foyt.fni.test.SqlAfter;
import fi.foyt.fni.test.SqlBefore;

public class IllusionEventPagesTestsBase extends AbstractUITest {

  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-page-setup.sql"})
  @SqlAfter ({"illusion-event-open-page-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testLoginRequired() throws Exception {
    testLoginRequired("/illusion/event/openevent/pages/testpage");
  }
  
  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-participant-setup.sql", "illusion-event-open-page-setup.sql"})
  @SqlAfter ({"illusion-event-open-page-teardown.sql", "illusion-event-open-participant-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testNotFound() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testNotFound("/illusion/event/openevent/pages/testpage/");
    testNotFound("/illusion/event/openevent/pages/nothing");
    testNotFound("/illusion/event/openevent/pages/");
    testNotFound("/illusion/event/openevent/pages");
    testNotFound("/illusion/event/noevent/pages/testpage");
    testNotFound("/illusion/event/noevent//pages/testpage");
    testNotFound("/illusion/event/noevent/*/pages/testpage");
    testNotFound("/illusion/event/1/pages/testpage");
    testNotFound("/illusion/event///pages/testpage");
    testNotFound("/illusion/event//*/pages/testpage");
    testNotFound("/illusion/event/~/pages/testpage");
  }
  
  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-page-setup.sql", "illusion-event-open-participant-setup.sql"})
  @SqlAfter ({"illusion-event-open-participant-teardown.sql", "illusion-event-open-page-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testPageTitle() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testTitle("/illusion/event/openevent/pages/testpage", "Open Event - Test Page");
  }
  
  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-page-setup.sql", "illusion-event-open-participant-setup.sql"})
  @SqlAfter ({"illusion-event-open-participant-teardown.sql", "illusion-event-open-page-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testPageText() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    navigate("/illusion/event/openevent/pages/testpage");
    assertSelectorTextIgnoreCase(".illusion-event-page-content p", "Page contents");
  }
  
  @Test
  @SqlBefore ({"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-page-setup.sql", "illusion-event-open-organizer-setup.sql"})
  @SqlAfter ({"illusion-event-open-organizer-teardown.sql", "illusion-event-open-page-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"})
  public void testCreatePage() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/manage-pages");
    testTitle("Open Event - Manage Pages");
    clickSelector(".illusion-event-manage-pages-new-page");
    waitForUrlMatches(".*/edit-page.*");
    assertUrlMatches(".*/illusion/event/openevent/edit-page.*");
    executeSql("update MaterialRevision set checksum = ? where id in (select id from DocumentRevision where document_id in (select id from Material where parentFolder_id = ? and urlName = ?))", "DELETE", 20000, "new_page");
    executeSql("delete from DocumentRevision where document_id in (select id from Material where parentFolder_id = ? and urlName = ?)", 20000, "new_page");
    executeSql("delete from MaterialRevision where checksum = ?", "DELETE");
    executeSql("delete from IllusionEventDocument where id in (select id from Material where parentFolder_id = ? and urlName = ?)", 20000, "new_page");
    executeSql("delete from Document where id in (select id from Material where parentFolder_id = ? and urlName = ?)", 20000, "new_page");
    executeSql("delete from Material where parentFolder_id = ? and urlName = ?", 20000, "new_page");
  }
  
}
