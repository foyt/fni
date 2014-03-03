package fi.foyt.fni.dropbox;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import fi.foyt.fni.auth.DropboxAuthenticationStrategy;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.dao.auth.UserIdentifierDAO;
import fi.foyt.fni.persistence.dao.materials.DropboxFileDAO;
import fi.foyt.fni.persistence.dao.materials.DropboxFolderDAO;
import fi.foyt.fni.persistence.dao.materials.DropboxRootFolderDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialDAO;
import fi.foyt.fni.persistence.dao.users.UserTokenDAO;
import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.auth.UserIdentifier;
import fi.foyt.fni.persistence.model.materials.DropboxFile;
import fi.foyt.fni.persistence.model.materials.DropboxFolder;
import fi.foyt.fni.persistence.model.materials.DropboxRootFolder;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.security.UnauthorizedException;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.auth.OAuthUtils;

@Dependent
public class DropboxManager implements Serializable {

  private static final long serialVersionUID = -1726856277015582236L;

  @Inject
  private Logger logger;

  @Inject
  private MaterialController materialController;

  @Inject
	private SystemSettingsController systemSettingsController;

  @Inject
  private UserTokenDAO userTokenDAO;

  @Inject
  private UserIdentifierDAO userIdentifierDAO;

  @Inject
  private DropboxFolderDAO dropboxFolderDAO;

  @Inject
  private DropboxRootFolderDAO dropboxRootFolderDAO;

  @Inject
  private DropboxFileDAO dropboxFileDAO;

  @Inject
  private MaterialDAO materialDAO;

  @Inject
  private DropboxAuthenticationStrategy dropboxAuthenticationStrategy;

  public Token getDropboxToken(User user) {
    List<UserIdentifier> dropboxIdentifiers = userIdentifierDAO.listByAuthSourceAndUser(AuthSource.DROPBOX, user);
    for (UserIdentifier dropboxIdentifier : dropboxIdentifiers) {
      UserToken dropboxToken = userTokenDAO.findByUserIdentifier(dropboxIdentifier);
      if (dropboxToken != null) {
        return new Token(dropboxToken.getToken(), dropboxToken.getSecret());
      }
    }

    return null;
  }

  public DropboxRootFolder getDropboxRootFolder(User user) {
    return dropboxRootFolderDAO.findByUser(user);
  }

