package fi.foyt.fni.materials;

import java.util.Date;
import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.PdfDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.Pdf;
import fi.foyt.fni.persistence.model.users.User;

@Dependent
@Stateful
public class PdfController {
	
	@Inject
  private PdfDAO pdfDAO;
  
  /* Pdf */

	public Pdf createPdf(User creator, Language language, Folder parentFolder, String urlName, String title, byte[] data) {
		Date now = new Date();
		return pdfDAO.create(creator, now, creator, now, language, parentFolder, urlName, title, data, MaterialPublicity.PRIVATE);
	}

	public Pdf findPdfById(Long pdfId) {
		return pdfDAO.findById(pdfId);
	}

}
