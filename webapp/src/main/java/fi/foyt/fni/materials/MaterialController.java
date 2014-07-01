package fi.foyt.fni.materials;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.scribe.model.Response;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.foyt.fni.drive.DriveManager;
import fi.foyt.fni.drive.SystemGoogleDriveCredentials;
import fi.foyt.fni.dropbox.DropboxManager;
import fi.foyt.fni.persistence.dao.common.LanguageDAO;
import fi.foyt.fni.persistence.dao.materials.BinaryDAO;
import fi.foyt.fni.persistence.dao.materials.DocumentDAO;
import fi.foyt.fni.persistence.dao.materials.DocumentRevisionDAO;
import fi.foyt.fni.persistence.dao.materials.FileDAO;
import fi.foyt.fni.persistence.dao.materials.FolderDAO;
import fi.foyt.fni.persistence.dao.materials.GoogleDocumentDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialTagDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialThumbnailDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialViewDAO;
import fi.foyt.fni.persistence.dao.materials.PdfDAO;
import fi.foyt.fni.persistence.dao.materials.PermaLinkDAO;
import fi.foyt.fni.persistence.dao.materials.StarredMaterialDAO;
import fi.foyt.fni.persistence.dao.materials.UserMaterialRoleDAO;
import fi.foyt.fni.persistence.dao.materials.VectorImageDAO;
import fi.foyt.fni.persistence.dao.materials.VectorImageRevisionDAO;
import fi.foyt.fni.persistence.dao.users.UserDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.Binary;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.DocumentRevision;
import fi.foyt.fni.persistence.model.materials.DropboxFile;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.GoogleDocument;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.materials.ImageSize;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialRole;
import fi.foyt.fni.persistence.model.materials.MaterialTag;
import fi.foyt.fni.persistence.model.materials.MaterialThumbnail;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.materials.MaterialView;
import fi.foyt.fni.persistence.model.materials.Pdf;
import fi.foyt.fni.persistence.model.materials.PermaLink;
import fi.foyt.fni.persistence.model.materials.StarredMaterial;
import fi.foyt.fni.persistence.model.materials.UserMaterialRole;
import fi.foyt.fni.persistence.model.materials.VectorImage;
import fi.foyt.fni.persistence.model.materials.VectorImageRevision;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.utils.data.FileData;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.html.HtmlUtils;
import fi.foyt.fni.utils.images.ImageUtils;
import fi.foyt.fni.utils.language.GuessedLanguage;
import fi.foyt.fni.utils.language.LanguageUtils;
import fi.foyt.fni.utils.search.SearchResult;
import fi.foyt.fni.utils.search.SearchResultScoreComparator;
import fi.foyt.fni.utils.servlet.RequestUtils;

@RequestScoped
@Stateful
public class MaterialController {

  private static final String MATERIALS_PATH = "materials";
  private static final long DEFAULT_MATERIAL_SIZE = 2048;
  private static final long DEFAULT_QUOTA = 1024 * 1024 * 10;
  private static final String DOCUMENT_TEMPLATE = "<!DOCTYPE HTML><html><head><meta charset=\"UTF-8\"><title>{0}</title><link rel=\"StyleSheet\" href=\"{1}\"/></head><body>{2}</body></html>";

  @Inject
  private Logger logger;

  @Inject
  private FullTextEntityManager fullTextEntityManager;

  @Inject
  private LanguageDAO languageDAO;

  @Inject
  private MaterialDAO materialDAO;

  @Inject
  private FolderDAO folderDAO;

  @Inject
  private FileDAO fileDAO;

  @Inject
  private PdfDAO pdfDAO;

  @Inject
  private BinaryDAO binaryDAO;

  @Inject
  private GoogleDocumentDAO googleDocumentDAO;

  @Inject
  private PermaLinkDAO permaLinkDAO;

  @Inject
  private StarredMaterialDAO starredMaterialDAO;

  @Inject
  private MaterialViewDAO materialViewDAO;

  @Inject
  private UserDAO userDAO;

  @Inject
  private MaterialTagDAO materialTagDAO;

  @Inject
  private UserMaterialRoleDAO userMaterialRoleDAO;

  @Inject
  private MaterialThumbnailDAO materialThumbnailDAO;

  @Inject
  private DocumentDAO documentDAO;

  @Inject
  private DocumentRevisionDAO documentRevisionDAO;

