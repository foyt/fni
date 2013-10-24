package fi.foyt.fni.api;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;

import fi.foyt.fni.api.beans.CompactMaterialBean;
import fi.foyt.fni.api.beans.CompleteMaterialBean;
import fi.foyt.fni.api.events.MaterialSharedEvent;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.materials.MaterialTypeComparator;
import fi.foyt.fni.materials.TitleComparator;
import fi.foyt.fni.persistence.dao.materials.DocumentDAO;
import fi.foyt.fni.persistence.dao.materials.FileDAO;
import fi.foyt.fni.persistence.dao.materials.FolderDAO;
import fi.foyt.fni.persistence.dao.materials.ImageDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialTagDAO;
import fi.foyt.fni.persistence.dao.materials.MaterialViewDAO;
import fi.foyt.fni.persistence.dao.materials.PdfDAO;
import fi.foyt.fni.persistence.dao.materials.UserMaterialRoleDAO;
import fi.foyt.fni.persistence.dao.materials.VectorImageDAO;
import fi.foyt.fni.persistence.dao.users.UserDAO;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.File;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.GoogleDocument;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialRole;
import fi.foyt.fni.persistence.model.materials.MaterialTag;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.materials.MaterialView;
import fi.foyt.fni.persistence.model.materials.Pdf;
import fi.foyt.fni.persistence.model.materials.StarredMaterial;
import fi.foyt.fni.persistence.model.materials.UserMaterialRole;
import fi.foyt.fni.persistence.model.materials.VectorImage;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.upload.UploadInfo;
import fi.foyt.fni.upload.UploadInfoFile;
import fi.foyt.fni.utils.search.SearchResult;

@Path("/materials/-")
@RequestScoped
@Stateful
@Produces ("application/json")
public class MaterialsAllRESTService extends RESTService {
	
  private final static String UPLOAD_INFO_SESSION_ATTR = "__upload_info__";

	@Inject
	private Logger logger;
	
	@Inject
	private MaterialPermissionController materialPermissionController;

	@Inject
	private SystemSettingsController systemSettingsController;
	
  @Inject
  private MaterialController materialController;
  
  @Inject
  private Event<MaterialSharedEvent> materialSharedEvent;

  @Inject
	private FullTextEntityManager fullTextEntityManager;
	
	@Inject
  private MaterialDAO materialDAO;

	@Inject
  private MaterialTagDAO materialTagDAO;
	
	@Inject
  private UserDAO userDAO;
	
	@Inject
  private UserMaterialRoleDAO userMaterialRoleDAO;

	@Inject
  private MaterialViewDAO materialViewDAO;

	@Inject
	private FolderDAO folderDAO;
	
	@Inject
	private DocumentDAO documentDAO;
	
	@Inject
	private FileDAO fileDAO;
	
	@Inject
	private ImageDAO imageDAO;

	@Inject
	private VectorImageDAO vectorImageDAO;
	
	@Inject
	private PdfDAO pdfDAO;
	
	
	
	@GET
	@Path ("/{FOLDERID}/list") 
	public Response list(
			@PathParam ("FOLDERID") String folderId,
			@QueryParam ("types") MaterialType[] materialTypes,
			@QueryParam ("sort") String sort,
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
    User loggedUser = getLoggedUser(httpHeaders);
		
		if (!hasRole(loggedUser, UserRole.GUEST)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}

		Folder parentFolder = null;
    if (!"HOME".equals(folderId)) {
    	parentFolder = folderDAO.findById(NumberUtils.createLong(folderId));
    	if (!materialPermissionController.hasAccessPermission(loggedUser, parentFolder)) {
  			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
    	}
    }
    
    List<Material> materials;
    
    if ((materialTypes == null)||(materialTypes.length == 0)) {
    	materials = materialController.listMaterialsByFolder(loggedUser, parentFolder);
    } else {
    	materials = materialController.listMaterialsByFolderAndTypes(loggedUser, parentFolder, Arrays.asList(materialTypes));
    }

    if (sort != null) {
      if ("FOLDERS_FIRST".equals(sort)) {
      	Collections.sort(materials, new MaterialTypeComparator(MaterialType.FOLDER));
      } else if ("TITLE".equals(sort)) {
      	Collections.sort(materials, new TitleComparator());
      } else {
  			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.invalidParameter", "sort")).build();
      } 
    }
    
    return Response.ok(new ApiResult<>(CompactMaterialBean.fromMaterialEntities(materials))).build();
	}
	
