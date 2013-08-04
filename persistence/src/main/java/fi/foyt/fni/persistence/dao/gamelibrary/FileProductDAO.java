package fi.foyt.fni.persistence.dao.gamelibrary;


import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.gamelibrary.FileProduct;
import fi.foyt.fni.persistence.model.gamelibrary.FileProductFile;

@DAO
public class FileProductDAO extends GenericDAO<FileProduct> {

	private static final long serialVersionUID = 1L;

	public FileProduct updateFile(FileProduct fileProduct, FileProductFile file) {
		fileProduct.setFile(file);
		getEntityManager().persist(fileProduct);
		return fileProduct;
	}
  
}
