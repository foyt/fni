package fi.foyt.fni.ckcc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import fi.foyt.ckc.CKCConnector;
import fi.foyt.ckc.CKCConnectorException;
import fi.foyt.ckc.CreateResult;
import fi.foyt.ckc.InitResult;
import fi.foyt.ckc.LoadResult;
import fi.foyt.ckc.Revision;
import fi.foyt.ckc.SaveResult;
import fi.foyt.ckc.Status;
import fi.foyt.ckc.UpdateResult;
import fi.foyt.ckc.utils.CKCUtils;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.common.LanguageDAO;
import fi.foyt.fni.persistence.dao.common.TagDAO;
import fi.foyt.fni.persistence.dao.materials.DocumentDAO;
import fi.foyt.fni.persistence.dao.materials.DocumentRevisionDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialRevisionSettingDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialRevisionTagDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialSettingDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialSettingKeyDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialTagDAO;
import fi.foyt.fni.persistence.dao.users.UserTokenDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.common.Tag;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.DocumentRevision;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialRevisionSetting;
import fi.foyt.fni.persistence.model.materials.MaterialSetting;
import fi.foyt.fni.persistence.model.materials.MaterialSettingKey;
import fi.foyt.fni.persistence.model.materials.MaterialTag;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.compression.CompressionUtils;
import fi.foyt.fni.utils.diff.DiffUtils;
import fi.foyt.fni.utils.diff.PatchResult;
import fi.foyt.fni.utils.html.GuessedLanguage;
import fi.foyt.fni.utils.html.HtmlUtils;

@ApplicationScoped
@Stateful
public class CKCConnectorImpl implements CKCConnector {

  private final static String TOKEN_SESSION_ATTRIBUTE = "__ckc_connector_token__";

  @Inject
  private MaterialController materialController;

	@Inject
	private MaterialPermissionController materialPermissionController;

  @Inject
  private SessionController sessionController;

  @Inject
  @DAO
  private DocumentDAO documentDAO;

  @Inject
  @DAO
  private DocumentRevisionDAO documentRevisionDAO;

  @Inject
  @DAO
  private LanguageDAO languageDAO;

  @Inject
  @DAO
  private TagDAO tagDAO;

  @Inject
  @DAO
  private MaterialTagDAO materialTagDAO;

  @Inject
  @DAO
  private MaterialSettingDAO materialSettingDAO;

  @Inject
  @DAO
  private MaterialSettingKeyDAO materialSettingKeyDAO;

  @Inject
  @DAO
  private MaterialRevisionSettingDAO materialRevisionSettingDAO;

  @Inject
  @DAO
  private MaterialRevisionTagDAO materialRevisionTagDAO;

  @Inject
  @DAO
  private UserTokenDAO userTokenDAO;

  @Override
  public InitResult init(HttpServletRequest request, String documentId) throws CKCConnectorException {
    User loggedUser = getLoggedUser(request);
    Long id = NumberUtils.createLong(documentId);
    Document document = documentDAO.findById(id);

    if (materialPermissionController.hasModifyPermission(loggedUser, document)) {
      String token = UUID.randomUUID().toString();
      request.getSession().setAttribute(TOKEN_SESSION_ATTRIBUTE, token);
      return new InitResult(Status.OK, token);
    }

    return new InitResult(Status.FORBIDDEN, null);
  }

