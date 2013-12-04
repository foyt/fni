package fi.foyt.fni.ubuntuone;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
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
import fi.foyt.fni.persistence.dao.materials.UbuntuOneRootFolderDAO;
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
import fi.foyt.fni.security.UnauthorizedException;
import fi.foyt.fni.utils.auth.OAuthUtils;

@Dependent
@Stateful
public class UbuntuOneManager {
  
  private static final int MAX_FILES = 10;

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
  private UbuntuOneRootFolderDAO ubuntuOneRootFolderDAO;
  
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

        UbuntuOneNode rootEntity = loadEntity(service, ubuntuOneToken, volumeRoot);
        if (rootEntity.getHasChildren()) {
          List<UbuntuOneNode> children = rootEntity.getChildren();
          for (int i = 0, l = children.size(); i < l; i++) {
            UbuntuOneNode child = children.get(i);
            String childKey = child.getKey();
            if ("directory".equals(child.getKind())) {
              UbuntuOneNode folderEntity = loadEntity(service, ubuntuOneToken, volumeRoot + child.getPath());
              synchronizeEntry(service, ubuntuOneToken, user, ubuntuOneRootFolder, folderEntity, 0);
              existingFolderKeys.remove(childKey);
            } else {
              synchronizeEntry(service, ubuntuOneToken, user, ubuntuOneRootFolder, child, 0);
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

        ubuntuOneRootFolderDAO.updateLastSynchronized(ubuntuOneRootFolder, new Date(), user);
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Ubuntu One Service request failed", e);
      }
    }
  }
  
  private UbuntuOneNode loadEntity(OAuthService service, Token ubuntuOneToken, String path) throws IOException {
    String requestPath = String.format("https://one.ubuntu.com/api/file_storage/v1%s?%s", escapePath(path), "include_children=true");
    Response response = OAuthUtils.doGetRequest(service, ubuntuOneToken, requestPath);
    if (response.getCode() == 200) {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(response.getBody(), UbuntuOneNode.class);
    } else {
      throw new IOException("Ubuntu One Service request failed with error code: " + response.getCode());
    }
  }

  private int synchronizeEntry(OAuthService service, Token ubuntuOneToken, User user, Folder parentFolder, UbuntuOneNode ubuntuOneNode, int filesProcessed) throws IOException {
    if (filesProcessed >= MAX_FILES) {
      return filesProcessed;
    }
    
    if (ubuntuOneNode.getIsLive()) {
      String kind = ubuntuOneNode.getKind();
      String key = ubuntuOneNode.getKey();
      String path = ubuntuOneNode.getPath();
      String contentPath = ubuntuOneNode.getContentPath();
      String title = extractTitle(path);
      Long generation = ubuntuOneNode.getGeneration();
  
      if ("directory".equals(kind)) {
        UbuntuOneNode folderEntity = loadEntity(service, ubuntuOneToken, ubuntuOneNode.getResource_path());
        filesProcessed = synchronizeFolder(service, ubuntuOneToken, user, parentFolder, folderEntity, key, contentPath, title, generation, filesProcessed);
      } else if ("file".equals(kind)) {
        filesProcessed = syncronizeFile(ubuntuOneToken, user, parentFolder, key, contentPath, title, generation, filesProcessed);
      }
    }
    
    return filesProcessed;
  }

