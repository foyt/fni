package fi.foyt.fni.persistence.dao.store;

import javax.enterprise.context.RequestScoped;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.store.FileProduct;
import fi.foyt.fni.persistence.model.store.FileProductFile;

@RequestScoped
@DAO
public class FileProductDAO extends GenericDAO<FileProduct> {

	public FileProduct updateFile(FileProduct fileProduct, FileProductFile file) {
		fileProduct.setFile(file);
		getEntityManager().persist(fileProduct);
		return fileProduct;
	}
  
}
