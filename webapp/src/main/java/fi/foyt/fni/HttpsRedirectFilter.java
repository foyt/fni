package fi.foyt.fni;

import java.io.IOException;

import javax.inject.Inject;
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

import fi.foyt.fni.system.SystemSettingsController;

@WebFilter (urlPatterns = { "/", "/login/*", "/gamelibrary/*", "/oauth2/*", "/store/*" })
public class HttpsRedirectFilter implements Filter {
  
  @Inject
  private SystemSettingsController systemSettingsController;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }
  
  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
    if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {
      HttpServletRequest request = (HttpServletRequest) req;
      HttpServletResponse response = (HttpServletResponse) resp;
      
      String siteHost = systemSettingsController.getSiteHost();
      String currentHost = request.getServerName();
      
      if (!request.isSecure() && (siteHost.equals(currentHost))) {
        StringBuilder redirectUrlBuilder = new StringBuilder();
        
        redirectUrlBuilder
          .append("https://")
          .append(currentHost);
        
        Integer httpsPort = systemSettingsController.getSiteHttpsPort();
        if (httpsPort != 443) {
          redirectUrlBuilder.append(":").append(httpsPort);
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
