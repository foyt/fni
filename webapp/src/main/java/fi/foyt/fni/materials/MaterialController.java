package fi.foyt.fni.materials;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.apache.xpath.XPathAPI;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.itextpdf.text.DocumentException;

import fi.foyt.fni.auth.DropboxAuthenticationStrategy;
import fi.foyt.fni.coops.CoOpsSessionController;
import fi.foyt.fni.drive.DriveManager;
import fi.foyt.fni.drive.SystemGoogleDriveCredentials;
import fi.foyt.fni.materials.operations.MaterialCopy;
import fi.foyt.fni.persistence.dao.auth.UserIdentifierDAO;
import fi.foyt.fni.persistence.dao.common.LanguageDAO;
import fi.foyt.fni.persistence.dao.common.TagDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventMaterialParticipantSettingDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventParticipantDAO;
import fi.foyt.fni.persistence.dao.materials.BinaryDAO;
import fi.foyt.fni.persistence.dao.materials.BookDesignDAO;
import fi.foyt.fni.persistence.dao.materials.BookTemplateDAO;
import fi.foyt.fni.persistence.dao.materials.CharacterSheetDAO;
import fi.foyt.fni.persistence.dao.materials.CharacterSheetDataDAO;
import fi.foyt.fni.persistence.dao.materials.CharacterSheetEntryDAO;
import fi.foyt.fni.persistence.dao.materials.CharacterSheetRollDAO;
import fi.foyt.fni.persistence.dao.materials.CharacterSheetRollLabelDAO;
import fi.foyt.fni.persistence.dao.materials.DocumentDAO;
import fi.foyt.fni.persistence.dao.materials.DocumentRevisionDAO;
import fi.foyt.fni.persistence.dao.materials.DropboxFileDAO;
import fi.foyt.fni.persistence.dao.materials.DropboxFolderDAO;
import fi.foyt.fni.persistence.dao.materials.DropboxRootFolderDAO;
import fi.foyt.fni.persistence.dao.materials.FileDAO;
import fi.foyt.fni.persistence.dao.materials.FolderDAO;
import fi.foyt.fni.persistence.dao.materials.GoogleDocumentDAO;
import fi.foyt.fni.persistence.dao.materials.ImageDAO;
import fi.foyt.fni.persistence.dao.materials.ImageRevisionDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialRevisionSettingDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialSettingDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialSettingKeyDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialShareGroupDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialShareUserDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialTagDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialThumbnailDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialViewDAO;
import fi.foyt.fni.persistence.dao.materials.PdfDAO;
import fi.foyt.fni.persistence.dao.materials.PermaLinkDAO;
import fi.foyt.fni.persistence.dao.materials.StarredMaterialDAO;
import fi.foyt.fni.persistence.dao.materials.TagWithCount;
import fi.foyt.fni.persistence.dao.materials.VectorImageDAO;
import fi.foyt.fni.persistence.dao.materials.VectorImageRevisionDAO;
import fi.foyt.fni.persistence.dao.users.UserDAO;
import fi.foyt.fni.persistence.dao.users.UserTokenDAO;
import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.auth.UserIdentifier;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.common.Tag;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.materials.Binary;
import fi.foyt.fni.persistence.model.materials.BookDesign;
import fi.foyt.fni.persistence.model.materials.BookTemplate;
import fi.foyt.fni.persistence.model.materials.CharacterSheet;
import fi.foyt.fni.persistence.model.materials.CharacterSheetData;
import fi.foyt.fni.persistence.model.materials.CharacterSheetEntry;
import fi.foyt.fni.persistence.model.materials.CharacterSheetRoll;
import fi.foyt.fni.persistence.model.materials.CharacterSheetRollLabel;
import fi.foyt.fni.persistence.model.materials.CoOpsSession;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.DocumentRevision;
import fi.foyt.fni.persistence.model.materials.DropboxFile;
import fi.foyt.fni.persistence.model.materials.DropboxFolder;
import fi.foyt.fni.persistence.model.materials.DropboxRootFolder;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.GoogleDocument;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.materials.ImageRevision;
import fi.foyt.fni.persistence.model.materials.ImageSize;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialRevision;
import fi.foyt.fni.persistence.model.materials.MaterialRevisionSetting;
import fi.foyt.fni.persistence.model.materials.MaterialSetting;
import fi.foyt.fni.persistence.model.materials.MaterialSettingKey;
import fi.foyt.fni.persistence.model.materials.MaterialShareGroup;
import fi.foyt.fni.persistence.model.materials.MaterialShareUser;
import fi.foyt.fni.persistence.model.materials.MaterialTag;
import fi.foyt.fni.persistence.model.materials.MaterialThumbnail;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.materials.MaterialView;
import fi.foyt.fni.persistence.model.materials.Pdf;
import fi.foyt.fni.persistence.model.materials.PermaLink;
import fi.foyt.fni.persistence.model.materials.StarredMaterial;
import fi.foyt.fni.persistence.model.materials.VectorImage;
import fi.foyt.fni.persistence.model.materials.VectorImageRevision;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.UnauthorizedException;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.auth.OAuthUtils;
import fi.foyt.fni.utils.data.FileData;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.html.HtmlUtils;
import fi.foyt.fni.utils.images.ImageUtils;
import fi.foyt.fni.utils.itext.B64ImgReplacedElementFactory;
import fi.foyt.fni.utils.language.GuessedLanguage;
import fi.foyt.fni.utils.language.LanguageUtils;
import fi.foyt.fni.utils.search.SearchResult;
import fi.foyt.fni.utils.search.SearchResultScoreComparator;
import fi.foyt.fni.utils.servlet.RequestUtils;

public class MaterialController {

  private static final String MATERIALS_PATH = "materials";
  private static final long DEFAULT_MATERIAL_SIZE = 2048;
  private static final long DEFAULT_QUOTA = 1024 * 1024 * 10;
  private static final String DOCUMENT_TEMPLATE = "<!DOCTYPE HTML><html><head><meta charset=\"UTF-8\"><title>{0}</title><link rel=\"StyleSheet\" href=\"{1}\"/></head><body>{2}</body></html>";

  @Inject
  private Logger logger;

  @Inject
  private FullTextEntityManager fullTextEntityManager;

  @Inject
  private LanguageDAO languageDAO;

  @Inject
  private MaterialDAO materialDAO;

  @Inject
  private BookDesignDAO bookDesignDAO;

  @Inject
  private BookTemplateDAO bookTemplateDAO;

  @Inject
  private FolderDAO folderDAO;

  @Inject
  private FileDAO fileDAO;

  @Inject
  private PdfDAO pdfDAO;

  @Inject
  private BinaryDAO binaryDAO;

  @Inject
  private GoogleDocumentDAO googleDocumentDAO;

  @Inject
  private PermaLinkDAO permaLinkDAO;

  @Inject
  private StarredMaterialDAO starredMaterialDAO;

  @Inject
  private MaterialViewDAO materialViewDAO;

  @Inject
  private UserDAO userDAO;

  @Inject
  private MaterialTagDAO materialTagDAO;

  @Inject
  private TagDAO tagDAO;
  
  @Inject
  private MaterialShareUserDAO materialShareUserDAO;
  
  @Inject
  private MaterialShareGroupDAO materialShareGroupDAO;

  @Inject
  private MaterialThumbnailDAO materialThumbnailDAO;

  @Inject
  private DocumentDAO documentDAO;

  @Inject
  private DocumentRevisionDAO documentRevisionDAO;

  @Inject
  private VectorImageDAO vectorImageDAO;

  @Inject
  private VectorImageRevisionDAO vectorImageRevisionDAO;

  @Inject
  private ImageDAO imageDAO;

  @Inject
  private ImageRevisionDAO imageRevisionDAO;

  @Inject
  private MaterialSettingKeyDAO materialSettingKeyDAO;

  @Inject
  private MaterialSettingDAO materialSettingDAO;

  @Inject
  private MaterialRevisionSettingDAO materialRevisionSettingDAO;

  @Inject
  private CharacterSheetDAO characterSheetDAO;
  
  @Inject
  private CharacterSheetDataDAO characterSheetDataDAO;

  @Inject
  private CharacterSheetEntryDAO characterSheetEntryDAO;

  @Inject
  private CharacterSheetRollLabelDAO characterSheetRollLabelDAO;

  @Inject
  private CharacterSheetRollDAO characterSheetRollDAO;

  @Inject
  private DropboxFolderDAO dropboxFolderDAO;

  @Inject
  private DropboxRootFolderDAO dropboxRootFolderDAO;

  @Inject
  private DropboxFileDAO dropboxFileDAO;

  @Inject
  private UserIdentifierDAO userIdentifierDAO;

  @Inject
  private UserTokenDAO userTokenDAO;
  
  @Inject
  private IllusionEventMaterialParticipantSettingDAO illusionEventMaterialParticipantSettingDAO;

  @Inject
  private IllusionEventDAO illusionEventDAO;

  @Inject
  private IllusionEventParticipantDAO illusionEventParticipantDAO;
  
  @Inject
  private DriveManager driveManager;
  
  @Inject
  private SystemGoogleDriveCredentials systemGoogleDriveCredentials;

  @Inject
  private MaterialPermissionController materialPermissionController;
  
  @Inject
  private CoOpsSessionController coOpsSessionController;

  @Inject
  private SystemSettingsController systemSettingsController;
  
  @Inject
  private DropboxAuthenticationStrategy dropboxAuthenticationStrategy;
  
  @Inject
  private PdfServiceClient pdfServiceClient;

  @Any
  @Inject
  private Instance<MaterialCopy<? extends Material>> materialCopies;
  
  /* Character Sheets */

  public CharacterSheet createCharacterSheet(Folder parentFolder, String title, String content, User creator, String styles, String scripts) {
    Date now = new Date();
    String urlName = getUniqueMaterialUrlName(creator, parentFolder, null, title);
    return characterSheetDAO.create(parentFolder, content, styles, scripts, null, title, urlName, MaterialPublicity.PRIVATE, creator, now, creator, now);
  }
  
  public CharacterSheet findCharacterSheetById(Long id) {
    return characterSheetDAO.findById(id);
  }
  