  @Override
  public UpdateResult update(HttpServletRequest request, String documentId, Long revisionNumber) throws CKCConnectorException {
    Status status = Status.OK;
    List<Revision> revisions = new ArrayList<Revision>();

    Long id = NumberUtils.createLong(documentId);

    Document document = documentDAO.findById(id);

    List<DocumentRevision> documentRevisions = documentRevisionDAO.listByDocumentAndRevisionGreaterThan(document, revisionNumber);
    Collections.sort(documentRevisions, new Comparator<DocumentRevision>() {
      @Override
      public int compare(DocumentRevision documentRevision1, DocumentRevision documentRevision2) {
        return documentRevision1.getRevision().compareTo(documentRevision2.getRevision());
      }
    });

    for (DocumentRevision documentRevision : documentRevisions) {
      byte[] patchData = documentRevision.getData();
      if (documentRevision.getCompressed()) {
        try {
          patchData = CompressionUtils.uncompressBzip2Array(patchData);
        } catch (IOException e) {
          throw new CKCConnectorException(e);
        }
      }

      try {
        Revision revision = new Revision(documentRevision.getRevision(), patchData != null ? new String(patchData, "UTF-8") : null);
        List<MaterialRevisionSetting> revisionSettings = materialRevisionSettingDAO.listByMaterialRevision(documentRevision);
        for (MaterialRevisionSetting revisionSetting : revisionSettings) {
          String property = revisionSetting.getKey().getName().substring(9);
          revision.addProperty(property, revisionSetting.getValue());
        }

        if (documentRevision.getTitle() != null) {
          revision.addProperty("title", documentRevision.getTitle());
        }

        if (documentRevision.getLanguage() != null) {
          revision.addProperty("langCode", documentRevision.getLanguage().getISO2());
        }

        Long tagChanges = materialRevisionTagDAO.countByMaterialRevision(documentRevision);
        if (tagChanges > 0) {
          StringBuilder tagsBuilder = new StringBuilder();
          // Basically if revision contains any tag changes we need to send them
          // all to client
          List<MaterialTag> materialTags = materialTagDAO.listByMaterial(document);
          for (MaterialTag materialTag : materialTags) {
            if (tagsBuilder.length() > 0) {
              tagsBuilder.append(',');
            }

            tagsBuilder.append(materialTag.getTag().getText());
          }

          revision.addProperty("metaKeywords", tagsBuilder.toString());
        }

        revisions.add(revision);
      } catch (UnsupportedEncodingException e) {
        throw new CKCConnectorException(e);
      }
    }

    return new UpdateResult(status, revisions);
  }

  @Override
  public CreateResult create(HttpServletRequest request, String content, String properties) {
    User loggedUser = getLoggedUser(request);

    Folder parentFolder = null;
    Language language = null;
    String title = null;
    List<Tag> tags = new ArrayList<Tag>();
    Map<MaterialSettingKey, String> documentSettings = new HashMap<MaterialSettingKey, String>();

    if (StringUtils.isNotBlank(properties)) {
      Map<String, String> propertyMap = CKCUtils.parseProperties(properties);
      Iterator<String> keyIterator = propertyMap.keySet().iterator();
      while (keyIterator.hasNext()) {
        String key = keyIterator.next();
        String value = propertyMap.get(key);
        if ("title".equals(key)) {
          // title is saved as a document title
          title = value;
        } else if ("langCode".equals(key)) {
          // language is saved as document language property
          language = languageDAO.findByIso2(value);
        } else if ("metaKeywords".equals(key)) {
          // keywords are saved as tags
          String[] tagTexts = value.split(",");
          for (String tagText : tagTexts) {
            String trimmedTag = tagText.trim();
            if (StringUtils.isNotBlank(trimmedTag)) {
              Tag tag = tagDAO.findByText(trimmedTag);
              if (tag == null)
                tag = tagDAO.create(trimmedTag);

              tags.add(tag);
            }
          }
        } else {
          // everything else is saved as document.property
          MaterialSettingKey settingKey = materialSettingKeyDAO.findByName("document." + key);
          if (settingKey != null) {
            documentSettings.put(settingKey, value);
          }
        }
      }
    }

    String urlName = materialController.getUniqueMaterialUrlName(loggedUser, parentFolder, null, title);
    Document document = documentDAO.create(loggedUser, language, parentFolder, urlName, title, content, MaterialPublicity.PRIVATE);

    if (language == null) {
      try {
        List<GuessedLanguage> guessedLanguages = HtmlUtils.getGuessedLanguages(document.getContentPlain(), 0.2);
        if (guessedLanguages.size() > 0) {
          language = languageDAO.findByIso2(guessedLanguages.get(0).getLanguageCode());
        }

        if (language != null) {
          documentDAO.updateLanguage(document, loggedUser, language);
        }
      } catch (IOException e) {
      }
    }

    for (Tag tag : tags) {
      materialTagDAO.create(document, tag);
    }

    for (MaterialSettingKey documentSettingKey : documentSettings.keySet()) {
      materialSettingDAO.create(document, documentSettingKey, documentSettings.get(documentSettingKey));
    }

    String token = UUID.randomUUID().toString();
    request.getSession().setAttribute(TOKEN_SESSION_ATTRIBUTE, token);

    return new CreateResult(Status.OK, token, document.getId().toString(), 0l);
  }

