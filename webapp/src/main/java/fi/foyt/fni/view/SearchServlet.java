package fi.foyt.fni.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.queryParser.ParseException;
import org.codehaus.jackson.map.ObjectMapper;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.search.SearchResult;

@WebServlet(urlPatterns = "/search/", name = "search")
@Transactional
public class SearchServlet extends AbstractServlet {

	private static final long serialVersionUID = 5027578435195813091L;
	
	@Inject
	private Logger logger;

	@Inject
	private PublicationController publicationController;

  @Inject
	private ForumController forumController;

  @Inject
	private MaterialController materialController;

  @Inject
  private UserController userController;

  @Inject
  private SessionController sessionController;

  @Inject
  private SystemSettingsController systemSettingsController;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String queryText = request.getParameter("q");
		if (StringUtils.isBlank(queryText)) {
			sendError(response, HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		int maxHits = 3;
		String maxHitsParamter = request.getParameter("maxHits");
		if (StringUtils.isNumeric(maxHitsParamter)) {
		  maxHits = NumberUtils.createInteger(maxHitsParamter);
		}
		
		List<Source> sources = null;
		String[] sourceParameters = request.getParameterValues("source");
		if ((sourceParameters == null)||(sourceParameters.length == 0)) {
			sources = Arrays.asList(Source.values());
		} else {
			sources = new ArrayList<>();
			for (String sourceParameter : sourceParameters) {
				Source source = Source.valueOf(sourceParameter);
				if (source != null) {
				  sources.add(source);
				} else {
					sendError(response, HttpServletResponse.SC_NOT_IMPLEMENTED);
					return;
				}
			}
		}
		
		Map<String, List<Map<String, Object>>> results = new HashMap<>();
		
		try {
			for (Source source : sources) {
				results.put(source.toString(), executeSearch(source, queryText, maxHits));
			}

			response.setContentType("application/json");
			
			try {
	      ObjectMapper objectMapper = new ObjectMapper();
	      ServletOutputStream outputStream = response.getOutputStream();
	      try {
	        outputStream.write(objectMapper.writeValueAsBytes(results));
	      } finally {
	        outputStream.flush();
	      }
      } catch (IOException e) {
        logger.log(Level.FINEST, "IOException occurred on servlet", e);
      }
			
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (ParseException e) {
			sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}
	
	private List<Map<String, Object>> executeSearch(Source source, String queryText, int maxHits) throws ParseException {
		switch (source) {
			case GAMELIBRARY:
			  return searchGameLibrary(queryText, maxHits);
			case BLOG:
				return searchBlog(queryText, maxHits);
			case FORGE:
				return searchForge(queryText, maxHits);
			case FORUM:
				return searchForum(queryText, maxHits);
			case USERS:
				return searchUsers(queryText, maxHits);
      default:
        logger.severe(String.format("Unknown source %s", source.toString()));
      break;
		}
		
		return null;
	}
	
	private List<Map<String, Object>> searchGameLibrary(String queryText, int maxHits) throws ParseException {
		List<Map<String, Object>> result = new ArrayList<>();

		List<SearchResult<Publication>> searchResults = publicationController.searchPublications(queryText, maxHits);
		
		for (SearchResult<Publication> searchResult : searchResults) {
			Map<String, Object> jsonItem = new HashMap<>();
      jsonItem.put("id", searchResult.getEntity().getId());
			jsonItem.put("name", searchResult.getTitle());
			jsonItem.put("link", systemSettingsController.getSiteUrl(false, true) + searchResult.getLink());
			jsonItem.put("path", searchResult.getLink());
			result.add(jsonItem);
		}
		
		return result;
	}
	
	private List<Map<String, Object>> searchBlog(String queryText, int maxHits) throws ParseException {
		List<Map<String, Object>> result = new ArrayList<>();
		return result;
	}
	
	private List<Map<String, Object>> searchForge(String queryText, int maxHits) throws ParseException {
	  User loggedUser = sessionController.getLoggedUser();
	  
		List<Map<String, Object>> result = new ArrayList<>();
		
		List<SearchResult<Material>> searchResults = materialController.searchMaterials(loggedUser, queryText, maxHits);
		for (SearchResult<Material> searchResult : searchResults) {
		  String link = materialController.getForgeMaterialViewerUrl(searchResult.getEntity());
		  String path = searchResult.getEntity().getPath();
		  
      Map<String, Object> jsonItem = new HashMap<>();
      jsonItem.put("id", searchResult.getEntity().getId());
      jsonItem.put("name", searchResult.getTitle());
      jsonItem.put("link", systemSettingsController.getSiteUrl(false, true) + link);
      jsonItem.put("path", path);
      result.add(jsonItem);
    }
		
		return result;
	}
	
	private List<Map<String, Object>> searchForum(String queryText, int maxHits) throws ParseException {
		List<Map<String, Object>> result = new ArrayList<>();
		
		List<SearchResult<ForumTopic>> searchResults = forumController.searchTopics(queryText, maxHits);
		for (SearchResult<ForumTopic> searchResult : searchResults) {
			Map<String, Object> jsonItem = new HashMap<>();
      jsonItem.put("id", searchResult.getEntity().getId());
			jsonItem.put("name", searchResult.getTitle());
			jsonItem.put("link", systemSettingsController.getSiteUrl(false, true) + searchResult.getLink());
			jsonItem.put("path", searchResult.getLink());
			result.add(jsonItem);
		}
		
		return result;
	}
	
	private List<Map<String, Object>> searchUsers(String queryText, int maxHits) throws ParseException {
	  List<Map<String, Object>> result = new ArrayList<>();
    
    List<SearchResult<User>> searchResults = userController.searchUsers(queryText, maxHits);
    for (SearchResult<User> searchResult : searchResults) {
      Map<String, Object> jsonItem = new HashMap<>();
      jsonItem.put("id", searchResult.getEntity().getId());
      jsonItem.put("name", searchResult.getTitle());
      jsonItem.put("link", systemSettingsController.getSiteUrl(false, true) + searchResult.getLink());
      jsonItem.put("path", searchResult.getLink());
      result.add(jsonItem);
    }
    
    return result;
	}
	
	private enum Source {
		GAMELIBRARY,
		USERS,
		FORGE,
		FORUM,
		BLOG
	}
	
}