  public void synchronizeFolder(DropboxRootFolder dropboxRootFolder) {
    User user = dropboxRootFolder.getCreator();
    Token dropboxToken = getDropboxToken(user);

    if (dropboxToken != null && dropboxRootFolder != null) {
      OAuthService service = dropboxAuthenticationStrategy.getOAuthService();

      Boolean hasMore = true;

      while (hasMore) {
        try {
          Map<String, String> parameters = new HashMap<String, String>();

          if (StringUtils.isNotBlank(dropboxRootFolder.getDeltaCursor()))
            parameters.put("cursor", dropboxRootFolder.getDeltaCursor());

          Map<String, Object> deltaResponse = serializeMap(OAuthUtils.doPostRequest(service, dropboxToken, "https://api.dropbox.com/1/delta", parameters).getBody());
          hasMore = (Boolean) deltaResponse.get("has_more");
          if (hasMore == null) {
            hasMore = false;
          }
          
          Boolean reset = (Boolean) deltaResponse.get("reset");
          String cursor = (String) deltaResponse.get("cursor");
          Date now = new Date();

          if (reset) {
            List<Material> files = materialDAO.listByParentFolder(dropboxRootFolder);
            for (Material file : files) {
              materialController.deleteMaterial(file, user);
            }
          }

          @SuppressWarnings("unchecked")
          List<Object> entries = (List<Object>) deltaResponse.get("entries");
          for (int i = 0, l = entries.size(); i < l; i++) {
            @SuppressWarnings("unchecked")
            List<Object> entry = (List<Object>) entries.get(i);
            String entryPath = (String) entry.get(0);
            @SuppressWarnings("unchecked")
            Map<String, Object> metaData = (Map<String, Object>) entry.get(1);
            if (metaData == null) {
              DropboxFile dropboxFile = dropboxFileDAO.findByDropboxPath(entryPath);
              if (dropboxFile != null) {
                materialController.deleteMaterial(dropboxFile, user);
                logger.info("Dropbox file " + entryPath + " removed.");
              } else {
                DropboxFolder dropboxFolder = dropboxFolderDAO.findByCreatorAndDropboxPath(user, entryPath);
                if (dropboxFolder != null) {
                  materialController.deleteMaterial(dropboxFolder, user);
                } else {
                  logger.warning("Could not find removed Dropbox file " + entryPath);
                }
              }
            } else {
              // String rev = metaData.getString("rev");
              // Boolean thumbExists = metaData.getBoolean("thumb_exists");
              // Long revision = metaData.getLong("revision");
              // Long bytes = metaData.getLong("bytes");
              // String modified = metaData.getString("modified");
              // String root = metaData.getString("root");
              // String icon = metaData.getString("icon");
              // String size = metaData.getString("size");
              Boolean isDir = (Boolean) metaData.get("is_dir");
              String path = (String) metaData.get("path");
              String[] parents = path.split("/");

              Folder parentFolder = null;
              if (parents.length == 2) {
                parentFolder = dropboxRootFolder;
              } else {
                // If the new entry includes parent folders that don't yet exist in
                // Forge & Illusion, we need to create those parent folders before continuing.
                parentFolder = dropboxRootFolder;
                for (int parentIndex = 1, parentsLength = parents.length - 1; parentIndex < parentsLength; parentIndex++) {
                  String parent = parents[parentIndex];
                  Folder foundFolder = (Folder) materialDAO.findByParentFolderAndUrlName(parentFolder, parent);
                  if (foundFolder != null) {
                    parentFolder = foundFolder;
                  } else {
                    String urlName = materialController.getUniqueMaterialUrlName(user, parentFolder, null, parent);
                    parentFolder = dropboxFolderDAO.create(user, now, user, now, parentFolder, urlName, parent, MaterialPublicity.PRIVATE, entryPath);
                    logger.info("Created new dropbox folder: " + parent);
                  }
                }
              }

              String title = extractTitle(path);
              String urlName = materialController.getUniqueMaterialUrlName(user, parentFolder, null, title);

              if (isDir) {
                dropboxFolderDAO.create(user, now, user, now, parentFolder, urlName, title, MaterialPublicity.PRIVATE, entryPath);
                logger.info("Created new dropbox folder: " + title);
              } else {
                String mimeType = (String) metaData.get("mime_type");
                // String clientMtime = metaData.optString("client_mtime");
                dropboxFileDAO.create(user, null, parentFolder, urlName, title, MaterialPublicity.PRIVATE, entryPath, mimeType);
                logger.info("Created new dropbox file: " + title);
              }
            }
          }

          dropboxRootFolderDAO.updateDeltaCursor(dropboxRootFolder, cursor, user);
          dropboxRootFolderDAO.updateLastSynchronized(dropboxRootFolder, new Date(), user);
        } catch (IOException e) {
          logger.log(Level.SEVERE, "Failed to read Dropbox Delta JSON", e);
        }
      }
    }
  }

  private Map<String, Object> serializeMap(String string) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(string, new TypeReference<Map<String, Object>>(){});
  }

  public DropboxFile getFile(String path) {
    DropboxFile dropboxFile = dropboxFileDAO.findByDropboxPath(path);
    return dropboxFile;
  }

  public Response getFileContent(User user, DropboxFile dropboxFile) throws IOException {
    Token dropboxToken = getDropboxToken(user);
    if (dropboxToken == null) {
    	throw new UnauthorizedException();
    }
    
    OAuthService service = dropboxAuthenticationStrategy.getOAuthService();

    String root = systemSettingsController.getSetting(SystemSettingKey.DROPBOX_ROOT);
    String url = "https://api-content.dropbox.com/1/files/" + root + dropboxFile.getDropboxPath();

    return OAuthUtils.doGetRequest(service, dropboxToken, url);
  }

  private String extractTitle(String path) {
    int slashPos = path.lastIndexOf("/");
    if (slashPos > -1) {
      return path.substring(slashPos + 1);
    }

    return path;
  }
}