  public CharacterSheet updateCharacterSheet(CharacterSheet characterSheet, String title, String contents, String styles, String scripts, User modifier) {
    if (!StringUtils.equals(characterSheet.getTitle(), title)) {
      characterSheetDAO.updateTitle(characterSheet, title);
      String urlName = getUniqueMaterialUrlName(characterSheet.getCreator(), characterSheet.getParentFolder(), characterSheet, title);
      characterSheetDAO.updateUrlName(characterSheet, urlName);
    }
    
    characterSheetDAO.updateContents(characterSheet, contents);
    characterSheetDAO.updateStyles(characterSheet, styles);
    characterSheetDAO.updateScripts(characterSheet, scripts);
    characterSheetDAO.updateModifier(characterSheet, modifier);
    characterSheetDAO.updateModified(characterSheet, new Date());
    return characterSheet;
  }
  
  public fi.foyt.fni.materials.CharacterSheetData getCharacterSheetData(CharacterSheet sheet) {
    fi.foyt.fni.materials.CharacterSheetData result = new fi.foyt.fni.materials.CharacterSheetData(getCharacterSheetMeta(sheet));
    
    for (CharacterSheetEntry entry : characterSheetEntryDAO.listBySheet(sheet)) {
      List<CharacterSheetData> datas = characterSheetDataDAO.listByEntry(entry);
      for (CharacterSheetData data : datas) {
        result.setValue(entry.getName(), data.getUser().getId(), data.getValue());
      }
    }
    
    return result;
  }
  
  public Map<String, String> getUserCharacterSheetData(CharacterSheet sheet, User user) {
    Map<String, String> result = new HashMap<>();
    
    for (CharacterSheetEntry entry : characterSheetEntryDAO.listBySheet(sheet)) {
      CharacterSheetData data = characterSheetDataDAO.findByEntryAndUser(entry, user);
      String value = data != null ? data.getValue() : null;
      result.put(entry.getName(), value);
    }
    
    return result;
  }
  
  public String getUserCharacterSheetValue(CharacterSheet sheet, User user, String entryName) {
    CharacterSheetEntry entry = characterSheetEntryDAO.findBySheetAndName(sheet, entryName);
    if (entry == null) {
      return null;
    }
    
    CharacterSheetData data = characterSheetDataDAO.findByEntryAndUser(entry, user);
    return data != null ? data.getValue() : null;
  }
  
  public void setUserCharacterSheetValue(CharacterSheet sheet, User user, String entryName, String value) {
    CharacterSheetEntry entry = characterSheetEntryDAO.findBySheetAndName(sheet, entryName);
    if (entry != null) {
      CharacterSheetData data = characterSheetDataDAO.findByEntryAndUser(entry, user);
      if (data != null) {
        characterSheetDataDAO.updateValue(data, value);
      } else {
        characterSheetDataDAO.create(entry, user, value);
      }
    } else {
      logger.severe(String.format("Could not find entry %s from character sheet %d", entryName, sheet.getId()));
    }
  }
  
  public CharacterSheetMeta getCharacterSheetMeta(CharacterSheet sheet) {
    CharacterSheetMeta result = new CharacterSheetMeta();
    
    for (CharacterSheetEntry entry : characterSheetEntryDAO.listBySheet(sheet)) {
      result.put(entry.getName(), new CharacterSheetMetaField(entry.getType(), null));
    }
    
    return result;
  }
  
  public void setCharacterSheetMeta(CharacterSheet sheet, CharacterSheetMeta meta) {
    for (String name : meta.keySet()) {
      CharacterSheetMetaField field = meta.get(name);
     
      CharacterSheetEntry entry = characterSheetEntryDAO.findBySheetAndName(sheet, name);
      if (entry == null) {
        characterSheetEntryDAO.create(sheet, name, field.getType());
      } else {
        characterSheetEntryDAO.updateType(entry, field.getType());
      }
    }
  }
  
  public CharacterSheetRoll addCharacterSheetRoll(CharacterSheet sheet, User user, String label, String roll, Integer result) {
    CharacterSheetRollLabel rollLabel = characterSheetRollLabelDAO.findBySheetAndLabel(sheet, label);
    if (rollLabel == null) {
      rollLabel = characterSheetRollLabelDAO.create(sheet, label);
    }
    
    return characterSheetRollDAO.create(rollLabel, roll, user, new Date(), result);
  }
  
  /* Book Layout */
  
  public BookDesign createBookDesign(Folder parentFolder, String title, User creator) {
    String urlName = getUniqueMaterialUrlName(creator, parentFolder, null, DigestUtils.md5Hex(String.valueOf(System.currentTimeMillis())));    
    return createBookDesign(parentFolder, urlName, title, "", null, null, null, null, creator);
  }

  public BookDesign createBookDesign(Folder parentFolder, String urlName, String title, String data, String styles, String fonts, String pageTypes, Language language, User creator) {
    Date now = new Date();
    return bookDesignDAO.create(creator, now, creator, now, language, parentFolder, urlName, title, data, styles, fonts, pageTypes, MaterialPublicity.PRIVATE);
  }

  public BookDesign findBookDesign(Long id) {
    return bookDesignDAO.findById(id);
  }
  
  public BookDesign updateBookDesign(BookDesign bookDesign, User modifier, String title, String data, String styles, String fonts, String pageTypes) {
    if (!StringUtils.equals(title, bookDesign.getTitle())) {
      String oldUrlName = bookDesign.getUrlName();
      String newUrlName = getUniqueMaterialUrlName(bookDesign.getCreator(), bookDesign.getParentFolder(), bookDesign, title);
      materialDAO.updateTitle(bookDesign, title, modifier);
      
      if (!StringUtils.equals(newUrlName, oldUrlName)) {
        materialDAO.updateUrlName(bookDesign, newUrlName, modifier);
      }
    }
    
    bookDesign = bookDesignDAO.updateData(bookDesign, data);
    bookDesign = bookDesignDAO.updateStyles(bookDesign, styles);
    bookDesign = bookDesignDAO.updateFonts(bookDesign, fonts);
    bookDesign = bookDesignDAO.updatePageTypes(bookDesign, pageTypes);
    bookDesign = bookDesignDAO.updateModified(bookDesign, new Date());
    bookDesign = bookDesignDAO.updateModifier(bookDesign, modifier);
    
    return bookDesign;
  }

  public TypedData printBookDesignAsPdf(User user, BookDesign bookDesign) {
    String url = String.format("%s/forge/bookDesignData/%d?secret=%s", systemSettingsController.getSiteUrl(false, true), bookDesign.getId(), systemSettingsController.getSetting(SystemSettingKey.PDF_SERVICE_CALLBACK_SECRET));
    Map<String, Object> options = new HashMap<>();
    options.put("pageSize", "A4");
    options.put("imageQuality", "100");
    options.put("marginTop", "0mm");
    options.put("marginLeft", "0mm");
    options.put("marginRight", "0mm");
    options.put("marginBottom", "0mm");
    options.put("printMediaType", Boolean.TRUE);
    
    return pdfServiceClient.getURLAsPdf(url, options);
  }
  
  /* Book Templates */
  
  public BookTemplate createBookTemplate(Folder parentFolder, String title, User creator) {
    String urlName = getUniqueMaterialUrlName(creator, parentFolder, null, DigestUtils.md5Hex(String.valueOf(System.currentTimeMillis())));    
    return createBookTemplate(parentFolder, urlName, title, "", null, null, null, null, null, null, creator);
  }

  public BookTemplate createBookTemplate(Folder parentFolder, String urlName, String title, String data, 
      String styles, String fonts, String description, String pageTypes, String iconUrl, Language language, User creator) {
    Date now = new Date();
    return bookTemplateDAO.create(creator, now, creator, now, language, parentFolder, urlName, title, data, styles, fonts, 
        pageTypes, description, iconUrl, MaterialPublicity.PRIVATE);
  }

  public BookTemplate findBookTemplate(Long id) {
    return bookTemplateDAO.findById(id);
  }
  
  public List<BookTemplate> listPublicBookTemplates() {
    return bookTemplateDAO.listByPublicity(MaterialPublicity.PUBLIC);
  }

  /* Document */

  public Document createDocument(Folder parentFolder, String title, User creator) {
    String urlName = getUniqueMaterialUrlName(creator, parentFolder, null, DigestUtils.md5Hex(String.valueOf(System.currentTimeMillis())));    
    return createDocument(parentFolder, urlName, title, "", null, creator);
  }

  public Document createDocument(Folder parentFolder, String urlName, String title, String data, Language language, User creator) {
    return documentDAO.create(creator, language, parentFolder, urlName, title, data, MaterialPublicity.PRIVATE);
  }
  
  public Document findDocumentById(Long documentId) {
    return documentDAO.findById(documentId);
  }

  public Document updateDocumentData(Document document, String data, User user) {
    return documentDAO.updateData(document, user, data);
  }

  public Document updateDocumentTitle(Document document, String title, User modifier) {
    String oldUrlName = document.getUrlName();
    String newUrlName = getUniqueMaterialUrlName(document.getCreator(), document.getParentFolder(), document, title);
    
    if (!StringUtils.equals(oldUrlName, newUrlName)) {
      String oldPath = document.getPath();
      PermaLink permaLink = permaLinkDAO.findByPath(oldPath);
      if (permaLink == null) {
        permaLink = permaLinkDAO.create(document, oldPath);
      }

      materialDAO.updateUrlName(document, newUrlName, modifier);
    }
    
    return (Document) materialDAO.updateTitle(document, title, modifier);
  }

  public Document updateDocumentLanguage(Document document, Language language, User modifier) {
    return documentDAO.updateLanguage(document, modifier, language);
  }
  
  /* Document Revisions */
  
  public DocumentRevision createDocumentRevision(Document document, Long revisionNumber, Date created, boolean compressed, boolean completeVersion, byte[] revisionBytes, String checksum, String clientId) {
    return documentRevisionDAO.create(document, revisionNumber, created, compressed, completeVersion, revisionBytes, checksum, clientId);
  }

  public List<DocumentRevision> listDocumentRevisionsAfter(Document document, Long revisionNumber) {
    List<DocumentRevision> documentRevisions = documentRevisionDAO.listByDocumentAndRevisionGreaterThan(document, revisionNumber);
    Collections.sort(documentRevisions, new Comparator<DocumentRevision>() {
      @Override
      public int compare(DocumentRevision documentRevision1, DocumentRevision documentRevision2) {
        return documentRevision1.getRevision().compareTo(documentRevision2.getRevision());
      }
    });
    
    return documentRevisions;
  }

