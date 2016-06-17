package fi.foyt.fni.test.ui.base.forge;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlParam;
import fi.foyt.fni.test.SqlSet;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet(id = "basic-users", before = "basic-users-setup.sql", after = "basic-users-teardown.sql"),
  @DefineSqlSet(id = "basic-materials", before = "basic-materials-setup.sql", after = "basic-materials-teardown.sql"),
  @DefineSqlSet(
    id = "book-design", 
    before = "book-design-setup.sql", 
    after = "book-design-teardown.sql", params = {
      @SqlParam (name = "id", value = "123"),
      @SqlParam (name = "urlName", value = "book-design"),
      @SqlParam (name = "data", value = "<section data-page-number=\"1\" class=\"forge-book-designer-page\" data-type-name=\"Contents\" data-type-id=\"pt-14f4a99f8d8-62338a2f\"><header></header><main style=\"height: calc(100% - 19px);\"><h1 style=\"text-align: center;\" class=\"\">&nbsp;Header</h1></main><footer>1</footer></section>"),
      @SqlParam (name = "fonts", value = "[{\"name\":\"Tinos\",\"url\":\"http://fonts.googleapis.com/css?family=Tinos:400,400italic,700,700italic\"}]"),
      @SqlParam (name = "styles", value = "[{\"name\":\"Body\",\"selector\":\"p\",\"rules\":{\"font-family\":\"Tinos\",\"font-size\":\"12pt\",\"margin-top\":\"14pt\",\"margin-bottom\":\"14pt\",\"font-variant\":\"normal\",\"name\":\"Body\"}},{\"name\":\"Header 1\",\"selector\":\"h1\",\"rules\":{\"font-family\":\"PT Serif\",\"font-size\":\"28pt\",\"margin-top\":\"18pt\",\"margin-bottom\":\"18pt\",\"font-weight\":\"bold\",\"font-variant\":\"small-caps\",\"name\":\"Header 1\"}},{\"name\":\"Header 2\",\"selector\":\"h2\",\"rules\":{\"font-family\":\"PT Serif\",\"font-size\":\"24pt\",\"margin-top\":\"16pt\",\"margin-bottom\":\"16pt\",\"font-weight\":\"bold\",\"font-variant\":\"small-caps\",\"name\":\"Header 2\"}},{\"name\":\"Header 3\",\"selector\":\"h3\",\"rules\":{\"font-family\":\"PT Serif\",\"font-size\":\"20pt\",\"margin-top\":\"14pt\",\"margin-bottom\":\"14pt\",\"font-weight\":\"bold\",\"name\":\"Header 3\"}},{\"name\":\"Header 4\",\"selector\":\"h4\",\"rules\":{\"font-family\":\"PT Serif\",\"font-size\":\"18pt\",\"margin-top\":\"12pt\",\"margin-bottom\":\"12pt\",\"font-weight\":\"bold\",\"name\":\"Header 4\"}},{\"name\":\"Header 5\",\"selector\":\"h5\",\"rules\":{\"font-family\":\"PT Serif\",\"font-size\":\"16pt\",\"margin-top\":\"10pt\",\"margin-bottom\":\"10pt\",\"font-weight\":\"bold\",\"name\":\"Header 5\"}},{\"name\":\"Header 6\",\"selector\":\"h6\",\"rules\":{\"font-family\":\"PT Serif\",\"font-size\":\"14pt\",\"margin-top\":\"8pt\",\"margin-bottom\":\"8pt\",\"font-weight\":\"bold\",\"name\":\"Header 6\"}},{\"name\":\"Bullet list\",\"selector\":\"ul\",\"rules\":{\"font-family\":\"Tinos\",\"font-size\":\"12pt\",\"margin-left\":\"30pt\",\"list-style-type\":\"disc\",\"name\":\"Bullet list\"}},{\"name\":\"Number list\",\"selector\":\"ol\",\"rules\":{\"font-family\":\"Tinos\",\"font-size\":\"12pt\",\"margin-left\":\"30pt\",\"list-style-type\":\"decimal\",\"name\":\"Number list\"}}]"),
      @SqlParam (name = "pageTypes", value = "[{\"id\":\"pt-14f4a99f8d8-62338a2f\",\"name\":\"Contents\",\"numberedPage\":true,\"pageRules\":{\"background-image\":\"url()\",\"background-position\":\"center center\",\"background-size\":\"\",\"background-repeat\":\"no-repeat\",\"padding-top\":\"4em\",\"padding-right\":\"4em\",\"padding-bottom\":\"4em\",\"padding-left\":\"4em\",\"name\":\"Contents\"},\"header\":{\"text\":\"\",\"rules\":{\"display\":\"none\",\"text-align\":\"center\",\"background-color\":\"\",\"padding-top\":\"0pt\",\"padding-right\":\"0pt\",\"padding-bottom\":\"0pt\",\"padding-left\":\"0pt\",\"show\":\"on\",\"font-family\":\"PT Serif\"}},\"footer\":{\"text\":\"[[PAGE]]\",\"rules\":{\"display\":\"block\",\"text-align\":\"center\"}}}]")
    }
  ),
  @DefineSqlSet(
    id = "book-design-folder", 
    before = "book-design-setup.sql", 
    after = "book-design-teardown.sql", params = {
      @SqlParam (name = "id", value = "124"),
      @SqlParam (name = "parentFolderId", value = "1"),
      @SqlParam (name = "urlName", value = "book-design_in_folder"),
      @SqlParam (name = "data", value = "<section data-page-number=\"1\" class=\"forge-book-designer-page\" data-type-name=\"Contents\" data-type-id=\"pt-14f4a99f8d8-62338a2f\"><header></header><main style=\"height: calc(100% - 19px);\"><h1 style=\"text-align: center;\" class=\"\">&nbsp;Header</h1></main><footer>1</footer></section>"),
      @SqlParam (name = "fonts", value = "[{\"name\":\"Tinos\",\"url\":\"http://fonts.googleapis.com/css?family=Tinos:400,400italic,700,700italic\"}]"),
      @SqlParam (name = "styles", value = "[{\"name\":\"Body\",\"selector\":\"p\",\"rules\":{\"font-family\":\"Tinos\",\"font-size\":\"12pt\",\"margin-top\":\"14pt\",\"margin-bottom\":\"14pt\",\"font-variant\":\"normal\",\"name\":\"Body\"}},{\"name\":\"Header 1\",\"selector\":\"h1\",\"rules\":{\"font-family\":\"PT Serif\",\"font-size\":\"28pt\",\"margin-top\":\"18pt\",\"margin-bottom\":\"18pt\",\"font-weight\":\"bold\",\"font-variant\":\"small-caps\",\"name\":\"Header 1\"}},{\"name\":\"Header 2\",\"selector\":\"h2\",\"rules\":{\"font-family\":\"PT Serif\",\"font-size\":\"24pt\",\"margin-top\":\"16pt\",\"margin-bottom\":\"16pt\",\"font-weight\":\"bold\",\"font-variant\":\"small-caps\",\"name\":\"Header 2\"}},{\"name\":\"Header 3\",\"selector\":\"h3\",\"rules\":{\"font-family\":\"PT Serif\",\"font-size\":\"20pt\",\"margin-top\":\"14pt\",\"margin-bottom\":\"14pt\",\"font-weight\":\"bold\",\"name\":\"Header 3\"}},{\"name\":\"Header 4\",\"selector\":\"h4\",\"rules\":{\"font-family\":\"PT Serif\",\"font-size\":\"18pt\",\"margin-top\":\"12pt\",\"margin-bottom\":\"12pt\",\"font-weight\":\"bold\",\"name\":\"Header 4\"}},{\"name\":\"Header 5\",\"selector\":\"h5\",\"rules\":{\"font-family\":\"PT Serif\",\"font-size\":\"16pt\",\"margin-top\":\"10pt\",\"margin-bottom\":\"10pt\",\"font-weight\":\"bold\",\"name\":\"Header 5\"}},{\"name\":\"Header 6\",\"selector\":\"h6\",\"rules\":{\"font-family\":\"PT Serif\",\"font-size\":\"14pt\",\"margin-top\":\"8pt\",\"margin-bottom\":\"8pt\",\"font-weight\":\"bold\",\"name\":\"Header 6\"}},{\"name\":\"Bullet list\",\"selector\":\"ul\",\"rules\":{\"font-family\":\"Tinos\",\"font-size\":\"12pt\",\"margin-left\":\"30pt\",\"list-style-type\":\"disc\",\"name\":\"Bullet list\"}},{\"name\":\"Number list\",\"selector\":\"ol\",\"rules\":{\"font-family\":\"Tinos\",\"font-size\":\"12pt\",\"margin-left\":\"30pt\",\"list-style-type\":\"decimal\",\"name\":\"Number list\"}}]"),
      @SqlParam (name = "pageTypes", value = "[{\"id\":\"pt-14f4a99f8d8-62338a2f\",\"name\":\"Contents\",\"numberedPage\":true,\"pageRules\":{\"background-image\":\"url()\",\"background-position\":\"center center\",\"background-size\":\"\",\"background-repeat\":\"no-repeat\",\"padding-top\":\"4em\",\"padding-right\":\"4em\",\"padding-bottom\":\"4em\",\"padding-left\":\"4em\",\"name\":\"Contents\"},\"header\":{\"text\":\"\",\"rules\":{\"display\":\"none\",\"text-align\":\"center\",\"background-color\":\"\",\"padding-top\":\"0pt\",\"padding-right\":\"0pt\",\"padding-bottom\":\"0pt\",\"padding-left\":\"0pt\",\"show\":\"on\",\"font-family\":\"PT Serif\"}},\"footer\":{\"text\":\"[[PAGE]]\",\"rules\":{\"display\":\"block\",\"text-align\":\"center\"}}}]")
    }
  ),
  @DefineSqlSet(
    id = "book-design-subfolder", 
    before = "book-design-setup.sql", 
    after = "book-design-teardown.sql", params = {
      @SqlParam (name = "id", value = "125"),
      @SqlParam (name = "parentFolderId", value = "2"),
      @SqlParam (name = "urlName", value = "book-design_in_subfolder"),
      @SqlParam (name = "data", value = "<section data-page-number=\"1\" class=\"forge-book-designer-page\" data-type-name=\"Contents\" data-type-id=\"pt-14f4a99f8d8-62338a2f\"><header></header><main style=\"height: calc(100% - 19px);\"><h1 style=\"text-align: center;\" class=\"\">&nbsp;Header</h1></main><footer>1</footer></section>"),
      @SqlParam (name = "fonts", value = "[{\"name\":\"Tinos\",\"url\":\"http://fonts.googleapis.com/css?family=Tinos:400,400italic,700,700italic\"}]"),
      @SqlParam (name = "styles", value = "[{\"name\":\"Body\",\"selector\":\"p\",\"rules\":{\"font-family\":\"Tinos\",\"font-size\":\"12pt\",\"margin-top\":\"14pt\",\"margin-bottom\":\"14pt\",\"font-variant\":\"normal\",\"name\":\"Body\"}},{\"name\":\"Header 1\",\"selector\":\"h1\",\"rules\":{\"font-family\":\"PT Serif\",\"font-size\":\"28pt\",\"margin-top\":\"18pt\",\"margin-bottom\":\"18pt\",\"font-weight\":\"bold\",\"font-variant\":\"small-caps\",\"name\":\"Header 1\"}},{\"name\":\"Header 2\",\"selector\":\"h2\",\"rules\":{\"font-family\":\"PT Serif\",\"font-size\":\"24pt\",\"margin-top\":\"16pt\",\"margin-bottom\":\"16pt\",\"font-weight\":\"bold\",\"font-variant\":\"small-caps\",\"name\":\"Header 2\"}},{\"name\":\"Header 3\",\"selector\":\"h3\",\"rules\":{\"font-family\":\"PT Serif\",\"font-size\":\"20pt\",\"margin-top\":\"14pt\",\"margin-bottom\":\"14pt\",\"font-weight\":\"bold\",\"name\":\"Header 3\"}},{\"name\":\"Header 4\",\"selector\":\"h4\",\"rules\":{\"font-family\":\"PT Serif\",\"font-size\":\"18pt\",\"margin-top\":\"12pt\",\"margin-bottom\":\"12pt\",\"font-weight\":\"bold\",\"name\":\"Header 4\"}},{\"name\":\"Header 5\",\"selector\":\"h5\",\"rules\":{\"font-family\":\"PT Serif\",\"font-size\":\"16pt\",\"margin-top\":\"10pt\",\"margin-bottom\":\"10pt\",\"font-weight\":\"bold\",\"name\":\"Header 5\"}},{\"name\":\"Header 6\",\"selector\":\"h6\",\"rules\":{\"font-family\":\"PT Serif\",\"font-size\":\"14pt\",\"margin-top\":\"8pt\",\"margin-bottom\":\"8pt\",\"font-weight\":\"bold\",\"name\":\"Header 6\"}},{\"name\":\"Bullet list\",\"selector\":\"ul\",\"rules\":{\"font-family\":\"Tinos\",\"font-size\":\"12pt\",\"margin-left\":\"30pt\",\"list-style-type\":\"disc\",\"name\":\"Bullet list\"}},{\"name\":\"Number list\",\"selector\":\"ol\",\"rules\":{\"font-family\":\"Tinos\",\"font-size\":\"12pt\",\"margin-left\":\"30pt\",\"list-style-type\":\"decimal\",\"name\":\"Number list\"}}]"),
      @SqlParam (name = "pageTypes", value = "[{\"id\":\"pt-14f4a99f8d8-62338a2f\",\"name\":\"Contents\",\"numberedPage\":true,\"pageRules\":{\"background-image\":\"url()\",\"background-position\":\"center center\",\"background-size\":\"\",\"background-repeat\":\"no-repeat\",\"padding-top\":\"4em\",\"padding-right\":\"4em\",\"padding-bottom\":\"4em\",\"padding-left\":\"4em\",\"name\":\"Contents\"},\"header\":{\"text\":\"\",\"rules\":{\"display\":\"none\",\"text-align\":\"center\",\"background-color\":\"\",\"padding-top\":\"0pt\",\"padding-right\":\"0pt\",\"padding-bottom\":\"0pt\",\"padding-left\":\"0pt\",\"show\":\"on\",\"font-family\":\"PT Serif\"}},\"footer\":{\"text\":\"[[PAGE]]\",\"rules\":{\"display\":\"block\",\"text-align\":\"center\"}}}]")
    }
  ),
  @DefineSqlSet(
    id = "material-share-user", 
    before = "material-share-user-setup.sql", 
    after = "material-share-user-teardown.sql"
  )
})

