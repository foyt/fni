package fi.foyt.fni.test.ui.base;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets({
  @DefineSqlSet (id = "forum-basic", before = { "basic-users-setup.sql", "basic-forum-setup.sql"}, after={"basic-forum-teardown.sql", "basic-users-teardown.sql"})
})
public class ForumIndexTestsBase extends AbstractUITest {

  @Test
  @SqlSets ("forum-basic")
  public void testTitle() {
    testTitle(getWebDriver(), "/forum/", "Forum");
  }

}
