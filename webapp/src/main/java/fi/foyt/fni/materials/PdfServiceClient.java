package fi.foyt.fni.materials;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.data.TypedData;

public class PdfServiceClient {
  
  @Inject
  private Logger logger;
  
  @Inject
  private SystemSettingsController systemSettingsController;

  @PostConstruct
  public void init() {
    serviceAuth = systemSettingsController.getSetting(SystemSettingKey.PDF_SERVICE_SECRET);
    serviceUrl = systemSettingsController.getSetting(SystemSettingKey.PDF_SERVICE_URL);
  }
  
  public TypedData getURLAsPdf(String url, Map<String, String> options) {
    try {
      DefaultHttpClient client = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(serviceUrl + "/pdf");
      httpPost.setHeader("Content-Type", "application/json");
      httpPost.setHeader("Authorization", serviceAuth);
      
      PdfRequest pdfRequest = new PdfRequest(url, options);
      String payload = (new ObjectMapper()).writeValueAsString(pdfRequest);
      httpPost.setEntity(new StringEntity(payload));
      
      HttpResponse response = client.execute(httpPost);
      
      HttpEntity entity = response.getEntity();
      try {
        int status = response.getStatusLine().getStatusCode();
        if (status == 200) {
          byte[] content = IOUtils.toByteArray(entity.getContent());
          String contentType = entity.getContentType().getValue();
          
          return new TypedData(content, contentType);
        } else {
          logger.log(Level.WARNING, String.format("Pdf service returned %d (%s)", status, IOUtils.toString(entity.getContent())));
        }
      } finally {
        EntityUtils.consume(entity);
      }
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to convert url %s into pdf");
    }

    return null;
  }
  
  private String serviceAuth;
  private String serviceUrl;

  @SuppressWarnings("unused")
  private static class PdfRequest {

    public PdfRequest(String url, Map<String, String> options) {
      super();
      this.url = url;
      this.options = options;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public Map<String, String> getOptions() {
      return options;
    }

    public void setOptions(Map<String, String> options) {
      this.options = options;
    }

    private String url;
    private Map<String, String> options;
  }

}
