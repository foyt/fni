package fi.foyt.fni.test.ui.base.forgepublic;

import org.junit.Test;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet(id = "users", before = "basic-users-setup.sql", after = "basic-users-teardown.sql"),
  @DefineSqlSet(id = "materials", before = { "basic-materials-setup.sql"}, after = {"basic-materials-teardown.sql"} ),
  @DefineSqlSet(id = "public-materials", before = {"basic-materials-public-setup.sql", "basic-materials-views-setup.sql" }, after = { "basic-materials-views-teardown.sql" } ),
  @DefineSqlSet(id = "public-material-tags", before = { "basic-tags-setup.sql", "basic-materials-tags-setup.sql", "basic-materials-views-setup.sql" }, after = { "basic-materials-tags-teardown.sql", "basic-tags-teardown.sql" } ),
})
public class ForgePublicMaterialTestsBase extends AbstractUITest {

  @Test
  @SqlSets ({"users", "materials", "public-materials"})
  public void testNotFound() {
    testNotFound("/forge/public/materials/666/");
    testNotFound("/forge/public/materials/abc");
    testNotFound("/forge/public/materials/666/document");
    testNotFound("/forge/public/materials/2/nodocument");
    testNotFound("/forge/public/materials/2");
    testNotFound("/forge/public/materials//document");
    testNotFound("/forge/public/materials/*/document");
    testNotFound("/forge/public/materials/~/document");
  }
  
  @Test
  @SqlSets ({"users", "materials" })
  public void testAccessDenied() {
    testAccessDenied("/forge/public/materials/2/document");
    testAccessDenied("/forge/public/materials/2/folder/image_in_folder");
    testAccessDenied("/forge/public/materials/2/pdf");
    testAccessDenied("/forge/public/materials/2/folder/subfolder/vectorimage_in_subfolder");
    testAccessDenied("/forge/public/materials/2/googledoc-hyphen");
  }
  
  @Test
  @SqlSets ({"users", "materials", "public-materials", "public-material-tags"})
  public void testDocument() {
    navigate("/forge/public/materials/2/document");
    waitForSelectorPresent(".forge-public-material");
    assertSelectorText(".forge-public-material-title h2", "document", true, true);
    assertSelectorText(".forge-public-material-container p", "Document in root", true, true);
    assertSelectorCount(".details-container .material-tags a", 4);
    assertSelectorText(".details-container .material-tags a:nth-of-type(1)", "Test User", true, true);
    assertSelectorText(".details-container .material-tags a:nth-of-type(2)", "Test", true, true);
    assertSelectorText(".details-container .material-tags a:nth-of-type(3)", "Tag", true, true);
    assertSelectorText(".details-container .material-tags a:nth-of-type(4)", "With Space", true, true);
    assertSelectorText(".details-container .created-modified", "Created: 1/6/10, Last Modified: 1/6/10", true, true);
    assertSelectorText(".details-container .description p", "Document material for automated tests", true, true);
    assertSelectorNotPresent(".license-link");
    assertSelectorPresent(".license a img");
    assertHref(".license a", "http://creativecommons.org/licenses/by-sa/4.0/");
    assertSelectorCount(".actions a", 2);
    assertSelectorText(".actions a:nth-of-type(1)", "Download PDF", true, true);
    assertSelectorText(".actions a:nth-of-type(2)", "Download HTML", true, true);
  }
  
  @Test
  @SqlSets ({"users", "materials", "public-materials", "public-material-tags"})
  public void testImage() {
    navigate("/forge/public/materials/2/folder/image_in_folder");
    waitForSelectorPresent(".forge-public-material");
    assertSelectorText(".forge-public-material-title h2", "Image in Folder", true, true);
    assertSelectorPresent(".forge-public-material-container img");
    assertSelectorCount(".details-container .material-tags a", 1);
    assertSelectorText(".details-container .material-tags a:nth-of-type(1)", "Test User", true, true);
    assertSelectorText(".details-container .created-modified", "Created: 1/10/10, Last Modified: 1/10/10", true, true);
    assertSelectorText(".details-container .description p", "Image for automated tests", true, true);
    assertSelectorPresent(".license-link");
    assertSelectorNotPresent(".license a img");
    assertHref(".license-link", "http://www.example.com/custom-license");
    assertSelectorCount(".actions a", 1);
    assertSelectorText(".actions a:nth-of-type(1)", "Download", true, true);
  }
  
  @Test
  @SqlSets ({"users", "materials", "public-materials", "public-material-tags"})
  public void testPdf() {
    navigate("/forge/public/materials/2/pdf");
    waitForSelectorPresent(".forge-public-material");
    assertSelectorText(".forge-public-material-title h2", "Pdf", true, true);
    assertSelectorPresent(".forge-public-material-container iframe");
    assertSelectorCount(".details-container .material-tags a", 1);
    assertSelectorText(".details-container .material-tags a:nth-of-type(1)", "Test User", true, true);
    assertSelectorText(".details-container .created-modified", "CREATED: 1/23/10, LAST MODIFIED: 1/23/10", true, true);
    assertSelectorText(".details-container .description p", "", true, true);
    assertSelectorPresent(".license-link");
    assertSelectorNotPresent(".license a img");
    assertHref(".license-link", "http://www.example.com/custom-license");
    assertSelectorCount(".actions a", 1);
    assertSelectorText(".actions a:nth-of-type(1)", "Download", true, true);
  }
  
  @Test
  @SqlSets ({"users", "materials", "public-materials", "public-material-tags"})
  public void testVectorImage() {
    navigate("/forge/public/materials/2/folder/subfolder/vectorimage_in_subfolder");
    waitForSelectorPresent(".forge-public-material");
    assertSelectorText(".forge-public-material-title h2", "Vector image in subfolder", true, true);
    assertSelectorPresent(".forge-public-material-container img");
    assertSelectorCount(".details-container .material-tags a", 1);
    assertSelectorText(".details-container .material-tags a:nth-of-type(1)", "Test User", true, true);
    assertSelectorText(".details-container .created-modified", "CREATED: 1/14/10, LAST MODIFIED: 1/14/10", true, true);
    assertSelectorText(".details-container .description p", "", true, true);
    assertSelectorPresent(".license-link");
    assertSelectorNotPresent(".license a img");
    assertSelectorCount(".actions a", 1);
    assertSelectorText(".actions a:nth-of-type(1)", "Download", true, true);
  }
  
  @Test
  @SqlSets ({"users", "materials", "public-materials", "public-material-tags"})
  public void testGoogleDrive() {
    navigate("/forge/public/materials/2/googledoc-hyphen");
    waitForSelectorPresent(".forge-public-material");
    assertSelectorText(".forge-public-material-title h2", "Google Doc", true, true);
    assertSelectorPresent(".forge-public-material-container iframe");
    assertSelectorCount(".details-container .material-tags a", 1);
    assertSelectorText(".details-container .material-tags a:nth-of-type(1)", "Test User", true, true);
    assertSelectorText(".details-container .created-modified", "CREATED: 1/22/10, LAST MODIFIED: 1/22/10", true, true);
    assertSelectorText(".details-container .description p", "", true, true);
    assertSelectorPresent(".license-link");
    assertSelectorNotPresent(".license a img");
    assertSelectorCount(".actions a", 1);
    assertSelectorText(".actions a:nth-of-type(1)", "Download", true, true);
  }
  
}