  public Long getDocumentRevision(Document document) {
    Long result = documentRevisionDAO.maxRevisionByDocument(document);
    if (result == null) {
      result = 0l;
    }
    
    return result;
  }
  
  /* Document Revision Settings */

  public MaterialRevisionSetting createDocumentRevisionSetting(MaterialRevision materialRevision, String key, String value) {
    MaterialSettingKey settingKey = materialSettingKeyDAO.findByName(key);
    if (settingKey != null) {
      return materialRevisionSettingDAO.create(materialRevision, settingKey, value);
    }
    
    return null;
  }

  public List<MaterialRevisionSetting> listDocumentRevisionSettings(DocumentRevision documentRevision) {
    return materialRevisionSettingDAO.listByMaterialRevision(documentRevision);
  }
  
  /* Document PDF */

  public TypedData printDocumentAsPdf(String contextPath, String baseUrl, User user, Document document) throws DocumentException, IOException, ParserConfigurationException, SAXException {
    ITextRenderer renderer = new ITextRenderer();
    ReplacedElementFactory replacedElementFactory = new B64ImgReplacedElementFactory();
    renderer.getSharedContext().setReplacedElementFactory(replacedElementFactory);
    
    String documentContent = document.getData();
    org.w3c.dom.Document domDocument = tidyForPdf(document.getTitle(), documentContent);
    
    try {
      NodeList imageList = XPathAPI.selectNodeList(domDocument, "//img");
      for (int i = 0, l = imageList.getLength(); i < l; i++) {
        Element imageElement = (Element) imageList.item(i);
        String src = imageElement.getAttribute("src");
        
        try {
          boolean internal = false;
          
          if (src.startsWith("http://") || src.startsWith("https://")) {
            if (src.startsWith(baseUrl)) {
              src = RequestUtils.stripCtxPath(contextPath, src.substring(baseUrl.length()));  
              internal = true;
            } else {
              internal = false;
            }
          } else {
            src = RequestUtils.stripCtxPath(contextPath, src);
            internal = true;
          }
          
          if (internal) {
            Material material = findMaterialByCompletePath(src);
            if (materialPermissionController.hasAccessPermission(user, material)) {
              if (material.getType() == MaterialType.IMAGE) {
                Image image = (Image) material;

                StringBuilder srcBuilder = new StringBuilder()
                  .append("data:")
                  .append(image.getContentType())
                  .append(";base64,")
                  .append(new String(Base64.encodeBase64(image.getData())));
                
                imageElement.setAttribute("src", srcBuilder.toString());
              }
            }
          }
          
        } catch (Exception e) {
          // If anything goes wrong we just leave this img "as is".
        }
      }
    } catch (Exception e) {
      // If anything goes wrong we just leave the document "as is".
    }
    
    ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
    renderer.setDocument(domDocument, baseUrl);
    renderer.layout();
    renderer.createPDF(pdfStream);
    pdfStream.flush();
    pdfStream.close();
    
    return new TypedData(pdfStream.toByteArray(), "application/pdf");
  }
  
  private org.w3c.dom.Document tidyForPdf(String title, String bodyContent) throws ParserConfigurationException, IOException, SAXException {
    String documentHtml = HtmlUtils.getAsHtmlText(title, bodyContent);
    String cleanedHtml = null;
    
    ByteArrayOutputStream tidyStream = new ByteArrayOutputStream();
    try {
      Tidy tidy = new Tidy();
      tidy.setInputEncoding("UTF-8");
      tidy.setOutputEncoding("UTF-8");
      tidy.setShowWarnings(true);
      tidy.setNumEntities(false);
      tidy.setXmlOut(true);
      tidy.setXHTML(true);

      cleanedHtml = HtmlUtils.printDocument(tidy.parseDOM(new StringReader(documentHtml), null));
    } catch (Exception e) {
      throw e;
    } finally {
      tidyStream.flush();
      tidyStream.close();
    }
    
    InputStream documentStream = new ByteArrayInputStream(cleanedHtml.getBytes("UTF-8"));
    try {

      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
      builderFactory.setNamespaceAware(false);
      builderFactory.setValidating(false);
      builderFactory.setFeature("http://xml.org/sax/features/namespaces", false);
      builderFactory.setFeature("http://xml.org/sax/features/validation", false);
      builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
      builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      DocumentBuilder builder = builderFactory.newDocumentBuilder();
  
      return builder.parse(documentStream);

    } finally {
      documentStream.close();
    }
  }
  
  /* Document Tags */

  public List<MaterialTag> listDocumentTags(Document document) {
    return materialTagDAO.listByMaterial(document);
  }
  
  public Document setDocumentTags(Document document, List<Tag> tags) {
    List<MaterialTag> removeTags = null;
    if (tags.size() > 0) {
      removeTags = materialTagDAO.listByMaterialAndTagsNotIn(document, tags);
    } else {
      removeTags = materialTagDAO.listByMaterial(document);
    }
    
    for (MaterialTag removeTag : removeTags) {
      materialTagDAO.delete(removeTag);
    }
    
    for (Tag tag : tags) {
      if (materialTagDAO.findByMaterialAndTag(document, tag) == null) {
        materialTagDAO.create(document, tag);
      }
    }
    
    return document;
  }

  /* Document Properties */
  
  public void setDocumentSetting(Document document, String key, String value) {
    MaterialSettingKey settingKey = materialSettingKeyDAO.findByName("document." + key);
    if (settingKey != null) {
      MaterialSetting materialSetting = materialSettingDAO.findByMaterialAndKey(document, settingKey);
      if (materialSetting != null) {
        materialSettingDAO.updateValue(materialSetting, value);
      } else {
        materialSettingDAO.create(document, settingKey, value);
      }
    }
  }

  public List<MaterialSetting> listDocumentSettings(Document document) {
    return materialSettingDAO.listByMaterial(document);
  }
  
  /* Folder */

  public Folder createFolder(Folder parentFolder, String title, User creator) {
    Date now = new Date();
    String urlName = getUniqueMaterialUrlName(creator, parentFolder, null, title);
    return folderDAO.create(creator, now, creator, now, null, parentFolder, urlName, title, MaterialPublicity.PRIVATE);
  }
  
  public Folder findFolderById(Long folderId) {
    return folderDAO.findById(folderId);
  }
  
  /* GoogleDocument */

  public GoogleDocument createGoogleDocument(User creator, Language language, Folder parentFolder, String title, String documentId, String mimeType, MaterialPublicity publicity) {
    String urlName = getUniqueMaterialUrlName(creator, parentFolder, null, title);
    return googleDocumentDAO.create(creator, language, parentFolder, urlName, title, documentId, mimeType, publicity);
  }
  
  public GoogleDocument findGoogleDocumentById(Long googleDocumentId) {
    return googleDocumentDAO.findById(googleDocumentId);
  }

  public GoogleDocument findGoogleDocumentByCreatorAndDocumentId(User creator, String documentId) {
    return googleDocumentDAO.findByCreatorAndDocumentId(creator, documentId);
  }
  
  public String getGoogleDocumentEditLink(GoogleDocument googleDocument) throws IOException, GeneralSecurityException {
    Drive systemDrive = driveManager.getDrive(systemGoogleDriveCredentials.getSystemCredential());
    File file = driveManager.getFile(systemDrive, googleDocument.getDocumentId());
    return file.getAlternateLink();
  }
  
  public TypedData getGoogleDocumentData(GoogleDocument googleDocument) throws MalformedURLException, IOException, GeneralSecurityException {
    Drive systemDrive = driveManager.getDrive(systemGoogleDriveCredentials.getSystemCredential());
    File file = driveManager.getFile(systemDrive, googleDocument.getDocumentId());
    TypedData typedData = null;
    String mimeType = googleDocument.getMimeType();
    
    if (GoogleDriveType.DOCUMENT.getMimeType().equals(mimeType)) {
      typedData = driveManager.exportFile(systemDrive, file, "text/html");
    } else if (GoogleDriveType.DRAWING.getMimeType().equals(mimeType)) {
      typedData = driveManager.exportFile(systemDrive, file, "image/png");
    } else if (GoogleDriveType.PRESENTATION.getMimeType().equals(mimeType)) {
      typedData = driveManager.exportFile(systemDrive, file, "application/pdf");
    } else if (GoogleDriveType.SPREADSHEET.getMimeType().equals(mimeType)) {
      typedData = driveManager.exportSpreadsheet(systemDrive, file);
    } else {
      typedData = driveManager.downloadFile(systemDrive, file);
    }
    
    return typedData;
  }

  /* Images */
  
  public Image createImage(Folder parentFolder, User loggedUser, byte[] data, String contentType, String title) {
    Date now = new Date();
    String urlName = getUniqueMaterialUrlName(loggedUser, parentFolder, null, title);
    return imageDAO.create(loggedUser, now, loggedUser, now, null, parentFolder, urlName, title, data, contentType, MaterialPublicity.PRIVATE);
  }

  public Image findImageById(Long id) {
    return imageDAO.findById(id);
  }

  public void updateImageTitle(Image image, String title, User modifier) {
    materialDAO.updateTitle(image, title, modifier);
  }
  
  public Image updateImageContent(Image image, String contentType, byte[] data, User modifier) {
    return imageDAO.updateData(imageDAO.updateContentType(image, modifier, contentType), modifier, data);
  }
  
  /* Image Revisions */
  
  public ImageRevision createImageRevision(Image image, Long revisionNumber, Date created, boolean compressed, boolean completeVersion, byte[] revisionBytes, String checksum, String clientId) {
    return imageRevisionDAO.create(image, revisionNumber, created, compressed, completeVersion, revisionBytes, checksum, clientId);
  }

  public List<ImageRevision> listImageRevisionsAfter(Image image, Long revisionNumber) {
    List<ImageRevision> imageRevisions = imageRevisionDAO.listByImageAndRevisionGreaterThan(image, revisionNumber);
    Collections.sort(imageRevisions, new Comparator<ImageRevision>() {
      @Override
      public int compare(ImageRevision revision1, ImageRevision revision2) {
        return revision1.getRevision().compareTo(revision2.getRevision());
      }
    });
    
    return imageRevisions;
  }
  
