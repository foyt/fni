package fi.foyt.fni.drive;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.Insert;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;

import fi.foyt.fni.utils.data.TypedData;

@Dependent
@Stateless
public class DriveManager {

	private static final String APPLICATION_NAME = "Forge & Illusion";
  private static final HttpTransport TRANSPORT = new NetHttpTransport();
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  public GoogleCredential getAccessTokenCredential(String accessToken) {
    Details webDetails = new Details();
    
    String apiKey = "bogus";
    String apiSecret = "bogus";
    
    webDetails.setClientId(apiKey);
    webDetails.setClientSecret(apiSecret);
    
    GoogleClientSecrets secrets = new GoogleClientSecrets();
    secrets.setWeb(webDetails);
     
    return new GoogleCredential.Builder()
      .setClientSecrets(secrets)
      .setTransport(TRANSPORT)
      .setJsonFactory(JSON_FACTORY)
      .build()
      .setAccessToken(accessToken);
  }
  
	public Drive getDrive(GoogleCredential credential) {
		return new Drive.Builder(TRANSPORT, JSON_FACTORY, credential)
  	  .setApplicationName(APPLICATION_NAME)
      .build();
	}
	
	public File insertFile(Drive drive, String title, String description, String mimeType, String parentId, boolean convert, byte[] content) throws IOException {
		File file = new File();
    file.setTitle(title);
    file.setDescription(description);
    file.setMimeType(mimeType);

    if (StringUtils.isNotBlank(parentId)) {
      file.setParents(Arrays.asList(new ParentReference().setId(parentId)));
    }
    
    Files files = drive.files();
    Insert insert;

    if (content != null) {
      ByteArrayContent fileContent = new ByteArrayContent(mimeType, content);
      insert = files.insert(file, fileContent);
      insert.getMediaHttpUploader().setDirectUploadEnabled(true);
    } else {
      insert = files.insert(file);
    }
    
    insert.setConvert(convert);
    
    return insert.execute();
	}
	
	public File getFile(Drive drive, String fileId) throws IOException {
		Files files = drive.files();
		return files.get(fileId).execute();
	}

	public FileList listFiles(Drive drive, String q) throws IOException {
		return drive.files()
      .list()
      .setQ(q)
      .execute();
	}

	public TypedData exportFile(Drive drive, File file, String format) throws IOException {
		Map<String, String> exportLinks = file.getExportLinks();
		if (exportLinks != null) {
			if (exportLinks.containsKey(format)) {
				String exportLink = exportLinks.get(format);
				return downloadUrl(drive, exportLink);
			}
		}
		
		return null;
	}
	
	public TypedData exportSpreadsheet(Drive drive, File file) throws MalformedURLException, IOException {
		String exportLink = file.getAlternateLink() + "&chrome=false&output=html";
		return downloadUrl(drive, exportLink);
	}

	public TypedData downloadFile(Drive drive, File file) throws MalformedURLException, IOException {
		return downloadUrl(drive, file.getDownloadUrl());
	}
	
	public void deleteFile(Drive drive, File file) throws IOException {
		drive.files().delete(file.getId()).execute();
	}
	
	public PermissionList listPermissions(Drive drive, String fileId) throws IOException {
		return drive.permissions().list(fileId).execute();
	}

	public Permission insertPermission(Drive drive, String fileId, Permission permission) throws IOException {
		return drive.permissions().insert(fileId, permission).execute();
	}
	
	public boolean hasRoles(Drive drive, String email, String fileId, String... roles) throws IOException {
		PermissionList permissionList = listPermissions(drive, fileId);
		List<Permission> items = permissionList.getItems();
		for (Permission item : items) {
			if ("user".equals(item.getType())) {
				if (email.equals(item.getValue())) {
					for (String role : roles) {
						if (role.equals(item.getRole())) {
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}

	private TypedData downloadUrl(Drive drive, String url) throws MalformedURLException, IOException {
		URL exportUrl = new URL(url);
		
		HttpURLConnection urlConnection = (HttpURLConnection) exportUrl.openConnection();
		
		GoogleCredential credential = (GoogleCredential) drive.getRequestFactory().getInitializer();
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		urlConnection.addRequestProperty("Authorization", "Bearer " + credential.getAccessToken());
		InputStream inputStream = urlConnection.getInputStream();
		try {
		  byte[] data = IOUtils.toByteArray(inputStream);
		  return new TypedData(data, urlConnection.getContentType());
		} finally {
			inputStream.close();
		}
	}
}
