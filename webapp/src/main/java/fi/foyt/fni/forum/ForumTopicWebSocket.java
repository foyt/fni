package fi.foyt.fni.forum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.transaction.Transactional;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

@ServerEndpoint ("/ws/forum/{TOPICID}")
public class ForumTopicWebSocket {
  
  private static final Map<Long, List<Session>> clientMap = new HashMap<>();

  @PostConstruct
  public void init() {
  }
  
  @OnOpen
  @Transactional
  public void onOpen(final Session client, EndpointConfig endpointConfig, @PathParam("TOPICID") Long topicId) throws IOException {
    synchronized (this) {
      addClient(topicId, client);
    }
  }
  
  @OnClose
  @Transactional
  public void onClose(final Session session, CloseReason closeReason, @PathParam("TOPICID") Long topicId) {
    synchronized (this) {
      removeClient(topicId, session);
    }
  }

  public void onForumPostCreated(@Observes (during = TransactionPhase.AFTER_COMPLETION) @ForumPostCreated ForumPostEvent event) throws JsonGenerationException, JsonMappingException, IOException {
    Map<String, Object> data = new HashMap<>();
    data.put("type", "created");
    data.put("postId", event.getForumPostId());
    String message = (new ObjectMapper()).writeValueAsString(data);
    
    for (Session client : getClients(event.getForumTopicId())) {
      client.getAsyncRemote().sendText(message);
    }
  }

  public void onForumPostModified(@Observes (during = TransactionPhase.AFTER_COMPLETION) @ForumPostModified ForumPostEvent event) throws JsonGenerationException, JsonMappingException, IOException {
    Map<String, Object> data = new HashMap<>();
    data.put("type", "modified");
    data.put("postId", event.getForumPostId());
    String message = (new ObjectMapper()).writeValueAsString(data);
    
    for (Session client : getClients(event.getForumTopicId())) {
      client.getAsyncRemote().sendText(message);
    }
  }
  
  private void addClient(Long topicId, Session client) {
    List<Session> clientList = clientMap.get(topicId);
    if (clientList == null) {
      clientList = new ArrayList<>();
    }
    
    clientList.add(client);
    
    clientMap.put(topicId, clientList);
  }
  
  private List<Session> getClients(Long topicId) {
    return clientMap.get(topicId);
  }

  private void removeClient(Long topicId, Session session) {
    List<Session> clients = getClients(topicId);
    if (clients != null) {
      clients.remove(session);
    }
  }
  
}