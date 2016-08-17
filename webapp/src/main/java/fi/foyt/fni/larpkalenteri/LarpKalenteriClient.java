package fi.foyt.fni.larpkalenteri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import fi.foyt.fni.larpkalenteri.AVIResolver.AVIProperties;
import fi.foyt.fni.larpkalenteri.Event.Status;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.system.SystemSettingsController;

@ApplicationScoped
public class LarpKalenteriClient {

  @Inject
  private SystemSettingsController systemSettingsController;
  
  @Inject
  private Logger logger;
  
  @PostConstruct
  public void init() {
    String baseUrl = systemSettingsController.getSetting(SystemSettingKey.LARP_KALENTERI_URL);
    
    restBaseUrl = String.format("%s/rest", baseUrl);
    tokenEndpoint = String.format("%s/oauth2/token", baseUrl);
    clientId = systemSettingsController.getSetting(SystemSettingKey.LARP_KALENTERI_CLIENT_ID);
    clientSecret = systemSettingsController.getSetting(SystemSettingKey.LARP_KALENTERI_CLIENT_SECRET);
    accessToken = null;
    expires = null;
  }
  
  public List<String> translateGenres(List<fi.foyt.fni.persistence.model.illusion.Genre> illusionGenres) {
    List<String> result = new ArrayList<>();
    
    if ((illusionGenres != null) && (!illusionGenres.isEmpty())) {
      Map<String, String> genreMap = new HashMap<>();
      
      for (Genre genre : listGenres()) {
        genreMap.put(genre.getName().get("fi"), genre.getId());
      }
      
      for (fi.foyt.fni.persistence.model.illusion.Genre genre : illusionGenres) {
        result.add(genreMap.get(genre.getName()));
      }
    }
    
    return result;
  }
  
  public Long translateAVI(AVIProperties aviProperties) {
    if (aviProperties != null) {
      switch (aviProperties.getAvi()) {
        case "1": // Southern Finland AVI"
          return 2l;
        case "2": // Southwestern Finland AVI
          return 3l;
        case "3": // Eastern Finland AVI
          return 5l;
        case "4": // Western and Inland Finland AV
          return 4l;
        case "5": // Northern Finland AVI
          return 6l;
        case "6": // Lapland AVI
          return 7l;
        case "7": // State Department of Åland
      }
    }
    
    return 8l;
  }
  
  public String translateType(fi.foyt.fni.persistence.model.illusion.IllusionEventType illusionType) {
    if (illusionType == null) {
      return null;
    }
    
    for (Type type : listTypes()) {
      if (type.getName().get("fi").equals(illusionType.getName())) {
        return type.getId();
      }
    }
    
    return null;
  }
  
  public Event createEvent(String name, String type, Date start, Date end, String textDate, Date signUpStart, Date signUpEnd, Long locationDropDown,
      String location, String iconURL, List<String> genres, String cost, Integer ageLimit, Boolean beginnerFriendly, String storyDescription,
      String infoDescription, String organizerName, String organizerEmail, String link1, String link2, Status status, String password, Boolean eventFull,
      Boolean invitationOnly, Boolean languageFree, Long illusionId) throws IOException {
    
    return createEvent(name, type, getDateTime(start), getDateTime(end), textDate, getDateTime(signUpStart), getDateTime(signUpEnd), locationDropDown, location, iconURL, genres, cost, ageLimit, beginnerFriendly, storyDescription, infoDescription, organizerName, organizerEmail, link1, link2, status, password, eventFull, invitationOnly, languageFree, illusionId);
  }
  
  public Event createEvent(String name, String type, DateTime start, DateTime end, String textDate, DateTime signUpStart, DateTime signUpEnd, Long locationDropDown,
      String location, String iconURL, List<String> genres, String cost, Integer ageLimit, Boolean beginnerFriendly, String storyDescription,
      String infoDescription, String organizerName, String organizerEmail, String link1, String link2, Status status, String password, Boolean eventFull,
      Boolean invitationOnly, Boolean languageFree, Long illusionId) throws IOException {
    
    Event event = new Event(null, name, type, start, end, textDate, signUpStart, signUpEnd, locationDropDown,
        location, iconURL, genres, cost, ageLimit, beginnerFriendly, storyDescription,
        infoDescription, organizerName, organizerEmail, link1, link2, status, password, eventFull,
        invitationOnly, languageFree, illusionId);
    
    return doPostRequest(String.format("%s/events/", restBaseUrl), event);
  }
  
