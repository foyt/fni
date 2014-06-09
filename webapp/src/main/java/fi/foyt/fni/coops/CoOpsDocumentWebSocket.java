package fi.foyt.fni.coops;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import fi.foyt.coops.CoOpsConflictException;
import fi.foyt.coops.CoOpsForbiddenException;
import fi.foyt.coops.CoOpsInternalErrorException;
import fi.foyt.coops.CoOpsNotFoundException;
import fi.foyt.coops.CoOpsUsageException;
import fi.foyt.coops.model.Patch;
import fi.foyt.fni.materials.CoOpsSessionController;
import fi.foyt.fni.materials.DocumentController;
import fi.foyt.fni.persistence.model.materials.CoOpsSession;
import fi.foyt.fni.persistence.model.materials.CoOpsSessionType;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.Material;

@ServerEndpoint ("/ws/coops/document/{FILEID}/{SESSIONID}")
public class CoOpsDocumentWebSocket {
  
  private static final Map<String, Map<String, Session>> fileClients = new HashMap<String, Map<String, Session>>();

  @Inject
  private CoOpsApiDocument coOpsApiDocument;

  @Inject
  private DocumentController documentController;
  
  @Inject
  private CoOpsSessionController coOpsSessionController;
  
  @OnOpen
  @Transactional
  public void onOpen(final Session client, EndpointConfig endpointConfig, @PathParam("FILEID") String fileId, @PathParam("SESSIONID") String sessionId) throws IOException {
    synchronized (this) {
      CoOpsSession coOpsSession = coOpsSessionController.findSessionBySessionId(sessionId);
      if (coOpsSession == null) {
        client.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Not Found"));
        return;
      }
      
      if (!coOpsSession.getMaterial().getId().equals(NumberUtils.createLong(fileId))) {
        client.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Session is associated with another fileId"));
        return;
      }
      
      Map<String, Session> sessions = fileClients.get(fileId);
      if (sessions == null) {
        fileClients.put(fileId, new HashMap<String, Session>());
      }
      
      fileClients.get(fileId).put(client.getId(), client);
      
      coOpsSessionController.updateSessionType(coOpsSession, CoOpsSessionType.WS);
      
      Material material = coOpsSession.getMaterial();
      if (material instanceof Document) {
        Document document = (Document) material;
        Long currentRevisionNumber = documentController.getDocumentRevision(document);
        
        if (coOpsSession.getJoinRevision() < currentRevisionNumber) {
          ObjectMapper objectMapper = new ObjectMapper();
          List<Patch> patches;
          try {
            patches = coOpsApiDocument.fileUpdate(coOpsSession.getMaterial().getId().toString(), coOpsSession.getSessionId(), coOpsSession.getJoinRevision());
            for (Patch patch : patches) {
              sendPatch(client, patch);
            }
          } catch (CoOpsInternalErrorException e) {
            client.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "Internal Error"));
          } catch (CoOpsUsageException e) {
            client.getAsyncRemote().sendText(objectMapper.writeValueAsString(new ErrorMessage("patchError", 400, e.getMessage())));
          } catch (CoOpsNotFoundException e) {
            client.getAsyncRemote().sendText(objectMapper.writeValueAsString(new ErrorMessage("patchError", 404, e.getMessage())));
          } catch (CoOpsForbiddenException e) {
            client.getAsyncRemote().sendText(objectMapper.writeValueAsString(new ErrorMessage("patchError", 500, e.getMessage())));
          }
        }      
      } else {
        client.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Session is associated with non document material"));
      }
    }
  }
  
  @OnClose
  @Transactional
  public void onClose(final Session session, CloseReason closeReason, @PathParam("FILEID") String fileId, @PathParam("SESSIONID") String sessionId) {
    synchronized (this) {
      fileClients.get(fileId).remove(session.getId());
      
      CoOpsSession coOpsSession = coOpsSessionController.findSessionBySessionId(sessionId);
      if (coOpsSession != null) {
        coOpsSessionController.closeSession(coOpsSession);
      }
    }
  }

  @OnMessage
  @Transactional
  public void onMessage(Reader messageReader, Session client, @PathParam("FILEID") String fileId, @PathParam("SESSIONID") String sessionId) throws IOException {
    CoOpsSession coOpsSession = coOpsSessionController.findSessionBySessionId(sessionId);
    if (coOpsSession == null) {
      client.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Not Found"));
      return;
    }
    
    if (!coOpsSession.getMaterial().getId().equals(NumberUtils.createLong(fileId))) {
      client.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Session is associated with another fileId"));
      return;
    }
    
    ObjectMapper objectMapper = new ObjectMapper();
    
    try {
      PatchMessage patchMessage;
      
      try {
        patchMessage = objectMapper.readValue(messageReader, PatchMessage.class);
      } catch (IOException e) {
        throw new CoOpsInternalErrorException(e);
      } 
      
      if (patchMessage == null) {
        throw new CoOpsInternalErrorException("Could not parse message");
      }
      
      if (!patchMessage.getType().equals("patch")) {
        throw new CoOpsInternalErrorException("Unknown message type: " + patchMessage.getType());
      }
      
      Patch patch = patchMessage.getData();
  
      coOpsApiDocument.filePatch(fileId, patch.getSessionId(), patch.getRevisionNumber(), patch.getPatch(), patch.getProperties(), patch.getExtensions());
    } catch (CoOpsInternalErrorException e) {
      client.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "Internal Error"));
    } catch (CoOpsUsageException e) {
      client.getAsyncRemote().sendText(objectMapper.writeValueAsString(new ErrorMessage("patchError", 400, e.getMessage())));
    } catch (CoOpsNotFoundException e) {
      client.getAsyncRemote().sendText(objectMapper.writeValueAsString(new ErrorMessage("patchError", 404, e.getMessage())));
    } catch (CoOpsConflictException e) {
      client.getAsyncRemote().sendText(objectMapper.writeValueAsString(new ErrorMessage("patchRejected", 409, "Conflict")));
    } catch (CoOpsForbiddenException e) {
      client.getAsyncRemote().sendText(objectMapper.writeValueAsString(new ErrorMessage("patchError", 500, e.getMessage())));
    }
  }
  
  public void onCoOpsPatch(@Observes CoOpsPatchEvent event) throws JsonGenerationException, JsonMappingException, IOException {
    synchronized (this) {
      Map<String, Session> clients = fileClients.get(event.getFileId());
      if (clients != null) {
        for (Session client : clients.values()) {
          sendPatch(client, event.getPatch());
        }
      }
    }
  }
  
  private void sendPatch(Session client, Patch patch) throws JsonGenerationException, JsonMappingException, IOException {
    UpdateMessage updateMessage = new UpdateMessage(patch);
    String message = (new ObjectMapper()).writeValueAsString(updateMessage);
    client.getAsyncRemote().sendText(message);
  }
  
}
