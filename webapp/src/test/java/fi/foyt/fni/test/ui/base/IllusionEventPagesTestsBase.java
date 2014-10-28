package fi.foyt.fni.test.ui.base;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
  ),
  @DefineSqlSet (id = "illusion-open-page-hidden", 
    before = {"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-page-setup.sql" }, 
    after = {"illusion-event-open-page-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"}
  ),
  @DefineSqlSet (id = "illusion-open-page-hidden-participant", 
    before = {"illusion-basic-setup.sql", "illusion-event-open-setup.sql", "illusion-event-open-page-setup.sql", "illusion-event-open-participant-setup.sql"}, 
    after = {"illusion-event-open-participant-teardown.sql", "illusion-event-open-page-teardown.sql", "illusion-event-open-teardown.sql", "illusion-basic-teardown.sql"}
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
  
  @Test
  @SqlSets ("illusion-open-page-organizer")
  public void testPagePermaLink() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    navigate("/illusion/event/openevent/manage-pages");
    testTitle("Open Event - Manage Pages");
    clickSelector(".illusion-event-manage-pages-new-page");
    waitForUrlMatches(".*/edit-page.*");
    String pageId = null;
    
    Pattern pattern = Pattern.compile("(.*pageId=)([0-9]{1,})(.*)");
    Matcher matcher = pattern.matcher(getWebDriver().getCurrentUrl());
    if (matcher.matches()) {
      pageId = matcher.group(2);
    }
    
    waitForSelectorText(".illusion-edit-page-editor-status", "Loaded", true);
    clearSelectorInput(".illusion-edit-page-title");
    setSelectorInputValue(".illusion-edit-page-title", "changed");
    clickSelector(".illusion-edit-page-editor-status");
    waitForSelectorText(".illusion-edit-page-editor-status", "Saved", true);
    executeSql("update IllusionEventSetting set value = '{\"" + pageId + "\":{\"visibility\":\"VISIBLE\"}}' where id = 1");
    
    testTitle("/illusion/event/openevent/pages/new_page", "Open Event - changed");

    executeSql("delete from PermaLink where material_id = (select id from Material where parentFolder_id = ? and urlName = ?)", 20000, "changed");
    executeSql("delete from MaterialRevisionSetting where materialRevision_id in (select id from DocumentRevision where document_id in (select id from Material where parentFolder_id = ? and urlName = ?))", 20000, "changed");
    executeSql("update MaterialRevision set checksum = ? where id in (select id from DocumentRevision where document_id in (select id from Material where parentFolder_id = ? and urlName = ?))", "DELETE", 20000, "changed");
    executeSql("delete from DocumentRevision where document_id in (select id from Material where parentFolder_id = ? and urlName = ?)", 20000, "changed");
    executeSql("delete from MaterialRevision where checksum = ?", "DELETE");
    executeSql("delete from CoOpsSession where material_id in (select id from Material where parentFolder_id = ? and urlName = ?)", 20000, "changed");
    executeSql("delete from IllusionEventDocument where id in (select id from Material where parentFolder_id = ? and urlName = ?)", 20000, "changed");
    executeSql("delete from Document where id in (select id from Material where parentFolder_id = ? and urlName = ?)", 20000, "changed");
    executeSql("delete from Material where parentFolder_id = ? and urlName = ?", 20000, "changed");
  }

  @Test
  @SqlSets ("illusion-open-page-hidden")
  public void testHiddenNotLoggedIn() throws UnsupportedEncodingException {
    testAccessDenied("/illusion/event/openevent/pages/testpage");
  }
  
  @Test
  @SqlSets ("illusion-open-page-hidden")
  public void testHiddenLoggedIn() throws UnsupportedEncodingException {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/pages/testpage");
  }
  
  @Test
  @SqlSets ("illusion-open-page-hidden-participant")
  public void testHiddenLoggedParticipant() throws UnsupportedEncodingException {
    loginInternal("user@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/pages/testpage");
  }
  
  @Test
  @SqlSets ("illusion-open-page-participant")
  public void testVisibleForParticipantsNotLoggedIn() throws UnsupportedEncodingException {
    testLoginRequired("/illusion/event/openevent/pages/testpage");
  }
  
  @Test
  @SqlSets ("illusion-open-page-participant")
  public void testVisibleForParticipantsLoggedIn() throws UnsupportedEncodingException {
    loginInternal("admin@foyt.fi", "pass");
    testAccessDenied("/illusion/event/openevent/pages/testpage");
  }
  
}
