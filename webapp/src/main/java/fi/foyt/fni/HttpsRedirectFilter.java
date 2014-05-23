  package fi.foyt.fni;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

@WebFilter (urlPatterns = { "/login/" })
public class HttpsRedirectFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }
  
  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
    if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {
      HttpServletRequest request = (HttpServletRequest) req;
      HttpServletResponse response = (HttpServletResponse) resp;
      
      if (!request.isSecure()) {
        StringBuilder redirectUrlBuilder = new StringBuilder();
        
        redirectUrlBuilder
          .append("https://")
          .append(request.getServerName());
        
        if (request.getServerPort() == 8080) {
          redirectUrlBuilder.append(":8443");
        }
        
        redirectUrlBuilder.append(request.getRequestURI());

        if (StringUtils.isNotBlank(request.getQueryString())) {
          redirectUrlBuilder.append('?').append(request.getQueryString());
        }

        response.sendRedirect(redirectUrlBuilder.toString());
        return;
      } 
    }
    
    chain.doFilter(req, resp);
  }

  @Override
  public void destroy() {
  }

}
