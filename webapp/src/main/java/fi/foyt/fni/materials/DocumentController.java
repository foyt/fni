package fi.foyt.fni.materials;

import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.DocumentDAO;
import fi.foyt.fni.persistence.dao.materials.DocumentRevisionDAO;
import fi.foyt.fni.persistence.model.materials.Document;

@Dependent
@Stateful
public class DocumentController {
	
	@Inject
	private Logger logger;

  @Inject
  private DocumentDAO documentDAO;

  @Inject
  private DocumentRevisionDAO documentRevisionDAO;
  
	public Document findDocumentById(Long documentId) {
		return documentDAO.findById(documentId);
	}

	public Long getDocumentRevision(Document document) {
		return documentRevisionDAO.maxRevisionByDocument(document);
	}
	
}
