package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.BookDesign;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

public class BookDesignDAO extends GenericDAO<BookDesign> {

	private static final long serialVersionUID = 1L;

	public BookDesign create(User creator, Date created, User modifier, Date modified, Language language, 
	    Folder parentFolder,  String urlName, String title, String data, 
	    String styles, String fonts, MaterialPublicity publicity) {
	  
    BookDesign bookDesign = new BookDesign();
    bookDesign.setCreated(created);
    bookDesign.setCreator(creator);
    bookDesign.setData(data);
    bookDesign.setStyles(styles);
    bookDesign.setFonts(fonts);
    bookDesign.setLanguage(language);
    bookDesign.setModified(modified);
    bookDesign.setModifier(modifier);
    bookDesign.setParentFolder(parentFolder);
    bookDesign.setPublicity(publicity);
    bookDesign.setTitle(title);
    bookDesign.setUrlName(urlName);

    return persist(bookDesign);
  }
	

  public BookDesign updateModifier(BookDesign bookDesign, User modifier) {
    bookDesign.setModifier(modifier);
    return persist(bookDesign);
  }

  public BookDesign updateModified(BookDesign bookDesign, Date modified) {
    bookDesign.setModified(modified);
    return persist(bookDesign);
  }

  public BookDesign updateData(BookDesign bookDesign, String data) {
    bookDesign.setData(data);
    return persist(bookDesign);
  }

  public BookDesign updateStyles(BookDesign bookDesign, String styles) {
    bookDesign.setStyles(styles);
    return persist(bookDesign);
  }

  public BookDesign updateFonts(BookDesign bookDesign, String fonts) {
    bookDesign.setFonts(fonts);
    return persist(bookDesign);
  }

}
