package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.MimeTypeParseException;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.temp.SessionTempController;
import fi.foyt.fni.utils.data.FileData;

@RequestScoped
@Named
@Stateful
@Join (path = "/forge/upload", to = "/forge/upload.jsf")
@LoggedIn
public class ForgeUploadBackingBean {
  
  @Parameter
  private Long parentFolderId;
  
  @Inject
  private Logger logger;

  @Inject
  private MaterialController materialController;

  @Inject
  private SessionController sessionController;

  @Inject
  private MaterialPermissionController materialPermissionController;

  @Inject
  private SessionTempController sessionTempController;
  
  @Inject
  private NavigationController navigationController;

	@RequestAction
	public String load() {
	  if (!sessionController.isLoggedIn()) {
      return navigationController.requireLogin();
    }
	  
    folders = new ArrayList<>();
    if (parentFolderId != null) {
      Folder parentFolder = parentFolderId != null ? materialController.findFolderById(parentFolderId) : null;
      if (parentFolder != null) {
        if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), parentFolder)) {
          return navigationController.accessDenied();
        }
        
        Folder folder = parentFolder;
        while (folder != null) {
          folders.add(0, folder);
          folder = folder.getParentFolder();
        };
      } else {
        return navigationController.notFound();
      }
    }
    
    return null;
	}
	
	public Long getParentFolderId() {
    return parentFolderId;
  }
	
	public void setParentFolderId(Long parentFolderId) {
    this.parentFolderId = parentFolderId;
  }
	
	public String getFileInfos() {
    return fileInfos;
  }
	
	public void setFileInfos(String fileInfos) {
    this.fileInfos = fileInfos;
  }

  public List<Folder> getFolders() {
    return folders;
  }
  
  public Boolean getConvert() {
    return convert;
  }
  
  public void setConvert(Boolean convert) {
    this.convert = convert;
  }
  
  public String save() {
    if (!sessionController.isLoggedIn()) {
      return navigationController.requireLogin();
    }
    
    User loggedUser = sessionController.getLoggedUser();
    
    Folder parentFolder = null;
    if (parentFolderId != null) {
      parentFolder = materialController.findFolderById(parentFolderId);
      if (parentFolder != null) {
        if (!materialPermissionController.hasModifyPermission(sessionController.getLoggedUser(), parentFolder)) {
          return navigationController.accessDenied();
        }
      } else {
        return navigationController.notFound();
      }
    }
    
    List<FileInfo> files;
    try {
      files = new ObjectMapper().readValue(getFileInfos(), new TypeReference<List<FileInfo>>() { });
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to unmarshal file infos", e);
      return navigationController.internalError();
    }
    
    List<FileData> fileDatas = new ArrayList<>();

    for (FileInfo file : files) {
      try {
        byte[] data = sessionTempController.getTempFileData(file.getFileId());
        fileDatas.add(new FileData(null, file.getFileName(), data, file.getFileType(), null));
        sessionTempController.deleteTempFile(file.getFileId());
      } catch (IOException e) {
        logger.log(Level.SEVERE, String.format("Failed to extract file data for file %s", file.getFileId()), e);
        return navigationController.internalError();
      }
    }
    
    for (FileData fileData : fileDatas) {
      if (convert) {
        try {
          materialController.createMaterial(parentFolder, loggedUser, fileData);
        } catch (MimeTypeParseException | IOException | GeneralSecurityException e) {
          logger.log(Level.SEVERE, String.format("Failed to convert file into Forge & Illusion format"), e);
          return navigationController.internalError();
        }
      } else {
        materialController.createFile(parentFolder, loggedUser, fileData.getData(), fileData.getContentType(), fileData.getFileName());
      }
    }

    if (parentFolder != null) {
      Long ownerId = parentFolder.getCreator().getId();
      String urlPath = parentFolder.getPath().substring(String.valueOf(ownerId).length() + 1);
      return String.format("/forge/folder.jsf?faces-redirect=true&ownerId=%d&urlName=%s", ownerId, urlPath);
    } else {
      return "/forge/index.jsf?faces-redirect=true";
    }
  }
  
  private List<Folder> folders;
  private String fileInfos;
  private Boolean convert;
  
  public static class FileInfo {
    
    public String getFileId() {
      return fileId;
    }
    
    public void setFileId(String fileId) {
      this.fileId = fileId;
    }
    
    public String getFileName() {
      return fileName;
    }
    
    public void setFileName(String fileName) {
      this.fileName = fileName;
    }
    
    public String getFileType() {
      return fileType;
    }
    
    public void setFileType(String fileType) {
      this.fileType = fileType;
    }
    
    private String fileId;
    private String fileName;
    private String fileType;
  }
}