  @Inject
  private VectorImageDAO vectorImageDAO;

  @Inject
  private VectorImageRevisionDAO vectorImageRevisionDAO;

  @Inject
  private DriveManager driveManager;
  
  @Inject
  private SystemGoogleDriveCredentials systemGoogleDriveCredentials;

  @Inject
  private MaterialPermissionController materialPermissionController;

  @Inject
  private ImageController imageController;
  
  @Inject
  private GoogleDriveMaterialController googleDriveMaterialController;

  @Inject
  private DropboxManager dropboxManager;

  public MimeType parseMimeType(String mimeType) throws MimeTypeParseException {
    MimeType type = new MimeType(mimeType);
    return type;
  }

  public boolean isDownloadableType(MaterialType materialType) {
    switch (materialType) {
      case DROPBOX_FOLDER:
      case DROPBOX_ROOT_FOLDER:
      case FOLDER:
      case ILLUSION_GROUP_FOLDER:
        return false;
      default:
        return true;
    }
  }

  public boolean isEditableType(MaterialType materialType) {
    switch (materialType) {
    case DOCUMENT:
    case FOLDER:
    case VECTOR_IMAGE:
      return true;
    default:
      return false;
    }
  }

  public boolean isMovableType(MaterialType type) {
    switch (type) {
    case DROPBOX_ROOT_FOLDER:
    case DROPBOX_FILE:
    case DROPBOX_FOLDER:
    case ILLUSION_GROUP_FOLDER:
    case ILLUSION_GROUP_DOCUMENT:
      return false;
    default:
      break;
    }

    return true;
  }

  public boolean isShareableType(MaterialType type) {
    switch (type) {
    case DROPBOX_ROOT_FOLDER:
    case DROPBOX_FILE:
    case DROPBOX_FOLDER:
      return false;
    default:
      break;
    }

    return true;
  }

  public boolean isPrintableAsPdfType(MaterialType type) {
    switch (type) {
    case DOCUMENT:
      return true;
    default:
      break;
    }

    return false;
  }

  public boolean isDeletableType(MaterialType type) {
    switch (type) {
    case DROPBOX_FILE:
    case DROPBOX_FOLDER:
    case ILLUSION_GROUP_FOLDER:
    case ILLUSION_GROUP_DOCUMENT:
      return false;
    default:
      break;
    }

    return true;
  }

  public Material findMaterialById(Long materialId) {
    return materialDAO.findById(materialId);
  }

  public Material findMaterialByPath(Folder rootFolder, String path) {
    int lastSlash = path.lastIndexOf('/');
    String urlName;
    Folder parentFolder;

    if (lastSlash != -1) {
      path = path.substring(0, lastSlash);
      urlName = path.substring(lastSlash + 1);
      parentFolder = (Folder) materialDAO.findByParentFolderAndUrlName(rootFolder, path);
    } else {
      urlName = path;
      parentFolder = rootFolder;
    }

    return materialDAO.findByParentFolderAndUrlName(parentFolder, urlName);
  }

  public Material findMaterialByPermaLink(String path) {
    PermaLink permaLink = permaLinkDAO.findByPath(path);
    if (permaLink != null)
      return permaLink.getMaterial();

    return null;
  }

  private List<SearchResult<Material>> searchMaterialByTitleAndContent(User user, String[] criterias, int maxResults) throws ParseException {
    List<SearchResult<Material>> result = new ArrayList<>();

    // find by title and content
    StringBuilder queryStringBuilder = new StringBuilder();
    queryStringBuilder.append("+(");
    for (int i = 0, l = criterias.length; i < l; i++) {
      String criteria = QueryParser.escape(criterias[i]);

      queryStringBuilder.append("title:");
      queryStringBuilder.append(criteria);
      queryStringBuilder.append("* ");

      queryStringBuilder.append("contentPlain:");
      queryStringBuilder.append(criteria);
      queryStringBuilder.append("* ");

      if (i < l - 1)
        queryStringBuilder.append(' ');
    }

    queryStringBuilder.append(")");

    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
    QueryParser parser = new QueryParser(Version.LUCENE_35, "", analyzer);

    Query luceneQuery = parser.parse(queryStringBuilder.toString());
    FullTextQuery query = (FullTextQuery) fullTextEntityManager.createFullTextQuery(luceneQuery, 
        Document.class, File.class, Folder.class, GoogleDocument.class, Image.class, Pdf.class, VectorImage.class);
    query.setProjection(FullTextQuery.SCORE, FullTextQuery.THIS);
    query.setMaxResults(maxResults);
    
    @SuppressWarnings("unchecked")
    List<Object[]> resultRows = query.getResultList();

    for (Object[] resultRow : resultRows) {
      Float score = (Float) resultRow[0];
      Material material = (Material) resultRow[1];
      if (material != null) {
        if (materialPermissionController.isPublic(user, material) || materialPermissionController.hasAccessPermission(user, material)) {
          result.add(new SearchResult<Material>(material, material.getTitle(), material.getPath(), material.getTitle(), null, score));
        }
      }
    }

    return result;
  }

