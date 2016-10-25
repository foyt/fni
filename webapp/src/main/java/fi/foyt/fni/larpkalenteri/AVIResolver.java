package fi.foyt.fni.larpkalenteri;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AVIResolver {
  
  @Inject
  private Logger logger;
  
  private static final String FORMAT = "JSON";
  private static final String TYPE = "tilastointialueet:avi1000k_2014";
  private static final String URL = "http://geo.stat.fi:8080/geoserver/tilastointialueet/ows";
  
  public AVIProperties query(double lat, double lng) {
    try {
      Response response = new ObjectMapper().readValue(executeQuery(createQueryDocument(lat, lng)), Response.class);
      if (response.getFeatures().length == 1) {
        return response.getFeatures()[0].getProperties();
      }
    } catch (IOException e) {
      logger.log(Level.WARNING, "Failed to resolve avi", e);
    }
    
    return null;
  }
  
  private String createQueryDocument(double lat, double lng) throws IOException {
    return String.format(IOUtils.toString(getClass().getResourceAsStream("wfsquery.template")), FORMAT, TYPE, lng, lat); 
  }
  
  private String executeQuery(String queryDocument) throws IOException {
    DefaultHttpClient client = new DefaultHttpClient();
    try {
      HttpPost httpPost = new HttpPost(URL);
      httpPost.setEntity(new StringEntity(queryDocument));
      
      HttpResponse response = client.execute(httpPost);
      
      HttpEntity entity = response.getEntity();
      try {
        int status = response.getStatusLine().getStatusCode();
        if (status == 200) {
          return IOUtils.toString(entity.getContent(), "UTF-8");
        }
  
        throw new IOException(String.format("Server returned error code %d", status));
      } finally {
        EntityUtils.consume(entity);
      }
    } finally {
      client.close();
    }
  }
  
  @JsonIgnoreProperties (ignoreUnknown = true)
  private static class Response {
    
    public Feature[] getFeatures() {
      return features;
    }
    
    @SuppressWarnings("unused")
    public void setFeatures(Feature[] features) {
      this.features = features;
    }
    
    private Feature[] features;
  }
  
  @JsonIgnoreProperties (ignoreUnknown = true)
  private static class Feature {
    
    public AVIProperties getProperties() {
      return properties;
    }
    
    @SuppressWarnings("unused")
    public void setProperties(AVIProperties properties) {
      this.properties = properties;
    }
    
    private AVIProperties properties;
  }

  @JsonIgnoreProperties (ignoreUnknown = true)
  public static class AVIProperties {
    
    public String getAvi() {
      return avi;
    }
    
    public void setAvi(String avi) {
      this.avi = avi;
    }
    
    public String getName() {
      return name;
    }
    
    public void setName(String name) {
      this.name = name;
    }
    
    public String getNanm() {
      return nanm;
    }
    
    public void setNanm(String nanm) {
      this.nanm = nanm;
    }
    
    public String getNimi() {
      return nimi;
    }
    
    public void setNimi(String nimi) {
      this.nimi = nimi;
    }
    
    private String avi;
    private String name;
    private String nanm;
    private String nimi;
  }
  
}
