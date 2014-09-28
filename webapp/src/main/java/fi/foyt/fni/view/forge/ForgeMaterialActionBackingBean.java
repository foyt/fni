package fi.foyt.fni.view.forge;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;

import fi.foyt.fni.materials.DocumentController;
import fi.foyt.fni.materials.FolderController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.materials.MaterialUserController;
import fi.foyt.fni.materials.PdfController;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialRole;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.security.UnauthorizedException;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Named
@Stateful
public class ForgeMaterialActionBackingBean {

	@Inject
	private MaterialController materialController;

  @Inject
  private UserController userController;

	@Inject
	private DocumentController documentController;

  @Inject
	private PdfController pdfController;

  @Inject
	private FolderController folderController;
	
	@Inject
	private SessionController sessionController;

  @Inject
	private MaterialUserController materialUserController;

  @Inject
	private MaterialPermissionController materialPermissionController;
  
  public Long getMaterialId() {
    return materialId;
  }
  
  public void setMaterialId(Long materialId) {
    this.materialId = materialId;
  }
  
  public Long getMoveTargetFolderId() {
    return moveTargetFolderId;
  }
  
  public void setMoveTargetFolderId(Long moveTargetFolderId) {
    this.moveTargetFolderId = moveTargetFolderId;
  }
  
  public Long getParentFolderId() {
    return parentFolderId;
  }
  
  public void setParentFolderId(Long parentFolderId) {
    this.parentFolderId = parentFolderId;
  }
  
  public String getMaterialSharePublicity() {
    return materialSharePublicity;
  }
  
  public void setMaterialSharePublicity(String materialSharePublicity) {
    this.materialSharePublicity = materialSharePublicity;
  }
  
  public Map<String, String> getMaterialShareCollaborators() {
    return materialShareCollaborators;
  }
  
  public void setMaterialShareCollaborators(Map<String, String> materialShareCollaborators) {
    this.materialShareCollaborators = materialShareCollaborators;
  }

  public String getNewFolderName() {
    return newFolderName;
  }
  
  public void setNewFolderName(String newFolderName) {
    this.newFolderName = newFolderName;
  }
  
  @LoggedIn
  @Secure (Permission.MATERIAL_DELETE)
  @SecurityContext(context = "#{forgeMaterialActionBackingBean.materialId}")
  public void deleteMaterial() throws IOException {
    Material material = materialController.findMaterialById(getMaterialId());
    if (material != null) {
      Folder parentFolder = material.getParentFolder();
      materialController.deleteMaterial(material, sessionController.getLoggedUser());
      
      if (parentFolder != null) {
        FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
          .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
          .append("/forge/folders/")
          .append(parentFolder.getPath())
          .toString());
      } else {
        FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
          .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
          .append("/forge/")
          .toString());
      }
    }
  }

  @LoggedIn
  @Secure (Permission.MATERIAL_MODIFY)
  @SecurityContext(context = "#{forgeMaterialActionBackingBean.materialId}")
  public void moveMaterial() throws IOException {
    
    Material material = materialController.findMaterialById(getMaterialId());
    if (material != null) {
      Folder targetFolder = moveTargetFolderId == null ? null : folderController.findFolderById(moveTargetFolderId);
      materialController.moveMaterial(material, targetFolder, sessionController.getLoggedUser());
      
      if (targetFolder != null) {
        FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
          .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
          .append("/forge/folders/")
          .append(targetFolder.getPath())
          .toString());
      } else {
        FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
          .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
          .append("/forge/")
          .toString());
      }
    }
  }

	@LoggedIn
	@Secure (Permission.MATERIAL_ACCESS)
	@SecurityContext(context = "#{forgeMaterialActionBackingBean.materialId}")
	public void printFile() throws DocumentException, IOException, ParserConfigurationException, SAXException {
		// TODO: Proper error handling 
		
		Document document = documentController.findDocumentById(getMaterialId());
		if (document == null) {
			throw new FileNotFoundException();
		}
		
		User loggedUser = sessionController.getLoggedUser();
		
		String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		String baseUrl = FacesUtils.getLocalAddress(true);
		
		TypedData pdfData = documentController.printDocumentAsPdf(contextPath, baseUrl, loggedUser, document);
		if (pdfData != null) {
			Folder parentFolder = document.getParentFolder();
			
			pdfController.createPdf(loggedUser, document.getLanguage(), parentFolder, document.getUrlName() + ".pdf", document.getTitle(), pdfData.getData());
			
			if (parentFolder != null) {
				FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
    		  .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
    		  .append("/forge/folders/")
    		  .append(parentFolder.getPath())
    		  .toString());
			} else {
				FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
    		  .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
    		  .append("/forge/")
    		  .toString());
			}
		}
	}
	
	@LoggedIn
	@Secure (Permission.MATERIAL_MODIFY)
  @SecurityContext(context = "#{forgeMaterialActionBackingBean.materialId}")
  public void materialShareSave() {
	  User loggedUser = sessionController.getLoggedUser();
	  
	  Material material = materialController.findMaterialById(getMaterialId());
	  
	  Map<String, String> collaborators = getMaterialShareCollaborators();
	  for (String collaboratorStr : collaborators.keySet()) {
	    if (StringUtils.isNotBlank(collaboratorStr)) {
	      Long id = NumberUtils.createLong(StringUtils.substring(collaboratorStr, 1));
        String roleStr = collaborators.get(collaboratorStr);
        MaterialRole role = StringUtils.isBlank(roleStr) || "NONE".equals(roleStr) ? null : MaterialRole.valueOf(roleStr);
	          
	      switch (collaboratorStr.charAt(0)) {
  	      case 'U':
  	        User user = userController.findUserById(id);
  	        // TODO: Modifier
  	        materialUserController.setMaterialUserRole(user, material, role);
  	      break;
  	    }
  	    
	    }
	  }
	  
	  MaterialPublicity publicity = MaterialPublicity.valueOf(getMaterialSharePublicity());
	  
	  if (publicity != material.getPublicity()) {
	    materialController.updateMaterialPublicity(material, publicity, loggedUser);
	  }
	}
	
	@LoggedIn
  public void newFolder() throws IOException {
	  if (StringUtils.isBlank(getNewFolderName())) {
	    FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("forge.newFolder.nameRequired"));
	  } else {
	    User loggedUser = sessionController.getLoggedUser();
  	  Folder parentFolder = getParentFolderId() != null ? folderController.findFolderById(getParentFolderId()) : null;
  	  if (parentFolder != null) {
  	    if (!materialPermissionController.hasModifyPermission(loggedUser, parentFolder)) {
  	      throw new UnauthorizedException();
  	    }
  	  }
  	  
	    Folder folder = folderController.createFolder(parentFolder, getNewFolderName(), loggedUser);
      FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
        .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
        .append("/forge/folders/")
        .append(folder.getPath())
        .toString());
	  }
	}
	
	private Long materialId;
	private Long parentFolderId;
	private Long moveTargetFolderId;
	private String materialSharePublicity;
	private Map<String, String> materialShareCollaborators;
	private String newFolderName;
}
