package fi.foyt.fni.test.ui.base.forum;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets({
  @DefineSqlSet (
    id = "forum-basic", 
    before = { "basic-users-setup.sql", "basic-forum-setup.sql"}, 
    after={"basic-forum-teardown.sql", "basic-users-teardown.sql"}
  ),
  @DefineSqlSet (
    id = "forum-special-characters", 
    before = {"basic-users-setup.sql", "basic-forum-setup.sql", "forum-with-special-characters-setup.sql"}, 
    after = {"forum-with-special-characters-teardown.sql", "basic-forum-teardown.sql", "basic-users-teardown.sql"}
  )
})
public class ForumPostEditTestsBase extends AbstractUITest {

  private static final String TEST_POST = "/forum/1_topic_forum/single_topic/edit/11";

  @Test
  @SqlSets ("forum-basic")
  public void testLoginRedirect() throws Exception {
    testLoginRequired(TEST_POST);
  }

  @Test
  @SqlSets ("forum-basic")
  public void testUser() throws Exception {
    loginInternal("guest@foyt.fi", "pass");
    testAccessDenied(TEST_POST);
  }

  @Test
  @SqlSets ("forum-basic")
  public void testAdmin() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    navigate(TEST_POST);
    waitForSelectorVisible(".cke_wysiwyg_frame");
    assertSelectorVisible(".cke_wysiwyg_frame");
    assertSelectorEnabled("input[type=\"submit\"]");
  }

  @Test
  @SqlSets ("forum-basic")
  public void testNotFound() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    testNotFound("/forum/q/single_topic/edit/8");
    testNotFound("/forum/1_topic_forum/q/edit/8");
    testNotFound("/forum//single_topic/edit/8");
    testNotFound("/forum/1_topic_forum//edit/8");
    testNotFound("/forum///edit/8");
    testNotFound("/forum/1_topic_forum/single_topic/edit/abc");
    testNotFound("/forum/1_topic_forum/single_topic/edit/1024");
    testNotFound("/forum/1_topic_forum/single_topic/edit/9");
  }

  @Test
  @SqlSets ("forum-special-characters")
  public void testWithSpecialCharacters() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    navigate("/forum/with-special.characters/with-special.characters/edit/28");
    assertTitle("Edit Reply");
  }

}
