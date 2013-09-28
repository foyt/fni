package fi.foyt.fni.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryParser.ParseException;
import org.codehaus.jackson.map.ObjectMapper;

import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.utils.search.SearchResult;
import fi.foyt.fni.view.AbstractTransactionedServlet;

@WebServlet(urlPatterns = "/search/")
public class SearchServlet extends AbstractTransactionedServlet {

	private static final long serialVersionUID = 5027578435195813091L;

	@Inject
	private PublicationController publicationController;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String queryText = request.getParameter("q");
		if (StringUtils.isBlank(queryText)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		Map<String, List<Map<String, Object>>> results = new HashMap<>();
		
		List<Map<String, Object>> publicationResult = new ArrayList<>();
		try {
			List<SearchResult<Publication>> searchResults = publicationController.searchPublications(queryText);
			
			for (SearchResult<Publication> searchResult : searchResults) {
				Map<String, Object> jsonItem = new HashMap<>();
				jsonItem.put("name", searchResult.getTitle());
				jsonItem.put("link", searchResult.getLink());
				publicationResult.add(jsonItem);
			}
			
			results.put("publications", publicationResult);

			response.setContentType("application/json");
			
			ObjectMapper objectMapper = new ObjectMapper();
			ServletOutputStream outputStream = response.getOutputStream();
			try {
				outputStream.write(objectMapper.writeValueAsBytes(results));
			} finally {
				outputStream.flush();
				outputStream.close();
			}
			
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (ParseException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}
	
}
