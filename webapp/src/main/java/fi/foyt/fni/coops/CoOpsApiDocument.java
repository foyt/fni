package fi.foyt.fni.coops;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import fi.foyt.coops.CoOpsAlgorithm;
import fi.foyt.coops.CoOpsConflictException;
import fi.foyt.coops.CoOpsForbiddenException;
import fi.foyt.coops.CoOpsInternalErrorException;
import fi.foyt.coops.CoOpsNotFoundException;
import fi.foyt.coops.CoOpsNotImplementedException;
import fi.foyt.coops.CoOpsUsageException;
import fi.foyt.coops.model.File;
import fi.foyt.coops.model.Join;
import fi.foyt.coops.model.Patch;
import fi.foyt.fni.materials.CoOpsSessionController;
import fi.foyt.fni.materials.DocumentController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.common.Tag;
import fi.foyt.fni.persistence.model.materials.CoOpsSession;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.DocumentRevision;
import fi.foyt.fni.persistence.model.materials.MaterialRevisionSetting;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.system.TagController;
import fi.foyt.fni.utils.compression.CompressionUtils;
import fi.foyt.fni.utils.diff.DiffUtils;
import fi.foyt.fni.utils.diff.PatchResult;

@Dependent
@Stateless
public class CoOpsApiDocument extends AbstractCoOpsApiImpl {

  private final static String COOPS_DOCUMENT_CONTENTTYPE = "text/html;editor=CKEditor";
  
  @Inject
  private CoOpsSessionController coOpsSessionController;

  @Inject
  private DocumentController documentController;

  @Inject
  private SessionController sessionController;

  @Inject
  private TagController tagController;
  
  @Inject
  private SystemSettingsController systemSettingsController;

  @Inject
  private MaterialPermissionController materialPermissionController;
  
  @PostConstruct
  public void init() {
    Map<String, CoOpsAlgorithm> algorithms = new HashMap<>();
    algorithms.put("dmp", new CoOpsDmpAlgorithm());
    setAlgorithms(algorithms);
  }