  private List<SearchResult<Material>> searchMaterialByTags(User user, String[] criterias, int maxResults) throws ParseException {
    List<SearchResult<Material>> result = new ArrayList<>();

    // find by title and content
    StringBuilder queryStringBuilder = new StringBuilder();
    queryStringBuilder.append("+(");
    for (int i = 0, l = criterias.length; i < l; i++) {
      String criteria = QueryParser.escape(criterias[i]);

      queryStringBuilder.append("tag.text:");
      queryStringBuilder.append(criteria);
      queryStringBuilder.append("* ");

      if (i < l - 1)
        queryStringBuilder.append(' ');
    }

    queryStringBuilder.append(")");

    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
    QueryParser parser = new QueryParser(Version.LUCENE_35, "", analyzer);

    Query luceneQuery = parser.parse(queryStringBuilder.toString());
    FullTextQuery query = (FullTextQuery) fullTextEntityManager.createFullTextQuery(luceneQuery, MaterialTag.class);
    query.setProjection(FullTextQuery.SCORE, FullTextQuery.THIS);
    query.setMaxResults(maxResults);
    @SuppressWarnings("unchecked")
    List<Object[]> resultRows = query.getResultList();

    for (Object[] resultRow : resultRows) {
      Float score = (Float) resultRow[0];
      MaterialTag materialTag = (MaterialTag) resultRow[1];
      Material material = materialTag.getMaterial();
      if (material != null) {
        if (materialPermissionController.isPublic(user, material) || materialPermissionController.hasAccessPermission(user, material)) {
          result.add(new SearchResult<Material>(material, material.getTitle(), material.getPath(), material.getTitle(), null, score));
        }
      }
    }

    return result;
  }

  public List<SearchResult<Material>> searchMaterials(User user, String text, int maxResults) throws ParseException {
    String[] criterias = text.replace(",", " ").replaceAll("\\s+", " ").split(" ");
    List<SearchResult<Material>> results = searchMaterialByTitleAndContent(user, criterias, maxResults);
    addSearchResults(results, searchMaterialByTags(user, criterias, maxResults));

    Collections.sort(results, new SearchResultScoreComparator<Material>());

    while (results.size() > maxResults) {
      results.remove(results.size() - 1);
    }
    
    return Collections.unmodifiableList(results);
  }
  
  private void addSearchResults(List<SearchResult<Material>> results, List<SearchResult<Material>> items) {
    List<Long> existingIds = new ArrayList<>();
    for (SearchResult<Material> result : results) {
      existingIds.add(result.getEntity().getId());
    }
    
    for (SearchResult<Material> item : items) {
      if (!existingIds.contains(item.getEntity().getId())) {
        results.add(item);
      }
    }
  }

  public Material updateMaterialPublicity(Material material, MaterialPublicity publicity, User modifier) {
    return materialDAO.updatePublicity(material, publicity, modifier);
  }

  public StarredMaterial starMaterial(Material material, User user) {
    StarredMaterial starredMaterial = starredMaterialDAO.findByMaterialAndUser(material, user);
    Date now = new Date();

    if (starredMaterial == null)
      starredMaterial = starredMaterialDAO.create(material, user, now);
    else
      starredMaterialDAO.updateCreated(starredMaterial, now);

    return starredMaterial;
  }

  public void unstarMaterial(Material material, User user) {
    StarredMaterial starredMaterial = starredMaterialDAO.findByMaterialAndUser(material, user);
    if (starredMaterial != null)
      starredMaterialDAO.delete(starredMaterial);
  }