  public Long getImageRevision(Image image) {
    Long result = imageRevisionDAO.maxRevisionByImage(image);
    if (result == null) {
      result = 0l;
    }
    
    return result;
  }

  /* Image Properties */
  
  public void setImageSetting(Image image, String key, String value) {
    MaterialSettingKey settingKey = materialSettingKeyDAO.findByName("image." + key);
    if (settingKey != null) {
      MaterialSetting materialSetting = materialSettingDAO.findByMaterialAndKey(image, settingKey);
      if (materialSetting != null) {
        materialSettingDAO.updateValue(materialSetting, value);
      } else {
        materialSettingDAO.create(image, settingKey, value);
      }
    }
  }

  public List<MaterialSetting> listImageSettings(Image image) {
    return materialSettingDAO.listByMaterial(image);
  }
  
  /* Image Tags */

  public List<MaterialTag> listImageTags(Image image) {
    return materialTagDAO.listByMaterial(image);
  }
  
  public Image setImageTags(Image image, List<Tag> tags) {
    List<MaterialTag> removeTags = null;
    if (tags.size() > 0) {
      removeTags = materialTagDAO.listByMaterialAndTagsNotIn(image, tags);
    } else {
      removeTags = materialTagDAO.listByMaterial(image);
    }
    
    for (MaterialTag removeTag : removeTags) {
      materialTagDAO.delete(removeTag);
    }
    
    for (Tag tag : tags) {
      if (materialTagDAO.findByMaterialAndTag(image, tag) == null) {
        materialTagDAO.create(image, tag);
      }
    }
    
    return image;
  }

  /* Image Revision Settings */

  public MaterialRevisionSetting createImageRevisionSetting(MaterialRevision materialRevision, String key, String value) {
    MaterialSettingKey settingKey = materialSettingKeyDAO.findByName("image." + key);
    if (settingKey != null) {
      return materialRevisionSettingDAO.create(materialRevision, settingKey, value);
    }
    
    return null;
  }

  public List<MaterialRevisionSetting> listImageRevisionSettings(ImageRevision imageRevision) {
    return materialRevisionSettingDAO.listByMaterialRevision(imageRevision);
  }
  
  /* Pdf */

  public Pdf createPdf(User creator, Language language, Folder parentFolder, String urlName, String title, byte[] data) {
    Date now = new Date();
    return pdfDAO.create(creator, now, creator, now, language, parentFolder, urlName, title, data, MaterialPublicity.PRIVATE);
  }

  public Pdf findPdfById(Long pdfId) {
    return pdfDAO.findById(pdfId);
  }
  
  /* VectorImage */
  
  public VectorImage createVectorImage(Language language, Folder parentFolder, String title, String data, User creator) {
    String urlName = getUniqueMaterialUrlName(creator, parentFolder, null, DigestUtils.md5Hex(String.valueOf(System.currentTimeMillis())));    
    return vectorImageDAO.create(creator, language, parentFolder, urlName, title, data, MaterialPublicity.PRIVATE);
  }
  
  public VectorImage findVectorImageById(Long documentId) {
    return vectorImageDAO.findById(documentId);
  }

  public VectorImage updateVectorImageData(VectorImage vectorImage, String data, User modifier) {
    return vectorImageDAO.updateData(vectorImage, modifier, data);
  }

  public VectorImage updateVectorImageTitle(VectorImage vectorImage, String title, User modifier) {
    String oldUrlName = vectorImage.getUrlName();
    String newUrlName = getUniqueMaterialUrlName(vectorImage.getCreator(), vectorImage.getParentFolder(), vectorImage, title);
    
    if (!StringUtils.equals(oldUrlName, newUrlName)) {
      String oldPath = vectorImage.getPath();
      PermaLink permaLink = permaLinkDAO.findByPath(oldPath);
      if (permaLink == null) {
        permaLink = permaLinkDAO.create(vectorImage, oldPath);
      }

      materialDAO.updateUrlName(vectorImage, newUrlName, modifier);
    }
    
    return (VectorImage) materialDAO.updateTitle(vectorImage, title, modifier);
  }

  /* Dropbox */
  
  public Token getDropboxToken(User user) {
    List<UserIdentifier> dropboxIdentifiers = userIdentifierDAO.listByAuthSourceAndUser(AuthSource.DROPBOX, user);
    for (UserIdentifier dropboxIdentifier : dropboxIdentifiers) {
      UserToken dropboxToken = userTokenDAO.findByUserIdentifier(dropboxIdentifier);
      if (dropboxToken != null) {
        return new Token(dropboxToken.getToken(), dropboxToken.getSecret());
      }
    }

    return null;
  }

  public DropboxRootFolder getDropboxRootFolder(User user) {
    return dropboxRootFolderDAO.findByUser(user);
  }

  public void synchronizeDropboxFolder(DropboxRootFolder dropboxRootFolder) {
    User user = dropboxRootFolder.getCreator();
    Token dropboxToken = getDropboxToken(user);

    if (dropboxToken != null && dropboxRootFolder != null) {
      OAuthService service = dropboxAuthenticationStrategy.getOAuthService();

      Boolean hasMore = true;

      while (hasMore) {
        try {
          Map<String, String> parameters = new HashMap<String, String>();

          if (StringUtils.isNotBlank(dropboxRootFolder.getDeltaCursor()))
            parameters.put("cursor", dropboxRootFolder.getDeltaCursor());

          Map<String, Object> deltaResponse = new ObjectMapper().readValue(OAuthUtils.doPostRequest(service, dropboxToken, "https://api.dropbox.com/1/delta", parameters).getBody(), new TypeReference<Map<String, Object>>(){});
          hasMore = (Boolean) deltaResponse.get("has_more");
          if (hasMore == null) {
            hasMore = false;
          }
          
          Boolean reset = (Boolean) deltaResponse.get("reset");
          String cursor = (String) deltaResponse.get("cursor");
          Date now = new Date();

          if (reset) {
            List<Material> files = materialDAO.listByParentFolder(dropboxRootFolder);
            for (Material file : files) {
              deleteMaterial(file, user);
            }
          }

          @SuppressWarnings("unchecked")
          List<Object> entries = (List<Object>) deltaResponse.get("entries");
          for (int i = 0, l = entries.size(); i < l; i++) {
            @SuppressWarnings("unchecked")
            List<Object> entry = (List<Object>) entries.get(i);
            String entryPath = (String) entry.get(0);
            @SuppressWarnings("unchecked")
            Map<String, Object> metaData = (Map<String, Object>) entry.get(1);
            if (metaData == null) {
              DropboxFile dropboxFile = dropboxFileDAO.findByDropboxPath(entryPath);
              if (dropboxFile != null) {
                deleteMaterial(dropboxFile, user);
                logger.info("Dropbox file " + entryPath + " removed.");
              } else {
                DropboxFolder dropboxFolder = dropboxFolderDAO.findByCreatorAndDropboxPath(user, entryPath);
                if (dropboxFolder != null) {
                  deleteMaterial(dropboxFolder, user);
                } else {
                  logger.warning("Could not find removed Dropbox file " + entryPath);
                }
              }
            } else {
              // String rev = metaData.getString("rev");
              // Boolean thumbExists = metaData.getBoolean("thumb_exists");
              // Long revision = metaData.getLong("revision");
              // Long bytes = metaData.getLong("bytes");
              // String modified = metaData.getString("modified");
              // String root = metaData.getString("root");
              // String icon = metaData.getString("icon");
              // String size = metaData.getString("size");
              Boolean isDir = (Boolean) metaData.get("is_dir");
              String path = (String) metaData.get("path");
              String[] parents = path.split("/");

              Folder parentFolder = null;
              if (parents.length == 2) {
                parentFolder = dropboxRootFolder;
              } else {
                // If the new entry includes parent folders that don't yet exist in
                // Forge & Illusion, we need to create those parent folders before continuing.
                parentFolder = dropboxRootFolder;
                for (int parentIndex = 1, parentsLength = parents.length - 1; parentIndex < parentsLength; parentIndex++) {
                  String parent = parents[parentIndex];
                  Folder foundFolder = (Folder) materialDAO.findByParentFolderAndUrlName(parentFolder, parent);
                  if (foundFolder != null) {
                    parentFolder = foundFolder;
                  } else {
                    String urlName = getUniqueMaterialUrlName(user, parentFolder, null, parent);
                    parentFolder = dropboxFolderDAO.create(user, now, user, now, parentFolder, urlName, parent, MaterialPublicity.PRIVATE, entryPath);
                    logger.info("Created new dropbox folder: " + parent);
                  }
                }
              }
              
              int slashPos = path.lastIndexOf("/");
              String title = slashPos > -1 ? path.substring(slashPos + 1) : path;
              String urlName = getUniqueMaterialUrlName(user, parentFolder, null, title);

              if (isDir) {
                dropboxFolderDAO.create(user, now, user, now, parentFolder, urlName, title, MaterialPublicity.PRIVATE, entryPath);
                logger.info("Created new dropbox folder: " + title);
              } else {
                String mimeType = (String) metaData.get("mime_type");
                // String clientMtime = metaData.optString("client_mtime");
                dropboxFileDAO.create(user, null, parentFolder, urlName, title, MaterialPublicity.PRIVATE, entryPath, mimeType);
                logger.info("Created new dropbox file: " + title);
              }
            }
          }

          dropboxRootFolderDAO.updateDeltaCursor(dropboxRootFolder, cursor, user);
          dropboxRootFolderDAO.updateLastSynchronized(dropboxRootFolder, new Date(), user);
        } catch (IOException e) {
          logger.log(Level.SEVERE, "Failed to read Dropbox Delta JSON", e);
        }
      }
    }
  }

  public DropboxFile getDropboxFile(String path) {
    DropboxFile dropboxFile = dropboxFileDAO.findByDropboxPath(path);
    return dropboxFile;
  }

  public Response getDropboxFileContent(User user, DropboxFile dropboxFile) throws IOException {
    Token dropboxToken = getDropboxToken(user);
    if (dropboxToken == null) {
      throw new UnauthorizedException();
    }
    
    OAuthService service = dropboxAuthenticationStrategy.getOAuthService();

    String root = systemSettingsController.getSetting(SystemSettingKey.DROPBOX_ROOT);
    String url = "https://api-content.dropbox.com/1/files/" + root + dropboxFile.getDropboxPath();

    return OAuthUtils.doGetRequest(service, dropboxToken, url);
  }
  
