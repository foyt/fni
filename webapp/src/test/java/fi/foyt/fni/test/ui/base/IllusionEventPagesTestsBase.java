package fi.foyt.fni.test.ui.base;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets ({
  @DefineSqlSet (id = "illusion-open-page", 
    before = {"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-page-setup.sql", "illusion-event-open-page-participants-setup.sql"}, 
    after = {"illusion-event-open-page-participants-teardown.sql", "illusion-event-open-page-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"}
  ),
  @DefineSqlSet (id = "illusion-open-page-participant", 
    before = {"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-page-setup.sql", "illusion-event-open-page-participants-setup.sql", "illusion-event-open-participant-setup.sql"}, 
    after = {"illusion-event-open-participant-teardown.sql", "illusion-event-open-page-participants-teardown.sql", "illusion-event-open-page-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"}
  ),
  @DefineSqlSet (id = "illusion-open-page-organizer", 
    before = {"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-page-setup.sql", "illusion-event-open-page-participants-setup.sql", "illusion-event-open-organizer-setup.sql"}, 
    after = {"illusion-event-open-organizer-teardown.sql", "illusion-event-open-page-participants-teardown.sql", "illusion-event-open-page-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"}
  )
})
public class IllusionEventPagesTestsBase extends AbstractUITest {
  
  @Test
  @SqlSets ("illusion-open-page")
  public void testLoginRequired() throws Exception {
    testLoginRequired("/illusion/event/openevent/pages/testpage");
  }
  
  @Test
  @SqlSets ("illusion-open-page")
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
  @SqlSets ("illusion-open-page-participant")
  public void testPageTitle() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testTitle("/illusion/event/openevent/pages/testpage", "Open Event - Test Page");
  }
  
  @Test
  @SqlSets ("illusion-open-page-participant")
  public void testPageText() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    navigate("/illusion/event/openevent/pages/testpage");
    assertSelectorTextIgnoreCase(".illusion-event-page-content p", "Page contents");
  }
  
  @Test
  @SqlSets ("illusion-open-page-organizer")
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