  public List<Material> listStarredMaterialsByUser(User user, Integer firstResult, Integer maxResults) {
    List<Material> materials = new ArrayList<Material>();

    List<StarredMaterial> starredMaterials = starredMaterialDAO.listByUserSortByCreated(user, firstResult, maxResults);
    for (StarredMaterial starredMaterial : starredMaterials) {
      materials.add(starredMaterial.getMaterial());
    }

    return materials;
  }

  public List<Material> listStarredMaterialsByUser(User user) {
    List<Material> materials = new ArrayList<Material>();

    List<StarredMaterial> starredMaterials = starredMaterialDAO.listByUserSortByCreated(user);
    for (StarredMaterial starredMaterial : starredMaterials) {
      materials.add(starredMaterial.getMaterial());
    }

    return materials;
  }

  public List<Material> listViewedMaterialsByUser(User user, Integer firstResult, Integer maxResults) {
    List<Material> materials = new ArrayList<Material>();

    List<MaterialView> viewedMaterials = materialViewDAO.listByUserSortByViewed(user, firstResult, maxResults);
    for (MaterialView viewedMaterial : viewedMaterials) {
      materials.add(viewedMaterial.getMaterial());
    }

    return materials;
  }

  public List<Material> listModifiedMaterialsByUser(User user, Integer firstResult, Integer maxResults) {
    return materialDAO.listByModifierExcludingTypesSortByModified(user, Arrays.asList(new MaterialType[] { MaterialType.FOLDER }), firstResult, maxResults);
  }

  @SuppressWarnings("unchecked")
  public List<Material> listMaterialsByFolder(User user, Folder folder) {
    List<MaterialRole> roles = Arrays.asList(MaterialRole.MAY_EDIT, MaterialRole.MAY_VIEW);

    if (folder != null) {
      return materialDAO.listByParentFolder(folder);
    } else {
      return (List<Material>) CollectionUtils.union(materialDAO.listByRootFolderAndCreator(user), materialDAO.listByRootFolderAndUserAndRoles(user, roles));
    }
  }

  @SuppressWarnings("unchecked")
  public List<Material> listMaterialsByFolderAndTypes(User user, Folder folder, Collection<MaterialType> types) {
    List<MaterialRole> roles = Arrays.asList(MaterialRole.MAY_EDIT, MaterialRole.MAY_VIEW);

    if (folder != null) {
      return materialDAO.listByParentFolderAndTypes(folder, types);
    } else {
      return (List<Material>) CollectionUtils.union(materialDAO.listByRootFolderAndTypesAndCreator(types, user),
          materialDAO.listByRootFolderAndUserAndTypesAndRoles(user, types, roles));
    }
  }

  public String getUniqueMaterialUrlName(User owner, Folder parentFolder, Material material, String title) {
    String urlName = RequestUtils.createUrlName(title);
    if (material != null && urlName.equals(material.getUrlName()))
      return urlName;

    String baseName = urlName;
    Material urlMaterial = null;
    int i = 0;
    do {
      if (parentFolder == null) {
        urlMaterial = materialDAO.findByRootFolderAndUrlName(owner, urlName);
      } else {
        urlMaterial = materialDAO.findByParentFolderAndUrlName(parentFolder, urlName);
      }

      if (urlMaterial == null) {
        if (material != null) {
          String path = null;

          if (material.getParentFolder() != null)
            path = material.getParentFolder().getPath() + '/' + urlName;
          else {
            path = material.getCreator().getId().toString() + '/' + urlName;
          }

          PermaLink permaLink = permaLinkDAO.findByPath(path);
          if (permaLink != null) {
            if (permaLink.getMaterial().getId().equals(material.getId())) {
              return urlName;
            }
          } else {
            return urlName;
          }
        } else {
          return urlName;
        }
      }

      if (material != null) {
        if (urlMaterial != null && urlMaterial.getId().equals(material.getId()))
          return urlName;
      }

      urlName = baseName + '_' + (++i);
    } while (true);
  }