  private int synchronizeFolder(OAuthService service, Token ubuntuOneToken, User user, Folder parentFolder, 
      UbuntuOneNode ubuntuOneNode, String key, String contentPath, String title, Long generation, int filesProcessed) throws IOException {
    
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

    if (ubuntuOneNode.getHasChildren()) {
      List<UbuntuOneNode> children = ubuntuOneNode.getChildren();
      for (int i = 0, l = children.size(); i < l; i++) {
        UbuntuOneNode child = children.get(i);
        String childKey = child.getKey();
        
        if ("directory".equals(child.getKind())) {
          existingFolderKeys.remove(childKey);
          UbuntuOneNode folderEntity = loadEntity(service, ubuntuOneToken, child.getResource_path());
          filesProcessed = synchronizeEntry(service, ubuntuOneToken, user, folder, folderEntity, filesProcessed);
        } else {
          existingFileKeys.remove(childKey);
          filesProcessed = synchronizeEntry(service, ubuntuOneToken, user, folder, child, filesProcessed);
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
    
    return filesProcessed;
  }

  private int syncronizeFile(Token ubuntuOneToken, User user, Folder parentFolder, String key, String contentPath, String title, Long generation, int filesProcessed) {
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

        filesProcessed++;
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
        
        filesProcessed++;
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Failed to import file from Ubuntu One: " + contentPath, e);
      }
    }
    
    return filesProcessed;
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
    if (ubuntuOneToken != null) {
      return getFileContent(ubuntuOneToken, file.getContentPath());
    } else {
    	throw new UnauthorizedException();
    }
  }
  
  private String extractTitle(String path) {
    int slashPos = path.lastIndexOf("/");
    if (slashPos > -1) {
      return path.substring(slashPos + 1);
    }

    return path;
  }

  @SuppressWarnings ("unused")
  private static class UbuntuOneNode {

    public String getResource_path() {
      return resource_path;
    }

    public void setResource_path(String resource_path) {
      this.resource_path = resource_path;
    }

    public String getKind() {
      return kind;
    }

    public void setKind(String kind) {
      this.kind = kind;
    }

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      this.path = path;
    }

    public Boolean getIsPublic() {
      return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
      this.isPublic = isPublic;
    }

    public String getParentPath() {
      return parentPath;
    }

    public void setParentPath(String parentPath) {
      this.parentPath = parentPath;
    }

    public String getVolumePath() {
      return volumePath;
    }

    public void setVolumePath(String volumePath) {
      this.volumePath = volumePath;
    }

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public Date getWhenCreated() {
      return whenCreated;
    }

    public void setWhenCreated(Date whenCreated) {
      this.whenCreated = whenCreated;
    }

    public Date getWhenChanged() {
      return whenChanged;
    }

    public void setWhenChanged(Date whenChanged) {
      this.whenChanged = whenChanged;
    }

    public Long getGeneration() {
      return generation;
    }

    public void setGeneration(Long generation) {
      this.generation = generation;
    }

    public Long getGenerationCreated() {
      return generationCreated;
    }

    public void setGenerationCreated(Long generationCreated) {
      this.generationCreated = generationCreated;
    }

    public String getContentPath() {
      return contentPath;
    }

    public void setContentPath(String contentPath) {
      this.contentPath = contentPath;
    }

    public String getHash() {
      return hash;
    }

    public void setHash(String hash) {
      this.hash = hash;
    }

    public String getPublicUrl() {
      return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
      this.publicUrl = publicUrl;
    }

    public Long getSize() {
      return size;
    }

    public void setSize(Long size) {
      this.size = size;
    }

    public Boolean getHasChildren() {
      return hasChildren;
    }

    public void setHasChildren(Boolean hasChildren) {
      this.hasChildren = hasChildren;
    }

    public Boolean getIsRoot() {
      return isRoot;
    }

    public void setIsRoot(Boolean isRoot) {
      this.isRoot = isRoot;
    }

    public List<UbuntuOneNode> getChildren() {
      return children;
    }

    public void setChildren(List<UbuntuOneNode> children) {
      this.children = children;
    }

    public Boolean getIsLive() {
      return isLive;
    }

    public void setIsLive(Boolean isLive) {
      this.isLive = isLive;
    }

    private String resource_path;

    private String kind;

    private String path;

    @JsonProperty("is_public")
    private Boolean isPublic;

    @JsonProperty("parent_path")
    private String parentPath;

    @JsonProperty("volume_path")
    private String volumePath;

    private String key;

    @JsonProperty("when_created")
    private Date whenCreated;

    @JsonProperty("when_changed")
    private Date whenChanged;

    private Long generation;

    @JsonProperty("generation_created")
    private Long generationCreated;

    @JsonProperty("content_path")
    private String contentPath;

    private String hash;

    @JsonProperty("public_url")
    private String publicUrl;

    private Long size;

    @JsonProperty("has_children")
    private Boolean hasChildren;

    @JsonProperty("is_root")
    private Boolean isRoot;

    private List<UbuntuOneNode> children;

    @JsonProperty("is_live")
    private Boolean isLive;
  }
}

