package fi.foyt.fni.coops;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import fi.foyt.coops.CoOpsConflictException;
import fi.foyt.coops.CoOpsForbiddenException;
import fi.foyt.coops.CoOpsInternalErrorException;
import fi.foyt.coops.CoOpsNotFoundException;
import fi.foyt.coops.CoOpsUsageException;
import fi.foyt.fni.materials.DocumentController;
import fi.foyt.fni.persistence.model.materials.CoOpsSession;
import fi.foyt.fni.persistence.model.materials.CoOpsSessionType;
import fi.foyt.fni.persistence.model.materials.Document;

@Stateless
public class CoOpsSessionEventsListener {
  
  @Inject
  private Logger logger;

  @Inject
  private CoOpsApiDocument coOpsApiDocument;

  @Inject
  private CoOpsSessionController coOpsSessionController;

  @Inject
  private DocumentController documentController;

  @Inject
  private CoOpsSessionEventsController coOpsSessionEventsController;
 
  public void onSessionOpen(@Observes CoOpsSessionOpenEvent event) {
    String sessionId = event.getSessionId();
    sendSessionEventPatch(sessionId, "OPEN");
  }
 
  public void onSessionClose(@Observes CoOpsSessionCloseEvent event) {
    String sessionId = event.getSessionId();
    sendSessionEventPatch(sessionId, "CLOSE");
  }
  
  private void sendSessionEventPatch(String sessionId, String status) {
    CoOpsSession session = coOpsSessionController.findSessionBySessionId(sessionId);
    if (session != null) {
      Document document = (Document) session.getMaterial();
      Long currentRevisionNumber = documentController.getDocumentRevision(document);
      
      CoOpsSession serverSession = coOpsSessionController.createSession(session.getMaterial(), session.getUser(), CoOpsSessionType.SERVER, session.getAlgorithm(), currentRevisionNumber);
      try {
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("sessionEvents", coOpsSessionEventsController.createSessionEvents(Arrays.asList(session), status));
        
        try {
          coOpsApiDocument.filePatch(session.getMaterial().getId().toString(), serverSession.getSessionId(), currentRevisionNumber, null, null, extensions);
        } catch (CoOpsInternalErrorException | CoOpsUsageException | CoOpsNotFoundException | CoOpsConflictException | CoOpsForbiddenException e) {
          logger.log(Level.WARNING, "Could not send a sessionEvent patch", e);
        }
      } finally {
        coOpsSessionController.closeSession(serverSession, true); 
      }
    }
  }
  
}
