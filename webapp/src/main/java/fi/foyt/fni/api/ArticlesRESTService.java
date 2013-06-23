package fi.foyt.fni.api;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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

import org.apache.commons.lang3.StringUtils;
import fi.foyt.fni.api.beans.CompletePublishedArticleBean;
import fi.foyt.fni.articles.ArticleDataBean;
import fi.foyt.fni.articles.ArticleController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.materials.MaterialDAO;
import fi.foyt.fni.persistence.dao.materials.PublishedArticleDAO;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.PublishedArticle;
import fi.foyt.fni.persistence.model.materials.PublishedArticleType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;

@Path("/articles")
@RequestScoped
@Stateful
@Produces ("application/json")
public class ArticlesRESTService extends RESTService {

	@Inject
	private MaterialPermissionController materialPermissionController;

	@Inject
	private ArticleController articleController;
	 
	@Inject
	@DAO
  private MaterialDAO materialDAO;
	
	@Inject
	@DAO
	private PublishedArticleDAO publishedArticleDAO;
  
  /**
   * Returns article content. 
   */
  @GET
  @Path ("/{ARTICLEID}")
  public Response article(
  		@PathParam ("ARTICLEID") Long articleId,
  		@Context HttpHeaders httpHeaders) {
  
		Locale browserLocale = getBrowserLocale(httpHeaders);
    PublishedArticle article = publishedArticleDAO.findById(articleId);
    if (article == null) {    	
    	return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "ARTICLEID")).build();
    }

    ArticleDataBean articleData = null;
    
    try {
      articleData = articleController.getArticleData(article);
    } catch (UnsupportedEncodingException e) {
    	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.configurationError")).build();
    }
    
    if (articleData == null) {
    	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.articles.unsupportedType")).build();
    }
    
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("id", articleData.getId());
    result.put("title", articleData.getTitle());
    result.put("content", articleData.getContent());
    
    return Response.ok(new ApiResult<Map<String, Object>>(result)).build();
  }

  /**
   * Publishes a material as article. User needs to be logged in as administrator and needs to have modification permission into the material
   * @return 
   */
	@POST
	@PUT
  @Path ("/publish")
	public Response publish(
			@FormParam ("materialId") Long materialId,
			@FormParam ("type") String typeParam,
			@Context HttpHeaders httpHeaders) {

		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

		if (!hasRole(loggedUser, UserRole.ADMINISTRATOR)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
		Material material = materialDAO.findById(materialId);
    if (material == null) {
    	return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "materialId")).build();
    }
		
		if (!materialPermissionController.hasAccessPermission(loggedUser, material)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
		PublishedArticleType type = PublishedArticleType.valueOf(typeParam);
		Date now = new Date();
    
    PublishedArticle publishedArticle = publishedArticleDAO.create(material, type, loggedUser, now);
    
    return Response.ok(new ApiResult<CompletePublishedArticleBean>(CompletePublishedArticleBean.fromEntity(publishedArticle))).build();
	}
	
  /**
   * Unpublishes as article. User needs to be logged in as administrator
   */
  @POST
  @Path ("/unpublish/{MATERIALID}")
  public Response unpublish(
  		@PathParam ("MATERIALID") Long materialId,
  		@Context HttpHeaders httpHeaders) {

  	Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

		if (!hasRole(loggedUser, UserRole.ADMINISTRATOR)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}

  	Material material = materialDAO.findById(materialId);
    if (material == null) {			
    	return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "materialId")).build();
    }
    
    PublishedArticle article = publishedArticleDAO.findByMaterial(material);
    if (article == null) {
    	return Response.status(Response.Status.NOT_FOUND).entity(ApiMessages.getText(browserLocale, "error.generic.notFound")).build();
    }
    
    publishedArticleDAO.delete(article);
    
    return Response.ok().build();
  }
  
  /**
   * Sets article as default for the locale. User needs to be logged in as administrator
   * @return 
   */
  @POST
  @PUT
  @Path ("/setLocaleDefault/{ARTICLEID}")
  public Response setLocaleDefault(
  		@PathParam ("ARTICLEID") Long articleId,
  		@FormParam ("locale") String localeParam,
  		@Context HttpHeaders httpHeaders) {

  	Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

		if (!hasRole(loggedUser, UserRole.ADMINISTRATOR)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
  	
		Locale locale;
    if (StringUtils.isNotBlank(localeParam))
    	locale = new Locale(localeParam);
    else
    	locale = browserLocale;
    
    PublishedArticle article = publishedArticleDAO.findById(articleId);
    if (article == null) {			
    	return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "ARTICLEID")).build();
    }
    
    articleController.setLocaleDefaultArticle(locale, article);
    
    return Response.ok(new ApiResult<CompletePublishedArticleBean>(CompletePublishedArticleBean.fromEntity(article))).build();
  }
  
}
