package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;

import javax.persistence.EntityManager;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.Pdf;
import fi.foyt.fni.persistence.model.users.User;

public class PdfDAO extends GenericDAO<Pdf> {

	private static final long serialVersionUID = 1L;

	public Pdf create(User creator, Date created, User modifier, Date modified, Language language, Folder parentFolder, String urlName, String title, byte[] data, MaterialPublicity publicity) {
    EntityManager entityManager = getEntityManager();

    Pdf pdf = new Pdf();
    pdf.setCreated(created);
    pdf.setCreator(creator);
    pdf.setData(data);
    pdf.setModified(modified);
    pdf.setModifier(modifier);
    pdf.setTitle(title);
    pdf.setUrlName(urlName);
    pdf.setPublicity(publicity);

    if (language != null)
      pdf.setLanguage(language);

    if (parentFolder != null)
      pdf.setParentFolder(parentFolder);

    entityManager.persist(pdf);

    return pdf;
  }
  
  public Pdf updateData(Pdf pdf, User modifier, byte[] data) {
    EntityManager entityManager = getEntityManager();

    pdf.setData(data);
    pdf.setModified(new Date());
    pdf.setModifier(modifier);

    entityManager.persist(pdf);
    
    return pdf;
  }

}
