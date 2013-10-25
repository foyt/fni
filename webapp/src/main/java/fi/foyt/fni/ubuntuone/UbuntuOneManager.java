package fi.foyt.fni.ubuntuone;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import fi.foyt.fni.auth.UbuntuOneAuthenticationStrategy;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.dao.auth.UserIdentifierDAO;
import fi.foyt.fni.persistence.dao.materials.FolderDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialDAO;
import fi.foyt.fni.persistence.dao.materials.UbuntuOneFileDAO;
import fi.foyt.fni.persistence.dao.materials.UbuntuOneFolderDAO;
import fi.foyt.fni.persistence.dao.users.UserTokenDAO;
import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.auth.UserIdentifier;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.UbuntuOneFile;
import fi.foyt.fni.persistence.model.materials.UbuntuOneFolder;
import fi.foyt.fni.persistence.model.materials.UbuntuOneRootFolder;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.utils.auth.OAuthUtils;

@Dependent
@Stateful
public class UbuntuOneManager {
  
  private static final int MAX_NEW_FILES = 10;

  @Inject
  private Logger logger;
  
  @Inject
  private MaterialController materialController;
  
  @Inject
  private UserTokenDAO userTokenDAO;

  @Inject
  private UserIdentifierDAO userIdentifierDAO;

  @Inject
  private FolderDAO folderDAO;

  @Inject
  private MaterialDAO materialDAO;

  @Inject
  private UbuntuOneFolderDAO ubuntuOneFolderDAO;

  @Inject
  private UbuntuOneFileDAO ubuntuOneFileDAO;
  
  @Inject
  private UbuntuOneAuthenticationStrategy ubuntuOneAuthenticationStrategy;

  public Token getUbuntuOneToken(User user) {
    List<UserIdentifier> ubuntuOneIdentifiers = userIdentifierDAO.listByAuthSourceAndUser(AuthSource.UBUNTU_ONE, user);
    for (UserIdentifier ubuntuOneIdentifier : ubuntuOneIdentifiers) {
      UserToken ubuntuOneToken = userTokenDAO.findByUserIdentifier(ubuntuOneIdentifier);
      if (ubuntuOneToken != null) {
        return new Token(ubuntuOneToken.getToken(), ubuntuOneToken.getSecret());
      }
    }

    return null;
  }

  public void synchronizeFolder(UbuntuOneRootFolder ubuntuOneRootFolder) {
    User user = ubuntuOneRootFolder.getCreator();
    Token ubuntuOneToken = getUbuntuOneToken(user);

    if (ubuntuOneToken != null) {
      OAuthService service = ubuntuOneAuthenticationStrategy.getOAuthService();
      
      try {
        String volumeRoot = "/~/Ubuntu One"; 
        List<String> existingFileKeys = ubuntuOneFileDAO.listUbuntuOneKeysByParentFolderAndCreator(ubuntuOneRootFolder, user);
        List<String> existingFolderKeys = ubuntuOneFolderDAO.listUbuntuOneKeysByParentFolderAndCreator(ubuntuOneRootFolder, user);

        JSONObject rootEntity = loadEntity(service, ubuntuOneToken, volumeRoot);
        if (rootEntity.getBoolean("has_children")) {
          JSONArray children = rootEntity.getJSONArray("children");
          for (int i = 0, l = children.length(); i < l; i++) {
            JSONObject child = children.getJSONObject(i);
            String childKey = child.getString("key");
            if ("directory".equals(child.getString("kind"))) {
              JSONObject folderEntity = loadEntity(service, ubuntuOneToken, volumeRoot + child.getString("path"));
              synchronizeEntry(service, ubuntuOneToken, user, ubuntuOneRootFolder, folderEntity);
              existingFolderKeys.remove(childKey);
            } else {
              synchronizeEntry(service, ubuntuOneToken, user, ubuntuOneRootFolder, child);
              existingFileKeys.remove(childKey);
            }
          }
        }

        for (String existingFolderKey : existingFolderKeys) {
          UbuntuOneFolder ubuntuOneFolder = ubuntuOneFolderDAO.findByUbuntuOneKey(existingFolderKey);
          logger.info("Deleted Ubuntu One Folder: " + ubuntuOneFolder.getContentPath());
          materialController.deleteMaterial(ubuntuOneFolder, user);
        }

        for (String existingFileKey : existingFileKeys) {
          UbuntuOneFile ubuntuOneFile = ubuntuOneFileDAO.findByUbuntuOneKey(existingFileKey);
          logger.info("Deleted Ubuntu One File: " + ubuntuOneFile.getContentPath());
          materialController.deleteMaterial(ubuntuOneFile, user);
        } 

      } catch (JSONException e) {
        logger.log(Level.SEVERE, "Failed to parse Ubuntu One delta JSON", e);
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Ubuntu One Service request failed", e);
      }
    }
  }
  
