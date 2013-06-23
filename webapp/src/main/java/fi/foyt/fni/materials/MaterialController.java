package fi.foyt.fni.materials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.materials.BinaryDAO;
import fi.foyt.fni.persistence.dao.materials.DocumentDAO;
import fi.foyt.fni.persistence.dao.materials.DocumentRevisionDAO;
import fi.foyt.fni.persistence.dao.materials.DropboxFileDAO;
import fi.foyt.fni.persistence.dao.materials.FileDAO;
import fi.foyt.fni.persistence.dao.materials.FolderDAO;
import fi.foyt.fni.persistence.dao.materials.GoogleDocumentDAO;
import fi.foyt.fni.persistence.dao.materials.ImageDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialTagDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialThumbnailDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialViewDAO;
import fi.foyt.fni.persistence.dao.materials.PdfDAO;
import fi.foyt.fni.persistence.dao.materials.PermaLinkDAO;
import fi.foyt.fni.persistence.dao.materials.StarredMaterialDAO;
import fi.foyt.fni.persistence.dao.materials.UbuntuOneFileDAO;
import fi.foyt.fni.persistence.dao.materials.UserMaterialRoleDAO;
import fi.foyt.fni.persistence.dao.materials.VectorImageDAO;
import fi.foyt.fni.persistence.dao.materials.VectorImageRevisionDAO;
import fi.foyt.fni.persistence.dao.users.UserDAO;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.DocumentRevision;
import fi.foyt.fni.persistence.model.materials.DropboxFile;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.GoogleDocument;
import fi.foyt.fni.persistence.model.materials.GoogleDocumentType;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.materials.ImageSize;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialRole;
import fi.foyt.fni.persistence.model.materials.MaterialTag;
import fi.foyt.fni.persistence.model.materials.MaterialThumbnail;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.materials.MaterialView;
import fi.foyt.fni.persistence.model.materials.PermaLink;
import fi.foyt.fni.persistence.model.materials.StarredMaterial;
import fi.foyt.fni.persistence.model.materials.UbuntuOneFile;
import fi.foyt.fni.persistence.model.materials.UserMaterialRole;
import fi.foyt.fni.persistence.model.materials.VectorImage;
import fi.foyt.fni.persistence.model.materials.VectorImageRevision;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.servlet.RequestUtils;

@RequestScoped
@Stateful
public class MaterialController {

	private static final String MATERIALS_PATH = "materials";
  private static final long DEFAULT_MATERIAL_SIZE = 2048;
  private static final long DEFAULT_QUOTA = 1024 * 1024 * 10;

  @Inject
  @DAO
  private MaterialDAO materialDAO;

  @Inject
  @DAO
  private FolderDAO folderDAO;
  
  @Inject
  @DAO
  private FileDAO fileDAO;
  
  @Inject
  @DAO
  private PdfDAO pdfDAO;
  
  @Inject
  @DAO
  private ImageDAO imageDAO;
  
  @Inject
  @DAO
  private BinaryDAO binaryDAO;
  
  @Inject
  @DAO
  private DropboxFileDAO dropboxFileDAO;
  
  @Inject
  @DAO
  private UbuntuOneFileDAO ubuntuOneFileDAO;

  @Inject
  @DAO
  private GoogleDocumentDAO googleDocumentDAO;
  
  @Inject
  @DAO
  private PermaLinkDAO permaLinkDAO;

  @Inject
  @DAO
  private StarredMaterialDAO starredMaterialDAO;
  
  @Inject
  @DAO
  private MaterialViewDAO materialViewDAO;
  
  @Inject
  @DAO
  private UserDAO userDAO;
  
  @Inject
  @DAO
  private MaterialTagDAO materialTagDAO;

  @Inject
  @DAO
  private UserMaterialRoleDAO userMaterialRoleDAO;

  @Inject
  @DAO
  private MaterialThumbnailDAO materialThumbnailDAO;

  @Inject
  @DAO
  private DocumentDAO documentDAO;
  
  @Inject
  @DAO
  private DocumentRevisionDAO documentRevisionDAO;

  @Inject
  @DAO
  private VectorImageDAO vectorImageDAO;
  
  @Inject
  @DAO
  private VectorImageRevisionDAO vectorImageRevisionDAO;

  public String getMaterialMimeType(Material material) {
    switch (material.getType()) {
      case DOCUMENT:
        return "text/html";
      case FILE:
        return fileDAO.findById(material.getId()).getContentType();
      case IMAGE:
        return imageDAO.findById(material.getId()).getContentType();
      case PDF:
        return pdfDAO.findById(material.getId()).getContentType();
      case VECTOR_IMAGE:
        return "image/svg+xml";
      case DROPBOX_FILE:
        return dropboxFileDAO.findById(material.getId()).getMimeType();
      case UBUNTU_ONE_FILE:
        return ubuntuOneFileDAO.findById(material.getId()).getMimeType();
      case GOOGLE_DOCUMENT:
        GoogleDocument googleDocument = googleDocumentDAO.findById(material.getId());
        switch (googleDocument.getDocumentType()) {
          case DOCUMENT:
            return "text/html";
          case DRAWING:
            return "image/svg+xml";
          case SPREADSHEET:
            return "application/vnd.oasis.opendocument.spreadsheet";
          case PRESENTATION:
            return "application/vnd.oasis.opendocument.presentation";
          default:
            return "application/octet-stream";
        }
      default:
        return "application/octet-stream";
    }
  }