  public MimeType parseMimeType(String mimeType) throws MimeTypeParseException {
    MimeType type = new MimeType(mimeType);
    return type;
  }

  public boolean isDownloadableType(MaterialType materialType) {
    switch (materialType) {
      case DROPBOX_FOLDER:
      case DROPBOX_ROOT_FOLDER:
      case FOLDER:
      case ILLUSION_GROUP_FOLDER:
      case ILLUSION_FOLDER:
        return false;
      default:
        return true;
    }
  }

  public boolean isEditableType(MaterialType materialType) {
    switch (materialType) {
    case DOCUMENT:
    case FOLDER:
    case VECTOR_IMAGE:
      return true;
    default:
      return false;
    }
  }

  public boolean isMovableType(MaterialType type) {
    switch (type) {
    case DROPBOX_ROOT_FOLDER:
    case DROPBOX_FILE:
    case DROPBOX_FOLDER:
    case ILLUSION_FOLDER:
    case ILLUSION_GROUP_FOLDER:
    case ILLUSION_GROUP_DOCUMENT:
      return false;
    default:
      break;
    }

    return true;
  }

  public boolean isShareableType(MaterialType type) {
    switch (type) {
    case DROPBOX_ROOT_FOLDER:
    case DROPBOX_FILE:
    case DROPBOX_FOLDER:
      return false;
    default:
      break;
    }

    return true;
  }

  public boolean isPrintableAsPdfType(MaterialType type) {
    switch (type) {
    case DOCUMENT:
      return true;
    default:
      break;
    }

    return false;
  }

  public boolean isDeletableType(MaterialType type) {
    switch (type) {
    case DROPBOX_FILE:
    case DROPBOX_FOLDER:
    case ILLUSION_FOLDER:
    case ILLUSION_GROUP_FOLDER:
    case ILLUSION_GROUP_DOCUMENT:
      return false;
    default:
      break;
    }

    return true;
  }

  public Material findMaterialById(Long materialId) {
    return materialDAO.findById(materialId);
  }

  public Material findMaterialByPath(Folder rootFolder, String path) {
    int lastSlash = path.lastIndexOf('/');
    String urlName;
    Folder parentFolder;

    if (lastSlash != -1) {
      if ((lastSlash + 1) >= path.length()) {
        return null;
      }
      
      path = path.substring(0, lastSlash);
      urlName = path.substring(lastSlash + 1);
      parentFolder = (Folder) materialDAO.findByParentFolderAndUrlName(rootFolder, path);
    } else {
      urlName = path;
      parentFolder = rootFolder;
    }

    return materialDAO.findByParentFolderAndUrlName(parentFolder, urlName);
  }

  public Material findMaterialByPermaLink(String path) {
    PermaLink permaLink = permaLinkDAO.findByPath(path);
    if (permaLink != null)
      return permaLink.getMaterial();

    return null;
  }

  private List<SearchResult<Material>> searchMaterialByTitleAndContent(User user, String[] criterias, int maxResults) throws ParseException {
    List<SearchResult<Material>> result = new ArrayList<>();

    // find by title and content
    StringBuilder queryStringBuilder = new StringBuilder();
    queryStringBuilder.append("+(");
    for (int i = 0, l = criterias.length; i < l; i++) {
      String criteria = QueryParser.escape(criterias[i]);

      queryStringBuilder.append("title:");
      queryStringBuilder.append(criteria);
      queryStringBuilder.append("* ");

      queryStringBuilder.append("contentPlain:");
      queryStringBuilder.append(criteria);
      queryStringBuilder.append("* ");

      if (i < l - 1)
        queryStringBuilder.append(' ');
    }

    queryStringBuilder.append(")");

    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
    QueryParser parser = new QueryParser(Version.LUCENE_35, "", analyzer);

    Query luceneQuery = parser.parse(queryStringBuilder.toString());
    FullTextQuery query = (FullTextQuery) fullTextEntityManager.createFullTextQuery(luceneQuery, 
        Document.class, File.class, Folder.class, GoogleDocument.class, Image.class, Pdf.class, VectorImage.class);
    query.setProjection(FullTextQuery.SCORE, FullTextQuery.THIS);
    query.setMaxResults(maxResults);
    
    @SuppressWarnings("unchecked")
    List<Object[]> resultRows = query.getResultList();

    for (Object[] resultRow : resultRows) {
      Float score = (Float) resultRow[0];
      Material material = (Material) resultRow[1];
      if (material != null) {
        if (materialPermissionController.isPublic(user, material) || materialPermissionController.hasAccessPermission(user, material)) {
          result.add(new SearchResult<Material>(material, material.getTitle(), material.getPath(), material.getTitle(), null, score));
        }
      }
    }

    return result;
  }

  private List<SearchResult<Material>> searchMaterialByTags(User user, String[] criterias, int maxResults) throws ParseException {
    List<SearchResult<Material>> result = new ArrayList<>();

    // find by title and content
    StringBuilder queryStringBuilder = new StringBuilder();
    queryStringBuilder.append("+(");
    for (int i = 0, l = criterias.length; i < l; i++) {
      String criteria = QueryParser.escape(criterias[i]);

      queryStringBuilder.append("tag.text:");
      queryStringBuilder.append(criteria);
      queryStringBuilder.append("* ");

      if (i < l - 1)
        queryStringBuilder.append(' ');
    }

    queryStringBuilder.append(")");

    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
    QueryParser parser = new QueryParser(Version.LUCENE_35, "", analyzer);

    Query luceneQuery = parser.parse(queryStringBuilder.toString());
    FullTextQuery query = (FullTextQuery) fullTextEntityManager.createFullTextQuery(luceneQuery, MaterialTag.class);
    query.setProjection(FullTextQuery.SCORE, FullTextQuery.THIS);
    query.setMaxResults(maxResults);
    @SuppressWarnings("unchecked")
    List<Object[]> resultRows = query.getResultList();

    for (Object[] resultRow : resultRows) {
      Float score = (Float) resultRow[0];
      MaterialTag materialTag = (MaterialTag) resultRow[1];
      Material material = materialTag.getMaterial();
      if (material != null) {
        if (materialPermissionController.isPublic(user, material) || materialPermissionController.hasAccessPermission(user, material)) {
          result.add(new SearchResult<Material>(material, material.getTitle(), material.getPath(), material.getTitle(), null, score));
        }
      }
    }

    return result;
  }

  public List<SearchResult<Material>> searchMaterials(User user, String text, int maxResults) throws ParseException {
    String[] criterias = text.replace(",", " ").replaceAll("\\s+", " ").split(" ");
    List<SearchResult<Material>> results = searchMaterialByTitleAndContent(user, criterias, maxResults);
    addSearchResults(results, searchMaterialByTags(user, criterias, maxResults));

    Collections.sort(results, new SearchResultScoreComparator<Material>());

    while (results.size() > maxResults) {
      results.remove(results.size() - 1);
    }
    
    return Collections.unmodifiableList(results);
  }
  
  private void addSearchResults(List<SearchResult<Material>> results, List<SearchResult<Material>> items) {
    List<Long> existingIds = new ArrayList<>();
    for (SearchResult<Material> result : results) {
      existingIds.add(result.getEntity().getId());
    }
    
    for (SearchResult<Material> item : items) {
      if (!existingIds.contains(item.getEntity().getId())) {
        results.add(item);
      }
    }
  }

  public Material updateMaterialPublicity(Material material, MaterialPublicity publicity, User modifier) {
    return materialDAO.updatePublicity(material, publicity, modifier);
  }

  public StarredMaterial starMaterial(Material material, User user) {
    StarredMaterial starredMaterial = starredMaterialDAO.findByMaterialAndUser(material, user);
    Date now = new Date();

    if (starredMaterial == null)
      starredMaterial = starredMaterialDAO.create(material, user, now);
    else
      starredMaterialDAO.updateCreated(starredMaterial, now);

    return starredMaterial;
  }

  public void unstarMaterial(Material material, User user) {
    StarredMaterial starredMaterial = starredMaterialDAO.findByMaterialAndUser(material, user);
    if (starredMaterial != null)
      starredMaterialDAO.delete(starredMaterial);
  }

  public List<Material> listStarredMaterialsByUser(User user, Integer firstResult, Integer maxResults) {
    List<Material> materials = new ArrayList<Material>();

    List<StarredMaterial> starredMaterials = starredMaterialDAO.listByUserSortByCreated(user, firstResult, maxResults);
    for (StarredMaterial starredMaterial : starredMaterials) {
      materials.add(starredMaterial.getMaterial());
    }

    return materials;
  }

  public List<Material> listStarredMaterialsByUser(User user) {
    List<Material> materials = new ArrayList<Material>();

    List<StarredMaterial> starredMaterials = starredMaterialDAO.listByUserSortByCreated(user);
    for (StarredMaterial starredMaterial : starredMaterials) {
      materials.add(starredMaterial.getMaterial());
    }

    return materials;
  }

  public List<Material> listViewedMaterialsByUser(User user, Integer firstResult, Integer maxResults) {
    List<Material> materials = new ArrayList<Material>();

    List<MaterialView> viewedMaterials = materialViewDAO.listByUserSortByViewed(user, firstResult, maxResults);
    for (MaterialView viewedMaterial : viewedMaterials) {
      materials.add(viewedMaterial.getMaterial());
    }

    return materials;
  }
  
  public void markMaterialView(Material material, User user) {
    MaterialView materialView = materialViewDAO.findByMaterialAndUser(material, user);
    if (materialView == null) {
      materialViewDAO.create(material, user, 1, new Date());
    } else {
      materialViewDAO.updateCount(materialView, materialView.getCount() + 1); 
      materialViewDAO.updateViewed(materialView, new Date());
    }
  }

  public List<Material> listModifiedMaterialsByUser(User user, Integer firstResult, Integer maxResults) {
    return materialDAO.listByModifierExcludingTypesSortByModified(user, Arrays.asList(new MaterialType[] { MaterialType.FOLDER }), firstResult, maxResults);
  }
  
  public Material findRandomPublicMaterial() {
    List<Material> materials = materialDAO.listRandomMaterialsByPublicity(MaterialPublicity.PUBLIC, 0, 1);
    if (!materials.isEmpty()) {
      return materials.get(0);
    }
    
    return null;
  }

