package fi.foyt.fni.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;

import fi.foyt.fni.api.beans.CompactDocumentBean;
import fi.foyt.fni.api.beans.CompactPdfBean;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.dao.common.LanguageDAO;
import fi.foyt.fni.persistence.dao.materials.DocumentDAO;
import fi.foyt.fni.persistence.dao.materials.DocumentRevisionDAO;
import fi.foyt.fni.persistence.dao.materials.FolderDAO;
import fi.foyt.fni.persistence.dao.materials.PdfDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.materials.Pdf;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.compression.CompressionUtils;
import fi.foyt.fni.utils.diff.DiffUtils;
import fi.foyt.fni.utils.html.HtmlUtils;
import fi.foyt.fni.utils.itext.B64ImgReplacedElementFactory;
import fi.foyt.fni.utils.language.GuessedLanguage;
import fi.foyt.fni.utils.language.LanguageUtils;
import fi.foyt.fni.utils.servlet.RequestUtils;

@Path("/materials/documents")
@RequestScoped
@Stateful
@Produces ("application/json")
public class MaterialsDocumentsRESTService extends RESTService {
  
	@Inject
	private Logger logger;

	@Inject
	private MaterialPermissionController materialPermissionController;

	@Inject
	private SystemSettingsController systemSettingsController;
	
  @Inject
  private MaterialController materialController;
	
	@Inject
	private DocumentDAO documentDAO;
	
	@Inject
	private DocumentRevisionDAO documentRevisionDAO;
	
	@Inject
	private FolderDAO folderDAO;

	@Inject
	private PdfDAO pdfDAO;
	
	@Inject
	private LanguageDAO languageDAO;