  public String getForgeMaterialViewerName(Material material) {
    MaterialType type = material.getType();
    
    switch (type) {
      case DROPBOX_FILE:
        return "binary";
      case DROPBOX_FOLDER:
      case DROPBOX_ROOT_FOLDER:
        return "folders";
      case GOOGLE_DOCUMENT:
        GoogleDocument googleDocument = (GoogleDocument) material;
        String mimeType = googleDocument.getMimeType();
        GoogleDriveType googleDriveType = GoogleDriveType.findByMimeType(mimeType);
        if (googleDriveType != null) {
          switch (googleDriveType) {
            case DOCUMENT:
            case SPREADSHEET:
              return "google-drive";
            default:
            break;
          }
        }
        
        return "binary";
      case DOCUMENT:
        return "documents";
      case BINARY:
      case FILE:
      case PDF:
        return "binary";
      case FOLDER:
        return "folders";
      case IMAGE:
        return "images";
      case VECTOR_IMAGE:
        return "vectorimages";
      case ILLUSION_GROUP_FOLDER:
        return "folders";
      case ILLUSION_GROUP_DOCUMENT: 
        return "documents";
    }
  
    return "todo";
  }
  
  public String getForgeMaterialViewerUrl(Material material) {
    return new StringBuilder()
      .append("/forge/")
      .append(getForgeMaterialViewerName(material))
      .append('/')
      .append(material.getPath())
      .toString();
  }
  
  public void moveMaterial(Material material, Folder parentFolder, User modifyingUser) {
    updateMaterialPermaLinks(material);
    materialDAO.updateParentFolder(material, parentFolder, modifyingUser);
  }
  
  private void updateMaterialPermaLinks(Material material) {
    if (material instanceof Folder) {
      List<Material> children = materialDAO.listByParentFolder((Folder) material);
      for (Material child : children) {
        updateMaterialPermaLinks(child);
      }
    }
    
    String oldPath = material.getPath();
    PermaLink permaLink = permaLinkDAO.findByPath(oldPath);
    if (permaLink == null) {
      permaLink = permaLinkDAO.create(material, oldPath);
    }
  }

  public void deleteMaterial(Material material, User deletingUser) {

    switch (material.getType()) {
    case FOLDER:
    case DROPBOX_ROOT_FOLDER:
    case DROPBOX_FOLDER:
      /**
       * When removing a Dropbox folder, all child resources have to be removed also
       */
      recursiveDelete(folderDAO.findById(material.getId()), deletingUser);
      break;
    case DOCUMENT:
      Document document = (Document) material;
      List<DocumentRevision> documentRevisions = documentRevisionDAO.listByDocument(document);
      for (DocumentRevision documentRevision : documentRevisions) {
        documentRevisionDAO.delete(documentRevision);
      }
      break;
    case VECTOR_IMAGE:
      VectorImage vectorImage = (VectorImage) material;
      List<VectorImageRevision> vectorImageRevisions = vectorImageRevisionDAO.listByVectorImage(vectorImage);
      for (VectorImageRevision vectorImageRevision : vectorImageRevisions) {
        vectorImageRevisionDAO.delete(vectorImageRevision);
      }
      break;
    default:
      break;
    }

    List<MaterialTag> tags = materialTagDAO.listByMaterial(material);
    for (MaterialTag tag : tags) {
      materialTagDAO.delete(tag);
    }

    List<StarredMaterial> starredMaterials = starredMaterialDAO.listByMaterial(material);
    for (StarredMaterial starredMaterial : starredMaterials) {
      starredMaterialDAO.delete(starredMaterial);
    }

    List<PermaLink> permaLinks = permaLinkDAO.listByMaterial(material);
    for (PermaLink permaLink : permaLinks) {
      permaLinkDAO.delete(permaLink);
    }

    List<UserMaterialRole> userMaterialRoles = userMaterialRoleDAO.listByMaterial(material);
    for (UserMaterialRole userMaterialRole : userMaterialRoles) {
      userMaterialRoleDAO.delete(userMaterialRole);
    }

    List<MaterialThumbnail> thumbnails = materialThumbnailDAO.listByMaterial(material);
    for (MaterialThumbnail thumbnail : thumbnails) {
      materialThumbnailDAO.delete(thumbnail);
    }

    List<MaterialView> materialViews = materialViewDAO.listByMaterial(material);
    for (MaterialView materialView : materialViews) {
      materialViewDAO.delete(materialView);
    }

    materialDAO.delete(material);
  }

  public boolean isStarred(User user, Material material) {
    StarredMaterial starredMaterial = starredMaterialDAO.findByMaterialAndUser(material, user);
    return starredMaterial != null;
  }

