package fi.foyt.fni.test.ui.base.forge;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet(id = "basic-users", before = "basic-users-setup.sql", after = "basic-users-teardown.sql"),
  @DefineSqlSet(id = "basic-materials", before = "basic-materials-setup.sql", after = "basic-materials-teardown.sql"),
  @DefineSqlSet(id = "book-designs", before = "book-designs-setup.sql", after = "book-designs-teardown.sql")
})
public class ForgeBookDesignTestsBase extends AbstractUITest {

  private static final String BOOK_DESIGN_IN_ROOT = "/forge/book-designs/2/book-design";
  private static final String BOOK_DESIGN_IN_FOLDER = "/forge/book-designs/2/folder/book-design_in_folder";
  private static final String BOOK_DESIGN_IN_SUBFOLDER = "/forge/book-designs/2/folder/subfolder/book-design_in_subfolder";

  @Test
  @SqlSets ({"basic-users", "basic-materials", "book-designs"})
  public void testLoginRedirect() throws Exception {
    testLoginRequired(BOOK_DESIGN_IN_ROOT);
    testLoginRequired(BOOK_DESIGN_IN_FOLDER);
    testLoginRequired(BOOK_DESIGN_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-users", "basic-materials", "book-designs"})
  public void testForbidden() throws Exception {
    loginInternal("guest@foyt.fi", "pass");
    testAccessDenied(BOOK_DESIGN_IN_ROOT);
    testAccessDenied(BOOK_DESIGN_IN_FOLDER);
    testAccessDenied(BOOK_DESIGN_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-users", "basic-materials", "book-designs"})
  public void testNotFound() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    testNotFound("/forge/book-designs/2/folder/subfolder");
    testNotFound("/forge/book-designs//book-design");
    testNotFound("/forge/book-designs/a/book-design");
    testNotFound("/forge/book-designs/2");
    testNotFound("/forge/book-designs/2/");
    testNotFound("/forge/book-designs/2/*");
  }

  @Test
  @SqlSets ({"basic-users", "basic-materials", "book-designs"})
  public void testMayEdit() {
    loginInternal("admin@foyt.fi", "pass");
    testMayEdit(BOOK_DESIGN_IN_ROOT);
    testMayEdit(BOOK_DESIGN_IN_FOLDER);
    testMayEdit(BOOK_DESIGN_IN_SUBFOLDER);
  }

  @Test
  @SqlSets ({"basic-users", "basic-materials", "book-designs"})
  public void testMayView() {
    loginInternal("librarian@foyt.fi", "pass");
    testMayView(BOOK_DESIGN_IN_ROOT);
    testMayView(BOOK_DESIGN_IN_FOLDER);
    testMayView(BOOK_DESIGN_IN_SUBFOLDER);
  }
  
  @Test
  @SqlSets ({"basic-users", "basic-materials", "book-designs"})
  public void textCreateSharedFolder() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    navigate("/forge/folders/2/folder");
    clickSelector(".forge-new-material-menu");
    clickSelector(".forge-new-book-design");
    testMayEdit();
    executeSql("delete from BookDesign where id in (select id from Material where type='BOOK_DESIGN' and parentFolder_id = 1)");
    executeSql("delete from MaterialView where material_id in (select id from Material where type='BOOK_DESIGN' and parentFolder_id = 1)");
    executeSql("delete from Material where type='BOOK_DESIGN' and parentFolder_id = 1");
  }
  
  private void testMayEdit(String path) {
    navigate(path);
    assertMayEdit();
  }
  
  private void testMayView(String path) {
    navigate(path);
    assertMayView();
  }
  
  private void assertMayEdit() {
    waitForSelectorVisible(".title input");
    waitForSelectorVisible(".book-designer");
    assertSelectorEnabled(".title input");
    assertSelectorVisible(".book-designer");
    assertSelectorNotVisible(".book-design-read-only");
  }
  
  private void assertMayView() {
    waitForSelectorVisible(".book-design-read-only");
    assertSelectorVisible(".book-design-read-only");
    assertSelectorNotVisible(".book-designer");
    assertSelectorNotVisible(".title input");
  }

}