  private JSONObject loadEntity(OAuthService service, Token ubuntuOneToken, String path) throws IOException, JSONException {
    String requestPath = String.format("https://one.ubuntu.com/api/file_storage/v1%s?%s", escapePath(path), "include_children=true");
    Response response = OAuthUtils.doGetRequest(service, ubuntuOneToken, requestPath);
    if (response.getCode() == 200) {
      return new JSONObject(response.getBody());
    } else {
      throw new IOException("Ubuntu One Service request failed with error code: " + response.getCode());
    }
  }

  private void synchronizeEntry(OAuthService service, Token ubuntuOneToken, User user, Folder parentFolder, JSONObject jsonEntity) throws JSONException, IOException {
    if (newFilesAdded > MAX_NEW_FILES) {
      // When new file count has exceeded maximum count we stop the synchronization.
      return;
    }
    
    String kind = jsonEntity.getString("kind");
    String key = jsonEntity.getString("key");
    String path = jsonEntity.getString("path");
    String contentPath = jsonEntity.getString("content_path");
    String title = extractTitle(path);
    Long generation = jsonEntity.getLong("generation");

    if ("directory".equals(kind)) {
      JSONObject folderEntity = loadEntity(service, ubuntuOneToken, jsonEntity.getString("resource_path"));
      synchronizeFolder(service, ubuntuOneToken, user, parentFolder, folderEntity, key, contentPath, title, generation);
    } else if ("file".equals(kind)) {
      syncronizeFile(ubuntuOneToken, user, parentFolder, key, contentPath, title, generation);
    }
  }

  private void synchronizeFolder(OAuthService service, Token ubuntuOneToken, User user, Folder parentFolder, 
      JSONObject jsonEntity, String key, String contentPath, String title, Long generation) throws JSONException, IOException {
    
    List<String> existingFileKeys = null;
    List<String> existingFolderKeys = null;
    
    UbuntuOneFolder folder = ubuntuOneFolderDAO.findByUbuntuOneKey(key);
    if (folder != null) {
      // Folder already exists
      if (folder.getGeneration() < generation) {
        // Folder needs to be updated
        String urlName = materialController.getUniqueMaterialUrlName(user, parentFolder, folder, title);
        materialDAO.updateTitle(folder, title, user);
        materialDAO.updateUrlName(folder, urlName, user);
        materialDAO.updateParentFolder(folder, parentFolder, user);
        ubuntuOneFolderDAO.updateGeneration(folder, generation, user);
        ubuntuOneFolderDAO.updateContentPath(folder, contentPath, user);

        logger.info("Updated Ubuntu One Folder " + folder.getContentPath() + " to generation " + folder.getGeneration());
      }
      
      existingFileKeys = ubuntuOneFileDAO.listUbuntuOneKeysByParentFolderAndCreator(folder, user);
      existingFolderKeys = ubuntuOneFolderDAO.listUbuntuOneKeysByParentFolderAndCreator(folder, user);
    } else {
      // Folder is new
      String urlName = materialController.getUniqueMaterialUrlName(user, parentFolder, null, title);
      folder = ubuntuOneFolderDAO.create(user, null, parentFolder, urlName, title, MaterialPublicity.PRIVATE, key, generation, contentPath);
      existingFileKeys = new ArrayList<String>();
      existingFolderKeys = new ArrayList<String>();

      logger.info("Added new Ubuntu One Folder " + folder.getContentPath() + " generation " + folder.getGeneration());
    }

    if (jsonEntity.getBoolean("has_children")) {
      JSONArray children = jsonEntity.getJSONArray("children");
      for (int i = 0, l = children.length(); i < l; i++) {
        JSONObject child = children.getJSONObject(i);
        String childKey = child.getString("key");
        
        if ("directory".equals(child.getString("kind"))) {
          existingFolderKeys.remove(childKey);
          JSONObject folderEntity = loadEntity(service, ubuntuOneToken, child.getString("resource_path"));
          synchronizeEntry(service, ubuntuOneToken, user, folder, folderEntity);
        } else {
          existingFileKeys.remove(childKey);
          synchronizeEntry(service, ubuntuOneToken, user, folder, child);
        }
      }
    }

    for (String existingFolderKey : existingFolderKeys) {
      UbuntuOneFolder ubuntuOneFolder = ubuntuOneFolderDAO.findByUbuntuOneKey(existingFolderKey);
      logger.info("Deleted Ubuntu One Folder: " + ubuntuOneFolder.getContentPath());
      materialController.deleteMaterial(ubuntuOneFolder, user);
    }

    for (String existingFileKey : existingFileKeys) {
      UbuntuOneFile ubuntuOneFile = ubuntuOneFileDAO.findByUbuntuOneKey(existingFileKey);
      logger.info("Deleted Ubuntu One File: " + ubuntuOneFile.getContentPath());
      materialController.deleteMaterial(ubuntuOneFile, user);
    } 
  }

