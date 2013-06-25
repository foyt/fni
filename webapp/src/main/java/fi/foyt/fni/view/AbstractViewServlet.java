package fi.foyt.fni.view;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.servlet.RequestUtils;

public abstract class AbstractViewServlet extends HttpServlet {

	private static final long serialVersionUID = 2501078339886319673L;

	protected void handleRedirect(HttpServletResponse response, String redirectUrl, boolean permanent) {
		response.setStatus(permanent ? HttpServletResponse.SC_MOVED_PERMANENTLY : HttpServletResponse.SC_TEMPORARY_REDIRECT);
		response.setHeader("Location", redirectUrl);
	}

	protected void handleNotFound(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.setContentType("text/html; charset=utf-8");
		request.getRequestDispatcher("/jsp/generic/error-404.jsp").include(request, response);
	}

	protected void handleInternalError(HttpServletRequest request, HttpServletResponse response, Throwable cause) throws ServletException, IOException {
		cause.printStackTrace();
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		request.setAttribute("cause", cause);
		response.setContentType("text/html; charset=utf-8");
		request.getRequestDispatcher("/jsp/generic/error-500.jsp").include(request, response);
	}

	protected void handleIncludeJsp(HttpServletRequest request, HttpServletResponse response, String includeJsp) throws ServletException, IOException {
		response.setContentType("text/html; charset=utf-8");
		request.getRequestDispatcher(includeJsp).include(request, response);
	}

	protected void handleData(HttpServletRequest request, HttpServletResponse response, TypedData data) throws IOException {
		response.setContentType(data.getContentType());
		ServletOutputStream outputStream = response.getOutputStream();
		outputStream.write(data.getData());
		outputStream.flush();
		outputStream.close();
  }
	
	protected void handleForbidden(HttpServletRequest request, HttpServletResponse response, boolean loggedIn) throws UnsupportedEncodingException, ServletException, IOException {
		if (!loggedIn) {
			StringBuilder redirectUrl = new StringBuilder()
			  .append(request.getContextPath())
			  .append(RequestUtils.stripCtxPath(request.getContextPath(), request.getRequestURI()));
			
			Map<String, String[]> parameterMap = request.getParameterMap();
			Iterator<String> parameterNames = parameterMap.keySet().iterator();
			if (parameterNames.hasNext()) {
				boolean first = true;
				while (parameterNames.hasNext()) {
  				String parameterName = parameterNames.next();
			  	String[] parameterValues = parameterMap.get(parameterName);
			  	for (String parameterValue : parameterValues) {
			  		redirectUrl
			  		  .append(first ? '?' : '&') 
			  		  .append(URLEncoder.encode(parameterName, "UTF-8"))
			  		  .append('=')
			  		  .append(URLEncoder.encode(parameterValue, "UTF-8"));

			  		first = false;
			  	}
			  }
			}
			
			response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
			response.setHeader("Location", request.getContextPath() + "/login?redirectUrl=" + URLEncoder.encode(redirectUrl.toString(), "UTF-8"));
		} else {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("text/html; charset=utf-8");
			request.getRequestDispatcher("/jsp/generic/error-403.jsp").include(request, response);
		}
	}
}
