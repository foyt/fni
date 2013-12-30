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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.jackson.map.ObjectMapper;

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
import fi.foyt.fni.materials.ImageController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.common.Tag;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.DocumentRevision;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.materials.ImageRevision;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialRevisionSetting;
import fi.foyt.fni.persistence.model.materials.MaterialType;
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
  private final static Map<MaterialType, String[]> COOPS_SUPPORTED_ALGORITHMS;
	private final static String COOPS_DOCUMENT_CONTENTTYPE = "text/html;editor=CKEditor";
  
  @Inject
	private MaterialController materialController;
  
  @Inject
  private DocumentController documentController;
  
  @Inject
  private ImageController imageController;

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
	  
	  Long materialId = NumberUtils.createLong(fileId);
	  if (materialId == null) {
      throw new CoOpsNotFoundException();
    }
    
	  Material material = materialController.findMaterialById(materialId);
	  if (material == null) {
      throw new CoOpsNotFoundException();
    }
	      
    if (!materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), material)) {
      throw new CoOpsForbiddenException();
    }

    switch (material.getType()) {
      case DOCUMENT:
       return handleFileDocument((Document) material);
      case IMAGE:
        return handleFileImage((Image) material);
      default:
        throw new CoOpsNotImplementedException("Material type " + material.getType() + " does not support CoOps");
    }
	}
	
	private File handleFileDocument(Document document) {
    Long revisionNumber = documentController.getDocumentRevision(document);
    String data = document.getData();
    return new File(document.getId().toString(), document.getTitle(), document.getModified(), revisionNumber, data, COOPS_DOCUMENT_CONTENTTYPE);
	}

  private File handleFileImage(Image image) {
    Long revisionNumber = imageController.getImageRevision(image);
    String data = Base64.encodeBase64String(image.getData());
    return new File(image.getId().toString(), image.getTitle(), image.getModified(), revisionNumber, data, image.getContentType());
  }
	
	@Override
	protected Join handleJoin(HttpServletRequest request, HttpServletResponse response, String protocolVersion, String[] algorithms, String fileId) throws CoOpsNotImplementedException, CoOpsUsageException, CoOpsNotFoundException, CoOpsForbiddenException {
    // TODO: WebSocket support when available in Application Container

    if (!COOPS_PROTOCOL_VERSION.equals(protocolVersion)) {
      throw new CoOpsNotImplementedException("Protocol version mismatch. Client is using " + protocolVersion + " and server " + COOPS_PROTOCOL_VERSION);
    }

    if (!StringUtils.isNumeric(fileId)) {
      throw new CoOpsUsageException();
    }
    
    Long materialId = NumberUtils.createLong(fileId);
    Material material = materialController.findMaterialById(materialId);
    if (material == null) {
      throw new CoOpsNotFoundException();
    }
    
    String[] supportedAlgorithms = COOPS_SUPPORTED_ALGORITHMS.get(material.getType());
    if (supportedAlgorithms == null) {
      throw new CoOpsNotImplementedException("Material type " + material.getType() + " does not support CoOps");
    }
    
    String algorithm = null;
    for (String clientAlgorithm : algorithms) {
      if (ArrayUtils.contains(supportedAlgorithms, clientAlgorithm)) {
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
    
    if (!materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), material)) {
      throw new CoOpsForbiddenException();
    }

    switch (material.getType()) {
      case DOCUMENT:
        return handleJoinDocument((Document) material);
      case IMAGE:
        return handleJoinImage((Image) material);
      default:
        throw new CoOpsNotImplementedException("Material type " + material.getType() + " does not support CoOps");
    }
	}

  private Join handleJoinDocument(Document document) {
    Long revisionNumber = documentController.getDocumentRevision(document);
    String data = document.getData();
    if (data == null) {
      data = "";
    }
    
    return new Join(COOPS_SUPPORTED_EXTENSIONS, revisionNumber, data, COOPS_DOCUMENT_CONTENTTYPE, UUID.randomUUID().toString()); 
  }
  
  private Join handleJoinImage(Image image) {
    Long revisionNumber = imageController.getImageRevision(image);
    String data = (image.getData() != null) ? Base64.encodeBase64String(image.getData()) : "";
    return new Join(COOPS_SUPPORTED_EXTENSIONS, revisionNumber, data, image.getContentType(), UUID.randomUUID().toString()); 
  }
	
	@Override
	protected void handlePatch(HttpServletRequest request, HttpServletResponse response, Patch patch, String fileId) throws CoOpsNotFoundException,
	    CoOpsForbiddenException, CoOpsConflictException, CoOpsUsageException, CoOpsInternalErrorException, CoOpsNotImplementedException {

    if (!StringUtils.isNumeric(fileId)) {
      throw new CoOpsUsageException();
    }
    
    Long materialId = NumberUtils.createLong(fileId);
    Material material = materialController.findMaterialById(materialId);
    if (material == null) {
      throw new CoOpsNotFoundException();
    }
    
    if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), material)) {
      throw new CoOpsForbiddenException();
    }

    switch (material.getType()) {
      case DOCUMENT:
        handlePatchDocument((Document) material, patch);
        return;
      case IMAGE:
        handlePatchImage((Image) material, patch);
        return;
      default:
        throw new CoOpsNotImplementedException("Material type " + material.getType() + " does not support CoOps");
    }
	}
	
	private void handlePatchImage(Image image, Patch patch) throws CoOpsUsageException, CoOpsConflictException {
	  Long revisionNumber = imageController.getImageRevision(image);
    if (!revisionNumber.equals(patch.getRevisionNumber())) {
      throw new CoOpsConflictException();
    } 
    
    byte[] patchData = null;
    String checksum = null;
    User loggedUser = sessionController.getLoggedUser();
    
    if (StringUtils.isNotBlank(patch.getPatch())) {
      ObjectMapper objectMapper = new ObjectMapper();
      
      UInt2DArrLWChange[] changes;
      try {
        changes = objectMapper.readValue(patch.getPatch(), UInt2DArrLWChange[].class);
      } catch (IOException e) {
        throw new CoOpsUsageException();
      }

      
      if (changes.length > 0) {
        Integer imageWidth = 860; // TODO: Image width
        byte[] data = image.getData();
        for (UInt2DArrLWChange change : changes) {
          int offset = (change.getX() + (change.getY() * imageWidth)) << 2;
          if (offset >= data.length) {
            throw new CoOpsUsageException();
          }
          
          long v = change.getV();
          byte r = (byte) ((v & 4278190080l) >> 24);
          byte g = (byte) ((v & 16711680) >> 16);
          byte b = (byte) ((v & 65280) >> 8);
          byte a = (byte) (v & 255);

          data[offset] = r;
          data[offset + 1] = g;
          data[offset + 2] = b;
          data[offset + 3] = a;
        }
        
        imageController.updateImageContent(image, image.getContentType(), data, sessionController.getLoggedUser());
      }
    }

    Long patchRevisionNumber = revisionNumber + 1;
    ImageRevision imageRevision = imageController.createImageRevision(image, patchRevisionNumber, new Date(), false, false, patchData, checksum, patch.getSessionId());
    
    Map<String, String> properties = patch.getProperties();
    if (properties != null) {
      Iterator<String> keyIterator = properties.keySet().iterator();
      while (keyIterator.hasNext()) {
        String key = keyIterator.next();
        String value = properties.get(key);
        if ("title".equals(key)) {
          // title is saved as a image title
          imageController.updateImageTitle(image, value, loggedUser);
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
          
          imageController.setImageTags(image, tags);
        } else {
          // everything else is saved as settings
          imageController.setImageSetting(image, key, value);
        }
        
        // Everything is saved as revision settings
        imageController.createImageRevisionSetting(imageRevision, key, value);
      }
    }
  }

  private void handlePatchDocument(Document document, Patch patch) throws CoOpsConflictException, CoOpsInternalErrorException {
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
	    throws CoOpsNotFoundException, CoOpsForbiddenException, CoOpsUsageException, CoOpsInternalErrorException, CoOpsNotImplementedException {

	  if (!StringUtils.isNumeric(fileId)) {
      throw new CoOpsUsageException();
    }
    
    Long materialId = NumberUtils.createLong(fileId);
    Material material = materialController.findMaterialById(materialId);
    if (material == null) {
      throw new CoOpsNotFoundException();
    }
    
    if (!materialPermissionController.hasAccessPermission(sessionController.getLoggedUser(), material)) {
      throw new CoOpsForbiddenException();
    }
    
    switch (material.getType()) {
      case DOCUMENT:
        return handleUpdateDocument((Document) material, revisionNumber);
      case IMAGE:
        return handleUpdateImage((Image) material, revisionNumber);
      default:
        throw new CoOpsNotImplementedException("Material type " + material.getType() + " does not support CoOps");
    }    
	}

  private List<Patch> handleUpdateDocument(Document document, Long revisionNumber) throws CoOpsInternalErrorException {
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


  private List<Patch> handleUpdateImage(Image image, Long revisionNumber) throws CoOpsInternalErrorException {
    List<ImageRevision> imageRevisions = imageController.listImageRevisionsAfter(image, revisionNumber);
    
    if (imageRevisions.isEmpty()) {
      return null;
    }
    
    List<Patch> updateResults = new ArrayList<>();
    
    for (ImageRevision imageRevision : imageRevisions) {
      String patch = null;
      byte[] patchData = imageRevision.getData();
      if (patchData != null) {
        if (imageRevision.getCompressed()) {
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
      
      List<MaterialRevisionSetting> revisionSettings = imageController.listImageRevisionSettings(imageRevision);
      if (revisionSettings.size() > 0) {
        properties = new HashMap<>();
        for (MaterialRevisionSetting revisionSetting : revisionSettings) {
          String key = StringUtils.removeStart(revisionSetting.getKey().getName(), "image.");
          properties.put(key, revisionSetting.getValue());
        }
      }
      
      if (patch != null) {
        updateResults.add(new Patch(imageRevision.getRevision(), patch, properties, imageRevision.getChecksum(), imageRevision.getSessionId()));
      } else {
        updateResults.add(new Patch(imageRevision.getRevision(), null, properties, null, imageRevision.getSessionId()));
      }
    }
    
    return updateResults;
  }

	private static class UInt2DArrLWChange {
	  
	  public Integer getX() {
      return x;
    }
	  
	  @SuppressWarnings("unused")
    public void setX(Integer x) {
      this.x = x;
    }
	  
	  public Integer getY() {
      return y;
    }
	  
	  @SuppressWarnings("unused")
    public void setY(Integer y) {
      this.y = y;
    }
	  
	  public Long getV() {
      return v;
    }
	  
	  @SuppressWarnings("unused")
    public void setV(Long v) {
      this.v = v;
    }
	  
	  private Integer x;
	  private Integer y;
	  private Long v;
	};
	
	static {
	  COOPS_SUPPORTED_ALGORITHMS = new HashMap<MaterialType, String[]>();
    COOPS_SUPPORTED_ALGORITHMS.put(MaterialType.DOCUMENT, new String[] { "dmp" });
    COOPS_SUPPORTED_ALGORITHMS.put(MaterialType.IMAGE, new String[] { "uint2darr-lw" });
	}
}
