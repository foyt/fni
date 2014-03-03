package fi.foyt.fni.materials;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.foyt.fni.drive.DriveManager;
import fi.foyt.fni.persistence.dao.materials.GoogleDocumentDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.GoogleDocument;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.utils.data.TypedData;

@Dependent
@Stateful
public class GoogleDriveMaterialController {
	
  @Inject
	private DriveManager driveManager;

  @Inject
	private MaterialController materialController;

	@Inject
	private GoogleDocumentDAO googleDocumentDAO;

	/* GoogleDocument */

  public GoogleDocument createGoogleDocument(User creator, Language language, Folder parentFolder, String title, String documentId, String mimeType, MaterialPublicity publicity) {
    String urlName = materialController.getUniqueMaterialUrlName(creator, parentFolder, null, title);
    return googleDocumentDAO.create(creator, language, parentFolder, urlName, title, documentId, mimeType, publicity);
  }
  
	public GoogleDocument findGoogleDocumentById(Long googleDocumentId) {
		return googleDocumentDAO.findById(googleDocumentId);
	}

  public GoogleDocument findGoogleDocumentByCreatorAndDocumentId(User creator, String documentId) {
    return googleDocumentDAO.findByCreatorAndDocumentId(creator, documentId);
  }
  
	public String getGoogleDocumentEditLink(GoogleDocument googleDocument) throws IOException, GeneralSecurityException {
		Drive systemDrive = driveManager.getSystemDrive();
		File file = driveManager.getFile(systemDrive, googleDocument.getDocumentId());
		return file.getAlternateLink();
	}
  
	public TypedData getGoogleDocumentData(GoogleDocument googleDocument) throws MalformedURLException, IOException, GeneralSecurityException {
		Drive systemDrive = driveManager.getSystemDrive();
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
	
}
