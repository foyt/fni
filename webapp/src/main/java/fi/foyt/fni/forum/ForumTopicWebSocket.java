package fi.foyt.fni.forum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
@ServerEndpoint ("/ws/forum/{TOPICID}")
@Singleton
@SuppressWarnings ("squid:S3306")
public class ForumTopicWebSocket {
  
  @Inject
  private Logger logger;
  
  private Map<Long, List<Session>> clientMap;
  
  @PostConstruct
  public void init() {
    clientMap = new HashMap<>();
  }
  
  @OnOpen
  public void onOpen(final Session client, EndpointConfig endpointConfig, @PathParam("TOPICID") Long topicId) throws IOException {
    synchronized (this) {
      addClient(topicId, client);
    }
  }
  
  @OnClose
  public void onClose(final Session session, CloseReason closeReason, @PathParam("TOPICID") Long topicId) {
    synchronized (this) {
      removeClient(topicId, session);
    }
  }

  public void onForumPostCreated(@Observes (during = TransactionPhase.AFTER_COMPLETION) @ForumPostCreated ForumPostEvent event) {
    try {
      Map<String, Object> data = new HashMap<>();
      data.put("type", "created");
      data.put("postId", event.getForumPostId());
      String message = (new ObjectMapper()).writeValueAsString(data);
      
      for (Session client : getClients(event.getForumTopicId())) {
        client
          .getAsyncRemote()
          .sendText(message);
      }
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to serialize created message", e);
    }
  }

  public void onForumPostModified(@Observes (during = TransactionPhase.AFTER_COMPLETION) @ForumPostModified ForumPostEvent event) {
    try {
      Map<String, Object> data = new HashMap<>();
      data.put("type", "modified");
      data.put("postId", event.getForumPostId());
      String message = (new ObjectMapper()).writeValueAsString(data);
      
      for (Session client : getClients(event.getForumTopicId())) {
        client
          .getAsyncRemote()
          .sendText(message);
      }
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to serialize updated message", e);
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
    List<Session> clients = clientMap.get(topicId);
    if (clients == null) {
      return Collections.emptyList();
    }
    
    return clients;
  }

  private void removeClient(Long topicId, Session session) {
    List<Session> clients = getClients(topicId);
    if (clients != null) {
      clients.remove(session);
    }
  }
  
}