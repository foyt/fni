package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;

import javax.persistence.EntityManager;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.File;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class FileDAO extends GenericDAO<File> {

	private static final long serialVersionUID = 1L;

	public File create(User creator, Date created, User modifier, Date modified, Language language, Folder parentFolder, String urlName, String title, byte[] data, String contentType, MaterialPublicity publicity) {
    EntityManager entityManager = getEntityManager();

    File file = new File();
    file.setCreated(created);
    file.setCreator(creator);
    file.setData(data);
    file.setContentType(contentType);
    file.setModified(modified);
    file.setModifier(modifier);
    file.setTitle(title);
    file.setUrlName(urlName);
    file.setPublicity(publicity);

    if (language != null)
      file.setLanguage(language);

    if (parentFolder != null)
      file.setParentFolder(parentFolder);

    entityManager.persist(file);

    return file;
  }
  
  public File updateData(File file, User modifier, byte[] data) {
    EntityManager entityManager = getEntityManager();

    file.setData(data);
    file.setModified(new Date());
    file.setModifier(modifier);

    entityManager.persist(file);
    
    return file;
  }

  public File updateContentType(File file, User modifier, String contentType) {
    EntityManager entityManager = getEntityManager();

    file.setContentType(contentType);
    file.setModified(new Date());
    file.setModifier(modifier);

    entityManager.persist(file);
    
    return file;
  }

}