  /**
   * Returns material size in bytes.
   * 
   * @param material
   *          material
   * @return material size in bytes
   */
  @LoggedIn
  public long getUserMaterialsTotalSize(User user) {
    // Count of materials multiplied by default material size as a base value
    long materialTotalSize = materialDAO.countByCreator(user) * DEFAULT_MATERIAL_SIZE;

    // Documents,
    long documensTotalSize = documentDAO.lengthDataByCreator(user);
    // VectorImages,
    long vectorImagesTotalSize = vectorImageDAO.lengthDataByCreator(user).longValue();
    // and Binaries include significant amount of data besides default size
    // so we calculate them separately
    long binariesTotalSize = binaryDAO.lengthDataByCreator(user);

    return materialTotalSize + documensTotalSize + vectorImagesTotalSize + binariesTotalSize;
  }

  public long getUserQuota() {
    return DEFAULT_QUOTA;
  }

  public MaterialThumbnail getImageThumbnail(fi.foyt.fni.persistence.model.materials.Image image, ImageSize size) throws IOException {
    MaterialThumbnail materialThumbnail = materialThumbnailDAO.findByMaterialAndSize(image, size);
    if (materialThumbnail == null) {

      TypedData originalData = new TypedData(image.getData(), image.getContentType());
      if (size == ImageSize.ORIGINAL) {
        materialThumbnail = materialThumbnailDAO.create(image, size, originalData.getData(), originalData.getContentType());
      } else {
        TypedData resizedImage = fi.foyt.fni.utils.images.ImageUtils.resizeImage(originalData, size.getWidth(), size.getHeight(), null);
        materialThumbnail = materialThumbnailDAO.create(image, size, resizedImage.getData(), resizedImage.getContentType());
      }

      return materialThumbnail;
    }

    return materialThumbnail;
  }

  public Material findMaterialByCompletePath(String completePath) {
    String path = RequestUtils.stripTrailingSlash(completePath);
    String materialPath = RequestUtils.stripPrecedingSlash(path.substring(MATERIALS_PATH.length() + 1));

    PermaLink permaLink = permaLinkDAO.findByPath(materialPath);
    if (permaLink != null) {
      return permaLink.getMaterial();
    }

    String[] pathElements = materialPath.split("/");
    if (pathElements.length >= 2) {
      Long userId = NumberUtils.createLong(pathElements[0]);
      if (userId == null) {
        return null;
      }

      User owner = userDAO.findById(userId);

      Folder parentFolder = null;

      for (int i = 1, l = pathElements.length - 1; i < l; i++) {
        String pathElement = pathElements[i];
        if (parentFolder != null)
          parentFolder = (Folder) materialDAO.findByParentFolderAndUrlName(parentFolder, pathElement);
        else
          parentFolder = (Folder) materialDAO.findByRootFolderAndUrlName(owner, pathElement);
      }

      if (parentFolder != null)
        return materialDAO.findByParentFolderAndUrlName(parentFolder, pathElements[pathElements.length - 1]);
      else
        return materialDAO.findByRootFolderAndUrlName(owner, pathElements[pathElements.length - 1]);
    }

    return null;
  }

  public Material findByOwnerAndPath(User owner, String path) {
    if (StringUtils.isBlank(path) || (owner == null)) {
      return null;
    }

    String[] pathElements = path.split("/");
    Folder parentFolder = null;

    for (int i = 0, l = pathElements.length - 1; i < l; i++) {
      String pathElement = pathElements[i];
      if (parentFolder != null)
        parentFolder = (Folder) materialDAO.findByParentFolderAndUrlName(parentFolder, pathElement);
      else
        parentFolder = (Folder) materialDAO.findByRootFolderAndUrlName(owner, pathElement);
    }

    if (parentFolder != null)
      return materialDAO.findByParentFolderAndUrlName(parentFolder, pathElements[pathElements.length - 1]);
    else
      return materialDAO.findByRootFolderAndUrlName(owner, pathElements[pathElements.length - 1]);
  }