  public List<Material> listMaterialsByFolder(User user, Folder folder) {
    if (folder != null) {
      return materialDAO.listByParentFolder(folder);
    } else {
      return materialDAO.listByParentFolderIsNullAndShared(user);
    }
  }
  
  public List<Material> listMaterialsByFolderAndTypes(User user, Folder folder, Collection<MaterialType> types) {
    if (folder == null) {
      return materialDAO.listByFolderIsNullAndSharedAndTypes(user, types);
    } else {
      return materialDAO.listByParentFolderAndTypes(folder, types);
    }
  }
  
  public List<Material> listPublicMaterialsByCreatorAndTypes(User creator, List<MaterialType> types) {
    return materialDAO.listByPublicityAndCreatorAndAndTypes(MaterialPublicity.PUBLIC, creator, types);
  }

  public List<Material> listPublicMaterialsByTags(List<Tag> tags) {
    return materialTagDAO.listMaterialsByPublicityAndTags(MaterialPublicity.PUBLIC, tags);
  }
  
  public List<MaterialTag> listMaterialTags(Material material) {
    return materialTagDAO.listByMaterial(material); 
  }
  
  public long countMaterialsByTag(Tag tag) {
    return materialTagDAO.countByTag(tag);
  }
  
  public long countPublicMaterialsByTag(Tag tag) {
    return materialTagDAO.countByTagAndMaterialPublicity(tag, MaterialPublicity.PUBLIC);
  }
  
  public List<TagWithCount> listPublicMaterialTagsWithCounts(int maxTags) {
    return materialTagDAO.listWithCountsByMaterialPublicityOrderByCountAndName(MaterialPublicity.PUBLIC, null, maxTags);
  }

  public List<Material> listLatestPublicMaterials(int maxResults) {
    return materialDAO.listByPublicityOrderByModified(MaterialPublicity.PUBLIC, 0, maxResults);
  }

  public List<Material> listMostPopuralMaterials(int maxResults) {
    return materialDAO.listByPublicityOrderByViews(MaterialPublicity.PUBLIC, 0, maxResults);
  }
  
  public Material updateMaterialTags(Material material, List<Tag> tags) {
    List<MaterialTag> existingTags = listMaterialTags(material);
    List<Tag> addTags = new ArrayList<>(tags);
    
    Map<Long, MaterialTag> existingTagMap = new HashMap<>();
    for (MaterialTag existingTag : existingTags) {
      existingTagMap.put(existingTag.getTag().getId(), existingTag);
    }
    
    for (int i = addTags.size() - 1; i >= 0; i--) {
      Tag addTag = addTags.get(i);
      
      if (existingTagMap.containsKey(addTag.getId())) {
        addTags.remove(i);
      } 
      
      existingTagMap.remove(addTag.getId());
    }
    
    for (MaterialTag removeTag : existingTagMap.values()) {
      materialTagDAO.delete(removeTag);
    }
    
    for (Tag tag : addTags) {
      materialTagDAO.create(material, tag);
    }
    
    return material;
  }
  
  public List<String> getMaterialTags(Material material) {
    List<MaterialTag> materialTags = listMaterialTags(material);
    
    List<String> result = new ArrayList<>(materialTags.size());
    for (MaterialTag materialTag : materialTags) {
      result.add(materialTag.getTag().getText());
    }
    
    return result;
  }

  public Material setMaterialTags(Material material, List<String> tagTexts) {
    List<Tag> materialTags = new ArrayList<>();
    
    for (String tagText : tagTexts) {
      Tag tag = tagDAO.findByText(tagText);
      if (tag == null) {
        tag = tagDAO.create(tagText);
      }
      
      materialTags.add(tag);
    }
    
    return updateMaterialTags(material, materialTags);
  }

  public Material updateMaterialTitle(Material material, String title, User modifier) {
    return materialDAO.updateTitle(material, title, modifier);
  }
  
  public Material updateMaterialDescription(Material material, String description) {
    return materialDAO.updateDescription(material, description);
  }

  public Material updateMaterialLicense(Material material, String license) {
    return materialDAO.updateLicense(material, license);
  }

  public Material updateMaterialLanguage(Material material, Language language) {
    return materialDAO.updateLanguage(material, language);
  }
  
  public String getUniqueMaterialUrlName(User owner, Folder parentFolder, Material material, String title) {
    String urlName = RequestUtils.createUrlName(title);
    if (material != null && urlName.equals(material.getUrlName()))
      return urlName;

    String baseName = urlName;
    Material urlMaterial = null;
    int i = 0;
    do {
      if (parentFolder == null) {
        urlMaterial = materialDAO.findByRootFolderAndUrlName(owner, urlName);
      } else {
        urlMaterial = materialDAO.findByParentFolderAndUrlName(parentFolder, urlName);
      }

      if (urlMaterial == null) {
        if (material != null) {
          String path = null;

          if (material.getParentFolder() != null)
            path = material.getParentFolder().getPath() + '/' + urlName;
          else {
            path = material.getCreator().getId().toString() + '/' + urlName;
          }

          PermaLink permaLink = permaLinkDAO.findByPath(path);
          if (permaLink != null) {
            if (permaLink.getMaterial().getId().equals(material.getId())) {
              return urlName;
            }
          } else {
            return urlName;
          }
        } else {
          return urlName;
        }
      }

      if (material != null) {
        if (urlMaterial != null && urlMaterial.getId().equals(material.getId()))
          return urlName;
      }

      urlName = baseName + '_' + (++i);
    } while (true);
  }

  public String getForgeMaterialViewerName(Material material) {
    MaterialType type = material.getType();
    
    switch (type) {
      case DROPBOX_FILE:
        return "binary";
      case DROPBOX_FOLDER:
      case DROPBOX_ROOT_FOLDER:
        return "folders";
      case GOOGLE_DOCUMENT:
        GoogleDocument googleDocument = (GoogleDocument) material;
        String mimeType = googleDocument.getMimeType();
        GoogleDriveType googleDriveType = GoogleDriveType.findByMimeType(mimeType);
        if (googleDriveType != null) {
          switch (googleDriveType) {
            case DOCUMENT:
            case SPREADSHEET:
              return "google-drive";
            default:
            break;
          }
        }
        
        return "binary";
      case DOCUMENT:
        return "documents";
      case BINARY:
      case FILE:
      case PDF:
        return "binary";
      case FOLDER:
        return "folders";
      case IMAGE:
        return "images";
      case VECTOR_IMAGE:
        return "vectorimages";
      case ILLUSION_FOLDER:
      case ILLUSION_GROUP_FOLDER:
        return "folders";
      case ILLUSION_GROUP_DOCUMENT: 
        return "documents";
      case CHARACTER_SHEET:
        return "character-sheets";
      case BOOK_DESIGN:
        return "book-designs";
      case BOOK_TEMPLATE:
        return "book-templates";
    }
  
    return "todo";
  }
  
  public String getMaterialIcon(MaterialType type) {
    switch (type) {
      case DROPBOX_FILE:
        return "file";
      case DROPBOX_FOLDER:
        return "folder";
      case DROPBOX_ROOT_FOLDER:
        return "dropbox";
      case GOOGLE_DOCUMENT:
        return "google-drive";
      case DOCUMENT:
        return "document";
      case BINARY:
        return "file";
      case FILE:
        return "file";
      case PDF:
        return "pdf";
      case FOLDER:
        return "folder";
      case IMAGE:
        return "image";
      case VECTOR_IMAGE:
        return "vector-image";
      case ILLUSION_GROUP_FOLDER:
        return "illusion-group-folder";
      case ILLUSION_FOLDER:
        return "illusion-group-folder";
      case ILLUSION_GROUP_DOCUMENT:
        return "document";
      case CHARACTER_SHEET:
        return "character-sheet";
      case BOOK_DESIGN:
        return "book-design";
      case BOOK_TEMPLATE:
        return "book-template";
    }

    return null;
  }
  
  public String getForgeMaterialViewerUrl(Material material) {
    return new StringBuilder()
      .append("/forge/")
      .append(getForgeMaterialViewerName(material))
      .append('/')
      .append(material.getPath())
      .toString();
  }
  
  public void moveMaterial(Material material, Folder parentFolder, User modifyingUser) {
    updateMaterialPermaLinks(material);
    materialDAO.updateParentFolder(material, parentFolder, modifyingUser);
  }
  
  private void updateMaterialPermaLinks(Material material) {
    if (material instanceof Folder) {
      List<Material> children = materialDAO.listByParentFolder((Folder) material);
      for (Material child : children) {
        updateMaterialPermaLinks(child);
      }
    }
    
    String oldPath = material.getPath();
    PermaLink permaLink = permaLinkDAO.findByPath(oldPath);
    if (permaLink == null) {
      permaLink = permaLinkDAO.create(material, oldPath);
    }
  }

