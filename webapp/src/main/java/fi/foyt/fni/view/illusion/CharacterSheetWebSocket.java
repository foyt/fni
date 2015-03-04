package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
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

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventMaterialController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSettingKey;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.materials.Material;

@ServerEndpoint ("/ws/{EVENTID}/characterSheet/{MATERIALID}/{PARTICIPANTID}/{KEY}")
public class CharacterSheetWebSocket {
  
  private static final Map<String, List<Session>> clientMap = new HashMap<>();

  @Inject
  private MaterialController materialController;
  
  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private IllusionEventMaterialController illusionEventMaterialController;
  
  @PostConstruct
  public void init() {
  }
  
  @OnOpen
  @Transactional
  public void onOpen(final Session client, EndpointConfig endpointConfig, @PathParam("EVENTID") Long eventId, @PathParam("MATERIALID") Long materialId, @PathParam("PARTICIPANTID") Long participantId, @PathParam("KEY") String key) throws IOException {
    synchronized (this) {
      Material material = materialController.findMaterialById(materialId);
      if (material == null) {
        client.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Not Found"));
        return;
      }
      
      IllusionEvent illusionEvent = illusionEventController.findIllusionEventById(eventId);
      if (illusionEvent == null) {
        client.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Not Found"));
        return;
      }
      
      IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantById(participantId);
      if (participant == null) {
        client.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Not Found"));
        return;
      }
      
      if (!participant.getEvent().getId().equals(illusionEvent.getId())) {
        client.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Not Found"));
        return;
      }

      fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting webSocketKey = illusionEventMaterialController.findParticipantSettingByMaterialAndParticipantAndKey(material, participant, IllusionEventMaterialParticipantSettingKey.WEBSOCKET_KEY);
      if (webSocketKey == null || !webSocketKey.getValue().equals(key)) {
        client.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Forbidden"));
        return;
      }

      fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting participantSetting = illusionEventMaterialController.findParticipantSettingByMaterialAndParticipantAndKey(material, participant, IllusionEventMaterialParticipantSettingKey.CHARACTER_SHEET_DATA);
      if (participantSetting != null) {
        Map<String, String> sheetData = readSheetData(participantSetting);
        ObjectMapper objectMapper = new ObjectMapper();
        client.getAsyncRemote().sendText(objectMapper.writeValueAsString(new Message("load", objectMapper.writeValueAsString(new LoadMessageData(sheetData)))));
      }
      
      addClient(eventId, materialId, participant.getId(), client);
    }
  }
  
  @OnClose
  @Transactional
  public void onClose(final Session session, CloseReason closeReason, @PathParam("EVENTID") Long eventId, @PathParam("MATERIALID") Long materialId, @PathParam("PARTICIPANTID") Long participantId, @PathParam("KEY") String key) {
    synchronized (this) {
      removeClient(eventId, materialId, participantId);
    }
  }