  public Material createMaterial(Folder parentFolder, User user, FileData fileData) throws MimeTypeParseException, IOException, GeneralSecurityException {
    MimeType mimeType = parseMimeType(fileData.getContentType());

    if ("image".equals(mimeType.getPrimaryType())) {
      if ("svg".equals(mimeType.getSubType()) || "svg+xml".equals(mimeType.getSubType())) {
        return createVectorImage(parentFolder, user, new String(fileData.getData(), "UTF-8"), fileData.getFileName());
      } else {
        if (fileData.getContentType().equals("image/png")) {
          return imageController.createImage(parentFolder, user, fileData.getData(), fileData.getContentType(), fileData.getFileName());
        } else {
          return uploadImage(parentFolder, user, fileData);
        }
      }
    } else {
      switch (mimeType.getBaseType()) {
      case "application/pdf":
        return uploadPdf(parentFolder, user, fileData);
      case "text/plain":
        return uploadText(parentFolder, user, fileData);
      case "text/html":
      case "application/xhtml+xml":
        return uploadHtml(parentFolder, user, fileData);
      case "application/vnd.oasis.opendocument.text":
      case "application/vnd.sun.xml.writer":
      case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
      case "application/msword":
      case "application/x-mswrite":
      case "application/rtf":
      case "text/richtext":
        return uploadDocument(parentFolder, user, fileData);
      case "application/vnd.openxmlformats-officedocument.presentationml.slideshow":
      case "application/vnd.ms-powerpoint":
        // TODO: Warning: presentation
        return uploadDocument(parentFolder, user, fileData);
      case "application/vnd.ms-excel":
      case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
      case "application/vnd.oasis.opendocument.spreadsheet":
      case "text/csv":
      case "text/tab-separated-values":
        // TODO: Warning, spreadsheet
        return uploadDocument(parentFolder, user, fileData);
      }
    }

    return createFile(parentFolder, user, fileData.getData(), fileData.getContentType(), fileData.getFileName());
  }

  public Material createFile(Folder parentFolder, User loggedUser, byte[] data, String contentType, String title) {
    String urlName = getUniqueMaterialUrlName(loggedUser, parentFolder, null, title);
    Date now = new Date();

    return fileDAO.create(loggedUser, now, loggedUser, now, null, parentFolder, urlName, title, data, contentType, MaterialPublicity.PRIVATE);
  }

  private Material uploadImage(Folder parentFolder, User loggedUser, FileData fileData) throws IOException {
    TypedData imageData = ImageUtils.convertToPng(fileData);
    return imageController.createImage(parentFolder, loggedUser, imageData.getData(), imageData.getContentType(), fileData.getFileName());
  }

  private Material createVectorImage(Folder parentFolder, User loggedUser, String data, String title) {
    String urlName = getUniqueMaterialUrlName(loggedUser, parentFolder, null, title);
    return vectorImageDAO.create(loggedUser, null, parentFolder, urlName, title, data, MaterialPublicity.PRIVATE);
  }

  private Material uploadPdf(Folder parentFolder, User loggedUser, FileData fileData) {
    return createPdf(parentFolder, loggedUser, fileData.getData(), fileData.getFileName());
  }

  private Material uploadHtml(Folder parentFolder, User loggedUser, FileData fileData) throws UnsupportedEncodingException {
    String data = new String(fileData.getData(), "UTF-8");
    return createDocument(parentFolder, loggedUser, data, fileData.getFileName());
  }

  private Material uploadText(Folder parentFolder, User loggedUser, FileData fileData) throws UnsupportedEncodingException {

    String title = fileData.getFileName();
    String bodyContent = StringEscapeUtils.escapeHtml4(new String(fileData.getData(), "UTF-8"));
    bodyContent = bodyContent.replaceAll("\n", "<br/>");
    String data = HtmlUtils.getAsHtmlText(title, bodyContent);

    return createDocument(parentFolder, loggedUser, data, title);
  }

  private Material uploadDocument(Folder parentFolder, User loggedUser, FileData fileData) throws IOException, GeneralSecurityException {
    Drive drive = driveManager.getDrive(systemGoogleDriveCredentials.getSystemCredential());

    File file = driveManager.insertFile(drive, fileData.getFileName(), null, fileData.getContentType(), null, true, fileData.getData());
    try {
      TypedData htmlData = driveManager.exportFile(drive, file, "text/html");
      return createDocument(parentFolder, loggedUser, new String(htmlData.getData(), "UTF-8"), fileData.getFileName());
    } finally {
      driveManager.deleteFile(drive, file);
    }
  }

