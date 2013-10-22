package fi.foyt.fni.view.forge;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;

import fi.foyt.fni.materials.DocumentController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.PdfController;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Named
@Stateful
public class ForgeMaterialActionBackingBean {

	@Inject
	private MaterialController materialController;

	@Inject
	private DocumentController documentController;

	@Inject
	private PdfController pdfController;
	
	@Inject
	private SessionController sessionController;


	public void deleteMaterial() throws IOException {
		// TODO: Security 
		
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
	public void printFile() throws DocumentException, IOException, ParserConfigurationException, SAXException {
		// TODO: Security
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
	
	public Long getMaterialId() {
		return materialId;
	}
	
	public void setMaterialId(Long materialId) {
		this.materialId = materialId;
	}
	
	private Long materialId;
}
