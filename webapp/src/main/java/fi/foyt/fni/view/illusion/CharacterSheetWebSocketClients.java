package fi.foyt.fni.view.illusion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.websocket.Session;

@Singleton
public class CharacterSheetWebSocketClients {

  @PostConstruct
  public void init() {
    clientMap = new HashMap<>();
  }
  
  public void addClient(Long eventId, Long materialId, Long participantId, Session client) {
    String clientId = getClientId(eventId, materialId, participantId);
    
    List<Session> clientList = clientMap.get(clientId);
    if (clientList == null) {
      clientList = new ArrayList<>();
    }
    
    clientList.add(client);
    
    clientMap.put(clientId, clientList);
  }
  
  public List<Session> getParticipantClients(Long eventId, Long materialId, Long participantId) {
    return clientMap.get(getClientId(eventId, materialId, participantId));
  }

  public List<Session> getParticipantOtherClients(Session client, Long eventId, Long materialId, Long participantId) {
    List<Session> result = new ArrayList<>();
    
    List<Session> participantClients = getParticipantClients(eventId, materialId, participantId);
    if (participantClients != null) {
      for (Session otherClient : participantClients) {
        if (!otherClient.getId().equals(client.getId())) {
          result.add(otherClient);
        }
      }
    }
    
    return result;
  }

  public void removeClient(String sessionId, Long eventId, Long materialId, Long participantId) {
    List<Session> participantClients = getParticipantClients(eventId, materialId, participantId);
    if (participantClients != null) {
      for (Session participantClient : participantClients) {
        if (participantClient.getId().equals(sessionId)) {
          participantClients.remove(participantClient);
          break;
        }
      }
      
      clientMap.put(getClientId(eventId, materialId, participantId), participantClients);
    }
  }

  private String getClientId(Long eventId, Long materialId, Long participantId) {
    return new StringBuilder()
      .append(eventId)
      .append('-')
      .append(materialId)
      .append('-')
      .append(participantId)
      .toString();
  }
  
  private Map<String, List<Session>> clientMap;
}
