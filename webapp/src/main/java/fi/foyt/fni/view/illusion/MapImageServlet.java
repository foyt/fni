package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import fi.foyt.fni.view.AbstractFileServlet;

@WebServlet(urlPatterns = "/illusion/mapImage", name = "illusion-mapimage")
public class MapImageServlet extends AbstractFileServlet {

	private static final long serialVersionUID = 8109481247044843102L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  // TODO: Cache
	  
		String url = request.getParameter("url");
	  
	  if (StringUtils.isBlank(url)) {
	    response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
		}
	  
	  HttpClient client = new DefaultHttpClient();
	  HttpGet httpGet = new HttpGet(url);
	  HttpResponse httpResponse = client.execute(httpGet);
	  
	  switch (httpResponse.getStatusLine().getStatusCode()) {
	    case 200:
	      HttpEntity entity = httpResponse.getEntity();
	      try {
	        response.setContentType(entity.getContentType().getValue());
	        
	        InputStream inputStream = entity.getContent();
	        try {
	          ServletOutputStream outputStream = response.getOutputStream();
	          try {
	            IOUtils.copy(inputStream, outputStream);
	          } finally {
	            outputStream.flush();
	          }
	          
	        } finally {
	          inputStream.close();
	        }
	      } finally {
	        EntityUtils.consume(entity);
	      }
	    break;
	    case 404:
	      response.sendError(HttpServletResponse.SC_NOT_FOUND);
	      return;
	    default:
	      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	      return;
	  }
	}
}