  @Override
  public LoadResult load(HttpServletRequest request, String documentId) throws CKCConnectorException {
    Status status = Status.OK;

    Long id = NumberUtils.createLong(documentId);
    Document document = documentDAO.findById(id);

    Long lastRevision = documentRevisionDAO.maxRevisionByDocument(document);
    if (lastRevision == null)
      lastRevision = 0l;

    Map<String, String> properties = new HashMap<String, String>();
    List<MaterialSetting> materialSettings = materialSettingDAO.listByMaterial(document);
    for (MaterialSetting materialSetting : materialSettings) {
      String name = materialSetting.getKey().getName().substring(9);
      properties.put(name, materialSetting.getValue());

    }

    if (document.getTitle() != null) {
      properties.put("title", document.getTitle());
    }

    if (document.getLanguage() != null) {
      properties.put("langCode", document.getLanguage().getISO2());
    }

    StringBuilder tagsBuilder = new StringBuilder();
    // Basically if revision contains any tag changes we need to send them all
    // to client
    List<MaterialTag> materialTags = materialTagDAO.listByMaterial(document);
    for (MaterialTag materialTag : materialTags) {
      if (tagsBuilder.length() > 0) {
        tagsBuilder.append(',');
      }

      tagsBuilder.append(materialTag.getTag().getText());
    }

    properties.put("metaKeywords", tagsBuilder.toString());

    try {
      return new LoadResult(status, lastRevision, new String(document.getData(), "UTF-8"), properties);
    } catch (UnsupportedEncodingException e) {
      throw new CKCConnectorException(e);
    }
  }

