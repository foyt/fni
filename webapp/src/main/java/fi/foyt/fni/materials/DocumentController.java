package fi.foyt.fni.materials;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.materials.DocumentDAO;
import fi.foyt.fni.persistence.dao.materials.DocumentRevisionDAO;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.DocumentRevision;
import fi.foyt.fni.persistence.model.users.User;

@Dependent
@Stateful
public class DocumentController {
	
	@Inject
	private Logger logger;

  @Inject
  private DocumentDAO documentDAO;

  @Inject
  private DocumentRevisionDAO documentRevisionDAO;
  
  /* Document */
  
	public Document findDocumentById(Long documentId) {
		return documentDAO.findById(documentId);
	}

	public Document updateDocumentData(Document document, String data, User user) {
		return documentDAO.updateData(document, user, data);
	}
	
	/* Revisions */
	
	public DocumentRevision createDocumentRevision(Document document, Long revisionNumber, Date created, boolean compressed, boolean completeVersion, byte[] revisionBytes, String checksum) {
	  return documentRevisionDAO.create(document, revisionNumber, created, compressed, completeVersion, revisionBytes, checksum, null, null);
	}

	public List<DocumentRevision> listDocumentRevisionsAfter(Document document, Long revisionNumber) {
		List<DocumentRevision> documentRevisions = documentRevisionDAO.listByDocumentAndRevisionGreaterThan(document, revisionNumber);
    Collections.sort(documentRevisions, new Comparator<DocumentRevision>() {
      @Override
      public int compare(DocumentRevision documentRevision1, DocumentRevision documentRevision2) {
        return documentRevision1.getRevision().compareTo(documentRevision2.getRevision());
      }
    });
    
    return documentRevisions;
	}

	public Long getDocumentRevision(Document document) {
		Long result = documentRevisionDAO.maxRevisionByDocument(document);
		if (result == null) {
			result = 0l;
		}
		
		return result;
	}
	
}
