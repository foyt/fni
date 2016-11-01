package fi.foyt.fni.drive;

import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.DriveScopes;

@ApplicationScoped
public class SystemGoogleDriveCredentials implements Serializable {
  
  private static final long serialVersionUID = -7571174639569905090L;
  
  @Inject
  private Logger logger;

  @PostConstruct
  public void init() {
    String accountId = System.getProperty("fni-google-drive.accountId");
    String accountUser = System.getProperty("fni-google-drive.accountUser");
    java.io.File keyFile = new java.io.File(System.getProperty("fni-google-drive.keyFile"));

    try {
      systemCredential = new GoogleCredential.Builder()
        .setTransport(new NetHttpTransport())
        .setJsonFactory(new JacksonFactory())
        .setServiceAccountId(accountId)
        .setServiceAccountScopes(Arrays.asList(DriveScopes.DRIVE))
        .setServiceAccountPrivateKeyFromP12File(keyFile)
        .setServiceAccountUser(accountUser)
        .build();
    } catch (GeneralSecurityException | IOException e) {
      logger.log(Level.SEVERE, "Error occurred while trying to initiate System Google Drive credentials", e);
    }
  }
  
  public GoogleCredential getSystemCredential() {
    return systemCredential;
  }
  
  private GoogleCredential systemCredential;
}