  @Override
  public File fileGet(String fileId, Long revisionNumber) throws CoOpsNotImplementedException, CoOpsNotFoundException, CoOpsUsageException, CoOpsInternalErrorException, CoOpsForbiddenException {
    Document document = findDocument(fileId);
    
    if (!materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), document)) {
      throw new CoOpsForbiddenException();
    }
    
    if (revisionNumber != null) {
      // TODO: Implement
      throw new CoOpsNotImplementedException();
    } else {
      Long currentRevisionNumber = documentController.getDocumentRevision(document);
      String data = document.getData();
      
      Map<String, String> properties = settingToProperties("document.", documentController.listDocumentSettings(document));
      properties.put("title", document.getTitle());

      return new File(currentRevisionNumber, data, COOPS_DOCUMENT_CONTENTTYPE, properties);
    }
  }

  @Override
  public List<Patch> fileUpdate(String fileId, String sessionId, Long revisionNumber) throws CoOpsNotFoundException, CoOpsInternalErrorException, CoOpsUsageException, CoOpsForbiddenException {
    Document document = findDocument(fileId);
    
    if (!materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), document)) {
      throw new CoOpsForbiddenException();
    }
    
    List<DocumentRevision> documentRevisions = documentController.listDocumentRevisionsAfter(document, revisionNumber);
    List<Patch> updateResults = new ArrayList<>();

    if (!documentRevisions.isEmpty()) {
      for (DocumentRevision documentRevision : documentRevisions) {
        String patch = null;
        byte[] patchData = documentRevision.getData();
        if (patchData != null) {
          if (documentRevision.getCompressed()) {
            try {
              patchData = CompressionUtils.uncompressBzip2Array(patchData);
            } catch (IOException e) {
              throw new CoOpsInternalErrorException(e);
            }
          }
  
          try {
            patch = new String(patchData, "UTF-8");
          } catch (UnsupportedEncodingException e) {
            throw new CoOpsInternalErrorException(e);
          }
        }
        
        Map<String, String> properties = null;
        
        List<MaterialRevisionSetting> revisionSettings = documentController.listDocumentRevisionSettings(documentRevision);
        if (revisionSettings.size() > 0) {
          properties = new HashMap<>();
          for (MaterialRevisionSetting revisionSetting : revisionSettings) {
            String key = StringUtils.removeStart(revisionSetting.getKey().getName(), "document.");
            properties.put(key, revisionSetting.getValue());
          }
        }
        
        // TODO: Implement extensions
        Map<String, Object> extensions = new HashMap<String, Object>();
        
        if (patch != null) {
          updateResults.add(new Patch(documentRevision.getSessionId(), documentRevision.getRevision(), documentRevision.getChecksum(), patch, properties, extensions));
        } else {
          updateResults.add(new Patch(documentRevision.getSessionId(), documentRevision.getRevision(), null, null, properties, extensions));
        }
      }    
    }

    return updateResults;
  }

  @Override
  public void filePatch(String fileId, String sessionId, Long revisionNumber, String patch, Map<String, String> properties, Map<String, Object> extensions) throws CoOpsInternalErrorException, CoOpsUsageException, CoOpsNotFoundException, CoOpsConflictException, CoOpsForbiddenException {
    Document document = findDocument(fileId);
    
    if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), document)) {
      throw new CoOpsForbiddenException();
    }

    Long currentRevision = documentController.getDocumentRevision(document);
    if (!currentRevision.equals(revisionNumber)) {
      throw new CoOpsConflictException();
    } 
    
    byte[] patchData = null;
    String checksum = null;
    User loggedUser = sessionController.getLoggedUser();
    
    if (StringUtils.isNotBlank(patch)) {
      String oldData = document.getData();
      PatchResult patchResult = DiffUtils.applyPatch(oldData, patch);
      if (!patchResult.allApplied()) {
        throw new CoOpsConflictException();
      }
      
      String data = patchResult.getPatchedText();
      documentController.updateDocumentData(document, data, loggedUser);
      checksum = DigestUtils.md5Hex(data);
      try {
        patchData = patch.getBytes("UTF-8");
      } catch (UnsupportedEncodingException e) {
        throw new CoOpsInternalErrorException(e);
      }
    }

    Long patchRevisionNumber = currentRevision + 1;
    DocumentRevision documentRevision = documentController.createDocumentRevision(document, patchRevisionNumber, new Date(), false, false, patchData, checksum, sessionId);
    
    if (properties != null) {
      Iterator<String> keyIterator = properties.keySet().iterator();
      while (keyIterator.hasNext()) {
        String key = keyIterator.next();
        String value = properties.get(key);
        if ("title".equals(key)) {
          // title is saved as a document title
          documentController.updateDocumentTitle(document, value, loggedUser);
        } else if ("langCode".equals(key)) {
          // language is saved as document language property
          Language language = systemSettingsController.findLocaleByIso2(value);
          documentController.updateDocumentLanguage(document, language, loggedUser);
        } else if ("metaKeywords".equals(key)) {
          // keywords are saved as tags
          List<Tag> tags = new ArrayList<>();
          
          String[] tagTexts = value.split(",");
          for (String tagText : tagTexts) {
            String trimmedTag = tagText.trim();
            if (StringUtils.isNotBlank(trimmedTag)) {
              Tag tag = tagController.findTagByText(trimmedTag);
              if (tag == null) {
                tag = tagController.createTag(trimmedTag);
              }
              tags.add(tag);
            }
          }
          
          documentController.setDocumentTags(document, tags);
        } else {
          // everything else is saved as document.property
          documentController.setDocumentSetting(document, "document." + key, value);
        }
        
        // Everything is saved as revision setting
        documentController.createDocumentRevisionSetting(documentRevision, "document." + key, value);
      }
    }
  }

  @Override
  public Join fileJoin(String fileId, List<String> algorithms, String protocolVersion) throws CoOpsNotFoundException, CoOpsNotImplementedException, CoOpsInternalErrorException, CoOpsForbiddenException, CoOpsUsageException {
    Document document = findDocument(fileId);
    
    if (!COOPS_PROTOCOL_VERSION.equals(protocolVersion)) {
      throw new CoOpsNotImplementedException("Protocol version mismatch. Client is using " + protocolVersion + " and server " + COOPS_PROTOCOL_VERSION);
    }
    
    String chosenAlgorithm = chooseAlgorithm(algorithms);
    if (StringUtils.isBlank(chosenAlgorithm)) {
      throw new CoOpsNotImplementedException(
          "Server and client do not have a commonly supported algorithm. " + 
          "Server supported: " + StringUtils.join(getSupportedAlgorithmNames(), ',') + ", " + 
          "Client supported: " + StringUtils.join(algorithms, ','));
    }
    
    User loggedUser = sessionController.getLoggedUser();
    
    if (!materialPermissionController.hasAccessPermission(loggedUser, document)) {
      throw new CoOpsForbiddenException();
    }
    
    Long currentRevision = documentController.getDocumentRevision(document);
    String data = document.getData();
    if (data == null) {
      data = "";
    }
    
    Map<String, String> properties = settingToProperties("document.", documentController.listDocumentSettings(document));
    properties.put("title", document.getTitle());

    // TODO: Implement extensions
    Map<String, Map<String, String>> extensions = new HashMap<String, Map<String,String>>();
    
    CoOpsSession coOpsSession = coOpsSessionController.createSession(document, loggedUser, chosenAlgorithm, currentRevision);
    
    return new Join(coOpsSession.getId().toString(), coOpsSession.getAlgorithm(), coOpsSession.getJoinRevision(), data, COOPS_DOCUMENT_CONTENTTYPE, properties, extensions);
  }

  protected Document findDocument(String fileId) throws CoOpsUsageException, CoOpsNotFoundException {
    if (!StringUtils.isNumeric(fileId)) {
      throw new CoOpsUsageException("fileId must be a number");
    }
    
    Long documentId = NumberUtils.createLong(fileId);
    if (documentId == null) {
      throw new CoOpsUsageException("fileId must be a number");
    }
    
    Document document = documentController.findDocumentById(documentId);
    if (document == null) {
      throw new CoOpsNotFoundException();
    }
    
    return document;
  }

}