  private Material createDocument(Folder parentFolder, User loggedUser, String data, String title) {
    List<GuessedLanguage> guessedLanguages;
    Language language = null;
    try {
      guessedLanguages = LanguageUtils.getGuessedLanguages(data, 0.2);
      if (guessedLanguages.size() > 0) {
        String languageCode = guessedLanguages.get(0).getLanguageCode();
        language = languageDAO.findByIso2(languageCode);
      }
    } catch (IOException e) {
      // It's really not very serious if language detection fails.
      logger.log(Level.WARNING, "Language detection failed", e);
    }

    String urlName = getUniqueMaterialUrlName(loggedUser, parentFolder, null, title);

    return documentDAO.create(loggedUser, language, parentFolder, urlName, title, data, MaterialPublicity.PRIVATE);
  }

  private Material createPdf(Folder parentFolder, User loggedUser, byte[] data, String title) {
    String urlName = getUniqueMaterialUrlName(loggedUser, parentFolder, null, title);
    Date now = new Date();

    return pdfDAO.create(loggedUser, now, loggedUser, now, null, parentFolder, urlName, title, data, MaterialPublicity.PRIVATE);
  }

  private void recursiveDelete(Folder folder, User user) {
    List<Material> childMaterials = materialDAO.listByParentFolder(folder);
    for (Material childMaterial : childMaterials) {
      if (childMaterial instanceof Folder) {
        recursiveDelete((Folder) childMaterial, user);
      }

      deleteMaterial(childMaterial, user);
    }
  }

  public GoogleDocument findGoogleDocumentById(Long id) {
    return googleDocumentDAO.findById(id);
  }

  public FileData getMaterialData(String contextPath, User user, Material material) throws UnsupportedEncodingException,
      MalformedURLException, IOException, GeneralSecurityException {
    switch (material.getType()) {
      case IMAGE:
        return getBinaryMaterialData((Image) material);
      case DOCUMENT:
      case ILLUSION_GROUP_DOCUMENT:
        return getDocumentData(contextPath, (Document) material);
      case VECTOR_IMAGE:
        return getVectorImageData((VectorImage) material);
      case PDF:
        return getBinaryMaterialData((Pdf) material);
      case FILE:
        return getBinaryMaterialData((fi.foyt.fni.persistence.model.materials.File) material);
      case GOOGLE_DOCUMENT:
        TypedData typedData = googleDriveMaterialController.getGoogleDocumentData((GoogleDocument) material);
        return new FileData(null, material.getUrlName(), typedData.getData(), typedData.getContentType(), typedData.getModified());
      case DROPBOX_FILE:
        return getDropboxMaterialData(user, (DropboxFile) material);
      case BINARY:
        return getBinaryMaterialData((Binary) material);
      case DROPBOX_FOLDER:
      case DROPBOX_ROOT_FOLDER:
      case FOLDER:
      case ILLUSION_GROUP_FOLDER:
      break;
    }
    
    return null;
  }

  private FileData getDocumentData(String contextPath, Document document) throws UnsupportedEncodingException {
    String bodyContent = document.getData();
    String title = document.getTitle();
    String styleSheet = contextPath + "/uresources/material-document-style.css";
    String htmlContent = MessageFormat.format(DOCUMENT_TEMPLATE, title, styleSheet, bodyContent);
    return new FileData(null, document.getUrlName(), htmlContent.getBytes("UTF-8"), "text/html", document.getModified());
  }

  private FileData getVectorImageData(VectorImage vectorImage) throws UnsupportedEncodingException {
    String data = vectorImage.getData();
    return new FileData(null, vectorImage.getUrlName(), data != null ? data.getBytes("UTF-8") : null, "image/svg+xml", vectorImage.getModified());
  }

  private FileData getBinaryMaterialData(Binary binary) {
    return new FileData(null, binary.getUrlName(), binary.getData(), binary.getContentType(), binary.getModified());
  }
  
  private FileData getDropboxMaterialData(User user, DropboxFile dropboxFile) throws IOException {
    Response response = dropboxManager.getFileContent(user, dropboxFile);
    if (response.getCode() == 200) {
      byte[] data = null;
      
      InputStream inputStream = response.getStream();
      try {
        data = IOUtils.toByteArray(inputStream);
        return new FileData(null, dropboxFile.getUrlName(), data, dropboxFile.getMimeType(), dropboxFile.getModified());
      } finally {
        inputStream.close();
      }
    }
    
    return null;
  }
}
