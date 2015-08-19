package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.BookTemplate;
import fi.foyt.fni.persistence.model.materials.BookTemplate_;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

public class BookTemplateDAO extends GenericDAO<BookTemplate> {

	private static final long serialVersionUID = 1L;

	public BookTemplate create(User creator, Date created, User modifier, Date modified, 
	    Language language, Folder parentFolder, String urlName, String title, String data, 
	    String styles, String fonts, String description, String iconUrl, MaterialPublicity publicity) {
    BookTemplate bookTemplate = new BookTemplate();
    
    bookTemplate.setCreated(created);
    bookTemplate.setCreator(creator);
    bookTemplate.setData(data);
    bookTemplate.setStyles(styles);
    bookTemplate.setFonts(fonts);
    bookTemplate.setIconUrl(iconUrl);
    bookTemplate.setDescription(description);
    bookTemplate.setLanguage(language);
    bookTemplate.setModified(modified);
    bookTemplate.setModifier(modifier);
    bookTemplate.setParentFolder(parentFolder);
    bookTemplate.setPublicity(publicity);
    bookTemplate.setTitle(title);
    bookTemplate.setUrlName(urlName);

    return persist(bookTemplate);
  }
	
  public List<BookTemplate> listByPublicity(MaterialPublicity publicity) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<BookTemplate> criteria = criteriaBuilder.createQuery(BookTemplate.class);
    Root<BookTemplate> root = criteria.from(BookTemplate.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(BookTemplate_.publicity), publicity)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public BookTemplate updateModifier(BookTemplate bookTemplate, User modifier) {
    bookTemplate.setModifier(modifier);
    return persist(bookTemplate);
  }

  public BookTemplate updateModified(BookTemplate bookTemplate, Date modified) {
    bookTemplate.setModified(modified);
    return persist(bookTemplate);
  }

  public BookTemplate updateData(BookTemplate bookTemplate, String data) {
    bookTemplate.setData(data);
    return persist(bookTemplate);
  }

  public BookTemplate updateStyles(BookTemplate bookTemplate, String styles) {
    bookTemplate.setStyles(styles);
    return persist(bookTemplate);
  }

  public BookTemplate updateFonts(BookTemplate bookTemplate, String fonts) {
    bookTemplate.setFonts(fonts);
    return persist(bookTemplate);
  }

}