  private void syncronizeFile(Token ubuntuOneToken, User user, Folder parentFolder, String key, String contentPath, String title, Long generation) {
    UbuntuOneFile file = ubuntuOneFileDAO.findByUbuntuOneKey(key);
    if (file != null) {
      // File exists
      if (file.getGeneration() < generation) {
        // File needs to be updated. Btw. we assume that file content type remains unchanged.
        String urlName = materialController.getUniqueMaterialUrlName(user, parentFolder, file, title);
        materialDAO.updateTitle(file, title, user);
        materialDAO.updateUrlName(file, urlName, user);
        materialDAO.updateParentFolder(file, parentFolder, user);
        ubuntuOneFileDAO.updateGeneration(file, generation, user);
        ubuntuOneFileDAO.updateContentPath(file, contentPath, user);

        logger.info("Updated Ubuntu One File " + file.getContentPath() + " to generation " + file.getGeneration());
      }
    } else {
      // File is new
      String urlName = materialController.getUniqueMaterialUrlName(user, parentFolder, file, title);
      // U1 API does not unfortunately provide mime type for files
      // so we need to fetch it from U1 server in a separate request.

      try {
        Response response = getFileContent(ubuntuOneToken, contentPath);
        if (response.getCode() == HttpURLConnection.HTTP_OK) {
          String mimeType = response.getHeader("Content-Type");
          UbuntuOneFile ubuntuOneFile = ubuntuOneFileDAO.create(user, null, parentFolder, urlName, title, MaterialPublicity.PRIVATE, key, generation, contentPath, mimeType);
          logger.info("Added new Ubuntu One File " + ubuntuOneFile.getContentPath() + " generation " + ubuntuOneFile.getGeneration());
        } else {
          logger.log(Level.WARNING, "Failed to import file from Ubuntu One: " + contentPath + ", error code: " + response.getCode());
        }
        
        newFilesAdded++;
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Failed to import file from Ubuntu One: " + contentPath, e);
      }
    }
  }
  
  public Response getFileContent(Token ubuntuOneToken, String contentPath) throws IOException {
    OAuthService service = ubuntuOneAuthenticationStrategy.getOAuthService();

    String url = String.format("https://files.one.ubuntu.com%s", escapePath(contentPath));

    return OAuthUtils.doGetRequest(service, ubuntuOneToken, url);
  }
  
  private String escapePath(String path) throws UnsupportedEncodingException {
    String urlEncoded = URLEncoder.encode(path, "UTF-8");
    return urlEncoded.replaceAll("%2F", "/").replaceAll("%7E", "~").replace("+", "%20");
  }
  
  public Response getFileContent(User user, UbuntuOneFile file) throws IOException {
    Token ubuntuOneToken = getUbuntuOneToken(user);
    return getFileContent(ubuntuOneToken, file.getContentPath());
  }
  
  private String extractTitle(String path) {
    int slashPos = path.lastIndexOf("/");
    if (slashPos > -1) {
      return path.substring(slashPos + 1);
    }

    return path;
  }
  
  private int newFilesAdded = 0;
}

