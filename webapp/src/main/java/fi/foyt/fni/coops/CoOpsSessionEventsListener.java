package fi.foyt.fni.coops;

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
import fi.foyt.fni.materials.CoOpsSessionController;
import fi.foyt.fni.materials.DocumentController;
import fi.foyt.fni.persistence.model.materials.CoOpsSession;
import fi.foyt.fni.persistence.model.materials.CoOpsSessionType;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.users.UserController;

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
  private UserController userController;
 
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
      User sessionUser = session.getUser();
      String email = userController.getUserPrimaryEmail(sessionUser);
      
      CoOpsSession serverSession = coOpsSessionController.createSession(session.getMaterial(), session.getUser(), CoOpsSessionType.SERVER, session.getAlgorithm(), currentRevisionNumber);
      try {
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("sessionEvents", new Object[] { new SessionEvent(sessionId, sessionUser.getFullName(), email, status) } );
        
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
  
  public class SessionEvent {
    
    public SessionEvent(String sessionId, String displayName, String email, String status) {
      this.sessionId = sessionId;
      this.displayName = displayName;
      this.email = email;
      this.status = status;
    }

    public String getSessionId() {
      return sessionId;
    }
    
    public void setSessionId(String sessionId) {
      this.sessionId = sessionId;
    }
    
    public String getDisplayName() {
      return displayName;
    }
    
    public void setDisplayName(String displayName) {
      this.displayName = displayName;
    }
    
    public String getEmail() {
      return email;
    }
    
    public void setEmail(String email) {
      this.email = email;
    }
    
    public String getStatus() {
      return status;
    }
    
    public void setStatus(String status) {
      this.status = status;
    }
    
    private String sessionId;
    private String displayName;
    private String email;
    private String status;
  }
  
//  function sendSessionEventsPatch(fileId, sessionId, status) {
//    db.sessions.findOne({ _id: new ObjectId( sessionId.toString() ) }, function (sessionErr, session) {
//      if (sessionErr) {
//        console.log("Error occurred while finding a session: " + sessionErr);
//      } else {
//        if (session) {
//          findFile(fileId, function (err, file) {
//            if (err) {
//              console.log("Error occurred while trying to find a session file: " + err);
//            } else {
//              if (file) {
//                getUserInfo(session, function (err, info) {
//                  info.status = status;
//                  db.sessions.insert({ userId: session.userId, algorithm: session.algorithm }, function (serverSessionErr, serverSession) {
//                    if (serverSessionErr) {
//                      console.log("Error occurred while creating server session: " + serverSessionErr);
//                    } else {
//                      api.filePatch(file._id, {
//                        sessionId: serverSession._id.toString(),
//                        revisionNumber: file.revisionNumber,
//                        extensions: {
//                          sessionEvents: [info]
//                        }
//                      }, function (err, code, file) {
//                        db.sessions.remove({ _id: serverSession._id } );
//                      });
//                    }
//                  });
//                });
//                
//              } else {
//                console.log("Could not find a session file");
//              }
//            }
//          });
//        } else {
//          console.log("Could not find a session");
//        }
//      }
//    });
//  }
}
