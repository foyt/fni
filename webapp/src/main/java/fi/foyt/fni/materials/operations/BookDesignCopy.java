package fi.foyt.fni.materials.operations;

import java.util.Date;

import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.BookDesignDAO;
import fi.foyt.fni.persistence.model.materials.BookDesign;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.User;

public class BookDesignCopy implements MaterialCopy<BookDesign> {
  
  @Inject
  private BookDesignDAO bookDesignDAO;
  
  @Override
  public BookDesign copy(BookDesign original, Folder targetFolder, String urlName, User creator) {
    if (original == null) {
      return null;
    }
    
    Date now = new Date();
    
    return bookDesignDAO.create(creator, 
        now, 
        creator, 
        now, 
        original.getLanguage(),
        targetFolder,
        urlName,
        original.getTitle(),
        original.getData(),
        original.getStyles(),
        original.getFonts(),
        original.getPublicity());
  }
  
  @Override
  public MaterialType getType() {
    return MaterialType.BOOK_DESIGN;
  }
  
  @Override
  public MaterialType[] getAllowedTargets() {
    return new MaterialType[] {
      MaterialType.FOLDER
    };
  }

}
