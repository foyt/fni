package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.coops.CoOpsConflictException;
import fi.foyt.fni.coops.CoOpsForbiddenException;
import fi.foyt.fni.coops.CoOpsInternalErrorException;
import fi.foyt.fni.coops.CoOpsNotFoundException;
import fi.foyt.fni.coops.CoOpsNotImplementedException;
import fi.foyt.fni.coops.CoOpsUsageException;
import fi.foyt.fni.coops.model.File;
import fi.foyt.fni.coops.model.Join;
import fi.foyt.fni.coops.model.Patch;
import fi.foyt.fni.materials.DocumentController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.common.Tag;
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
import fi.foyt.fni.view.AbstractCoOpsServlet;

@WebServlet(urlPatterns = "/forge/coops/*", name = "forge-coops")
public class CoOpsServlet extends AbstractCoOpsServlet {

  private static final long serialVersionUID = 3385376824022930277L;
  private final static String[] COOPS_SUPPORTED_ALGORITHMS = { "dmp" };
	private final static String COOPS_DOCUMENT_CONTENTTYPE = "text/html;editor=CKEditor";

	@Inject
	private DocumentController documentController;

	@Inject
	private SessionController sessionController;

	@Inject
	private SystemSettingsController systemSettingsController;

	@Inject
	private TagController tagController;
	
	@Inject
  private MaterialPermissionController materialPermissionController;
	
	@Override
	protected File handleFile(HttpServletRequest request, HttpServletResponse response, Long revisionNumber, String fileId) throws CoOpsNotImplementedException, CoOpsNotFoundException, CoOpsForbiddenException {
	  if (revisionNumber != null) {
	    throw new CoOpsNotImplementedException();
	  }
	  
	  Long documentId = NumberUtils.createLong(fileId);
	  if (documentId == null) {
      throw new CoOpsNotFoundException();
    }
    
	  Document document = documentController.findDocumentById(documentId);
	  if (document == null) {
      throw new CoOpsNotFoundException();
    }
	      
    if (!materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), document)) {
      throw new CoOpsForbiddenException();
    }
    
	  Long documentRevisionNumber = documentController.getDocumentRevision(document);
	  
	  return new File(document.getId().toString(), document.getTitle(), document.getModified(), documentRevisionNumber, document.getData(), COOPS_DOCUMENT_CONTENTTYPE);
	}
	
	@Override
	protected Join handleJoin(HttpServletRequest request, HttpServletResponse response, String protocolVersion, String[] algorithms, String fileId) throws CoOpsNotImplementedException, CoOpsUsageException, CoOpsNotFoundException, CoOpsForbiddenException {
    // TODO: WebSocket support when available in Application Container

    if (!COOPS_PROTOCOL_VERSION.equals(protocolVersion)) {
      throw new CoOpsNotImplementedException("Protocol version mismatch. Client is using " + protocolVersion + " and server " + COOPS_PROTOCOL_VERSION);
    }
    
    String algorithm = null;
    for (String clientAlgorithm : algorithms) {
      if (ArrayUtils.contains(COOPS_SUPPORTED_ALGORITHMS, clientAlgorithm)) {
        algorithm = clientAlgorithm;
        break;
      }
    }
    
    if (algorithm == null) {
      throw new CoOpsNotImplementedException(
        "Server and client do not have a commonly supported algorithm. " + 
        "Server supported: " + StringUtils.join(COOPS_SUPPORTED_ALGORITHMS, ',') + ", " + 
        "Client supported: " + StringUtils.join(algorithms, ','));
    }

    if (!StringUtils.isNumeric(fileId)) {
      throw new CoOpsUsageException();
    }
    
    Long documentId = NumberUtils.createLong(fileId);
    Document document = documentController.findDocumentById(documentId);
    if (document == null) {
      throw new CoOpsNotFoundException();
    }
    
    if (!materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), document)) {
      throw new CoOpsForbiddenException();
    }

    Long revisionNumber = documentController.getDocumentRevision(document);
    String data = document.getData();
    if (data == null) {
      data = "";
    }
    
    return new Join(COOPS_SUPPORTED_EXTENSIONS, revisionNumber, data, COOPS_DOCUMENT_CONTENTTYPE, UUID.randomUUID().toString());
	}
	
	@Override
	protected void handlePatch(HttpServletRequest request, HttpServletResponse response, Patch patch, String fileId) throws CoOpsNotFoundException,
	    CoOpsForbiddenException, CoOpsConflictException, CoOpsUsageException, CoOpsInternalErrorException {

    if (!StringUtils.isNumeric(fileId)) {
      throw new CoOpsUsageException();
    }
    
    Long documentId = NumberUtils.createLong(fileId);
    Document document = documentController.findDocumentById(documentId);
    if (document == null) {
      throw new CoOpsNotFoundException();
    }
    
    if (!materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), document)) {
      throw new CoOpsForbiddenException();
    }
    
	  Long revisionNumber = documentController.getDocumentRevision(document);
    if (!revisionNumber.equals(patch.getRevisionNumber())) {
      throw new CoOpsConflictException();
    } 
    
    byte[] patchData = null;
    String checksum = null;
    User loggedUser = sessionController.getLoggedUser();
    
    if (StringUtils.isNotBlank(patch.getPatch())) {
      String oldData = document.getData();
      PatchResult patchResult = DiffUtils.applyPatch(oldData, patch.getPatch());
      if (!patchResult.allApplied()) {
        throw new CoOpsConflictException();
      }
      
      String data = patchResult.getPatchedText();
      documentController.updateDocumentData(document, data, loggedUser);
      checksum = DigestUtils.md5Hex(data);
      try {
        patchData = patch.getPatch().getBytes("UTF-8");
      } catch (UnsupportedEncodingException e) {
        throw new CoOpsInternalErrorException(e);
      }
    }

    Long patchRevisionNumber = revisionNumber + 1;
    DocumentRevision documentRevision = documentController.createDocumentRevision(document, patchRevisionNumber, new Date(), false, false, patchData, checksum, patch.getSessionId());
    
    Map<String, String> properties = patch.getProperties();
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
	protected List<Patch> handleUpdate(HttpServletRequest request, HttpServletResponse response, Long revisionNumber, String fileId)
	    throws CoOpsNotFoundException, CoOpsForbiddenException, CoOpsUsageException, CoOpsInternalErrorException {

	  if (!StringUtils.isNumeric(fileId)) {
      throw new CoOpsUsageException();
    }
    
    Long documentId = NumberUtils.createLong(fileId);
    Document document = documentController.findDocumentById(documentId);
    if (document == null) {
      throw new CoOpsNotFoundException();
    }
    
    if (!materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), document)) {
      throw new CoOpsForbiddenException();
    }
	  
	  List<DocumentRevision> documentRevisions = documentController.listDocumentRevisionsAfter(document, revisionNumber);
    
    if (documentRevisions.isEmpty()) {
      return null;
    }
    
    List<Patch> updateResults = new ArrayList<>();
    
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
      
      if (patch != null) {
        updateResults.add(new Patch(documentRevision.getRevision(), patch, properties, documentRevision.getChecksum(), documentRevision.getSessionId()));
      } else {
        updateResults.add(new Patch(documentRevision.getRevision(), null, properties, null, documentRevision.getSessionId()));
      }
    }

    return updateResults;
	}

}
