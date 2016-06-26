package fi.foyt.fni.coops;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.jackson.map.ObjectMapper;

import fi.foyt.coops.CoOpsConflictException;
import fi.foyt.coops.CoOpsForbiddenException;
import fi.foyt.coops.CoOpsInternalErrorException;
import fi.foyt.coops.CoOpsNotFoundException;
import fi.foyt.coops.CoOpsNotImplementedException;
import fi.foyt.coops.CoOpsUsageException;
import fi.foyt.coops.model.File;
import fi.foyt.coops.model.Join;
import fi.foyt.coops.model.Patch;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.common.Tag;
import fi.foyt.fni.persistence.model.materials.CoOpsSession;
import fi.foyt.fni.persistence.model.materials.CoOpsSessionType;
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
  private MaterialController materialController;

  @Inject
  private CoOpsSessionController coOpsSessionController;

  @Inject
  private SessionController sessionController;
  
  @Inject
  private TagController tagController;
  
  @Inject
  private SystemSettingsController systemSettingsController;

  @Inject
  private MaterialPermissionController materialPermissionController;

  @Inject
  private CoOpsSessionEventsController coOpsSessionEventsController;
  
  @Inject
  private Event<CoOpsPatchEvent> messageEvent;
  
  @Inject
  private Event<CoOpsSessionOpenEvent> sessionOpenEvent;

  @Inject
  private HttpServletRequest httpRequest;

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
      Long currentRevisionNumber = materialController.getDocumentRevision(document);
      String data = document.getData();
      
      Map<String, String> properties = settingToProperties("document.", materialController.listDocumentSettings(document));
      properties.put("title", document.getTitle());

      return new File(currentRevisionNumber, data, COOPS_DOCUMENT_CONTENTTYPE, properties);
    }
  }

  @Override
  public List<Patch> fileUpdate(String fileId, String sessionId, Long revisionNumber) throws CoOpsNotFoundException, CoOpsInternalErrorException, CoOpsUsageException, CoOpsForbiddenException {
    CoOpsSession session = coOpsSessionController.findSessionBySessionId(sessionId);
    if (session == null) {
      throw new CoOpsUsageException("Invalid session id"); 
    }
    
    if (revisionNumber == null) {
      throw new CoOpsUsageException("revisionNumber parameter is missing");
    }
    
    Document document = findDocument(fileId);
    
    if (!materialPermissionController.hasAccessPermission(session.getUser(), document)) {
      throw new CoOpsForbiddenException();
    }
    
    ObjectMapper objectMapper = new ObjectMapper();
    
    List<DocumentRevision> documentRevisions = materialController.listDocumentRevisionsAfter(document, revisionNumber);
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
        Map<String, Object> extensions = null;
        
        List<MaterialRevisionSetting> revisionSettings = materialController.listDocumentRevisionSettings(documentRevision);
        if (!revisionSettings.isEmpty()) {
          properties = new HashMap<>();
          for (MaterialRevisionSetting revisionSetting : revisionSettings) {
            String settingKey = revisionSetting.getKey().getName();
            
            if (StringUtils.startsWith(settingKey, "extension.")) {
              String key = StringUtils.removeStart(settingKey, "extension.");
              if (extensions == null) {
                extensions = new HashMap<String, Object>();
              }
              
              try {
                Object object = objectMapper.readValue(revisionSetting.getValue(), Object.class);
                extensions.put(key, object);
              } catch (IOException e) {
                throw new CoOpsInternalErrorException(e);
              }
            } else {
              String key = StringUtils.removeStart(settingKey, "document.");
              properties.put(key, revisionSetting.getValue());
            }
          }
        }
        
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
    CoOpsSession session = coOpsSessionController.findSessionBySessionId(sessionId);
    if (session == null) {
      throw new CoOpsUsageException("Invalid session id"); 
    }
    
    if (!"dmp".equals(session.getAlgorithm())) {
      throw new CoOpsUsageException("Algorithm is not supported by this server");
    }
    
    Document document = findDocument(fileId);
    
    if (!materialPermissionController.hasModifyPermission(session.getUser(), document)) {
      throw new CoOpsForbiddenException();
    }

    Long currentRevision = materialController.getDocumentRevision(document);
    if (!currentRevision.equals(revisionNumber)) {
      throw new CoOpsConflictException();
    }
    
    ObjectMapper objectMapper = new ObjectMapper();
    
    byte[] patchData = null;
    String checksum = null;
    
    if (StringUtils.isNotBlank(patch)) {
      String oldData = document.getData();
      PatchResult patchResult = DiffUtils.applyPatch(oldData, patch);
      if (!patchResult.allApplied()) {
        throw new CoOpsConflictException();
      }
      
      String data = patchResult.getPatchedText();
      materialController.updateDocumentData(document, data, session.getUser());
      checksum = DigestUtils.md5Hex(data);
      try {
        patchData = patch.getBytes("UTF-8");
      } catch (UnsupportedEncodingException e) {
        throw new CoOpsInternalErrorException(e);
      }
    }

    Long patchRevisionNumber = currentRevision + 1;
    DocumentRevision documentRevision = materialController.createDocumentRevision(document, patchRevisionNumber, new Date(), false, false, patchData, checksum, sessionId);
    
    if (properties != null) {
      Iterator<String> keyIterator = properties.keySet().iterator();
      while (keyIterator.hasNext()) {
        String key = keyIterator.next();
        String value = properties.get(key);
        if ("title".equals(key)) {
          // title is saved as a document title
          materialController.updateDocumentTitle(document, value, session.getUser());
        } else if ("langCode".equals(key)) {
          // language is saved as document language property
          Language language = systemSettingsController.findLocaleByIso2(value);
          materialController.updateDocumentLanguage(document, language, session.getUser());
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
          
          materialController.setDocumentTags(document, tags);
        } else {
          // everything else is saved as document.property
          materialController.setDocumentSetting(document, "document." + key, value);
        }
        
        // Everything is saved as revision setting
        materialController.createDocumentRevisionSetting(documentRevision, "document." + key, value);
      }
    }
    
    if (extensions != null) {
      for (String extension : extensions.keySet()) {
        try {
          String value = objectMapper.writeValueAsString(extensions.get(extension));
          materialController.createDocumentRevisionSetting(documentRevision, "extension." + extension, value);
        } catch (IOException e) {
          throw new CoOpsInternalErrorException(e);
        }
      }
    }
    
    messageEvent.fire(new CoOpsPatchEvent(fileId, new Patch(sessionId, patchRevisionNumber, checksum, patch, properties, extensions)));
  }

  @Override
  public Join fileJoin(String fileId, List<String> algorithms, String protocolVersion) throws CoOpsNotFoundException, CoOpsNotImplementedException, CoOpsInternalErrorException, CoOpsForbiddenException, CoOpsUsageException {
    Document document = findDocument(fileId);
    
    if (!COOPS_PROTOCOL_VERSION.equals(protocolVersion)) {
      throw new CoOpsNotImplementedException("Protocol version mismatch. Client is using " + protocolVersion + " and server " + COOPS_PROTOCOL_VERSION);
    }
    
    if (algorithms == null || algorithms.isEmpty()) {
      throw new CoOpsInternalErrorException("Invalid request");
    }
    
    if (!algorithms.contains("dmp")) {
      throw new CoOpsNotImplementedException(
          "Server and client do not have a commonly supported algorithm. " + 
          "Server supported: dmp" + 
          "Client supported: " + StringUtils.join(algorithms, ','));
    }
    
    User loggedUser = sessionController.getLoggedUser();
    
    if (!materialPermissionController.hasAccessPermission(loggedUser, document)) {
      throw new CoOpsForbiddenException();
    }
    
    Long currentRevision = materialController.getDocumentRevision(document);
    String data = document.getData();
    if (data == null) {
      data = "";
    }

    List<CoOpsSession> openSessions = coOpsSessionController.listSessionsByMaterialAndClosed(document, Boolean.FALSE);

    Map<String, String> properties = settingToProperties("document.", materialController.listDocumentSettings(document));
    properties.put("title", document.getTitle());

    Map<String, Object> extensions = new HashMap<>();

    CoOpsSession coOpsSession = coOpsSessionController.createSession(document, loggedUser, CoOpsSessionType.REST, "dmp", currentRevision);
    extensions.put("sessionEvents", coOpsSessionEventsController.createSessionEvents(openSessions, "OPEN"));
    
    String wsUrl = String.format("ws://%s:%s%s/ws/coops/document/%d/%s", 
        httpRequest.getServerName(), 
        httpRequest.getServerPort(), 
        httpRequest.getContextPath(), 
        document.getId(), 
        coOpsSession.getSessionId());
    
    Map<String, Object> webSocketExtension = new HashMap<>();
    webSocketExtension.put("ws", wsUrl);
    extensions.put("webSocket", webSocketExtension);
    
    sessionOpenEvent.fire(new CoOpsSessionOpenEvent(coOpsSession.getSessionId()));
    
    return new Join(coOpsSession.getSessionId(), coOpsSession.getAlgorithm(), coOpsSession.getJoinRevision(), data, COOPS_DOCUMENT_CONTENTTYPE, properties, extensions);
  }

  protected Document findDocument(String fileId) throws CoOpsUsageException, CoOpsNotFoundException {
    if (!StringUtils.isNumeric(fileId)) {
      throw new CoOpsUsageException("fileId must be a number");
    }
    
    Long documentId = NumberUtils.createLong(fileId);
    if (documentId == null) {
      throw new CoOpsUsageException("fileId must be a number");
    }
    
    Document document = materialController.findDocumentById(documentId);
    if (document == null) {
      throw new CoOpsNotFoundException();
    }
    
    return document;
  }

}