  public void deleteMaterial(Material material, User deletingUser) {

    switch (material.getType()) {
    case FOLDER:
    case DROPBOX_ROOT_FOLDER:
    case DROPBOX_FOLDER:
      /**
       * When removing a Dropbox folder, all child resources have to be removed also
       */
      recursiveDelete(folderDAO.findById(material.getId()), deletingUser);
      break;
    case ILLUSION_GROUP_DOCUMENT:
    case DOCUMENT:
      Document document = (Document) material;
      List<DocumentRevision> documentRevisions = documentRevisionDAO.listByDocument(document);
      for (DocumentRevision documentRevision : documentRevisions) {
        documentRevisionDAO.delete(documentRevision);
      }
      
      List<CoOpsSession> openSessions = coOpsSessionController.listSessionsByClosed(Boolean.FALSE);
      for (CoOpsSession openSession : openSessions) {
        coOpsSessionController.closeSession(openSession, true);        
      }
      
      List<CoOpsSession> sessions = coOpsSessionController.listSessionsByClosed(Boolean.TRUE);
      for (CoOpsSession session : sessions) {
        coOpsSessionController.deleteSession(session);
      }
      
      break;
    case VECTOR_IMAGE:
      VectorImage vectorImage = (VectorImage) material;
      List<VectorImageRevision> vectorImageRevisions = vectorImageRevisionDAO.listByVectorImage(vectorImage);
      for (VectorImageRevision vectorImageRevision : vectorImageRevisions) {
        vectorImageRevisionDAO.delete(vectorImageRevision);
      }
      break;
    default:
      break;
    }

    List<MaterialTag> tags = materialTagDAO.listByMaterial(material);
    for (MaterialTag tag : tags) {
      materialTagDAO.delete(tag);
    }

    List<StarredMaterial> starredMaterials = starredMaterialDAO.listByMaterial(material);
    for (StarredMaterial starredMaterial : starredMaterials) {
      starredMaterialDAO.delete(starredMaterial);
    }

    List<PermaLink> permaLinks = permaLinkDAO.listByMaterial(material);
    for (PermaLink permaLink : permaLinks) {
      permaLinkDAO.delete(permaLink);
    }
    
    List<MaterialShareUser> materialShareUsers = materialShareUserDAO.listByMaterial(material);
    for (MaterialShareUser materialShareUser : materialShareUsers) {
      materialShareUserDAO.delete(materialShareUser);
    }
    
    List<MaterialShareGroup> materialShareGroups = materialShareGroupDAO.listByMaterial(material);
    for (MaterialShareGroup materialShareGroup : materialShareGroups) {
      materialShareGroupDAO.delete(materialShareGroup);
    }

    List<MaterialThumbnail> thumbnails = materialThumbnailDAO.listByMaterial(material);
    for (MaterialThumbnail thumbnail : thumbnails) {
      materialThumbnailDAO.delete(thumbnail);
    }

    List<MaterialView> materialViews = materialViewDAO.listByMaterial(material);
    for (MaterialView materialView : materialViews) {
      materialViewDAO.delete(materialView);
    }
    
    for (IllusionEventMaterialParticipantSetting setting : illusionEventMaterialParticipantSettingDAO.listByMaterial(material)) {
      illusionEventMaterialParticipantSettingDAO.delete(setting);;
    }

    materialDAO.delete(material);
  }

  public boolean isStarred(User user, Material material) {
    StarredMaterial starredMaterial = starredMaterialDAO.findByMaterialAndUser(material, user);
    return starredMaterial != null;
  }

  /**
   * Returns material size in bytes.
   * 
   * @param material
   *          material
   * @return material size in bytes
   */
  @LoggedIn
  public long getUserMaterialsTotalSize(User user) {
    // Count of materials multiplied by default material size as a base value
    long materialTotalSize = materialDAO.countByCreator(user) * DEFAULT_MATERIAL_SIZE;

    // Documents,
    long documensTotalSize = documentDAO.lengthDataByCreator(user);
    // VectorImages,
    long vectorImagesTotalSize = vectorImageDAO.lengthDataByCreator(user).longValue();
    // and Binaries include significant amount of data besides default size
    // so we calculate them separately
    long binariesTotalSize = binaryDAO.lengthDataByCreator(user);

    return materialTotalSize + documensTotalSize + vectorImagesTotalSize + binariesTotalSize;
  }

  public long getUserQuota() {
    return DEFAULT_QUOTA;
  }

  public MaterialThumbnail getImageThumbnail(fi.foyt.fni.persistence.model.materials.Image image, ImageSize size) throws IOException {
    MaterialThumbnail materialThumbnail = materialThumbnailDAO.findByMaterialAndSize(image, size);
    if (materialThumbnail == null) {

      TypedData originalData = new TypedData(image.getData(), image.getContentType());
      if (size == ImageSize.ORIGINAL) {
        materialThumbnail = materialThumbnailDAO.create(image, size, originalData.getData(), originalData.getContentType());
      } else {
        TypedData resizedImage = fi.foyt.fni.utils.images.ImageUtils.resizeImage(originalData, size.getWidth(), size.getHeight(), null);
        materialThumbnail = materialThumbnailDAO.create(image, size, resizedImage.getData(), resizedImage.getContentType());
      }

      return materialThumbnail;
    }

    return materialThumbnail;
  }

  public Material findMaterialByCompletePath(String completePath) {
    String path = RequestUtils.stripTrailingSlash(completePath);
    String materialPath = RequestUtils.stripPrecedingSlash(path.substring(MATERIALS_PATH.length() + 1));

    PermaLink permaLink = permaLinkDAO.findByPath(materialPath);
    if (permaLink != null) {
      return permaLink.getMaterial();
    }

    String[] pathElements = materialPath.split("/");
    if (pathElements.length >= 2) {
      String userIdPart = pathElements[0];
      if (!NumberUtils.isNumber(userIdPart)) {
        return null;
      }

      Long userId = NumberUtils.createLong(userIdPart);
      if (userId == null) {
        return null;
      }

      User owner = userDAO.findById(userId);
      if (owner == null) {
        return null;
      }

      Folder parentFolder = null;

      for (int i = 1, l = pathElements.length - 1; i < l; i++) {
        String pathElement = pathElements[i];
        if (parentFolder != null)
          parentFolder = (Folder) materialDAO.findByParentFolderAndUrlName(parentFolder, pathElement);
        else
          parentFolder = (Folder) materialDAO.findByRootFolderAndUrlName(owner, pathElement);
      }

      if (parentFolder != null)
        return materialDAO.findByParentFolderAndUrlName(parentFolder, pathElements[pathElements.length - 1]);
      else
        return materialDAO.findByRootFolderAndUrlName(owner, pathElements[pathElements.length - 1]);
    }

    return null;
  }

  public Material findByOwnerAndPath(User owner, String path) {
    if (StringUtils.isBlank(path) || (owner == null)) {
      return null;
    }

    String[] pathElements = path.split("/");
    Folder parentFolder = null;

    for (int i = 0, l = pathElements.length - 1; i < l; i++) {
      String pathElement = pathElements[i];
      if (parentFolder != null)
        parentFolder = (Folder) materialDAO.findByParentFolderAndUrlName(parentFolder, pathElement);
      else
        parentFolder = (Folder) materialDAO.findByRootFolderAndUrlName(owner, pathElement);
    }

    if (parentFolder != null)
      return materialDAO.findByParentFolderAndUrlName(parentFolder, pathElements[pathElements.length - 1]);
    else
      return materialDAO.findByRootFolderAndUrlName(owner, pathElements[pathElements.length - 1]);
  }

  public Material createMaterial(Folder parentFolder, User user, FileData fileData) throws MimeTypeParseException, IOException, GeneralSecurityException {
    MimeType mimeType = parseMimeType(fileData.getContentType());

    if ("image".equals(mimeType.getPrimaryType())) {
      if ("svg".equals(mimeType.getSubType()) || "svg+xml".equals(mimeType.getSubType())) {
        return createVectorImage(parentFolder, user, new String(fileData.getData(), "UTF-8"), fileData.getFileName());
      } else {
        if (fileData.getContentType().equals("image/png")) {
          return createImage(parentFolder, user, fileData.getData(), fileData.getContentType(), fileData.getFileName());
        } else {
          return uploadImage(parentFolder, user, fileData);
        }
      }
    } else {
      switch (mimeType.getBaseType()) {
      case "application/pdf":
        return uploadPdf(parentFolder, user, fileData);
      case "text/plain":
        return uploadText(parentFolder, user, fileData);
      case "text/html":
      case "application/xhtml+xml":
        return uploadHtml(parentFolder, user, fileData);
      case "application/vnd.oasis.opendocument.text":
      case "application/vnd.sun.xml.writer":
      case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
      case "application/msword":
      case "application/x-mswrite":
      case "application/rtf":
      case "text/richtext":
        return uploadDocument(parentFolder, user, fileData);
      case "application/vnd.openxmlformats-officedocument.presentationml.slideshow":
      case "application/vnd.ms-powerpoint":
        // TODO: Warning: presentation
        return uploadDocument(parentFolder, user, fileData);
      case "application/vnd.ms-excel":
      case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
      case "application/vnd.oasis.opendocument.spreadsheet":
      case "text/csv":
      case "text/tab-separated-values":
        // TODO: Warning, spreadsheet
        return uploadDocument(parentFolder, user, fileData);
      }
    }

    return createFile(parentFolder, user, fileData.getData(), fileData.getContentType(), fileData.getFileName());
  }

  public Material createFile(Folder parentFolder, User loggedUser, byte[] data, String contentType, String title) {
    String urlName = getUniqueMaterialUrlName(loggedUser, parentFolder, null, title);
    Date now = new Date();

    return fileDAO.create(loggedUser, now, loggedUser, now, null, parentFolder, urlName, title, data, contentType, MaterialPublicity.PRIVATE);
  }

  private Material uploadImage(Folder parentFolder, User loggedUser, FileData fileData) throws IOException {
    TypedData imageData = ImageUtils.convertToPng(fileData);
    return createImage(parentFolder, loggedUser, imageData.getData(), imageData.getContentType(), fileData.getFileName());
  }

  private Material createVectorImage(Folder parentFolder, User loggedUser, String data, String title) {
    String urlName = getUniqueMaterialUrlName(loggedUser, parentFolder, null, title);
    return vectorImageDAO.create(loggedUser, null, parentFolder, urlName, title, data, MaterialPublicity.PRIVATE);
  }

  private Material uploadPdf(Folder parentFolder, User loggedUser, FileData fileData) {
    return createPdf(parentFolder, loggedUser, fileData.getData(), fileData.getFileName());
  }

  private Material uploadHtml(Folder parentFolder, User loggedUser, FileData fileData) throws UnsupportedEncodingException {
    String data = new String(fileData.getData(), "UTF-8");
    return createDocument(parentFolder, loggedUser, data, fileData.getFileName());
  }

  private Material uploadText(Folder parentFolder, User loggedUser, FileData fileData) throws UnsupportedEncodingException {

    String title = fileData.getFileName();
    String bodyContent = StringEscapeUtils.escapeHtml4(new String(fileData.getData(), "UTF-8"));
    bodyContent = bodyContent.replaceAll("\n", "<br/>");
    String data = HtmlUtils.getAsHtmlText(title, bodyContent);

    return createDocument(parentFolder, loggedUser, data, title);
  }

  private Material uploadDocument(Folder parentFolder, User loggedUser, FileData fileData) throws IOException, GeneralSecurityException {
    Drive drive = driveManager.getDrive(systemGoogleDriveCredentials.getSystemCredential());

    File file = driveManager.insertFile(drive, fileData.getFileName(), null, fileData.getContentType(), null, true, fileData.getData());
    try {
      TypedData htmlData = driveManager.exportFile(drive, file, "text/html");
      return createDocument(parentFolder, loggedUser, new String(htmlData.getData(), "UTF-8"), fileData.getFileName());
    } finally {
      driveManager.deleteFile(drive, file);
    }
  }

