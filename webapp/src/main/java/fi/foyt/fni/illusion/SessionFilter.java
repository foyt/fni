package fi.foyt.fni.illusion;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

@WebFilter (
  urlPatterns = "/illusion/index.jsf"  
)
public class SessionFilter implements Filter {
  
  @Inject
  private IllusionSessionController illusionSessionController;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (illusionSessionController.getSession() != null) {
      chain.doFilter(request, response);
    } else {
      HttpServletResponse servletResponse = (HttpServletResponse) response;
      servletResponse.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
      servletResponse.setHeader("Location", request.getServletContext().getContextPath() + "/illusion/session.jsf");
    }
  }

  @Override
  public void destroy() {
  }

}