	/**
   * Marks a material view. User needs to be logged in and material can either be public or user has to have access to it
   */
	@GET
	@Path ("/{MATERIALID}/markView")
	public Response markView(
			@PathParam("MATERIALID") Long materialId,
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
    User loggedUser = getLoggedUser(httpHeaders);

		if (!hasRole(loggedUser, UserRole.GUEST)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}

    Material material = materialDAO.findById(materialId);
    if (material == null) {
			return Response.status(Response.Status.NOT_FOUND).entity(ApiMessages.getText(browserLocale, "error.generic.notFound")).build();
    }
    
		if (!materialPermissionController.isPublic(loggedUser, material) && !materialPermissionController.hasAccessPermission(loggedUser, material)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
    MaterialView materialView = materialViewDAO.findByMaterialAndUser(material, loggedUser);
    Date now = new Date();
    
    if (materialView == null) {
    	materialView = materialViewDAO.create(material, loggedUser, 1, now);
    } else {
    	materialViewDAO.updateCount(materialView, materialView.getCount() + 1);
    	materialViewDAO.updateViewed(materialView, now);
    }
    
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("materialId", material.getId());
    result.put("views", materialView.getCount());
    
    return Response.ok(new ApiResult<>(result)).build();
	}

	/**
   * Deletes a material. Material user has to be owner of the material
	 * @return 
   */
	@POST
  @DELETE
	@Path ("/{MATERIALID}/delete")
	public Response delete(
			@PathParam("MATERIALID") Long materialId,
			@Context HttpHeaders httpHeaders) {
		
    User loggedUser = getLoggedUser(httpHeaders);
		Locale browserLocale = getBrowserLocale(httpHeaders);

  	if (!hasRole(loggedUser, UserRole.GUEST)) {
  	  Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}

    Material material = materialDAO.findById(materialId);
    if (material == null) {			
      Response.status(Response.Status.NOT_FOUND).entity(ApiMessages.getText(browserLocale, "error.generic.notFound")).build();
    }
    
    if (!materialPermissionController.hasModifyPermission(loggedUser, material)) {
      Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
    }
    
    materialController.deleteMaterial(material, loggedUser);
    
    return Response.ok().build();
	}

	/**
   * Stars a material. Material can either be public or user has to have access to it
   */
	@GET
	@Path ("/{MATERIALID}/star")
	public Response star(
			@PathParam("MATERIALID") Long materialId,
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
	  User loggedUser = getLoggedUser(httpHeaders);
    
	  if (!hasRole(loggedUser, UserRole.GUEST)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
	  Material material = materialDAO.findById(materialId);
    if (material == null) {
    	return Response.status(Response.Status.NOT_FOUND).entity(ApiMessages.getText(browserLocale, "error.generic.notFound")).build();
    }

	  if (!materialPermissionController.isPublic(loggedUser, material) && !materialPermissionController.hasAccessPermission(loggedUser, material)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
    
    StarredMaterial starredMaterial = materialController.starMaterial(material, loggedUser);
    
		boolean mayEdit = loggedUser != null ? materialPermissionController.hasModifyPermission(loggedUser, material) : false;
		boolean mayView = loggedUser != null ? materialPermissionController.hasAccessPermission(loggedUser, material) : false;
		boolean mayDelete = mayEdit;

    return Response.ok(new ApiResult<>(CompleteMaterialBean.fromEntity(loggedUser, starredMaterial.getMaterial(), mayEdit, mayView, mayDelete))).build();
	}
	
	/**
   * Unstars a material. Material can either be public or user has to have access to it
   */
	@GET
	@Path ("/{MATERIALID}/unstar")
	public Response unstar(
			@PathParam("MATERIALID") Long materialId,
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
	  User loggedUser = getLoggedUser(httpHeaders);
    
	  if (!hasRole(loggedUser, UserRole.GUEST)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
	  Material material = materialDAO.findById(materialId);
    if (material == null) {
    	return Response.status(Response.Status.NOT_FOUND).entity(ApiMessages.getText(browserLocale, "error.generic.notFound")).build();
    }
   
    if (!materialPermissionController.isPublic(loggedUser, material) && !materialPermissionController.hasAccessPermission(loggedUser, material)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
    
    materialController.unstarMaterial(material, loggedUser);

		boolean mayEdit = loggedUser != null ? materialPermissionController.hasModifyPermission(loggedUser, material) : false;
		boolean mayView = loggedUser != null ? materialPermissionController.hasAccessPermission(loggedUser, material) : false;
		boolean mayDelete = mayEdit;

    return Response.ok(new ApiResult<>(CompleteMaterialBean.fromEntity(loggedUser, material, mayEdit, mayView, mayDelete))).build();
	}
	
  /**
   * Moves a material to another folder. Logged user has to have modification permission to the material.
   */
	@POST
	@PUT
	@Path ("/{MATERIALID}/move")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response move(
			@PathParam("MATERIALID") Long materialId,
			@FormParam ("parentFolder") String parentFolderParam,
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
	  User loggedUser = getLoggedUser(httpHeaders);
    
	  if (!hasRole(loggedUser, UserRole.GUEST)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}

    Material material = materialDAO.findById(materialId);
    if (material == null) {
    	return Response.status(Response.Status.NOT_FOUND).entity(ApiMessages.getText(browserLocale, "error.generic.notFound")).build();
    }
    
    if (!materialPermissionController.hasModifyPermission(loggedUser, material)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
    }

    Folder parentFolder;
		
		if ("HOME".equals(parentFolderParam)) {
			parentFolder = null;
		} else {
  		Long parentFolderId = NumberUtils.createLong(parentFolderParam);
	  	parentFolder = folderDAO.findById(parentFolderId);
		}
    
    materialDAO.updateParentFolder(material, parentFolder, loggedUser);
    
    return Response.ok(new ApiResult<>(CompactMaterialBean.fromMaterialEntity(material))).build();
	}
	
  /**
   * Sets material sharing settings. Logged user has to have modification permission to the material.
   */
	@POST
	@PUT
	@Path ("/{MATERIALID}/share")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response share(
			@PathParam("MATERIALID") Long materialId,
			@FormParam ("publicity") String publicityParam,
			@FormParam ("mayEdit") String mayEditParam,
			@FormParam ("mayView") String mayViewParam,
			@FormParam ("noRole") String noRoleParam,
			@Context UriInfo uriInfo,
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
	  User loggedUser = getLoggedUser(httpHeaders);
    
	  if (!hasRole(loggedUser, UserRole.GUEST)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
	  
    Material material = materialDAO.findById(materialId);
    if (material == null) {
			return Response.status(Response.Status.NOT_FOUND).entity(ApiMessages.getText(browserLocale, "error.generic.notFound")).build();
    }
    
    if (!materialPermissionController.hasModifyPermission(loggedUser, material)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
    }

    if (StringUtils.isNotBlank(publicityParam)) {
    	materialDAO.updatePublicity(material, MaterialPublicity.valueOf(publicityParam), loggedUser);
    }
    
    if (StringUtils.isNotBlank(mayEditParam)) {
    	for (String mayEdit : mayEditParam.split(",")) {
    		User user = userDAO.findById(NumberUtils.createLong(mayEdit));
    		UserMaterialRole materialRole = userMaterialRoleDAO.findByMaterialAndUser(material, user);
    		if (materialRole == null) {
    			userMaterialRoleDAO.create(material, user, MaterialRole.MAY_EDIT);
    		} else {
    			userMaterialRoleDAO.updateRole(materialRole, MaterialRole.MAY_EDIT);
    		}
    		
    		materialSharedEvent.fire(new MaterialSharedEvent(uriInfo, loggedUser, user, material));
    	}
    }
    
    if (StringUtils.isNotBlank(mayViewParam)) {
    	for (String mayView : mayViewParam.split(",")) {
    		User user = userDAO.findById(NumberUtils.createLong(mayView));
    		UserMaterialRole materialRole = userMaterialRoleDAO.findByMaterialAndUser(material, user);
    		if (materialRole == null) {
    			userMaterialRoleDAO.create(material, user, MaterialRole.MAY_VIEW);
    		} else {
    			userMaterialRoleDAO.updateRole(materialRole, MaterialRole.MAY_VIEW);
    		}
    		
    		materialSharedEvent.fire(new MaterialSharedEvent(uriInfo, loggedUser, user, material));
    	}
    }
    
    if (StringUtils.isNotBlank(noRoleParam)) {
    	for (String noRole : noRoleParam.split(",")) {
    		User user = userDAO.findById(NumberUtils.createLong(noRole));
    		UserMaterialRole materialRole = userMaterialRoleDAO.findByMaterialAndUser(material, user);
    		if (materialRole != null) {
    			userMaterialRoleDAO.delete(materialRole);
    		}
    	}
    }
    
    return Response.ok(new ApiResult<>(CompactMaterialBean.fromMaterialEntity(material))).build();
	}

  /**
   * Uploads materials. User needs to be logged in and needs to have modification permission into the parent folder
 
	@POST
	@Path ("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response upload(
			@Context HttpServletRequest request,
			@Context HttpHeaders httpHeaders) {
		
		User loggedUser = getLoggedUser(httpHeaders);
		Locale browserLocale = getBrowserLocale(httpHeaders);
		HttpSession session = request.getSession();
		
		if (!hasRole(loggedUser, UserRole.GUEST)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
		UploadInfo uploadInfo = new UploadInfo();
		session.setAttribute(UPLOAD_INFO_SESSION_ATTR, uploadInfo);

		FileItemFactory factory = new MonitoredDiskFileItemFactory(MAX_UPLOAD_MEMORY, new java.io.File(System.getProperty("java.io.tmpdir")), uploadInfo);
		ServletFileUpload upload = new ServletFileUpload(factory);
  	upload.setHeaderEncoding("UTF-8");
    upload.setSizeMax(MAX_UPLOAD_SIZE);

		List<FileData> fileDatas;
    try {
      fileDatas = FileUploadUtils.getRequestFileItems(upload, request, true);
    } catch (IOException e) {
    	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.fileUploadError")).build();
    } catch (FileUploadException e) {
    	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.fileUploadError")).build();
    }
    
    String parentFolderParam = null;

    for (FileData fileData : fileDatas) {
    	if ("parentFolderId".equals(fileData.getFieldName())) {
    		String value = new String(fileData.getData());
    		if (StringUtils.isNotBlank(value))
    			parentFolderParam = value;
    	}
    }
    
    if (StringUtils.isBlank(parentFolderParam)) {
    	return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "parentFolderId")).build();
    }
    
		Folder parentFolder = null;

		if (!parentFolderParam.equals("HOME")) {
   		Long parentFolderId = NumberUtils.createLong(parentFolderParam);
			parentFolder = folderDAO.findById(parentFolderId);
			if (!materialPermissionController.hasModifyPermission(loggedUser, parentFolder)) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}
    }
	
		List<Material> materials = new ArrayList<Material>();
		try {
			for (FileData fileData : fileDatas) {
				if (StringUtils.isNotBlank(fileData.getFileName()) && fileData.getData() != null && fileData.getData().length > 0) {
					UploadInfoFile fileInfo = uploadInfo.getFileInfo(fileData.getFieldName());
					// TODO: Processed pct
					fileInfo.setProcessed(50d);
					materials.add(materialController.createMaterial(parentFolder, loggedUser, fileData));

					fileInfo.setProcessed(100d);
					fileInfo.setStatus(UploadInfoFile.Status.COMPLETE);
  			}
			}
		} catch (MimeTypeParseException | IOException | GeneralSecurityException e) {
			logger.log(Level.SEVERE, "File uploading failed", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.materials.uploadingFailed")).build();
		}
		
		Map<String, List<CompleteMaterialBean>> result = new HashMap<String, List<CompleteMaterialBean>>();
		List<CompleteMaterialBean> materialBeans = new ArrayList<>();
		for (Material material : materials) {
			boolean mayEdit = loggedUser != null ? materialPermissionController.hasModifyPermission(loggedUser, material) : false;
			boolean mayView = loggedUser != null ? materialPermissionController.hasAccessPermission(loggedUser, material) : false;
			boolean mayDelete = mayEdit;
			materialBeans.add(CompleteMaterialBean.fromEntity(material, mayEdit, mayView, mayDelete));
		}
		
		result.put("materials", materialBeans);
		
		return Response.ok().build();
	}  */
	
	@GET
	@Path ("/uploadStatus")
	public Response uploadStatus(@Context HttpServletRequest request) {
		HttpSession session = request.getSession();
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();

		UploadInfo uploadInfo = (UploadInfo) session.getAttribute(UPLOAD_INFO_SESSION_ATTR);

		if (uploadInfo != null) {
  		for (UploadInfoFile uploadInfoFile : uploadInfo.getFiles()) {
  			Map<String, Object> file = new HashMap<String, Object>();
  			file.put("fieldName", uploadInfoFile.getFieldName());
  			file.put("processed", uploadInfoFile.getProcessed());
  			file.put("status", uploadInfoFile.getStatus());
  			file.put("uploaded", uploadInfoFile.getUploaded());
  			result.add(file);
  		}
		}
		
		return Response.ok(new ApiResult<>(result)).build();
	}

  @GET
	@Path ("/search")
	@SuppressWarnings("unchecked")
	public Response search(
			@QueryParam ("text") String text,
			@Context UriInfo uriInfo,
			@Context HttpHeaders httpHeaders
	  ) {
  	
  	Locale browserLocale = getBrowserLocale(httpHeaders);
  	User loggedUser = getLoggedUser(httpHeaders);
		String[] criterias = text.replace(",", " ").replaceAll("\\s+", " ").split(" ");
		Map<String, List<?>> result = new HashMap<String, List<?>>();
		int maxFragments = 5;

		List<SearchResult<CompleteMaterialBean>> materials = new ArrayList<SearchResult<CompleteMaterialBean>>();
		
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
		Fragmenter fragmenter = new SimpleFragmenter();
		Set<Long> foundMaterialIds = new HashSet<Long>();
		
		try {
			Query luceneQuery = parser.parse(queryStringBuilder.toString());
	    FullTextQuery query = (FullTextQuery) fullTextEntityManager.createFullTextQuery(luceneQuery, Document.class, File.class, Folder.class, GoogleDocument.class, Image.class, Pdf.class, VectorImage.class);
  		Highlighter highlighter = new Highlighter(new QueryScorer(luceneQuery));
  		highlighter.setTextFragmenter(fragmenter);
  		
  		for (Material material : (List<Material>) query.getResultList()) {
  			foundMaterialIds.add(material.getId());
  			
  			if (materialPermissionController.isPublic(loggedUser, material) || materialPermissionController.hasAccessPermission(loggedUser, material)) {
    			String matchText = null;
    			
    			if (material.getType() == MaterialType.DOCUMENT) {
      			try {
      				Document document = (Document) material;
      				String content = document.getContentPlain();
          		TokenStream tokenStream = analyzer.tokenStream( "contentPlain", new StringReader(content) );
        			matchText = highlighter.getBestFragments(tokenStream, content, maxFragments, "...");
            } catch (IOException e) {
            	logger.log(Level.WARNING, "Lucene query analyzing failed", e);
            } catch (InvalidTokenOffsetsException e) {
            	logger.log(Level.WARNING, "Lucene query analyzing failed", e);
            }
    			}
    			
    			String link;
          try {
  	        link = getApplicationBaseUrl(uriInfo) + "/" + material.getPath();
          } catch (MalformedURLException e) {
      			logger.log(Level.SEVERE, "Failed to resolve application base path", e);
      			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.configurationError")).build();
          }
  
      		boolean mayEdit = loggedUser != null ? materialPermissionController.hasModifyPermission(loggedUser, material) : false;
      		boolean mayView = loggedUser != null ? materialPermissionController.hasAccessPermission(loggedUser, material) : false;
      		boolean mayDelete = mayEdit;
          
          List<String> tags = getMaterialTags(material);
    			materials.add(new SearchResult<CompleteMaterialBean>(CompleteMaterialBean.fromEntity(loggedUser, material, mayEdit, mayView, mayDelete), material.getTitle(), link, matchText, tags));
  			}
  		}
		} catch (ParseException e) {
			logger.log(Level.SEVERE, "Lucene query parsing failed", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.fullTextQueryParsingError")).build();
    }
		
		// find by tag
		queryStringBuilder = new StringBuilder();
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
		
		try {
			Query luceneQuery = parser.parse(queryStringBuilder.toString());
	    FullTextQuery query = (FullTextQuery) fullTextEntityManager.createFullTextQuery(luceneQuery, MaterialTag.class);
	    
	    Highlighter highlighter = new Highlighter(new QueryScorer(luceneQuery));
  		highlighter.setTextFragmenter(fragmenter);
  		
  		for (MaterialTag materialTag : (List<MaterialTag>) query.getResultList()) {
  			Material material = materialTag.getMaterial();
  			if (!foundMaterialIds.contains(material.getId())) {
  				foundMaterialIds.add(material.getId());
  				
  				if (materialPermissionController.isPublic(loggedUser, material) || materialPermissionController.hasAccessPermission(loggedUser, material)) {
      			String link;
            try {
    	        link = getApplicationBaseUrl(uriInfo) + "/" + material.getPath();
            } catch (MalformedURLException e) {
        			logger.log(Level.SEVERE, "Failed to resolve application base path", e);
        			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.configurationError")).build();
            }
            
        		boolean mayEdit = loggedUser != null ? materialPermissionController.hasModifyPermission(loggedUser, material) : false;
        		boolean mayView = loggedUser != null ? materialPermissionController.hasAccessPermission(loggedUser, material) : false;
        		boolean mayDelete = mayEdit;

            List<String> tags = getMaterialTags(material);
      			materials.add(new SearchResult<CompleteMaterialBean>(CompleteMaterialBean.fromEntity(loggedUser, material, mayEdit, mayView, mayDelete), material.getTitle(), link, null, tags));
  				}
  			}
  		}
  		
		} catch (ParseException e) {
			logger.log(Level.SEVERE, "Lucene query parsing failed", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.fullTextQueryParsingError")).build();
    }
		
		result.put("materials", materials);
		
		return Response.ok(new ApiResult<>(result)).build();
	}
  
  private List<String> getMaterialTags(Material material) {
  	List<String> result = new ArrayList<String>();
  	List<MaterialTag> materialTags = materialTagDAO.listByMaterial(material);
  	for (MaterialTag materialTag : materialTags) {
  		result.add(materialTag.getTag().getText());
  	}
  	
  	return result;
  }
}