  private Material createDocument(Folder parentFolder, User loggedUser, String data, String title) {
    List<GuessedLanguage> guessedLanguages;
    Language language = null;
    try {
      guessedLanguages = LanguageUtils.getGuessedLanguages(data, 0.2);
      if (guessedLanguages.size() > 0) {
        String languageCode = guessedLanguages.get(0).getLanguageCode();
        language = languageDAO.findByIso2(languageCode);
      }
    } catch (IOException e) {
      // It's really not very serious if language detection fails.
      logger.log(Level.WARNING, "Language detection failed", e);
    }

    String urlName = getUniqueMaterialUrlName(loggedUser, parentFolder, null, title);

    return documentDAO.create(loggedUser, language, parentFolder, urlName, title, data, MaterialPublicity.PRIVATE);
  }

  private Material createPdf(Folder parentFolder, User loggedUser, byte[] data, String title) {
    String urlName = getUniqueMaterialUrlName(loggedUser, parentFolder, null, title);
    Date now = new Date();

    return pdfDAO.create(loggedUser, now, loggedUser, now, null, parentFolder, urlName, title, data, MaterialPublicity.PRIVATE);
  }

  private void recursiveDelete(Folder folder, User user) {
    List<Material> childMaterials = materialDAO.listByParentFolder(folder);
    for (Material childMaterial : childMaterials) {
      if (childMaterial instanceof Folder) {
        recursiveDelete((Folder) childMaterial, user);
      }

      deleteMaterial(childMaterial, user);
    }
  }

  public FileData getMaterialData(String contextPath, User user, Material material) throws UnsupportedEncodingException,
      MalformedURLException, IOException, GeneralSecurityException {
    switch (material.getType()) {
      case IMAGE:
        return getBinaryMaterialData((Image) material);
      case DOCUMENT:
      case ILLUSION_GROUP_DOCUMENT:
        return getDocumentData(contextPath, (Document) material);
      case VECTOR_IMAGE:
        return getVectorImageData((VectorImage) material);
      case PDF:
        return getBinaryMaterialData((Pdf) material);
      case FILE:
        return getBinaryMaterialData((fi.foyt.fni.persistence.model.materials.File) material);
      case GOOGLE_DOCUMENT:
        TypedData typedData = getGoogleDocumentData((GoogleDocument) material);
        return new FileData(null, material.getUrlName(), typedData.getData(), typedData.getContentType(), typedData.getModified());
      case DROPBOX_FILE:
        return getDropboxMaterialData(user, (DropboxFile) material);
      case BINARY:
        return getBinaryMaterialData((Binary) material);
      case CHARACTER_SHEET:
        return getCharacterSheetMaterialData(contextPath, (CharacterSheet) material);
      case BOOK_DESIGN:
        return getBookDesignData((BookDesign) material);
      case DROPBOX_FOLDER:
      case DROPBOX_ROOT_FOLDER:
      case FOLDER:
      case ILLUSION_FOLDER:
      case ILLUSION_GROUP_FOLDER:
      case BOOK_TEMPLATE:
      break;
    }
    
    return null;
  }

  private FileData getBookDesignData(BookDesign bookDesign) throws IOException {
    String html = new BookDesignRenderer(bookDesign).toHtml();
    return new FileData(null, bookDesign.getUrlName(), html.getBytes("UTF-8"), "text/html", bookDesign.getModified());
  }

  private FileData getCharacterSheetMaterialData(String contextPath, CharacterSheet characterSheet) throws UnsupportedEncodingException {
    StringBuilder htmlBuilder = new StringBuilder();
    htmlBuilder.append("<!DOCTYPE html>");
    htmlBuilder.append("<html>");
    htmlBuilder.append("<head>");
    htmlBuilder.append("<meta charset=\"UTF-8\">");
    
    htmlBuilder.append("<script type=\"text/javascript\" charset=\"utf8\" src=\"//cdnjs.cloudflare.com/ajax/libs/jquery/2.1.1/jquery.min.js\"></script>");
    htmlBuilder.append("<script type=\"text/javascript\" charset=\"utf8\" src=\"//cdnjs.cloudflare.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js\"></script>");
    htmlBuilder.append("<script type=\"text/javascript\" charset=\"utf8\" src=\"//cdnjs.cloudflare.com/ajax/libs/Base64/0.3.0/base64.min.js\"></script>");
    htmlBuilder.append("<link rel=\"StyleSheet\" href=\"//cdnjs.cloudflare.com/ajax/libs/jqueryui/1.10.4/css/jquery-ui.min.css\"></link>");
    htmlBuilder.append("<script type=\"text/javascript\" charset=\"utf8\" src=\"" + contextPath + "/scripts/gui/character-sheet.js\"></script>");
    
    if (StringUtils.isNotBlank(characterSheet.getTitle())) {
      htmlBuilder.append("<title>");
      htmlBuilder.append(StringEscapeUtils.escapeHtml4(characterSheet.getTitle()));
      htmlBuilder.append("</title>");
    }
    
    if (StringUtils.isNoneBlank(characterSheet.getStyles())) {
      htmlBuilder.append("<style type=\"text/css\">");
      htmlBuilder.append(characterSheet.getStyles());
      htmlBuilder.append("</style>");
    }
    
    if (StringUtils.isNoneBlank(characterSheet.getScripts())) {
      htmlBuilder.append("<script type=\"text/javascript\">");
      htmlBuilder.append(characterSheet.getScripts());
      htmlBuilder.append("</script>");
    }
    
    htmlBuilder.append("</head>");
    htmlBuilder.append("<body>");
    htmlBuilder.append(characterSheet.getContents());
    htmlBuilder.append("</body>");
    htmlBuilder.append("</html>");
    
    return new FileData(null, characterSheet.getUrlName(), htmlBuilder.toString().getBytes("UTF-8"), "text/html", characterSheet.getModified());
  }

  private FileData getDocumentData(String contextPath, Document document) throws UnsupportedEncodingException {
    String bodyContent = document.getData();
    String title = document.getTitle();
    String styleSheet = String.format("%s/theme/css/material-document-style.css", contextPath == null ? "" : contextPath);
    String htmlContent = MessageFormat.format(DOCUMENT_TEMPLATE, title, styleSheet, bodyContent);
    return new FileData(null, document.getUrlName(), htmlContent.getBytes("UTF-8"), "text/html", document.getModified());
  }

  private FileData getVectorImageData(VectorImage vectorImage) throws UnsupportedEncodingException {
    String data = vectorImage.getData();
    return new FileData(null, vectorImage.getUrlName(), data != null ? data.getBytes("UTF-8") : null, "image/svg+xml", vectorImage.getModified());
  }

  private FileData getBinaryMaterialData(Binary binary) {
    return new FileData(null, binary.getUrlName(), binary.getData(), binary.getContentType(), binary.getModified());
  }
  
  private FileData getDropboxMaterialData(User user, DropboxFile dropboxFile) throws IOException {
    Response response = getDropboxFileContent(user, dropboxFile);
    if (response.getCode() == 200) {
      byte[] data = null;
      
      InputStream inputStream = response.getStream();
      try {
        data = IOUtils.toByteArray(inputStream);
        return new FileData(null, dropboxFile.getUrlName(), data, dropboxFile.getMimeType(), dropboxFile.getModified());
      } finally {
        inputStream.close();
      }
    }
    
    return null;
  }
  
  /* Copy */
  
  @SuppressWarnings("unchecked")
  public <T extends Material> T copyMaterial(T material, Folder targetFolder, User creator) {
    MaterialCopy<T> copyOperation = (MaterialCopy<T>) getCopyOperation(material.getType());
    if (copyOperation != null) {
      
      // New URL name
      
      String urlName = getUniqueMaterialUrlName(creator, targetFolder, null, material.getTitle());
      
      // Copy Material
      
      T copy = copyOperation.copy(material, targetFolder, urlName, creator);
      if (copy == null) {
        return null;
      }

      // Common Properties

      for (MaterialSetting setting : materialSettingDAO.listByMaterial(material)) {
        materialSettingDAO.create(copy, setting.getKey(), setting.getValue());
      }
      
      for (MaterialTag tag : materialTagDAO.listByMaterial(material)) {
        materialTagDAO.create(copy, tag.getTag());
      }
      
      IllusionEventFolder targetIllusionFolder = findMaterialIllusionFolder(targetFolder);
      if (targetIllusionFolder != null) {
        IllusionEvent targetEvent = illusionEventDAO.findByFolder(targetIllusionFolder);
        
        for (IllusionEventMaterialParticipantSetting setting : illusionEventMaterialParticipantSettingDAO.listByMaterial(material)) {
          IllusionEventParticipant participant = illusionEventParticipantDAO.findByEventAndUser(targetEvent, setting.getParticipant().getUser());
          if (participant != null) {
            illusionEventMaterialParticipantSettingDAO.create(copy, participant, setting.getKey(), setting.getValue());
          }
        }
      }

      return copy;
    }
    
    return null;
  }
  
  private IllusionEventFolder findMaterialIllusionFolder(Material material) {
    if (material == null) {
      return null;
    }
    
    if (material.getType() == MaterialType.ILLUSION_GROUP_FOLDER) {
      return (IllusionEventFolder) material;
    }
    
    Folder parent = material.getParentFolder();
    while (parent != null) {
      if (parent.getType() == MaterialType.ILLUSION_GROUP_FOLDER) {
        return (IllusionEventFolder) parent;
      }
      
      parent = parent.getParentFolder();
    }
    
    return null;
  }

  public boolean isCopyableType(MaterialType type) {
    return getCopyOperation(type) != null;
  }
  
  public MaterialType[] getAllowedCopyTargets(MaterialType type) {
    MaterialCopy<?> materialCopy = getCopyOperation(type);
    if (materialCopy != null) {
      return materialCopy.getAllowedTargets();
    }
    
    return null;
  }
  
  private MaterialCopy<?> getCopyOperation(MaterialType type) {
    for (MaterialCopy<? extends Material> materialCopy : materialCopies) {
      if (materialCopy.getType().equals(type)) {
        return materialCopy;
      }
    }
    
    return null;
  }

}
