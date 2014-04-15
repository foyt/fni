package fi.foyt.fni.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
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
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.search.SearchResult;
import fi.foyt.fni.view.AbstractTransactionedServlet;

@WebServlet(urlPatterns = "/search/", name = "search")
public class SearchServlet extends AbstractTransactionedServlet {

	private static final long serialVersionUID = 5027578435195813091L;

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
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String queryText = request.getParameter("q");
		if (StringUtils.isBlank(queryText)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
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
					response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
					return;
				}
			}
		}
		
		Map<String, List<Map<String, Object>>> results = new HashMap<>();
		
		try {
			for (Source source : sources) {
				results.put(source.toString(), executeSearch(source, request.getContextPath(), queryText, maxHits));
			}

			response.setContentType("application/json");
			
			ObjectMapper objectMapper = new ObjectMapper();
			ServletOutputStream outputStream = response.getOutputStream();
			try {
				outputStream.write(objectMapper.writeValueAsBytes(results));
			} finally {
				outputStream.flush();
			}
			
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (ParseException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}
	
	private List<Map<String, Object>> executeSearch(Source source, String contextPath, String queryText, int maxHits) throws ParseException {
		switch (source) {
			case GAMELIBRARY:
			  return searchGameLibrary(contextPath, queryText, maxHits);
			case BLOG:
				return searchBlog(queryText, maxHits);
			case FORGE:
				return searchForge(contextPath, queryText, maxHits);
			case FORUM:
				return searchForum(contextPath, queryText, maxHits);
			case USERS:
				return searchUsers(contextPath, queryText, maxHits);
		}
		
		return null;
	}
	
	private List<Map<String, Object>> searchGameLibrary(String contextPath, String queryText, int maxHits) throws ParseException {
		List<Map<String, Object>> result = new ArrayList<>();

		List<SearchResult<Publication>> searchResults = publicationController.searchPublications(queryText, maxHits);
		
		for (SearchResult<Publication> searchResult : searchResults) {
			Map<String, Object> jsonItem = new HashMap<>();
      jsonItem.put("id", searchResult.getEntity().getId());
			jsonItem.put("name", searchResult.getTitle());
			jsonItem.put("link", contextPath + searchResult.getLink());
			result.add(jsonItem);
		}
		
		return result;
	}
	
	private List<Map<String, Object>> searchBlog(String queryText, int maxHits) throws ParseException {
		List<Map<String, Object>> result = new ArrayList<>();
		return result;
	}
	
	private List<Map<String, Object>> searchForge(String contextPath, String queryText, int maxHits) throws ParseException {
	  User loggedUser = sessionController.getLoggedUser();
	  
		List<Map<String, Object>> result = new ArrayList<>();
		
		List<SearchResult<Material>> searchResults = materialController.searchMaterials(loggedUser, queryText, maxHits);
		for (SearchResult<Material> searchResult : searchResults) {
		  String link = materialController.getForgeMaterialViewerUrl(searchResult.getEntity());
		  
      Map<String, Object> jsonItem = new HashMap<>();
      jsonItem.put("id", searchResult.getEntity().getId());
      jsonItem.put("name", searchResult.getTitle());
      jsonItem.put("link", contextPath + link);
      result.add(jsonItem);
    }
		
		return result;
	}
	
	private List<Map<String, Object>> searchForum(String contextPath, String queryText, int maxHits) throws ParseException {
		List<Map<String, Object>> result = new ArrayList<>();
		
		List<SearchResult<ForumTopic>> searchResults = forumController.searchTopics(queryText, maxHits);
		for (SearchResult<ForumTopic> searchResult : searchResults) {
			Map<String, Object> jsonItem = new HashMap<>();
      jsonItem.put("id", searchResult.getEntity().getId());
			jsonItem.put("name", searchResult.getTitle());
			jsonItem.put("link", contextPath + searchResult.getLink());
			result.add(jsonItem);
		}
		
		return result;
	}
	
	private List<Map<String, Object>> searchUsers(String contextPath, String queryText, int maxHits) throws ParseException {
	  List<Map<String, Object>> result = new ArrayList<>();
    
    List<SearchResult<User>> searchResults = userController.searchUsers(queryText, maxHits);
    for (SearchResult<User> searchResult : searchResults) {
      Map<String, Object> jsonItem = new HashMap<>();
      jsonItem.put("id", searchResult.getEntity().getId());
      jsonItem.put("name", searchResult.getTitle());
      jsonItem.put("link", contextPath + searchResult.getLink());
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