public class ForgeBookDesignTestsBase extends AbstractUITest {

  private static final String BOOK_DESIGN_IN_ROOT = "/forge/book-designs/2/book-design";
  private static final String BOOK_DESIGN_IN_FOLDER = "/forge/book-designs/2/folder/book-design_in_folder";
  private static final String BOOK_DESIGN_IN_SUBFOLDER = "/forge/book-designs/2/folder/subfolder/book-design_in_subfolder";

  @Test
  @SqlSets (sets = {
    @SqlSet ( id = "basic-users" ), 
    @SqlSet ( id = "basic-materials" ), 
    @SqlSet ( id = "book-design" ),
    @SqlSet ( id = "book-design-folder" ),
    @SqlSet ( id = "book-design-subfolder" ),
    @SqlSet ( id = "material-share-user", params = {
      @SqlParam (name = "id", value = "1001"),
      @SqlParam (name = "materialId", value = "123"), 
      @SqlParam (name = "role", value = "MAY_VIEW"), 
      @SqlParam (name = "userId", value = "3")
    }),
    @SqlSet ( id = "material-share-user", params = {
      @SqlParam (name = "id", value = "1002"),
      @SqlParam (name = "materialId", value = "123"), 
      @SqlParam (name = "role", value = "MAY_EDIT"), 
      @SqlParam (name = "userId", value = "4")
    })
  })
  public void testLoginRedirect() throws Exception {
    testLoginRequired(BOOK_DESIGN_IN_ROOT);
    testLoginRequired(BOOK_DESIGN_IN_FOLDER);
    testLoginRequired(BOOK_DESIGN_IN_SUBFOLDER);
  }

