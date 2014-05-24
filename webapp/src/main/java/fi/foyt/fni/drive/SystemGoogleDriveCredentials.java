package fi.foyt.fni.drive;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.DriveScopes;

@ApplicationScoped
@Stateful
public class SystemGoogleDriveCredentials {
  
  @PostConstruct
  public void init() throws GeneralSecurityException, IOException {
    String accountId = System.getProperty("fni-google-drive.accountId");
    String accountUser = System.getProperty("fni-google-drive.accountUser");
    java.io.File keyFile = new java.io.File(System.getProperty("fni-google-drive.keyFile"));

    systemCredential = new GoogleCredential.Builder()
      .setTransport(new NetHttpTransport())
      .setJsonFactory(new JacksonFactory())
      .setServiceAccountId(accountId)
      .setServiceAccountScopes(Arrays.asList(DriveScopes.DRIVE))
      .setServiceAccountPrivateKeyFromP12File(keyFile)
      .setServiceAccountUser(accountUser)
      .build();
  }
  
  public GoogleCredential getSystemCredential() {
    return systemCredential;
  }
  
  private GoogleCredential systemCredential;
}
