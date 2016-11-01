package fi.foyt.fni.test.ui.base.gamelibrary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.openqa.selenium.Keys;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets ({
  @DefineSqlSet (id = "basic-users", before = { "basic-users-setup.sql" }, after = { "basic-users-teardown.sql" }),
  @DefineSqlSet (id = "basic-forum", before = { "basic-forum-setup.sql" }, after = { "basic-forum-teardown.sql" }),
  @DefineSqlSet (id = "store-products", before = { "basic-gamelibrary-setup.sql" }, after = { "basic-gamelibrary-teardown.sql" })
})
public class GameLibraryProposeGameTestsBase extends AbstractUITest {

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired("/gamelibrary/proposegame/");
  }
  
  @Test
  @SqlSets ("basic-users")
  public void testTitle() {
    loginInternal("user@foyt.fi", "pass");
    testTitle("/gamelibrary/proposegame/", "Forge & Illusion - Game Library");
  }
  
  @Test
  @SqlSets ("basic-users")
  public void testDesc() {
    loginInternal("user@foyt.fi", "pass");
    navigate("/gamelibrary/proposegame/");
    waitTitle("Forge & Illusion - Game Library");
    assertSelectorTextIgnoreCase(".view-header-description-title", "PROPOSE A GAME TO THE LIBRARY");
  }

  @Test
  @SqlSets ("basic-users")
  public void testPropose() throws Exception {
    if ("chrome".equals(getBrowser())) {
      // FIXME: File uploading fails with bad gateway on Sauce Labs when using Chrome.
      return;
    }
  
    loginInternal("user@foyt.fi", "pass");
    navigate("/gamelibrary/proposegame/");
    
    File testPng = getTestPng();
    File testPdf = getTestPdf();
    
    waitAndSendKeys(".gamelibrary-propose-game-form-name", "My awesome game");
    waitAndSendKeys(".gamelibrary-propose-game-form-description", "This game is just pretty awesome");
    waitAndSendKeys(".gamelibrary-propose-game-form-authors-share", "5");
    
    waitAndSendKeys(".gamelibrary-propose-game-form-section-image input[name='file']", testPng.getAbsolutePath());
    waitForSelectorPresent(".gamelibrary-propose-game-form-section-image .upload-field-file-name");
    assertSelectorCount(".gamelibrary-propose-game-form-section-image .upload-field-file-name", 1);
    assertSelectorText(".gamelibrary-propose-game-form-section-image .upload-field-file-name", testPng.getName(), true, true);
    
    waitAndSendKeys(".gamelibrary-propose-game-form-section-downloadable input[name='file']", testPdf.getAbsolutePath());
    waitForSelectorPresent(".gamelibrary-propose-game-form-section-downloadable .upload-field-file-name");
    assertSelectorCount(".gamelibrary-propose-game-form-section-downloadable .upload-field-file-name", 1);
    assertSelectorText(".gamelibrary-propose-game-form-section-downloadable .upload-field-file-name", testPdf.getName(), true, true);
    
    waitAndSendKeys(".gamelibrary-propose-game-form-section-printable input[name='file']", testPdf.getAbsolutePath());
    waitForSelectorPresent(".gamelibrary-propose-game-form-section-printable .upload-field-file-name");
    assertSelectorCount(".gamelibrary-propose-game-form-section-printable .upload-field-file-name", 1);
    assertSelectorText(".gamelibrary-propose-game-form-section-printable .upload-field-file-name", testPdf.getName(), true, true);
    
    waitAndClick(".gamelibrary-propose-game-send");

    MimeMessage[] messages = getGreenMail().getReceivedMessages();
    assertEquals(2, messages.length);
    
    List<String> recipientAddressses = new ArrayList<>();
    
    for (MimeMessage message : messages) {
      assertEquals("New Publication into the Game Library", message.getSubject());
      assertTrue(StringUtils.contains((String) message.getContent(), "Test User (user@foyt.fi) has proposed that My awesome game"));
      Address[] recipients = message.getRecipients(RecipientType.TO);

      for (Address recipient : recipients) {
        String address = ((InternetAddress) recipient).getAddress();
        if (!recipientAddressses.contains(address)) {
          recipientAddressses.add(address);
        }
      }
      
    }
    
    assertEquals(Arrays.asList("librarian@foyt.fi", "admin@foyt.fi"), recipientAddressses);

    waitForSelectorVisible(".gamelibrary-publication h3 a");
    
    assertSelectorText(".gamelibrary-publication h3 a", "My awesome game", true, true);
    assertSelectorText(".gamelibrary-publication .gamelibrary-publication-description", "This game is just pretty awesome", true, true);
      
    executeSql("update PublicationFile set contentType = 'DELETE' where id in (select printableFile_id from BookPublication where id in (select id from Publication where creator_id = ?) union select downloadableFile_id from BookPublication where id in (select id from Publication where creator_id = ?))", 2, 2);
    executeSql("update Publication set defaultImage_id = null where creator_id = ?", 2);
    executeSql("Update BookPublication set printableFile_id = null, downloadableFile_id = null where id in (select id from Publication where creator_id = ?)", 2);
    executeSql("delete from PublicationImage where publication_id in (select id from Publication where creator_id = ?)", 2);
    executeSql("delete from PublicationFile where contentType = 'DELETE'");
    executeSql("delete from BookPublication where id in (select id from Publication where creator_id = ?)", 2);
    executeSql("delete from Publication where creator_id = ?", 2);
  }

  @Test
  @SqlSets ("basic-users")
  public void testProposeTags() throws Exception {
    if ("chrome".equals(getBrowser())) {
      // FIXME: File uploading fails with bad gateway on Sauce Labs when using Chrome.
      return;
    }
    
    loginInternal("user@foyt.fi", "pass");
    navigate("/gamelibrary/proposegame/");
    
    File testPng = getTestPng();
    File testPdf = getTestPdf();
    
    waitAndSendKeys(".gamelibrary-propose-game-form-name", "My awesome game");
    waitAndSendKeys(".gamelibrary-propose-game-form-description", "This game is just pretty awesome");
    waitAndSendKeys(".gamelibrary-propose-game-form-authors-share", "5");
    waitAndClick(".tagsinput .ui-autocomplete-input");
    sendKeysSelector(".tagsinput .ui-autocomplete-input", "test tag" + Keys.ENTER);
    
    waitAndSendKeys(".gamelibrary-propose-game-form-section-image input[name='file']", testPng.getAbsolutePath());
    waitForSelectorPresent(".gamelibrary-propose-game-form-section-image .upload-field-file-name");
    assertSelectorCount(".gamelibrary-propose-game-form-section-image .upload-field-file-name", 1);
    assertSelectorText(".gamelibrary-propose-game-form-section-image .upload-field-file-name", testPng.getName(), true, true);
    
    waitAndSendKeys(".gamelibrary-propose-game-form-section-downloadable input[name='file']", testPdf.getAbsolutePath());
    waitForSelectorPresent(".gamelibrary-propose-game-form-section-downloadable .upload-field-file-name");
    assertSelectorCount(".gamelibrary-propose-game-form-section-downloadable .upload-field-file-name", 1);
    assertSelectorText(".gamelibrary-propose-game-form-section-downloadable .upload-field-file-name", testPdf.getName(), true, true);
    
    scrollIntoView(".gamelibrary-propose-game-send");
    waitAndClick(".gamelibrary-propose-game-send");

    waitForSelectorVisible(".gamelibrary-publication h3 a");
    assertSelectorCount(".gamelibrary-publication-tag", 1);
    assertSelectorText(".gamelibrary-publication-tag", "test tag", true, true);
    
    
    executeSql("delete from PublicationTag where publication_id in (select id from Publication where creator_id = ?)", 2);
    executeSql("delete from GameLibraryTag where text = ?", "test tag");
    executeSql("update PublicationFile set contentType = 'DELETE' where id in (select printableFile_id from BookPublication where id in (select id from Publication where creator_id = ?) union select downloadableFile_id from BookPublication where id in (select id from Publication where creator_id = ?))", 2, 2);
    executeSql("update Publication set defaultImage_id = null where creator_id = ?", 2);
    executeSql("Update BookPublication set printableFile_id = null, downloadableFile_id = null where id in (select id from Publication where creator_id = ?)", 2);
    executeSql("delete from PublicationImage where publication_id in (select id from Publication where creator_id = ?)", 2);
    executeSql("delete from PublicationFile where contentType = 'DELETE'");
    executeSql("delete from BookPublication where id in (select id from Publication where creator_id = ?)", 2);
    executeSql("delete from Publication where creator_id = ?", 2);
  }

  @Test
  @SqlSets ("basic-users")
  public void testProposeLicenseCC() throws Exception {
    if ("chrome".equals(getBrowser())) {
      // FIXME: File uploading fails with bad gateway on Sauce Labs when using Chrome.
      return;
    }
  
    loginInternal("user@foyt.fi", "pass");
    navigate("/gamelibrary/proposegame/");
    
    File testPng = getTestPng();
    File testPdf = getTestPdf();
    
    waitAndSendKeys(".gamelibrary-propose-game-form-name", "My awesome game");
    waitAndSendKeys(".gamelibrary-propose-game-form-description", "This game is just pretty awesome");
    waitAndSendKeys(".gamelibrary-propose-game-form-authors-share", "5");
    
    selectSelectBoxByValue(".gamelibrary-propose-game-form-section-license .type", "cc-3.0");
    waitAndClick(".gamelibrary-propose-game-form-section-license .creative-commons-container[data-type-id='cc-3.0'] input[data-attribute='sa']");
    waitAndClick(".gamelibrary-propose-game-form-section-license .creative-commons-container[data-type-id='cc-3.0'] input[data-attribute='nc']");

    waitAndSendKeys(".gamelibrary-propose-game-form-section-image input[name='file']", testPng.getAbsolutePath());
    waitForSelectorPresent(".gamelibrary-propose-game-form-section-image .upload-field-file-name");
    assertSelectorCount(".gamelibrary-propose-game-form-section-image .upload-field-file-name", 1);
    assertSelectorText(".gamelibrary-propose-game-form-section-image .upload-field-file-name", testPng.getName(), true, true);
    
    waitAndSendKeys(".gamelibrary-propose-game-form-section-downloadable input[name='file']", testPdf.getAbsolutePath());
    waitForSelectorPresent(".gamelibrary-propose-game-form-section-downloadable .upload-field-file-name");
    assertSelectorCount(".gamelibrary-propose-game-form-section-downloadable .upload-field-file-name", 1);
    assertSelectorText(".gamelibrary-propose-game-form-section-downloadable .upload-field-file-name", testPdf.getName(), true, true);
    
    waitAndClick(".gamelibrary-propose-game-send");

    waitForSelectorVisible(".gamelibrary-publication h3 a");
    
    assertEquals("https://creativecommons.org/licenses/by-nc-sa/3.0", findElementBySelector(".gamelibrary-publication-creative-commans-license-container a").getAttribute("href"));

    executeSql("update PublicationFile set contentType = 'DELETE' where id in (select printableFile_id from BookPublication where id in (select id from Publication where creator_id = ?) union select downloadableFile_id from BookPublication where id in (select id from Publication where creator_id = ?))", 2, 2);
    executeSql("update Publication set defaultImage_id = null where creator_id = ?", 2);
    executeSql("Update BookPublication set printableFile_id = null, downloadableFile_id = null where id in (select id from Publication where creator_id = ?)", 2);
    executeSql("delete from PublicationImage where publication_id in (select id from Publication where creator_id = ?)", 2);
    executeSql("delete from PublicationFile where contentType = 'DELETE'");
    executeSql("delete from BookPublication where id in (select id from Publication where creator_id = ?)", 2);
    executeSql("delete from Publication where creator_id = ?", 2);
  }

  @Test
  @SqlSets ("basic-users")
  public void testProposeLicenseOther() throws Exception {
    if ("chrome".equals(getBrowser())) {
      // FIXME: File uploading fails with bad gateway on Sauce Labs when using Chrome.
      return;
    }
  
    loginInternal("user@foyt.fi", "pass");
    navigate("/gamelibrary/proposegame/");
    
    File testPng = getTestPng();
    File testPdf = getTestPdf();
    
    waitAndSendKeys(".gamelibrary-propose-game-form-name", "My awesome game");
    waitAndSendKeys(".gamelibrary-propose-game-form-description", "This game is just pretty awesome");
    waitAndSendKeys(".gamelibrary-propose-game-form-authors-share", "5");
    selectSelectBoxByValue(".gamelibrary-propose-game-form-section-license .type", "link");
    waitAndSendKeys(".gamelibrary-propose-game-form-section-license .license", "http://mylicense.example.com/test");
    
    waitAndSendKeys(".gamelibrary-propose-game-form-section-image input[name='file']", testPng.getAbsolutePath());
    waitForSelectorPresent(".gamelibrary-propose-game-form-section-image .upload-field-file-name");
    assertSelectorCount(".gamelibrary-propose-game-form-section-image .upload-field-file-name", 1);
    assertSelectorText(".gamelibrary-propose-game-form-section-image .upload-field-file-name", testPng.getName(), true, true);
    
    waitAndSendKeys(".gamelibrary-propose-game-form-section-downloadable input[name='file']", testPdf.getAbsolutePath());
    waitForSelectorPresent(".gamelibrary-propose-game-form-section-downloadable .upload-field-file-name");
    assertSelectorCount(".gamelibrary-propose-game-form-section-downloadable .upload-field-file-name", 1);
    assertSelectorText(".gamelibrary-propose-game-form-section-downloadable .upload-field-file-name", testPdf.getName(), true, true);
    
    waitAndClick(".gamelibrary-propose-game-send");

    waitForSelectorVisible(".gamelibrary-publication h3 a");
    assertEquals("http://mylicense.example.com/test", findElementBySelector(".gamelibrary-publication-detail-license a").getAttribute("href"));
  

    executeSql("update PublicationFile set contentType = 'DELETE' where id in (select printableFile_id from BookPublication where id in (select id from Publication where creator_id = ?) union select downloadableFile_id from BookPublication where id in (select id from Publication where creator_id = ?))", 2, 2);
    executeSql("update Publication set defaultImage_id = null where creator_id = ?", 2);
    executeSql("Update BookPublication set printableFile_id = null, downloadableFile_id = null where id in (select id from Publication where creator_id = ?)", 2);
    executeSql("delete from PublicationImage where publication_id in (select id from Publication where creator_id = ?)", 2);
    executeSql("delete from PublicationFile where contentType = 'DELETE'");
    executeSql("delete from BookPublication where id in (select id from Publication where creator_id = ?)", 2);
    executeSql("delete from Publication where creator_id = ?", 2);
  }

}
