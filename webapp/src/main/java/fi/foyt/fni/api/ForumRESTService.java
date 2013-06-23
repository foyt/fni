package fi.foyt.fni.api;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;

import fi.foyt.fni.api.beans.CompleteForumPostBean;
import fi.foyt.fni.api.beans.CompleteForumTopicBean;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.forum.ForumDAO;
import fi.foyt.fni.persistence.dao.forum.ForumPostDAO;
import fi.foyt.fni.persistence.dao.forum.ForumTopicDAO;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.utils.search.SearchResult;
import fi.foyt.fni.utils.servlet.RequestUtils;

@Path("/forum")
@RequestScoped
@Stateful
@Produces ("application/json")
public class ForumRESTService extends RESTService {

	@Inject
	private FullTextEntityManager fullTextEntityManager;
	
	@Inject
	private Logger logger;
	
	@Inject
	@DAO
	private ForumDAO forumDAO;

	@Inject
	@DAO
	private ForumPostDAO forumPostDAO;
	
	@Inject
	@DAO
	private ForumTopicDAO forumTopicDAO;

	/**
	 * Creates new forum topic. User needs to be at least a User
	 *  
	 * @param forumId forum id
	 * @param subject topic subject
	 * @param content topic content
	 * @param httpHeaders request HTTP headers
	 * @return response
	 */
  @PUT
  @POST
  @Path ("/{FORUMID}/createTopic")
  public Response createTopic(
  		@PathParam ("FORUMID") Long forumId,
  		@FormParam ("subject") String subject,
  		@FormParam ("content") String content,
  		@Context HttpHeaders httpHeaders) {
// TODO: Post tidying
    
  	Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

		if (!hasRole(loggedUser, UserRole.USER)) {
		  return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
  	
    Date now = new Date();
    Forum forum = forumDAO.findById(forumId);
    String urlName = getUniqueTopicUrlName(forum, null, subject);
    ForumTopic forumTopic = forumTopicDAO.create(forum, loggedUser, now, now, urlName, subject, 0l);
    forumPostDAO.create(forumTopic, loggedUser, now, now, content, 0l);
    
    return Response.ok(
      CompleteForumTopicBean.fromEntity(forumTopic)
    ).build();
  }

  private String getUniqueTopicUrlName(Forum forum, ForumTopic forumTopic, String subject) {
    String urlName = RequestUtils.createUrlName(subject);
    if (forumTopic != null && urlName.equals(forumTopic.getUrlName()))
      return urlName;

    String baseName = urlName;
    ForumTopic urlTopic = null;
    int i = 0;
    do {
      urlTopic = forumTopicDAO.findByForumAndUrlName(forum, urlName);
      if (urlTopic == null)
        return urlName;
      if (forumTopic != null && forumTopic.getId().equals(urlTopic.getId()))
        return urlName;
      
      urlName = baseName + '_' + (++i);
    } while (true);
  }

  /**
   * Posts new reply to topic. User needs to be at least a User
   * @return 
   */
  @POST
  @Path ("/{TOPICID}/postReply") 
  public Response postReply(
  		@PathParam ("TOPICID") Long topicId,
  		@FormParam ("content") String content,
  		@Context HttpHeaders httpHeaders) {
// TODO: Post tidying
    
  	Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

		if (!hasRole(loggedUser, UserRole.USER)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
    
    ForumTopic forumTopic = forumTopicDAO.findById(topicId);
    Date now = new Date();
    ForumPost forumPost = forumPostDAO.create(forumTopic, loggedUser, now, now, content, 0l);
   
    return Response.ok(new ApiResult<>(CompleteForumPostBean.fromEntity(forumPost))).build();
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
		String[] criterias = text.replace(",", " ").replaceAll("\\s+", " ").split(" ");
		Map<String, List<?>> result = new HashMap<String, List<?>>();
		int maxFragments = 1;

		List<SearchResult<CompleteForumPostBean>> posts = new ArrayList<SearchResult<CompleteForumPostBean>>();
		
		StringBuilder queryStringBuilder = new StringBuilder();
		queryStringBuilder.append("+(");
		for (int i = 0, l = criterias.length; i < l; i++) {
			String criteria = QueryParser.escape(criterias[i]);
			
			queryStringBuilder.append("contentPlain:");
			queryStringBuilder.append(criteria);
			queryStringBuilder.append("* ");
			
			queryStringBuilder.append("topicSubject:");
			queryStringBuilder.append(criteria);
			queryStringBuilder.append("*");
			
			if (i < l - 1)
			  queryStringBuilder.append(' ');
		}
		
		queryStringBuilder.append(")");
		
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
		QueryParser parser = new QueryParser(Version.LUCENE_35, "", analyzer);
		try {
			Query luceneQuery = parser.parse(queryStringBuilder.toString());
	    FullTextQuery query = (FullTextQuery) fullTextEntityManager.createFullTextQuery(luceneQuery, ForumPost.class);
  		Highlighter highlighter = new Highlighter(new QueryScorer(luceneQuery));
  		highlighter.setTextFragmenter(new SimpleFragmenter());
  		
  		for (ForumPost forumPost : (List<ForumPost>) query.getResultList()) {
  			String content = forumPost.getContentPlain();
    		TokenStream tokenStream = analyzer.tokenStream( "contentPlain", new StringReader(content) );
    		try {
    			String matchText = highlighter.getBestFragments(tokenStream, content, maxFragments, "...");
    			if (StringUtils.isBlank(matchText)) {
    				matchText = StringUtils.abbreviate(forumPost.getContentPlain(), 100);
    			}
    			
          ForumTopic topic = forumPost.getTopic();
          String link = getApplicationBaseUrl(uriInfo) + "/forum/" + topic.getFullPath() + "#p" + forumPost.getId(); 
          posts.add(new SearchResult<CompleteForumPostBean>(CompleteForumPostBean.fromEntity(forumPost), forumPost.getTopic().getSubject(), link, matchText));
        } catch (IOException e) {
        	logger.log(Level.WARNING, "Lucene query analyzing failed", e);
        } catch (InvalidTokenOffsetsException e) {
        	logger.log(Level.WARNING, "Lucene query analyzing failed", e);
        }
  		}
		} catch (ParseException e) {
			logger.log(Level.SEVERE, "Lucene query parsing failed", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.fullTextQueryParsingError")).build();
    }

		result.put("posts", posts);
		
		return Response.ok(new ApiResult<>(result)).build();
	}
}