  @Test
  @SqlSets (sets = {
    @SqlSet ( id = "basic-users" ), 
    @SqlSet ( id = "basic-materials" ), 
    @SqlSet ( id = "book-design" ),
    @SqlSet ( id = "book-design-folder" ),
    @SqlSet ( id = "book-design-subfolder" ),
    @SqlSet ( id = "material-share-user", params = {
      @SqlParam (name = "id", value = "1001"),
      @SqlParam (name = "materialId", value = "123"), 
      @SqlParam (name = "role", value = "MAY_VIEW"), 
      @SqlParam (name = "userId", value = "3")
    }),
    @SqlSet ( id = "material-share-user", params = {
      @SqlParam (name = "id", value = "1002"),
      @SqlParam (name = "materialId", value = "123"), 
      @SqlParam (name = "role", value = "MAY_EDIT"), 
      @SqlParam (name = "userId", value = "4")
    })
  })
  public void testForbidden() throws Exception {
    loginInternal("guest@foyt.fi", "pass");
    testAccessDenied(BOOK_DESIGN_IN_ROOT);
    testAccessDenied(BOOK_DESIGN_IN_FOLDER);
    testAccessDenied(BOOK_DESIGN_IN_SUBFOLDER);
  }

  @Test
  @SqlSets (sets = {
    @SqlSet ( id = "basic-users" ), 
    @SqlSet ( id = "basic-materials" ), 
    @SqlSet ( id = "book-design" ),
    @SqlSet ( id = "book-design-folder" ),
    @SqlSet ( id = "book-design-subfolder" ),
    @SqlSet ( id = "material-share-user", params = {
      @SqlParam (name = "id", value = "1001"),
      @SqlParam (name = "materialId", value = "123"), 
      @SqlParam (name = "role", value = "MAY_VIEW"), 
      @SqlParam (name = "userId", value = "3")
    }),
    @SqlSet ( id = "material-share-user", params = {
      @SqlParam (name = "id", value = "1002"),
      @SqlParam (name = "materialId", value = "123"), 
      @SqlParam (name = "role", value = "MAY_EDIT"), 
      @SqlParam (name = "userId", value = "4")
    })
  })
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
  @SqlSets (sets = {
    @SqlSet ( id = "basic-users" ), 
    @SqlSet ( id = "basic-materials" ), 
    @SqlSet ( id = "book-design" ),
    @SqlSet ( id = "book-design-folder" ),
    @SqlSet ( id = "book-design-subfolder" ),
    @SqlSet ( id = "material-share-user", params = {
      @SqlParam (name = "id", value = "1001"),
      @SqlParam (name = "materialId", value = "123"), 
      @SqlParam (name = "role", value = "MAY_VIEW"), 
      @SqlParam (name = "userId", value = "3")
    }),
    @SqlSet ( id = "material-share-user", params = {
      @SqlParam (name = "id", value = "1002"),
      @SqlParam (name = "materialId", value = "123"), 
      @SqlParam (name = "role", value = "MAY_EDIT"), 
      @SqlParam (name = "userId", value = "4")
    })
  })
  public void testMayEdit() {
    loginInternal("admin@foyt.fi", "pass");
    testMayEdit(BOOK_DESIGN_IN_ROOT);
    testMayEdit(BOOK_DESIGN_IN_FOLDER);
    testMayEdit(BOOK_DESIGN_IN_SUBFOLDER);
  }