  public Event findEvent(Long id) throws IOException {
    return doGetRequest(String.format("%s/events/%d", restBaseUrl, id), new TypeReference<Event>() {});
  }

  public Event updateEvent(Long id, String name, String type, Date start, Date end, String textDate, Date signUpStart, Date signUpEnd, Long locationDropDown,
      String location, String iconURL, List<String> genres, String cost, Integer ageLimit, Boolean beginnerFriendly, String storyDescription,
      String infoDescription, String organizerName, String organizerEmail, String link1, String link2, Status status, String password, Boolean eventFull,
      Boolean invitationOnly, Boolean languageFree, Long illusionId) throws IOException {
    return updateEvent(id, name, type, getDateTime(start), getDateTime(end), textDate, getDateTime(signUpStart), getDateTime(signUpEnd), locationDropDown, location, 
        iconURL, genres, cost, ageLimit, beginnerFriendly, storyDescription, infoDescription, organizerName, organizerEmail, link1, link2, status, password, eventFull, 
        invitationOnly, languageFree, illusionId);
  }

  public Event updateEvent(Long id, String name, String type, DateTime start, DateTime end, String textDate, DateTime signUpStart, DateTime signUpEnd, Long locationDropDown,
      String location, String iconURL, List<String> genres, String cost, Integer ageLimit, Boolean beginnerFriendly, String storyDescription,
      String infoDescription, String organizerName, String organizerEmail, String link1, String link2, Status status, String password, Boolean eventFull,
      Boolean invitationOnly, Boolean languageFree, Long illusionId) throws IOException {
    
    Event event = new Event(id, name, type, start, end, textDate, signUpStart, signUpEnd, locationDropDown,
        location, iconURL, genres, cost, ageLimit, beginnerFriendly, storyDescription,
        infoDescription, organizerName, organizerEmail, link1, link2, status, password, eventFull,
        invitationOnly, languageFree, illusionId);
    
    return doPutRequest(String.format("%s/events/%d", restBaseUrl, id), event);
  }
  
  private List<Genre> listGenres() {
    if (genres == null) {
      try {
        genres = doGetRequest(String.format("%s/genres", restBaseUrl), new TypeReference<List<Genre>>() {});
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Could not retrive genres from Larp-kalenteri", e);
      }
    }
    
    return genres;
  }
  
  private List<Type> listTypes() {
    if (types == null) {
      try {
        types = doGetRequest(String.format("%s/types", restBaseUrl), new TypeReference<List<Type>>() {});
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Could not retrive types from Larp-kalenteri", e);
      }
    }
    
    return types;
  }
  
  private <T> T doGetRequest(String url, TypeReference<T> typeReference) throws IOException {
    String accessToken;
    try {
      accessToken = getAccessToken();
    } catch (OAuthSystemException | OAuthProblemException e) {
      throw new IOException(e);
    }
    
    DefaultHttpClient client = new DefaultHttpClient();
    HttpGet httpGet = new HttpGet(url);
    httpGet.setHeader("Accept", " application/json");
    httpGet.setHeader("Authorization", String.format("Bearer %s", accessToken));
    
    HttpResponse response = client.execute(httpGet);
    
    HttpEntity entity = response.getEntity();
    try {
      int status = response.getStatusLine().getStatusCode();
      if (status == 204) {
        return null;
      } else if (status == 200) {
        return unmarshalEntity(entity.getContent(), typeReference);
      }

      throw new IOException(String.format("Server returned error code %d", status));
      
    } finally {
      EntityUtils.consume(entity);
    } 
  }
  