  @OnMessage
  @Transactional
  public void onMessage(Reader messageReader, Session client, @PathParam("EVENTID") Long eventId, @PathParam("MATERIALID") Long materialId, @PathParam("PARTICIPANTID") Long participantId, @PathParam("KEY") String key) throws IOException {
    Material material = materialController.findMaterialById(materialId);
    if (material == null) {
      client.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Not Found"));
      return;
    }
    
    IllusionEvent illusionEvent = illusionEventController.findIllusionEventById(eventId);
    if (illusionEvent == null) {
      client.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Not Found"));
      return;
    }
    
    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantById(participantId);
    if (participant == null) {
      client.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Not Found"));
      return;
    }
    
    if (!participant.getEvent().getId().equals(illusionEvent.getId())) {
      client.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Not Found"));
      return;
    }

    fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting webSocketKey = illusionEventMaterialController.findParticipantSettingByMaterialAndParticipantAndKey(material, participant, IllusionEventMaterialParticipantSettingKey.WEBSOCKET_KEY);
    if (webSocketKey == null || !webSocketKey.getValue().equals(key)) {
      client.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Forbidden"));
      return;
    }
    
    fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting participantSetting = illusionEventMaterialController.findParticipantSettingByMaterialAndParticipantAndKey(material, participant, IllusionEventMaterialParticipantSettingKey.CHARACTER_SHEET_DATA);
    
    ObjectMapper objectMapper = new ObjectMapper();
    
    try {
      Message message = objectMapper.readValue(messageReader, Message.class);
      switch (message.getType()) {
        case "update":
          UpdateMessageData updateData = objectMapper.readValue(message.getData(), UpdateMessageData.class);
          if (StringUtils.isNotBlank(updateData.getKey())) {
            if (participantSetting == null) {
              Map<String, String> sheetData = new HashMap<>();
              sheetData.put(updateData.getKey(), updateData.getValue());
              illusionEventMaterialController.createParticipantSetting(material, participant, IllusionEventMaterialParticipantSettingKey.CHARACTER_SHEET_DATA, objectMapper.writeValueAsString(sheetData));
            } else {
              Map<String, String> sheetData = readSheetData(participantSetting);
              sheetData.put(updateData.getKey(), updateData.getValue());
              illusionEventMaterialController.updateParticipantSettingValue(participantSetting, objectMapper.writeValueAsString(sheetData));
            }
          }
          
          for (Session participantOtherClient : getParticipantOtherClients(client, eventId, materialId, participant.getId())) {
            participantOtherClient.getAsyncRemote().sendText(objectMapper.writeValueAsString(new Message("update", objectMapper.writeValueAsString(updateData))));
          }
        break;
      }
    } catch (IOException e) {
      client.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "Internal Error"));
    }
  }

  private Map<String, String> readSheetData(fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting participantSetting) throws IOException, JsonParseException, JsonMappingException {
    return new ObjectMapper().readValue(participantSetting.getValue(), new TypeReference<Map<String, String>>(){});
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
  
  private void addClient(Long eventId, Long materialId, Long participantId, Session client) {
    String clientId = getClientId(eventId, materialId, participantId);
    
    List<Session> clientList = clientMap.get(clientId);
    if (clientList == null) {
      clientList = new ArrayList<>();
    }
    
    clientList.add(client);
    
    clientMap.put(clientId, clientList);
  }
  
  private List<Session> getParticipantClients(Long eventId, Long materialId, Long participantId) {
    return clientMap.get(getClientId(eventId, materialId, participantId));
  }

  private List<Session> getParticipantOtherClients(Session client, Long eventId, Long materialId, Long participantId) {
    List<Session> result = new ArrayList<>();
    
    List<Session> participantClients = getParticipantClients(eventId, materialId, participantId);
    if (participantClients != null) {
      for (Session otherClient : participantClients) {
        if (!otherClient.getId().equals(client)) {
          result.add(otherClient);
        }
      }
    }
    
    return result;
  }

  private void removeClient(Long eventId, Long materialId, Long participantId) {
    clientMap.remove(getClientId(eventId, materialId, participantId));
  }
  
  @SuppressWarnings("unused")
  private static class Message {
    
    public Message() {
    }
    
    public Message(String type, String data) {
      this.type = type;
      this.data = data;
    }
    
    public String getType() {
      return type;
    }
    
    public void setType(String type) {
      this.type = type;
    }
    
    public String getData() {
      return data.toString();
    }
    
    public void setData(JsonNode data) {
      this.data = data;
    }
    
    private String type;
    private Object data;
  }
  
  @SuppressWarnings("unused")
  private static class LoadMessageData {
    
    public LoadMessageData() {
    }
    
    public LoadMessageData(Map<String, String> values) {
      this.values = values;
    }
    
    public Map<String, String> getValues() {
      return values;
    }
    
    public void setValues(Map<String, String> values) {
      this.values = values;
    }
    
    private Map<String, String> values;
  }
  
  @SuppressWarnings("unused")
  private static class UpdateMessageData {
    
    public String getKey() {
      return key;
    }
    
    public void setKey(String key) {
      this.key = key;
    }
    
    public String getValue() {
      return value;
    }
    
    
    public void setValue(String value) {
      this.value = value;
    }
    
    private String key;
    private String value;
  }
  
}