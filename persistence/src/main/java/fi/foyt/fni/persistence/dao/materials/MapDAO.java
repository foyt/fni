package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;

import javax.persistence.EntityManager;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.Map;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class MapDAO extends GenericDAO<Map> {

	private static final long serialVersionUID = 1L;

	public Map create(User creator, Date created, User modifier, Date modified, Language language, Folder parentFolder, String urlName, String title, byte[] data, MaterialPublicity publicity) {
    Map map = new Map();
    map.setCreated(created);
    map.setCreator(creator);
    map.setData(data);
    map.setModified(modified);
    map.setModifier(modifier);
    map.setTitle(title);
    map.setUrlName(urlName);
    map.setPublicity(publicity);

    if (language != null)
      map.setLanguage(language);

    if (parentFolder != null)
      map.setParentFolder(parentFolder);

    return persist(map);
  }
  
  public Map updateData(Map map, User modifier, byte[] data) {
    EntityManager entityManager = getEntityManager();

    map.setData(data);
    map.setModified(new Date());
    map.setModifier(modifier);

    entityManager.persist(map);
    
    return map;
  }

}