	/**
   * Creates a new document. User needs to be logged in and needs to have modification permission into the parent folder
   */
	@POST
	@PUT
	@Path ("/createDocument")
	public Response create(
			@FormParam ("title") String title,
			@FormParam ("data") String data,
			@FormParam ("parentFolderId") String parentFolderIdString,
			@FormParam ("language") String languageParam,
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

		if (!hasRole(loggedUser, UserRole.GUEST)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
				
		Long parentFolderId = "HOME".equals(parentFolderIdString) ? null : NumberUtils.createLong(parentFolderIdString);

		Folder parentFolder = null;
		if (parentFolderId != null) {
			parentFolder = folderDAO.findById(parentFolderId);
		}
		
		if (parentFolder != null) {
	    if (!materialPermissionController.hasModifyPermission(loggedUser, parentFolder)) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
	    }
		}
		
		if (StringUtils.isBlank(title)) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "title")).build();
		}

		if (HtmlUtils.containsMicrosoftCruft(data)) {
			// TODO: Inform user about this
			try {
	      data = HtmlUtils.printDocument(HtmlUtils.tidyToDOM(data));
      } catch (IOException e) {
      	logger.log(Level.SEVERE, "Document parsing failed", e);
  			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.materials.documents.couldNotTidyDocument")).build();
      }
		}

		Language language = StringUtils.isBlank(languageParam) ? null : languageDAO.findByIso2(languageParam);
		if (language == null) {
			List<GuessedLanguage> guessedLanguages;
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
		}

		String urlName = materialController.getUniqueMaterialUrlName(loggedUser, parentFolder, null, title);
		Document document = documentDAO.create(loggedUser, language, parentFolder, urlName, title, data, MaterialPublicity.PRIVATE);

		return Response.ok(new ApiResult<>(CompactDocumentBean.fromEntity(document))).build();
	}
  
  /**
   * Updates a document content. User needs to be logged in and has modification permission to document
   */
	@POST
  @PUT
  @Path ("/{DOCUMENTID}/updateDocument")
  public Response updateDocument(
  		@PathParam ("DOCUMENTID") Long documentId,
  		@FormParam ("title") String title,
  		@FormParam ("data") String data,
  		@FormParam ("language") String languageParam,
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);
		
    if (!hasRole(loggedUser, UserRole.GUEST)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
    
    Document document = documentDAO.findById(documentId);
    
    if (!materialPermissionController.hasModifyPermission(loggedUser, document)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
    }
    
    if (HtmlUtils.containsMicrosoftCruft(data)) {
      // TODO: Add warning: ApiMessages.getText(browserLocale, "error.materials.documents.tidyingNeeded"), Severity.INFO));
      try {
	      data = HtmlUtils.printDocument(HtmlUtils.tidyToDOM(data));
      } catch (IOException e) {
  			logger.log(Level.SEVERE, "Document tidying failed", e);
  			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.materials.documents.couldNotTidyDocument")).build();
      }
    }
    
    Language language = StringUtils.isBlank(languageParam) ? null : languageDAO.findByIso2(languageParam);
		if (language == null) {
			List<GuessedLanguage> guessedLanguages;
      try {
	      guessedLanguages = LanguageUtils.getGuessedLanguages(data, 0.2);
  			if (guessedLanguages.size() > 0) {
  				String languageCode = guessedLanguages.get(0).getLanguageCode();
  				language = languageDAO.findByIso2(languageCode);
  			}
      } catch (IOException e) {
      	// It's really not very serious if language detection fails.
      	// TODO: Localize
      	logger.log(Level.WARNING, "Language detection failed", e);
      }
		}
    
    String oldData = null;
    try {
	    oldData = new String(document.getData(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
			logger.log(Level.SEVERE, "UTF-8 not supported", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.configurationError")).build();
    }

		if (!StringUtils.isEmpty(oldData) && !data.equals(oldData)) {
			Long lastRevision = documentRevisionDAO.maxRevisionByDocument(document);
			if (lastRevision == null)
				lastRevision = 0l;

			boolean compressed = false;
			String patchData = DiffUtils.makePatch(data, oldData);
			byte[] patchBytes;
      
			try {
	      patchBytes = patchData.getBytes("UTF-8");
      } catch (UnsupportedEncodingException e) {
  			logger.log(Level.SEVERE, "UTF-8 not supported", e);
  			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.configurationError")).build();
      }

      try {
      	patchBytes = CompressionUtils.compressBzip2Array(patchBytes);
	      compressed = true;
      } catch (IOException e) {
      	// If revision compression fails, we log it and save the revision as uncompressed
      	logger.log(Level.WARNING, "revision compression failed", e);
      }

      documentRevisionDAO.create(document, lastRevision + 1, new Date(), compressed, false, patchBytes, null, null, null);
		}

		documentDAO.updateTitle(document, loggedUser, title);
		documentDAO.updateData(document, loggedUser, data);
    
		return Response.ok(new ApiResult<>(CompactDocumentBean.fromEntity(document))).build();
  }
	
  /**
   * Prints a document into a PDF. User needs to be logged in and has access to document
   */
	@POST
	@Path ("/{DOCUMENTID}/printToPdf")
  public Response printToPdf(
  		@PathParam ("DOCUMENTID") Long documentId,
  		@Context UriInfo uriInfo,
  		@Context HttpServletRequest servletRequest,
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

		if (!hasRole(loggedUser, UserRole.GUEST)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}

    Document document = documentDAO.findById(documentId);

    if (!materialPermissionController.hasAccessPermission(loggedUser, document)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
    }

    Date now = new Date();
    Folder parentFolder = document.getParentFolder();
    Pdf pdf = null;
    
    try {
    	byte[] pdfBytes = null;
  	  ITextRenderer renderer = new ITextRenderer();
  	  ReplacedElementFactory replacedElementFactory = new B64ImgReplacedElementFactory();
  	  renderer.getSharedContext().setReplacedElementFactory(replacedElementFactory);
  	  
  	  String documentContent = new String(document.getData(), "UTF-8");
  	  String baseUrl = getApplicationBaseUrl(uriInfo);
  	  String contextPath = servletRequest.getContextPath();
  	  
  	  org.w3c.dom.Document domDocument = null;
			try {
				domDocument = tidyForPdf(document.getTitle(), documentContent);
			} catch (ParserConfigurationException e1) {
				logger.log(Level.WARNING, "Failed to parse document", e1);
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.materials.documents.couldNotParseDocument")).build();
			} catch (SAXException e1) {
				logger.log(Level.WARNING, "Failed to parse document", e1);
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.materials.documents.couldNotParseDocument")).build();
			}
  	  
  	  try {
    	  NodeList imageList = XPathAPI.selectNodeList(domDocument, "//img");
    	  for (int i = 0, l = imageList.getLength(); i < l; i++) {
    	  	Element imageElement = (Element) imageList.item(i);
    	  	String src = imageElement.getAttribute("src");
    	  	
    	  	try {
    	  		boolean internal = false;
    	  		
    	  		if (src.startsWith("http://") || src.startsWith("https://")) {
    	  			if (src.startsWith(baseUrl)) {
    	  			  src = RequestUtils.stripCtxPath(contextPath, src.substring(baseUrl.length()));	
    	  			  internal = true;
    	  			} else {
    	  				internal = false;
    	  			}
    	  		} else {
    	  			src = RequestUtils.stripCtxPath(contextPath, src);
    	  			internal = true;
    	  		}
    	  		
    	  		if (internal) {
    	  			Material material = materialController.findMaterialByCompletePath(src);
    	  			if (materialPermissionController.hasAccessPermission(loggedUser, material)) {
      	  			if (material.getType() == MaterialType.IMAGE) {
      	  				Image image = (Image) material;
  
      	  				StringBuilder srcBuilder = new StringBuilder()
          	  			.append("data:")
          	  			.append(image.getContentType())
          	  		  .append(";base64,")
          	  		  .append(new String(Base64.encodeBase64(image.getData())));
      	  				
      	  				imageElement.setAttribute("src", srcBuilder.toString());
      	  			}
    	  			}
    	  		}
      	  	
    	  	} catch (Exception e) {
    	  		// If anything goes wrong we just leave this img "as is".
    	  	}
    	  }
	  	} catch (Exception e) {
	  		// If anything goes wrong we just leave the document "as is".
	  	}
  	  
  	  ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
  	  renderer.setDocument(domDocument, baseUrl);
  	  renderer.layout();
  	  renderer.createPDF(pdfStream);
  	  pdfStream.flush();
  	  pdfStream.close();
  	  
  	  pdfBytes = pdfStream.toByteArray();
    	
	    String urlName = materialController.getUniqueMaterialUrlName(document.getCreator(), parentFolder, null, document.getTitle());
	    pdf = pdfDAO.create(loggedUser, now, loggedUser, now, null, parentFolder, urlName, document.getTitle(), pdfBytes, MaterialPublicity.PRIVATE);
	    
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Google docs communication failed", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.materials.googledocuments.googleCommunicationError")).build();
		} 
    catch (DocumentException e) {
			logger.log(Level.SEVERE, "Document printing to PDF failed", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.materials.documents.couldNotPrintToPDF")).build();
    	
		}

		return Response.ok(new ApiResult<>(CompactPdfBean.fromEntity(pdf))).build();
  }
	
  private org.w3c.dom.Document tidyForPdf(String title, String bodyContent) throws ParserConfigurationException, IOException, SAXException {
  	String documentHtml = HtmlUtils.getAsHtmlText(title, bodyContent);
  	String cleanedHtml = null;
  	
  	ByteArrayOutputStream tidyStream = new ByteArrayOutputStream();
  	try {
    	Tidy tidy = new Tidy();
      tidy.setInputEncoding("UTF-8");
      tidy.setOutputEncoding("UTF-8");
      tidy.setShowWarnings(true);
      tidy.setNumEntities(false);
      tidy.setXmlOut(true);
      tidy.setXHTML(true);

      cleanedHtml = HtmlUtils.printDocument(tidy.parseDOM(new StringReader(documentHtml), null));
  	} catch (Exception e) {
  		throw e;
  	} finally {
      tidyStream.flush();
      tidyStream.close();
  	}
  	
    InputStream documentStream = new ByteArrayInputStream(cleanedHtml.getBytes("UTF-8"));
  	try {

      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
      builderFactory.setNamespaceAware(false);
      builderFactory.setValidating(false);
      builderFactory.setFeature("http://xml.org/sax/features/namespaces", false);
      builderFactory.setFeature("http://xml.org/sax/features/validation", false);
      builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
      builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      DocumentBuilder builder = builderFactory.newDocumentBuilder();
  
      return builder.parse(documentStream);

  	} finally {
  		documentStream.close();
  	}
	}

	/**
   * Returns document content. Document has to be either public or user has to have access to it
   **/
	@GET
	@Path ("/{DOCUMENTID}")
	public Response document(
			@PathParam ("DOCUMENTID") Long documentId,
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
		Document document = documentDAO.findById(documentId);
		User loggedUser = getLoggedUser(httpHeaders);
		
		if (!materialPermissionController.isPublic(loggedUser, document)) {
			if (!hasRole(loggedUser, UserRole.GUEST)) {
			  return Response.status(Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}

			if (!materialPermissionController.hasAccessPermission(loggedUser, document)) {
        return Response.status(Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}
		}
		
		try {
      String bodyContent = new String(document.getData(), "UTF-8");
      String html = HtmlUtils.getAsHtmlText(document.getTitle(), bodyContent);
      return createBinaryResponse(html.getBytes("UTF-8"), "text/html;charset=utf-8");
    } catch (UnsupportedEncodingException e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.configurationError")).build();
    }
	}
}
