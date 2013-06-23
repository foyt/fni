package fi.foyt.fni.api;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.session.SessionController;

public abstract class RESTService {

	private static final String API_PATH = "/v1/";
	
	@Inject
	private SessionController sessionController;
	
	@Inject
	private Logger logger;
	
	protected User getLoggedUser(HttpHeaders httpHeaders) {
	  return sessionController.getLoggedUser();
	}
	
	protected Locale getBrowserLocale(HttpHeaders httpHeaders) {
		Locale locale = httpHeaders.getLanguage();
		
		if (locale == null) {
  		List<Locale> acceptableLanguages = httpHeaders.getAcceptableLanguages();
  		if (acceptableLanguages != null && acceptableLanguages.size() > 0)
  			locale = acceptableLanguages.get(0);
		}
		
		if (locale != null)
			return locale;
		
		// TODO: Check if is supported language
		// TODO: System Default
		
		return new Locale("en", "US");
	}

	/**
	 * Returns base path of the application
	 * 
	 * @param uriInfo 
	 * @return
	 * @throws MalformedURLException
	 */
	protected String getApplicationBaseUrl(UriInfo uriInfo) throws MalformedURLException {
		String apiBasePath = uriInfo.getBaseUri().toString();
		return apiBasePath.substring(0, apiBasePath.length() - API_PATH.length());
  }
	
	protected boolean hasRole(User user, UserRole role) {
		if (user == null)
			return false;
		
		UserRole userRole = user.getRole();
		
		switch (role) {
		  case ADMINISTRATOR:
		  	if (userRole == UserRole.ADMINISTRATOR)
		  		return true;
		  break;
		  case USER:
		  	if ((userRole == UserRole.USER)||(userRole == UserRole.ADMINISTRATOR))
		  		return true;
		  break;
		  case GUEST:
		  	return true;
		}
		
		return false;
  }
	
	protected Response createBinaryResponse(final byte[] data, String contentType) {
		return createBinaryResponse(data, contentType, null);
	}

	protected Response createBinaryResponse(final byte[] data, String contentType, String urlName) {
		ResponseBuilder responseBuilder = Response.ok(new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				if (data != null)
				  output.write(data);
			}
		}, MediaType.valueOf(contentType));
		
		if (StringUtils.isNotBlank(urlName)) {
			responseBuilder.header("content-disposition", "attachment; filename=" + urlName);
		}
		
		return responseBuilder.build();
	}
}