  public MaterialArchetype getMaterialArchetype(Material material) {
    switch (material.getType()) {
      case DOCUMENT:
        return MaterialArchetype.DOCUMENT;
      case FILE:
        return MaterialArchetype.FILE;
      case FOLDER:
      case DROPBOX_FOLDER:
      case DROPBOX_ROOT_FOLDER:
      case UBUNTU_ONE_FOLDER:
      case UBUNTU_ONE_ROOT_FOLDER:
        return MaterialArchetype.FOLDER;
      case IMAGE:
        return MaterialArchetype.IMAGE;
      case VECTOR_IMAGE:
        return MaterialArchetype.VECTOR_IMAGE;
      case PDF:
        return MaterialArchetype.PDF;
      case DROPBOX_FILE:
        return getDropboxFileArchetype(dropboxFileDAO.findById(material.getId()));
      case UBUNTU_ONE_FILE:
        return getUbuntuOneFileArchetype(ubuntuOneFileDAO.findById(material.getId()));
      case GOOGLE_DOCUMENT:
        return getGoogleDocumentArchetype(googleDocumentDAO.findById(material.getId()));
      default:
        return MaterialArchetype.FILE;
    }
  }
  
  public MimeType parseMimeType(String mimeType) throws MimeTypeParseException {
    MimeType type = new MimeType(mimeType);
    return type;
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
  
  public MaterialBean getMaterialBean(Long materialId) {
    return getMaterialBean(materialDAO.findById(materialId));
  }
  
  public MaterialBean getMaterialBean(Material material) {
    String mimeType = getMaterialMimeType(material);
    return new MaterialBean(material.getId(), material.getTitle(), material.getType(), getMaterialArchetype(material), mimeType, material.getModified(), material.getCreated());
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
      return (List<Material>) CollectionUtils.union(materialDAO.listByRootFolderAndTypesAndCreator(types, user), materialDAO.listByRootFolderAndUserAndTypesAndRoles(user, types, roles));
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
      
      if (urlMaterial == null)
        return urlName;
      
      if (material != null) {
        if (urlMaterial != null && urlMaterial.getId().equals(material.getId()))
          return urlName;
      }
      
      urlName = baseName + '_' + (++i);
    } while (true);
  }

  
  public void deleteMaterial(Material material, User deletingUser) {
    
    switch (material.getType()) {
      case FOLDER:
      case DROPBOX_ROOT_FOLDER:
      case DROPBOX_FOLDER:
      case UBUNTU_ONE_ROOT_FOLDER:
      case UBUNTU_ONE_FOLDER:
        /** 
         * When removing Ubuntu One or Dropbox folder, all child resources have to be
         * removed also
         */ 
        recursiveDelete(folderDAO.findById(material.getId()), deletingUser);
      break;
      case IMAGE:
        Image image = (Image) material;
        List<User> users = userDAO.listByProfileImage(image);
        for (User user : users) {
          userDAO.updateProfileImage(user, null);
        }
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
   * @param material material
   * @return material size in bytes
   */
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
    
    return materialTotalSize + 
        documensTotalSize + 
        vectorImagesTotalSize + 
        binariesTotalSize;
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

  private MaterialArchetype getDropboxFileArchetype(DropboxFile material) {
    return getArchetypeByMimeType(material.getMimeType());
  }

  private MaterialArchetype getUbuntuOneFileArchetype(UbuntuOneFile material) {
    return getArchetypeByMimeType(material.getMimeType());
  }

  private MaterialArchetype getGoogleDocumentArchetype(GoogleDocument material) {
    GoogleDocumentType googleDocumentType = material.getDocumentType();
    switch (googleDocumentType) {
      case DOCUMENT:
        return MaterialArchetype.DOCUMENT;
      case DRAWING:
        return MaterialArchetype.VECTOR_IMAGE;
      case FOLDER:
        return MaterialArchetype.FOLDER;
      case PRESENTATION:
        return MaterialArchetype.PRESENTATION;
      case SPREADSHEET:
        return MaterialArchetype.SPREADSHEET;
    }

    return MaterialArchetype.FILE;
  }
  
  private MaterialArchetype getArchetypeByMimeType(String mimeType) {
    try {
      MimeType parsedMimeType = parseMimeType(mimeType);
      if ("image".equals(parsedMimeType.getPrimaryType())) {
        if (("svg".equals(parsedMimeType.getSubType()))||("svg+xml".equals(parsedMimeType.getSubType())))
          return MaterialArchetype.VECTOR_IMAGE;
        
        return MaterialArchetype.IMAGE;
      } else {
        if ("text".equals(parsedMimeType.getBaseType())) {
          if ("html".equals(parsedMimeType.getSubType())) {
            return MaterialArchetype.DOCUMENT;
          }
        }
        
        if ("application".equals(parsedMimeType.getBaseType()) && "pdf".equals(parsedMimeType.getSubType())) {
          return MaterialArchetype.PDF;
        }
        
        return MaterialArchetype.FILE;
      }
    } catch (MimeTypeParseException e) {
      return MaterialArchetype.FILE;
    }
  }
  
  private void recursiveDelete(Folder folder, User user) {
    List<Material> childMaterials = materialDAO.listByParentFolder(folder);
    for (Material childMaterial : childMaterials) {
      MaterialArchetype archetype = getMaterialArchetype(childMaterial);
      if (archetype == MaterialArchetype.FOLDER) {
        recursiveDelete(folderDAO.findById(childMaterial.getId()), user);
      }

      deleteMaterial(childMaterial, user);
    }
  }
}
