package fi.foyt.fni.materials;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.foyt.fni.drive.DriveManager;
import fi.foyt.fni.persistence.dao.materials.GoogleDocumentDAO;
import fi.foyt.fni.persistence.model.materials.GoogleDocument;
import fi.foyt.fni.utils.data.TypedData;

@Dependent
@Stateful
public class GoogleDriveMaterialController {
	
	@Inject
	private Logger logger;

	@Inject
	private DriveManager driveManager;

	@Inject
	private GoogleDocumentDAO googleDocumentDAO;

	/* GoogleDocument */
	
	public GoogleDocument findGoogleDocumentById(Long googleDocumentId) {
		return googleDocumentDAO.findById(googleDocumentId);
	}
	
	public TypedData getGoogleDocumentData(GoogleDocument googleDocument) throws MalformedURLException, IOException, GeneralSecurityException {
		Drive systemDrive = driveManager.getSystemDrive();
		File file = driveManager.getFile(systemDrive, googleDocument.getDocumentId());
		TypedData typedData = null;
		
		switch (googleDocument.getDocumentType()) {
			case DOCUMENT:
				typedData = driveManager.exportFile(systemDrive, file, "text/html");
			break;
			case DRAWING:
				typedData = driveManager.exportFile(systemDrive, file, "image/png");
			break;
			case PRESENTATION:
				typedData = driveManager.exportFile(systemDrive, file, "application/pdf");
			break;
			case SPREADSHEET:
				typedData = driveManager.exportSpreadsheet(systemDrive, file);
			break;
			case FOLDER:
			break;
			case FILE:
				typedData = driveManager.downloadFile(systemDrive, file);
			break;
		}
		
		return typedData;
	}

}
