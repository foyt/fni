package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
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
import org.codehaus.jackson.map.ObjectMapper;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventMaterialController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSettingKey;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.materials.CharacterSheet;

@ServerEndpoint ("/ws/{EVENTID}/characterSheet/{MATERIALID}/{PARTICIPANTID}/{KEY}")
@Stateless
public class CharacterSheetWebSocket {
  
  @Inject
  private MaterialController materialController;
  
  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private IllusionEventMaterialController illusionEventMaterialController;
  
  @Inject
  private CharacterSheetWebSocketClients clients;
  
  @OnOpen
  public void onOpen(final Session client, EndpointConfig endpointConfig, @PathParam("EVENTID") Long eventId, @PathParam("MATERIALID") Long materialId, @PathParam("PARTICIPANTID") Long participantId, @PathParam("KEY") String key) throws IOException {
    CharacterSheet sheet = materialController.findCharacterSheetById(materialId);
    if (sheet == null) {
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

    fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting webSocketKey = illusionEventMaterialController.findParticipantSettingByMaterialAndParticipantAndKey(sheet, participant, IllusionEventMaterialParticipantSettingKey.WEBSOCKET_KEY);
    if (webSocketKey == null || !webSocketKey.getValue().equals(key)) {
      client.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Forbidden"));
      return;
    }
    
    Map<String, String> sheetData = materialController.getUserCharacterSheetData(sheet, participant.getUser());
    if (sheetData != null) {
      ObjectMapper objectMapper = new ObjectMapper();
      client.getAsyncRemote().sendText(objectMapper.writeValueAsString(new Message("load", objectMapper.writeValueAsString(new LoadMessageData(sheetData)))));
    }
    
    clients.addClient(eventId, materialId, participant.getId(), client);
  }
  
  @OnClose
  public void onClose(final Session session, CloseReason closeReason, @PathParam("EVENTID") Long eventId, @PathParam("MATERIALID") Long materialId, @PathParam("PARTICIPANTID") Long participantId, @PathParam("KEY") String key) {
    clients.removeClient(session.getId(), eventId, materialId, participantId);
  }

  @OnMessage
  public void onMessage(Reader messageReader, Session client, @PathParam("EVENTID") Long eventId, @PathParam("MATERIALID") Long materialId, @PathParam("PARTICIPANTID") Long participantId, @PathParam("KEY") String key) throws IOException {
    CharacterSheet sheet = materialController.findCharacterSheetById(materialId);
    if (sheet == null) {
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

    fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting webSocketKey = illusionEventMaterialController.findParticipantSettingByMaterialAndParticipantAndKey(sheet, participant, IllusionEventMaterialParticipantSettingKey.WEBSOCKET_KEY);
    if (webSocketKey == null || !webSocketKey.getValue().equals(key)) {
      client.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Forbidden"));
      return;
    }
    
    ObjectMapper objectMapper = new ObjectMapper();
    
    try {
      Message message = objectMapper.readValue(messageReader, Message.class);
      switch (message.getType()) {
        case "update":
          UpdateMessageData updateData = objectMapper.readValue(message.getData(), UpdateMessageData.class);
          if (StringUtils.isNotBlank(updateData.getKey())) {
            materialController.setUserCharacterSheetValue(sheet, participant.getUser(), updateData.getKey(), updateData.getValue());
          }
          
          for (Session participantOtherClient : clients.getParticipantOtherClients(client, eventId, materialId, participant.getId())) {
            participantOtherClient.getAsyncRemote().sendText(objectMapper.writeValueAsString(new Message("update", objectMapper.writeValueAsString(updateData))));
          }
        break;
        case "roll":
          RollMessageData rollData = objectMapper.readValue(message.getData(), RollMessageData.class);
          materialController.addCharacterSheetRoll(sheet, participant.getUser(), rollData.getLabel(), rollData.getRoll(), rollData.getResult());
        break;
        case "ping":
          client.getAsyncRemote().sendText(objectMapper.writeValueAsString(new Message("pong", "{}")));
        break;
      }
    } catch (IOException e) {
      client.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "Internal Error"));
    }
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
  
  @SuppressWarnings("unused")
  private static class RollMessageData {
    
    public String getLabel() {
      return label;
    }
    
    public void setLabel(String label) {
      this.label = label;
    }
    
    public String getRoll() {
      return roll;
    }
    
    public void setRoll(String roll) {
      this.roll = roll;
    }
    
    public Integer getResult() {
      return result;
    }
    
    public void setResult(Integer result) {
      this.result = result;
    }
    
    private String label;
    private String roll;
    private Integer result;
  }

}