  @Test
  @SqlSets (sets = {
    @SqlSet ( id = "basic-users" ), 
    @SqlSet ( id = "basic-materials" ), 
    @SqlSet ( id = "book-design" ),
    @SqlSet ( id = "book-design-folder" ),
    @SqlSet ( id = "book-design-subfolder" ),
    @SqlSet ( id = "material-share-user", params = {
      @SqlParam (name = "id", value = "1001"),
      @SqlParam (name = "materialId", value = "123"), 
      @SqlParam (name = "role", value = "MAY_VIEW"), 
      @SqlParam (name = "userId", value = "3")
    }),
    @SqlSet ( id = "material-share-user", params = {
      @SqlParam (name = "id", value = "1002"),
      @SqlParam (name = "materialId", value = "123"), 
      @SqlParam (name = "role", value = "MAY_EDIT"), 
      @SqlParam (name = "userId", value = "4")
    })
  })
  public void testMayView() {
    loginInternal("librarian@foyt.fi", "pass");
    testMayView(BOOK_DESIGN_IN_ROOT);
    testMayView(BOOK_DESIGN_IN_FOLDER);
    testMayView(BOOK_DESIGN_IN_SUBFOLDER);
  }
  
  @Test
  @SqlSets (sets = {
    @SqlSet ( id = "basic-users" ), 
    @SqlSet ( id = "basic-materials" ), 
    @SqlSet ( id = "book-design" ),
    @SqlSet ( id = "book-design-folder" ),
    @SqlSet ( id = "book-design-subfolder" ),
    @SqlSet ( id = "material-share-user", params = {
      @SqlParam (name = "id", value = "1001"),
      @SqlParam (name = "materialId", value = "123"), 
      @SqlParam (name = "role", value = "MAY_VIEW"), 
      @SqlParam (name = "userId", value = "3")
    }),
    @SqlSet ( id = "material-share-user", params = {
      @SqlParam (name = "id", value = "1002"),
      @SqlParam (name = "materialId", value = "123"), 
      @SqlParam (name = "role", value = "MAY_EDIT"), 
      @SqlParam (name = "userId", value = "4")
    })
  })
  public void textCreateSharedFolder() throws Exception {
    loginInternal("admin@foyt.fi", "pass");
    navigate("/forge/folders/2/folder");
    waitAndClick(".forge-new-material-menu");
    waitAndClick(".forge-new-book-design");
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
