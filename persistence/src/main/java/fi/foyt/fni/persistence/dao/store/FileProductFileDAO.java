package fi.foyt.fni.persistence.dao.store;


import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.store.FileProductFile;

@DAO
public class FileProductFileDAO extends GenericDAO<FileProductFile> {
  
	private static final long serialVersionUID = 1L;

	public FileProductFile create(byte[] content, String contentType) {
		FileProductFile fileProductFile = new FileProductFile();
		fileProductFile.setContent(content);
		fileProductFile.setContentType(contentType);
		getEntityManager().persist(fileProductFile);
		return fileProductFile;
	}

	public FileProductFile updateContentType(FileProductFile fileProductFile, String contentType) {
		fileProductFile.setContentType(contentType);
		getEntityManager().persist(fileProductFile);
		return fileProductFile;
	}

	public FileProductFile updateContent(FileProductFile fileProductFile, byte[] content) {
		fileProductFile.setContent(content);
		getEntityManager().persist(fileProductFile);
		return fileProductFile;
	}
	
}