  @SuppressWarnings("unchecked")
  private <T> T doPostRequest(String url, T payload) throws IOException {
    String accessToken;
    try {
      accessToken = getAccessToken();
    } catch (OAuthSystemException | OAuthProblemException e) {
      throw new IOException(e);
    }
    
    String data = marshalEntity(payload);
    
    DefaultHttpClient client = new DefaultHttpClient();
    HttpPost httpPost = new HttpPost(url);
    httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
    httpPost.setHeader("Accept", "application/json; charset=UTF-8");
    httpPost.setHeader("Accept-Charset", "UTF-8");
    httpPost.setHeader("Authorization", String.format("Bearer %s", accessToken));
    httpPost.setEntity(new StringEntity(data, ContentType.create("application/json", "UTF-8")));

    HttpResponse response = client.execute(httpPost);
    
    HttpEntity entity = response.getEntity();
    try {
      int status = response.getStatusLine().getStatusCode();
      if (status == 204) {
        return null;
      } else if (status == 200) {
        return (T) unmarshalEntity(entity.getContent(), payload.getClass());
      }
      
      throw new IOException(String.format("Server returned error code %d", status));
      
    } finally {
      EntityUtils.consume(entity);
    }
  }
  
  @SuppressWarnings("unchecked")
  private <T> T doPutRequest(String url, T payload) throws IOException {
    String accessToken;
    try {
      accessToken = getAccessToken();
    } catch (OAuthSystemException | OAuthProblemException e) {
      throw new IOException(e);
    }
    
    String data = marshalEntity(payload);
    DefaultHttpClient client = new DefaultHttpClient();
    HttpPut httpPut = new HttpPut(url);
    httpPut.setHeader("Content-Type", "application/json; charset=UTF-8");
    httpPut.setHeader("Accept", "application/json; charset=UTF-8");
    httpPut.setHeader("Accept-Charset", "UTF-8");
    httpPut.setHeader("Authorization", String.format("Bearer %s", accessToken));
    httpPut.setEntity(new StringEntity(data, ContentType.create("application/json", "UTF-8")));
    
    HttpResponse response = client.execute(httpPut);
    
    HttpEntity entity = response.getEntity();
    try {
      int status = response.getStatusLine().getStatusCode();
      if (status == 204) {
        return null;
      } else if (status == 200) {
        return (T) unmarshalEntity(entity.getContent(), payload.getClass());
      }
      
      throw new IOException(String.format("Server returned error code %d (%s)", status, IOUtils.toString(entity.getContent())));
      
    } finally {
      EntityUtils.consume(entity);
    }
  }

  private String getAccessToken() throws OAuthSystemException, OAuthProblemException {
    if ((accessToken == null) || (expires == null) && (expires <= System.currentTimeMillis())) {
      OAuthClientRequest request = OAuthClientRequest
          .tokenLocation(tokenEndpoint)
          .setGrantType(GrantType.CLIENT_CREDENTIALS)
          .setClientId(clientId)
          .setClientSecret(clientSecret)
          .buildBodyMessage();

      OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

      OAuthJSONAccessTokenResponse response = oAuthClient.accessToken(request);
      
      expires = System.currentTimeMillis() + (response.getExpiresIn() * 1000);
      accessToken = response.getAccessToken();
    }

    return accessToken;
  }
  
  private DateTime getDateTime(Date date) {
    if (date == null) {
      return null;
    }
    
    return new DateTime(date);
  }
  
  private ObjectMapper createObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JSR310Module());
    objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.
        WRITE_DATES_AS_TIMESTAMPS , false);
    return objectMapper;
  }
  
  private <T> T unmarshalEntity(InputStream data, TypeReference<T> typeReference) throws JsonParseException, JsonMappingException, IOException {
    return createObjectMapper().readValue(data, typeReference);
  }

  private <T> T unmarshalEntity(InputStream data, Class<T> type) throws JsonParseException, JsonMappingException, IOException {
    return createObjectMapper().readValue(data, type);
  }

  private String marshalEntity(Object entity) throws IOException {
    ObjectMapper objectMapper = createObjectMapper();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      objectMapper.writeValue(out, entity);
      out.flush();
      return out.toString("UTF-8");
    } finally {
      out.close();
    }
  }

  private String restBaseUrl;
  private String tokenEndpoint;
  private String clientId;
  private String clientSecret;
  private String accessToken;
  private Long expires;
  private List<Genre> genres;
  private List<Type> types;
}
