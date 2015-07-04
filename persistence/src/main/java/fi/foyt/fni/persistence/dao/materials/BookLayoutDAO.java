package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.BookLayout;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

public class BookLayoutDAO extends GenericDAO<BookLayout> {

	private static final long serialVersionUID = 1L;

	public BookLayout create(User creator, Date created, User modifier, Date modified, Language language, Folder parentFolder,  String urlName, String title, String data, MaterialPublicity publicity) {
    BookLayout bookLayout = new BookLayout();
    bookLayout.setCreated(created);
    bookLayout.setCreator(creator);
    bookLayout.setData(data);
    bookLayout.setLanguage(language);
    bookLayout.setModified(modified);
    bookLayout.setModifier(modifier);
    bookLayout.setParentFolder(parentFolder);
    bookLayout.setPublicity(publicity);
    bookLayout.setTitle(title);
    bookLayout.setUrlName(urlName);

    return persist(bookLayout);
  }
	

  public BookLayout updateModifier(BookLayout bookLayout, User modifier) {
    bookLayout.setModifier(modifier);
    return persist(bookLayout);
  }

  public BookLayout updateModified(BookLayout bookLayout, Date modified) {
    bookLayout.setModified(modified);
    return persist(bookLayout);
  }

  public BookLayout updateData(BookLayout bookLayout, String data) {
    bookLayout.setData(data);
    return persist(bookLayout);
  }

  public BookLayout updateStyles(BookLayout bookLayout, String styles) {
    bookLayout.setStyles(styles);
    return persist(bookLayout);
  }

  public BookLayout updateFonts(BookLayout bookLayout, String fonts) {
    bookLayout.setFonts(fonts);
    return persist(bookLayout);
  }

}