  @Override
  public SaveResult save(HttpServletRequest request, String documentId, String patch, String properties) throws CKCConnectorException {
    Status status = Status.OK;
    Long revisionNumber = null;
    Long id = NumberUtils.createLong(documentId);

    Document document = documentDAO.findById(id);
    User loggedUser = getLoggedUser(request);

    boolean compressed = false;
    byte[] revisionBytes = null;
    Long lastRevision = documentRevisionDAO.maxRevisionByDocument(document);
    if (lastRevision == null)
      lastRevision = 0l;

    revisionNumber = lastRevision + 1;

    if (StringUtils.isNotBlank(patch)) {
      String oldData;
      try {
        oldData = new String(document.getData(), "UTF-8");
      } catch (UnsupportedEncodingException e) {
        throw new CKCConnectorException(e);
      }

      PatchResult patchResult = DiffUtils.applyPatch(oldData, patch);
      if (!patchResult.allApplied()) {
        status = Status.CONFLICT;
      } else {
        String data = patchResult.getPatchedText();

        if (!StringUtils.isEmpty(oldData) && !data.equals(oldData)) {

          String revisionData = DiffUtils.makePatch(oldData, data);

          try {
            revisionBytes = revisionData.getBytes("UTF-8");
          } catch (UnsupportedEncodingException e) {
            throw new CKCConnectorException(e);
          }
        }

        documentDAO.updateData(document, loggedUser, data);
      }
    }

    DocumentRevision documentRevision = documentRevisionDAO.create(document, revisionNumber, new Date(), compressed, false, revisionBytes, null, null);

    if (StringUtils.isNotBlank(properties)) {
      Map<String, String> changedProperties = CKCUtils.parseProperties(properties);
      Iterator<String> keyIterator = changedProperties.keySet().iterator();
      while (keyIterator.hasNext()) {
        String key = keyIterator.next();
        String value = changedProperties.get(key);
        if ("title".equals(key)) {
          // title is saved as a document title
          documentDAO.updateTitle(document, loggedUser, value);
          documentRevisionDAO.updateTitle(documentRevision, value);
        } else if ("langCode".equals(key)) {
          // language is saved as document language property
          Language language = languageDAO.findByIso2(value);
          if (language != null) {
            documentDAO.updateLanguage(document, loggedUser, language);
            documentRevisionDAO.updateLanguage(documentRevision, language);
          }
        } else if ("metaKeywords".equals(key)) {
          // keywords are saved as tags
          List<MaterialTag> existingTags = new ArrayList<MaterialTag>(materialTagDAO.listByMaterial(document));
          List<Tag> newTags = new ArrayList<Tag>();
          String[] tagTexts = value.split(",");
          for (String tagText : tagTexts) {
            String trimmedTag = tagText.trim();
            if (StringUtils.isNotBlank(trimmedTag)) {
              Tag tag = tagDAO.findByText(trimmedTag);
              if (tag == null)
                tag = tagDAO.create(trimmedTag);

              newTags.add(tag);
            }
          }

          for (Tag newTag : newTags) {
            boolean found = false;
            for (MaterialTag existingTag : existingTags) {
              if (existingTag.getTag().getId().equals(newTag.getId())) {
                // Material has already this tag
                existingTags.remove(existingTag);
                found = true;
                break;
              }
            }

            if (found != true) {
              // Material does not have this tag, so we need to add it
              materialTagDAO.create(document, newTag);
              materialRevisionTagDAO.create(documentRevision, newTag, Boolean.FALSE);
            }
          }

          // Rest of the existing tags have been removed
          for (MaterialTag existingTag : existingTags) {
            materialRevisionTagDAO.create(documentRevision, existingTag.getTag(), Boolean.TRUE);
            materialTagDAO.delete(existingTag);
          }
        } else {
          // everything else is saved as document.property
          MaterialSettingKey settingKey = materialSettingKeyDAO.findByName("document." + key);
          if (settingKey != null) {
            MaterialSetting materialSetting = materialSettingDAO.findByMaterialAndKey(document, settingKey);
            if (materialSetting == null) {
              materialSettingDAO.create(document, settingKey, value);
            } else {
              materialSettingDAO.updateValue(materialSetting, value);
            }

            materialRevisionSettingDAO.create(documentRevision, settingKey, value);
          }
        }
      }
    }

    if (document.getLanguage() == null) {
      try {
        List<GuessedLanguage> guessedLanguages = HtmlUtils.getGuessedLanguages(document.getContentPlain(), 0.2);
        if (guessedLanguages.size() > 0) {
          Language language = languageDAO.findByIso2(guessedLanguages.get(0).getLanguageCode());
          if (language != null) {
            documentDAO.updateLanguage(document, loggedUser, language);
            documentRevisionDAO.updateLanguage(documentRevision, language);
          }
        }
      } catch (IOException e) {
      }
    }

    return new SaveResult(status, revisionNumber);
  }

  @Override
  public boolean validateToken(HttpServletRequest request, String token) {
    String sessionToken = (String) request.getSession().getAttribute(TOKEN_SESSION_ATTRIBUTE);
    if (StringUtils.isBlank(sessionToken))
      return false;

    if (sessionToken.equals(token))
      return true;

    return false;
  }

  private User getLoggedUser(HttpServletRequest req) {
    return sessionController.getLoggedUser();
  }
}
