package fi.foyt.fni.persistence.dao.store;

import javax.enterprise.context.RequestScoped;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.store.FileProductFile;

@RequestScoped
@DAO
public class FileProductFileDAO extends GenericDAO<FileProductFile> {
  
	public FileProductFile create(byte[] content, String contentType) {
		FileProductFile fileProductFile = new FileProductFile();
		fileProductFile.setContent(content);
		fileProductFile.setContentType(contentType);
		getEntityManager().persist(fileProductFile);
		return fileProductFile;
	}
	
}
